package com.kculture.quest.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

// 사용자의 현재 GPS 좌표다.
public record LocationRequest(
        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude
) {
}
