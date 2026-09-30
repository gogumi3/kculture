package com.kculture.content.service;

// [Claude 작성 - 신규 파일]
// "검색어 -> 유튜브 영상 확보 -> Gemini로 한국적 요소 분석 -> 시드 장소와 자동 매칭"까지
// 한 번에 처리하는 새 서비스입니다. 기존 MvAnalysisService/SongService는 그대로 재사용만 하고
// 수정하지 않았습니다 (기존 파일 변경 없음).

import com.kculture.content.ai.GeminiVisionClient;
import com.kculture.content.ai.YoutubeSearchClient;
import com.kculture.content.domain.CulturalCategory;
import com.kculture.content.domain.CulturalElement;
import com.kculture.content.domain.AnalysisStatus;
import com.kculture.content.domain.Song;
import com.kculture.content.dto.AnalysisCompleteRequest;
import com.kculture.content.dto.AnalysisResponse;
import com.kculture.content.dto.CulturalElementRequest;
import com.kculture.content.dto.CulturalElementResponse;
import com.kculture.content.dto.SongResponse;
import com.kculture.content.exception.ContentNotFoundException;
import com.kculture.content.repository.CulturalElementRepository;
import com.kculture.content.repository.SongRepository;
import com.kculture.recommendation.domain.ElementPlaceMatch;
import com.kculture.recommendation.repository.ElementPlaceMatchRepository;
import com.kculture.travel.domain.Place;
import com.kculture.travel.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AutoAnalysisService {

    private final SongRepository songRepository;
    private final CulturalElementRepository culturalElementRepository;
    private final PlaceRepository placeRepository;
    private final ElementPlaceMatchRepository elementPlaceMatchRepository;
    private final MvAnalysisService mvAnalysisService; // 기존 서비스를 그대로 재사용 (상태 관리/저장 로직 중복 방지)
    private final YoutubeSearchClient youtubeSearchClient;
    private final GeminiVisionClient geminiVisionClient;

    // CulturalCategory(enum) <-> places.category(한글 문자열) 매칭용.
    // 프론트 src/theme/colors.ts의 badgeLabels와 동일한 한글 라벨을 사용합니다.
    private static final Map<CulturalCategory, String> CATEGORY_TO_PLACE_LABEL = new EnumMap<>(CulturalCategory.class);

    static {
        CATEGORY_TO_PLACE_LABEL.put(CulturalCategory.ARCHITECTURE, "건축");
        CATEGORY_TO_PLACE_LABEL.put(CulturalCategory.COSTUME, "의상");
        CATEGORY_TO_PLACE_LABEL.put(CulturalCategory.FOOD, "음식");
        CATEGORY_TO_PLACE_LABEL.put(CulturalCategory.ART_RITUAL, "의례");
        CATEGORY_TO_PLACE_LABEL.put(CulturalCategory.PROP, "소품");
    }

    // 검색어로 곡을 찾는다. DB에 없으면 유튜브에서 찾아서 새로 등록한다.
    @Transactional
    public SongResponse ensureSongForQuery(String query) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("검색어를 입력하세요.");
        }
        String trimmed = query.strip();

        List<Song> existing = songRepository.findByTitleContainingIgnoreCase(trimmed);
        if (!existing.isEmpty()) {
            return toResponse(existing.get(0));
        }

        YoutubeSearchClient.YoutubeVideo video = youtubeSearchClient.searchTopVideo(trimmed);
        if (video == null || video.videoId() == null) {
            throw new IllegalArgumentException("유튜브에서 '" + trimmed + "'에 맞는 영상을 찾지 못했어요.");
        }

        Song song = songRepository.findByYoutubeVideoId(video.videoId())
                .orElseGet(() -> songRepository.save(new Song(
                        video.title() != null && !video.title().isBlank() ? video.title() : trimmed,
                        video.channelTitle() != null ? video.channelTitle() : "Unknown",
                        video.videoId(),
                        video.thumbnailUrl()
                )));

        return toResponse(song);
    }

    // 곡의 유튜브 영상을 Gemini로 분석해서 한국적 요소를 뽑고, 시드 장소와 자동으로 매칭한다.
    // 이미 완료/진행 중인 분석이 있으면 새로 호출하지 않고 그대로 반환한다 (중복 API 호출 방지).
    @Transactional
    public AnalysisResponse runAutoAnalysis(Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ContentNotFoundException("곡을 찾을 수 없습니다."));
        if (song.getYoutubeVideoId() == null || song.getYoutubeVideoId().isBlank()) {
            throw new IllegalArgumentException("이 곡에는 연결된 유튜브 영상이 없어요.");
        }

        AnalysisResponse analysis = mvAnalysisService.createAnalysis(songId, "gemini-auto");
        if (analysis.status() != AnalysisStatus.PENDING) {
            // 이미 분석이 진행 중이거나(RUNNING) 끝난(DONE) 경우: 그대로 반환.
            return analysis;
        }

        analysis = mvAnalysisService.startAnalysis(analysis.id());
        String videoUrl = "https://www.youtube.com/watch?v=" + song.getYoutubeVideoId();

        List<GeminiVisionClient.ElementDraft> drafts;
        try {
            drafts = geminiVisionClient.analyzeVideo(videoUrl);
        } catch (Exception e) {
            mvAnalysisService.failAnalysis(analysis.id());
            throw new IllegalArgumentException("Gemini 분석 중 문제가 생겼어요: " + e.getMessage());
        }

        if (drafts == null || drafts.isEmpty()) {
            mvAnalysisService.failAnalysis(analysis.id());
            throw new IllegalArgumentException("이 영상에서는 한국적 요소를 찾지 못했어요.");
        }

        List<CulturalElementRequest> elementRequests = drafts.stream()
                .map(d -> new CulturalElementRequest(
                        d.category(), d.name(), d.description(), d.timestampSec(), d.confidence()))
                .toList();

        AnalysisResponse completed = mvAnalysisService.completeAnalysis(
                analysis.id(), new AnalysisCompleteRequest(elementRequests));

        matchElementsToSeedPlaces(completed.id());

        return completed;
    }

    // 방금 저장된 CulturalElement들을 카테고리가 같은 시드 Place와 자동으로 매칭한다.
    private void matchElementsToSeedPlaces(Long analysisId) {
        List<CulturalElementResponse> elements = mvAnalysisService.findElements(analysisId);

        for (CulturalElementResponse element : elements) {
            String placeCategory = CATEGORY_TO_PLACE_LABEL.get(element.category());
            if (placeCategory == null) {
                continue;
            }

            List<Place> candidates = placeRepository.search(null, placeCategory);
            if (candidates.isEmpty()) {
                continue;
            }

            CulturalElement elementRef = culturalElementRepository.getReferenceById(element.id());
            BigDecimal score = element.confidence() != null ? element.confidence() : new BigDecimal("0.750");

            int display = 1;
            for (Place place : candidates.subList(0, Math.min(2, candidates.size()))) {
                String reason = "Gemini가 뮤직비디오에서 찾은 '" + element.name()
                        + "'와 잘 어울리는 " + placeCategory + " 관련 장소예요.";
                elementPlaceMatchRepository.save(
                        new ElementPlaceMatch(elementRef, place, score, reason, display++, null));
            }
        }
    }

    private SongResponse toResponse(Song song) {
        return new SongResponse(song.getId(), song.getTitle(), song.getArtist(), song.getThumbnailUrl());
    }
}
