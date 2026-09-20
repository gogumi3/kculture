package com.kculture.recommendation.domain;

import com.kculture.content.domain.CulturalElement;
import com.kculture.travel.domain.Place;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "session_places")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 추천 장소 id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    // 어느 추천 묶음인지
    private RecommendationSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    // 추천한 장소
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "element_id")
    // 추천한 이유 null 가능
    private CulturalElement element;

    @Column(name = "display_order", nullable = false)
    // 이번 추천에서 보여줄 순서
    private int displayOrder = 0;

    @Column(name = "is_chosen", nullable = false,
            columnDefinition = "TINYINT(1)")
    // 사용자가 추천을 받고 선택 했는지
    private boolean chosen = false;

    @Column(name = "shown_at")
    // 실제 보여준 시간
    private LocalDateTime shownAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    // 추천 장소를 등록한시간
    private LocalDateTime createdAt;

    public SessionPlace(
            RecommendationSession session,
            Place place,
            CulturalElement element,
            int displayOrder
    ) {
        this.session = session;
        this.place = place;
        this.element = element;
        this.displayOrder = displayOrder;
    }

    // 사용자가 코스에 담기/빼기
    public void markChosen(boolean chosen) {
        this.chosen = chosen;
    }

    // 화면에 노출된 시점 기록 (S04)
    public void markShownNow() {
        this.shownAt = LocalDateTime.now();
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}