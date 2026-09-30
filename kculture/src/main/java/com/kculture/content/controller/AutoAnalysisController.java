package com.kculture.content.controller;

// [Claude 작성 - 신규 파일]
// 프론트가 검색 -> 자동 분석 파이프라인을 호출하는 새 엔드포인트 2개입니다.
// content.controller 패키지 소속이라 기존 ContentExceptionHandler(404/400 처리)를 그대로 사용합니다.

import com.kculture.content.dto.AnalysisResponse;
import com.kculture.content.dto.SongResponse;
import com.kculture.content.service.AutoAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/songs")
public class AutoAnalysisController {

    private final AutoAnalysisService autoAnalysisService;

    // 검색어로 곡을 찾거나, DB에 없으면 유튜브에서 가져와 새로 등록한다.
    @PostMapping("/auto-import")
    public SongResponse autoImport(@RequestParam String query) {
        return autoAnalysisService.ensureSongForQuery(query);
    }

    // 곡의 유튜브 영상을 Gemini로 분석해서 한국적 요소 + 장소 매칭까지 자동으로 만든다.
    @PostMapping("/{songId}/auto-analyze")
    public AnalysisResponse autoAnalyze(@PathVariable Long songId) {
        return autoAnalysisService.runAutoAnalysis(songId);
    }
}
