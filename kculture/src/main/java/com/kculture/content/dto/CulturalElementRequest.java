package com.kculture.content.dto;

import com.kculture.content.domain.CulturalCategory;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

// AI 분석 결과 한 개를 서버로 받을 때 사용하는 입력 DTO다.
public record CulturalElementRequest(
        @NotNull CulturalCategory category,
        @NotBlank @Size(max = 100) String name,
        @Size(max = 500) String description,
        @PositiveOrZero Integer timestampSec,
        @DecimalMin("0.0") @DecimalMax("1.0") BigDecimal confidence
) {
}
