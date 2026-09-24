package com.kculture.recommendation.dto;

import jakarta.validation.constraints.NotNull;

public record CreateSessionRequest(
        @NotNull Long songId
) {
}
