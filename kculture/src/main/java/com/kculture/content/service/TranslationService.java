package com.kculture.content.service;

import com.kculture.content.domain.Translation;
import com.kculture.content.dto.TranslationRequest;
import com.kculture.content.dto.TranslationResponse;
import com.kculture.content.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TranslationService {

    private final TranslationRepository translationRepository;

    public List<TranslationResponse> findTranslations(String entityType, Long entityId, String language) {
        return translationRepository
                .findByEntityTypeAndEntityIdAndLanguageOrderByFieldNameAsc(entityType, entityId, language)
                .stream().map(TranslationResponse::from).toList();
    }

    @Transactional
    public TranslationResponse saveTranslation(TranslationRequest request) {
        Translation translation = translationRepository
                .findByEntityTypeAndEntityIdAndFieldNameAndLanguage(
                        request.entityType(), request.entityId(), request.fieldName(), request.language()
                )
                .orElseGet(() -> new Translation(
                        request.entityType(), request.entityId(), request.fieldName(),
                        request.language(), request.textValue()
                ));

        translation.updateText(request.textValue());
        return TranslationResponse.from(translationRepository.save(translation));
    }
}
