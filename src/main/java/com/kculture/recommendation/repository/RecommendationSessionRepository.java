package com.kculture.recommendation.repository;

import com.kculture.recommendation.domain.RecommendationSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationSessionRepository
        extends JpaRepository<RecommendationSession, Long> {
}