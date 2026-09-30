package com.kculture.content.controller;

import com.kculture.content.dto.*;
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
    public AnalysisResponse failAnalysis(@PathVariable Long analysisId) {
        return analysisService.failAnalysis(analysisId);
    }
}
