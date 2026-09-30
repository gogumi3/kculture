package com.kculture.content.controller;

import com.kculture.content.dto.TranslationRequest;
import com.kculture.content.dto.TranslationResponse;
import com.kculture.content.service.TranslationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/translations")
public class TranslationController {

    private final TranslationService translationService;

    @GetMapping
    public List<TranslationResponse> findTranslations(
            @RequestParam String entityType,
            @RequestParam Long entityId,
            @RequestParam(defaultValue = "en") String language
    ) {
        return translationService.findTranslations(entityType, entityId, language);
    }

    @PostMapping
    public TranslationResponse saveTranslation(@Valid @RequestBody TranslationRequest request) {
        return translationService.saveTranslation(request);
    }
}
