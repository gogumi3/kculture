package com.kculture.content.repository;

import com.kculture.content.domain.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TranslationRepository extends JpaRepository<Translation, Long> {
    List<Translation> findByEntityTypeAndEntityIdAndLanguageOrderByFieldNameAsc(
            String entityType, Long entityId, String language
    );

    Optional<Translation> findByEntityTypeAndEntityIdAndFieldNameAndLanguage(
            String entityType, Long entityId, String fieldName, String language
    );
}
