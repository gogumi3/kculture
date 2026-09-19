package com.kculture.recommendation.dto;

import jakarta.validation.constraints.NotNull;

// 추천 세션 생성 요청. userId는 비로그인 검색 허용 위해 optional.
public record CreateSessionRequest(
        @NotNull Long songId,
        Long userId
) {
}
