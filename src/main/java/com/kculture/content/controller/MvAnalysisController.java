package com.kculture.content.controller;

import com.kculture.content.dto.*;
import com.kculture.content.service.MvAnalysisRunner;
import com.kculture.content.service.MvAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MvAnalysisController {

    private final MvAnalysisService analysisService;
    private final MvAnalysisRunner analysisRunner;

    // 곡 등록만 되어 있으면, 분석 생성 → 시작 → Gemini 백그라운드 분석까지 한 번에 트리거한다.
    @PostMapping("/songs/{songId}/analyses/run")
    public AnalysisResponse runAnalysis(
            @PathVariable Long songId,
            @RequestParam(defaultValue = "gemini") String model
    ) {
        MvAnalysisService.RunResult result = analysisService.createAndStart(songId, model);
        if (result.started()) {
            // createAndStart 트랜잭션이 커밋된 뒤 비동기 실행(RUNNING 상태가 이미 반영됨)
            analysisRunner.run(result.analysis().id(), songId);
        }
        return result.analysis();
    }

    @PostMapping("/songs/{songId}/analyses")
    public AnalysisResponse createAnalysis(
            @PathVariable Long songId,
            @RequestParam(defaultValue = "external-vlm") String modelName
    ) {
        return analysisService.createAnalysis(songId, modelName);
    }

    @GetMapping("/songs/{songId}/analyses")
    public List<AnalysisResponse> findAnalyses(@PathVariable Long songId) {
        return analysisService.findAnalyses(songId);
    }

    @GetMapping("/analyses/{analysisId}")
    public AnalysisResponse findAnalysis(@PathVariable Long analysisId) {
        return analysisService.findAnalysis(analysisId);
    }

    @GetMapping("/analyses/{analysisId}/elements")
    public List<CulturalElementResponse> findElements(@PathVariable Long analysisId) {
        return analysisService.findElements(analysisId);
    }

    @PostMapping("/analyses/{analysisId}/start")
    public AnalysisResponse startAnalysis(@PathVariable Long analysisId) {
        return analysisService.startAnalysis(analysisId);
    }

    @PostMapping("/analyses/{analysisId}/complete")
    public AnalysisResponse completeAnalysis(
            @PathVariable Long analysisId,
            @Valid @RequestBody AnalysisCompleteRequest request
    ) {
        return analysisService.completeAnalysis(analysisId, request);
    }

    @PostMapping("/analyses/{analysisId}/fail")
    public AnalysisResponse failAnalysis(
            @PathVariable Long analysisId,
            @RequestParam(required = false) String reason
    ) {
        return analysisService.failAnalysis(analysisId, reason);
    }
}
