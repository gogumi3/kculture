package com.kculture.recommendation.repository;

import com.kculture.recommendation.domain.RecommendationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.Optional;

// 선택한 추천 장소를 퀘스트로 전환할 때 추천 세션을 조회한다.
public interface RecommendationSessionRepository extends JpaRepository<RecommendationSession, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from RecommendationSession s where s.id = :id")
    Optional<RecommendationSession> findByIdForUpdate(@Param("id") Long id);
}
