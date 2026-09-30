package com.kculture.content.dto;

import com.kculture.content.domain.AnalysisStatus;
import com.kculture.content.domain.MvAnalysis;
import java.time.LocalDateTime;

// MV 분석의 현재 상태를 화면에 전달하는 DTO다.
public record AnalysisResponse(
        Long id,
        Long songId,
        AnalysisStatus status,
        String modelName,
        LocalDateTime startedAt,
        LocalDateTime finishedAt
) {
    public static AnalysisResponse from(MvAnalysis analysis) {
        return new AnalysisResponse(
                analysis.getId(),
                analysis.getSong().getId(),
                analysis.getStatus(),
                analysis.getModelName(),
                analysis.getStartedAt(),
                analysis.getFinishedAt()
        );
    }
}
