package com.kculture.travel.repository;

import com.kculture.travel.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

// 퀘스트 단계에 실제 방문 장소를 연결하기 위한 기본 장소 조회 Repository다.
public interface PlaceRepository extends JpaRepository<Place, Long> {
}
