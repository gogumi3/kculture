package com.kculture.content.dto;

import jakarta.validation.constraints.*;

// 번역 대상과 번역문을 저장할 때 사용하는 입력 DTO다.
public record TranslationRequest(
        @NotBlank @Size(max = 30) String entityType,
        @NotNull @Positive Long entityId,
        @NotBlank @Size(max = 50) String fieldName,
        @NotBlank @Pattern(regexp = "ko|en") String language,
        @NotBlank String textValue
) {
}
