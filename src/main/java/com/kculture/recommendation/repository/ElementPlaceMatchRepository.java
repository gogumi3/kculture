package com.kculture.recommendation.repository;

import com.kculture.recommendation.domain.ElementPlaceMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ElementPlaceMatchRepository extends JpaRepository<ElementPlaceMatch, Long> {

    // 요소별 추천 장소 (노출 순서 → 점수순)
    List<ElementPlaceMatch> findByElement_IdOrderByDisplayOrderAscMatchScoreDesc(Long elementId);

    // 세션 구성용: 여러 요소의 매칭 후보 한 번에 (점수 높은 순)
    List<ElementPlaceMatch> findByElement_IdInOrderByMatchScoreDesc(List<Long> elementIds);

    // 세션 서빙 시 매칭 근거(reason) 조회
    Optional<ElementPlaceMatch> findByElement_IdAndPlace_Id(Long elementId, Long placeId);
}
