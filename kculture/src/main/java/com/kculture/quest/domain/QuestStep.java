package com.kculture.quest.domain;

import com.kculture.travel.domain.Place;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        // 같은 퀘스트에 같은순서가 중복되는거 방지
        name = "quest_steps",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_step_order",
                        columnNames = {"quest_id", "step_order"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 방문 단계 id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id", nullable = false)
    // 소속 코스
    private Quest quest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    // 방문할 실제 장소
    private Place place;

    @Column(name = "step_order", nullable = false)
    // 코스안에서 순서
    private int stepOrder;

    @Column(name = "story", columnDefinition = "TEXT")
    // 도착 시 보여줄 설명 (긴 설명)
    private String story;

    @Column(name = "distance_hint", length = 100)
    // 거리 안내 문구
    private String distanceHint;

    @Column(name = "arrival_radius", nullable = false)
    // 도착 판정 반경 (미터 단위)
    private int arrivalRadius = 50;

    @Column(name = "created_at", nullable = false, updatable = false)
    // 단계 등록 시간
    private LocalDateTime createdAt;

    public QuestStep(
            Quest quest,
            Place place,
            int stepOrder,
            String story,
            String distanceHint,
            int arrivalRadius
    ) {
        this.quest = quest;
        this.place = place;
        this.stepOrder = stepOrder;
        this.story = story;
        this.distanceHint = distanceHint;
        this.arrivalRadius = arrivalRadius;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}