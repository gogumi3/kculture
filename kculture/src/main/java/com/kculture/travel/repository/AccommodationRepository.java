package com.kculture.travel.repository;

import com.kculture.travel.domain.Accommodation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long> {

    // 반경 검색 1차 필터: 바운딩박스(위경도 사각형) 안 + 유형 optional.
    // 정확한 원형 반경/거리순 정렬은 서비스에서 Haversine으로 처리.
    @Query("""
            select a from Accommodation a
            where a.latitude between :minLat and :maxLat
              and a.longitude between :minLng and :maxLng
              and (:type is null or a.accomType = :type)
              and (:priceRange is null or a.priceRange = :priceRange)
            """)
    List<Accommodation> findWithinBox(@Param("minLat") BigDecimal minLat,
                                      @Param("maxLat") BigDecimal maxLat,
                                      @Param("minLng") BigDecimal minLng,
                                      @Param("maxLng") BigDecimal maxLng,
                                      @Param("type") String type,
                                      @Param("priceRange") String priceRange);

    // 리스트 뷰: 지역(시/도)·유형 optional 필터
    @Query("""
            select a from Accommodation a
            where (:regionSido is null or a.regionSido = :regionSido)
              and (:type is null or a.accomType = :type)
            order by a.rating desc
            """)
    List<Accommodation> search(@Param("regionSido") String regionSido,
                               @Param("type") String type);
}
