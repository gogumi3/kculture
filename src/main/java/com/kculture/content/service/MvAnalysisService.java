package com.kculture.content.service;

import com.kculture.content.repository.CulturalElementRepository;
import com.kculture.content.repository.MvAnalysisRepository;
import com.kculture.content.repository.TranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MvAnalysisService {

    private final MvAnalysisRepository mvAnalysisRepository;
    private final CulturalElementRepository culturalElementRepository;
    private final TranslationRepository translationRepository;
}
