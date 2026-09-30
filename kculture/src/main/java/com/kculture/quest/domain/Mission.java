package com.kculture.quest.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "missions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 미션 id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false, unique = true)
    // 이 미션이 속한 단계
    private QuestStep step;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission_type", nullable = false, length = 20,
            columnDefinition = "VARCHAR(20)")
    private MissionType missionType;

    @Column(name = "question", length = 500)
    // 퀴즈 질문
    private String question;

    @Column(name = "answer", length = 200)
    // 퀴즈 채점 사용할 정답
    private String answer;

    @Column(name = "created_at", nullable = false, updatable = false)
    // 미션 등록 시간
    private LocalDateTime createdAt;

    public Mission(
            QuestStep step,
            MissionType missionType,
            String question,
            String answer
    ) {
        this.step = step;
        this.missionType = missionType;
        this.question = question;
        this.answer = answer;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}