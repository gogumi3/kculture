package com.kculture.quest.domain;

import com.kculture.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        // 같은 사용자의 같은 단계 상태가 여러 행으로 생기는것을 방지
        name = "user_step_status",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_stepstatus",
                        columnNames = {"user_id", "quest_step_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStepStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 단계 상태 기록 id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    // 사용자
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_step_id", nullable = false)
    // 대상 단계
    private QuestStep questStep;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20,
            columnDefinition = "VARCHAR(20)")
    private StepStatus status = StepStatus.LOCKED;

    @Column(name = "unlocked_at")
    // 단계 열리는 시간
    private LocalDateTime unlockedAt;

    @Column(name = "done_at")
    // 단계 완료 시간
    private LocalDateTime doneAt;

    public UserStepStatus(User user, QuestStep questStep) {
        this.user = user;
        this.questStep = questStep;
    }

    // 잠긴 단계를 진행 가능한 상태로 변경한다.
    public void unlock() {
        if (this.status != StepStatus.LOCKED) {
            throw new IllegalStateException("잠긴 단계만 해제할 수 있습니다.");
        }
        this.status = StepStatus.UNLOCKED;
        this.unlockedAt = LocalDateTime.now();
    }

    // 현재 진행 가능한 단계를 완료 상태로 변경한다.
    public void complete() {
        if (this.status != StepStatus.UNLOCKED) {
            throw new IllegalStateException("열린 단계만 완료할 수 있습니다.");
        }
        this.status = StepStatus.DONE;
        this.doneAt = LocalDateTime.now();
    }

}