package com.kculture.recommendation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateSessionRequest(
        @NotNull @Positive Long songId
) {
}
