package com.kculture.recommendation.domain;

import com.kculture.content.domain.CulturalElement;
import com.kculture.travel.domain.Place;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "element_place_match")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ElementPlaceMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 연결 결과 자체 id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "element_id", nullable = false)
    // 추천 근거가 된 추출 요소
    private CulturalElement element;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    // 연결된 실제 장소
    private Place place;

    @Column(name = "match_score", precision = 4, scale = 3)
    // 요소와 장소의 관련도
    private BigDecimal matchScore;

    @Column(name = "reason", length = 500)
    // 왜 연결했는지 설명
    private String reason;

    @Column(name = "display_order", nullable = false)
    // 노출 순서
    private int displayOrder = 0;

    @Column(name = "region_bonus", precision = 4, scale = 3)
    // 지역분산을 위한 가산점
    private BigDecimal regionBonus;

    @Column(name = "created_at", nullable = false, updatable = false)
    // 연결 결과 저장 시간
    private LocalDateTime createdAt;

    public ElementPlaceMatch(
            CulturalElement element,
            Place place,
            BigDecimal matchScore,
            String reason,
            int displayOrder,
            BigDecimal regionBonus
    ) {
        this.element = element;
        this.place = place;
        this.matchScore = matchScore;
        this.reason = reason;
        this.displayOrder = displayOrder;
        this.regionBonus = regionBonus;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}