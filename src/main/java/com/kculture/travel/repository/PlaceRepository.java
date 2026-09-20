package com.kculture.travel.repository;

import com.kculture.travel.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// 퀘스트 단계에 실제 방문 장소를 연결하기 위한 기본 장소 조회 Repository다.
public interface PlaceRepository extends JpaRepository<Place, Long> {

    // 이름 검색 (대소문자 무시)
    List<Place> findByNameContainingIgnoreCase(String keyword);

    // 지역(시/도)·카테고리 optional 필터. 파라미터가 null이면 해당 조건 무시.
    @Query("""
            select p from Place p
            where (:regionSido is null or p.regionSido = :regionSido)
              and (:category is null or p.category = :category)
            order by p.visitorIndex desc
            """)
    List<Place> search(@Param("regionSido") String regionSido,
                       @Param("category") String category);
}
