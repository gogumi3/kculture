package com.kculture.quest.domain;

import com.kculture.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        // 사용자와 퀘스트 조합당 전체 진행 기록을 하나만 허용
        name = "user_quest_progress",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_progress_user_quest",
                        columnNames = {"user_id", "quest_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserQuestProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 진행 기록 id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    // 진행 하는 사용자
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id", nullable = false)
    // 진행할 코스
    private Quest quest;

    @Column(name = "current_step", nullable = false)
    // 현재 단계 순서
    private int currentStep = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20,
            columnDefinition = "VARCHAR(20)")
    private QuestProgressStatus status = QuestProgressStatus.IN_PROGRESS;

    @Column(name = "started_at", nullable = false, updatable = false)
    // 코스 시작 시간
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    // 전체 코스 완료 시간
    private LocalDateTime completedAt;

    public UserQuestProgress(User user, Quest quest) {
        this.user = user;
        this.quest = quest;
    }

    @PrePersist
    private void onCreate() {
        this.startedAt = LocalDateTime.now();
    }

    // 다음 방문 단계가 열리면 현재 단계 번호를 변경한다.
    public void moveTo(int nextStep) {
        this.currentStep = nextStep;
    }

    // 마지막 단계를 끝냈을 때 전체 퀘스트를 완료 처리한다.
    public void complete() {
        this.status = QuestProgressStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

}