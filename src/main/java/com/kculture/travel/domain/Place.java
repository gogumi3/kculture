package com.kculture.travel.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "places")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // db에 저장된 장소 id
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    // 장소 이름
    private String name;

    @Column(name = "region_sido", length = 50)
    // 시 도
    private String regionSido;

    @Column(name = "region_sigungu", length = 50)
    // 시 군 구
    private String regionSigungu;

    @Column(name = "category", length = 50)
    // 고궁 , 시장 등 장소 분류
    private String category;

    @Column(name = "latitude", precision = 10, scale = 7)
    // 위도
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    // 경도
    private BigDecimal longitude;

    @Column(name = "kakao_place_id", length = 50)
    // 카카오 장소 식별값
    private String kakaoPlaceId;

    @Column(name = "visitor_index")
    // 방문자 관련 지표
    private Integer visitorIndex;

    @Column(name = "address", length = 300)
    // 주소
    private String address;

    @Column(name = "created_at", nullable = false, updatable = false)
    // 장소 데이터 등록 시간
    private LocalDateTime createdAt;

    public Place(
            // 이미 확보한 장소 정보를 담는 객체
            String name,
            String regionSido,
            String regionSigungu,
            String category,
            BigDecimal latitude,
            BigDecimal longitude,
            String kakaoPlaceId,
            Integer visitorIndex,
            String address
    ) {
        this.name = name;
        this.regionSido = regionSido;
        this.regionSigungu = regionSigungu;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.kakaoPlaceId = kakaoPlaceId;
        this.visitorIndex = visitorIndex;
        this.address = address;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}