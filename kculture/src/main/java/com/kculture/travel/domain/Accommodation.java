package com.kculture.travel.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accommodations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Accommodation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 숙박 시설 id
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    // 숙소 이름
    private String name;

    @Column(name = "accom_type", length = 50)
    // 숙소 유형
    private String accomType;

    @Column(name = "price_range", length = 30)
    // 가격대 (LOW/MID/HIGH 등, 필터용)
    private String priceRange;

    @Column(name = "rating", precision = 2, scale = 1)
    // 평점 0.0 ~ 5.0
    private BigDecimal rating;

    @Column(name = "kakao_place_id", length = 50)
    // 카카오맵 장소 ID (지도 마커 연동)
    private String kakaoPlaceId;

    @Column(name = "region_sido", length = 50)
    private String regionSido;

    @Column(name = "region_sigungu", length = 50)
    private String regionSigungu;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "address", length = 300)
    private String address;

    @Column(name = "phone", length = 30)
    // 전화번호
    private String phone;

    @Column(name = "created_at", nullable = false, updatable = false)
    // 숙소 데이터 등록 시간
    private LocalDateTime createdAt;

    public Accommodation(
            String name,
            String accomType,
            String regionSido,
            String regionSigungu,
            BigDecimal latitude,
            BigDecimal longitude,
            String address,
            String phone
    ) {
        this.name = name;
        this.accomType = accomType;
        this.regionSido = regionSido;
        this.regionSigungu = regionSigungu;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.phone = phone;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}