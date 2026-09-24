package com.kculture.quest.dto;

import jakarta.validation.constraints.*;

// 추천 결과에서 사용자가 선택한 장소들을 퀘스트로 바꾸는 요청이다.
public record QuestFromSessionRequest(
        @NotNull @Positive Long sessionId,
        @NotBlank @Size(max = 200) String title,
        @Size(max = 500) String description
) {
}
