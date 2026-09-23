package com.kculture.content.service;

import com.kculture.content.client.GeminiVideoClient;
import com.kculture.content.client.YoutubeDataClient;
import com.kculture.content.client.dto.YoutubeVideoInfo;
import com.kculture.content.domain.Song;
import com.kculture.content.dto.AnalysisCompleteRequest;
import com.kculture.content.dto.CulturalElementRequest;
import com.kculture.content.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MV 분석을 백그라운드에서 실행하는 오케스트레이터.
 * 컨트롤러/서비스와 별도 빈이므로 @Async 자기호출 문제가 없다.
 * 상태 전환은 MvAnalysisService(각각 @Transactional)를 그대로 재사용한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MvAnalysisRunner {

    private final SongRepository songRepository;
    private final YoutubeDataClient youtubeDataClient;
    private final GeminiVideoClient geminiVideoClient;
    private final MvAnalysisService analysisService;

    @Async("mvAnalysisExecutor")
    public void run(Long analysisId, Long songId) {
        try {
            Song song = songRepository.findById(songId).orElse(null);
            if (song == null || song.getYoutubeVideoId() == null || song.getYoutubeVideoId().isBlank()) {
                analysisService.failAnalysis(analysisId, "곡에 연결된 유튜브 영상이 없습니다.");
                return;
            }
            String videoId = song.getYoutubeVideoId();

            // 1) 공개/임베드 가능 여부 확인 — 아니면 Gemini 호출하지 않고 실패 처리
            YoutubeVideoInfo info = youtubeDataClient.fetch(videoId);
            if (!info.isPublicAndEmbeddable()) {
                analysisService.failAnalysis(analysisId,
                        "공개(public)·임베드 가능한 영상만 분석할 수 있습니다. status=" + info.privacyStatus());
                return;
            }

            // 2) Gemini URL 직접 분석 (다운로드 없음)
            List<CulturalElementRequest> elements = geminiVideoClient.analyze(videoId);

            // 요소가 0개여도 "이 MV엔 한국 문화 요소가 없음"은 정상 결과이므로
            // DONE + 빈 배열로 완료한다. (FAILED는 실제 오류에만 사용)
            analysisService.completeAnalysis(analysisId, new AnalysisCompleteRequest(elements));
        } catch (Exception e) {
            log.warn("MV 분석 실패 analysisId={}, songId={}", analysisId, songId, e);
            analysisService.failAnalysis(analysisId, e.getMessage());
        }
    }
}
