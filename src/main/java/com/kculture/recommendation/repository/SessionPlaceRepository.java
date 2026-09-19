package com.kculture.recommendation.repository;

import com.kculture.recommendation.domain.SessionPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionPlaceRepository extends JpaRepository<SessionPlace, Long> {

    // 세션의 추천 장소들 (노출 순서대로)
    List<SessionPlace> findBySession_IdOrderByDisplayOrderAsc(Long sessionId);
}
