package com.kculture.content.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cultural_elements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CulturalElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 추출 요소 id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    // 하나의 분석은 여러 요소 추출 할 수 있음
    @JoinColumn(name = "analysis_id", nullable = false)
    // 어느 분석 결과에 속하는지
    private MvAnalysis analysis;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30,
            columnDefinition = "VARCHAR(30)")
    // 건축,의상,음식등 분류
    private CulturalCategory category;

    @Column(name = "name", nullable = false, length = 100)
    // 건축에 고궁 << 같은 요소 이름
    private String name;

    @Column(name = "description", length = 500)
    // 요소에 대한 설명
    private String description;

    @Column(name = "timestamp_sec")
    // 영상에서 등장한 시점 (초 단위)
    private Integer timestampSec;

    @Column(name = "confidence", precision = 4, scale = 3)
    // ai 신뢰도
    private BigDecimal confidence;

    @Column(name = "created_at", nullable = false, updatable = false)
    // 추출 결과 저장 시간
    private LocalDateTime createdAt;

    public CulturalElement(
            // 출처 분석 작업, 분류, 요소이름, 설명, 등장 시점, 신뢰도
            MvAnalysis analysis,
            CulturalCategory category,
            String name,
            String description,
            Integer timestampSec,
            BigDecimal confidence
    ) {
        this.analysis = analysis;
        this.category = category;
        this.name = name;
        this.description = description;
        this.timestampSec = timestampSec;
        this.confidence = confidence;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}