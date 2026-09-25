package com.kculture.content.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

// 한 번의 분석에서 추출된 문화 요소 목록을 받는다.
public record AnalysisCompleteRequest(
        @NotNull List<@Valid CulturalElementRequest> elements
) {
}
