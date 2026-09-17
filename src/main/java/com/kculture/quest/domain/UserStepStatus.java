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
}