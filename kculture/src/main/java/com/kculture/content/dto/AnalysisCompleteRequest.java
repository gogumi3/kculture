package com.kculture.content.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

// 한 번의 분석에서 추출된 문화 요소 목록을 받는다.
public record AnalysisCompleteRequest(
        @NotEmpty List<@Valid CulturalElementRequest> elements
) {
}
