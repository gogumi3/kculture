package com.kculture.content.dto;

import com.kculture.content.domain.Translation;

public record TranslationResponse(
        Long id, String entityType, Long entityId,
        String fieldName, String language, String textValue
) {
    public static TranslationResponse from(Translation translation) {
        return new TranslationResponse(
                translation.getId(), translation.getEntityType(), translation.getEntityId(),
                translation.getFieldName(), translation.getLanguage(), translation.getTextValue()
        );
    }
}
