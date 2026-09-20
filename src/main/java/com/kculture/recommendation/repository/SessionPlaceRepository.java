package com.kculture.recommendation.repository;

import com.kculture.recommendation.domain.SessionPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SessionPlaceRepository extends JpaRepository<SessionPlace, Long> {
    // 사용자가 선택한 장소만 추천 노출 순서대로 가져온다.
    List<SessionPlace> findBySessionIdAndChosenTrueOrderByDisplayOrderAsc(Long sessionId);
}
