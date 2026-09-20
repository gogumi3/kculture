package com.kculture.recommendation.repository;

import com.kculture.recommendation.domain.RecommendationSession;
import org.springframework.data.jpa.repository.JpaRepository;

// 선택한 추천 장소를 퀘스트로 전환할 때 추천 세션을 조회한다.
public interface RecommendationSessionRepository extends JpaRepository<RecommendationSession, Long> {
}
