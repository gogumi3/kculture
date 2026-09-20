package com.kculture.travel.repository;

import com.kculture.travel.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    // 이름 검색
    List<Place> findByNameContainingIgnoreCase(String keyword);

    // 지역 / 카테고리 검색
    @Query("""
            select p from Place p
            where (:regionSido is null or p.regionSido = :regionSido)
              and (:category is null or p.category = :category)
            order by p.visitorIndex desc
            """)
    List<Place> search(
            @Param("regionSido") String regionSido,
            @Param("category") String category
    );
}