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