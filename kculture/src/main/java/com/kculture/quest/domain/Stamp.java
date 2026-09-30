package com.kculture.quest.domain;

import com.kculture.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        // 같은 사용자가 같은 스탬프 중복으로 받는 것을 방지
        name = "stamps",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_stamps_user_step",
                        columnNames = {"user_id", "step_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 스탬프 획득 기록 id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    // 스탬프를 획득한 사용자
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id", nullable = false)
    // 스탬프를 얻은 단계
    private QuestStep step;

    @Column(name = "photo_url", length = 500)
    // 인증 사진이 저장된 주소
    private String photoUrl;

    @Column(name = "acquired_at", nullable = false, updatable = false)
    // 스탬프 획득 시간
    private LocalDateTime acquiredAt;

    public Stamp(User user, QuestStep step, String photoUrl) {
        this.user = user;
        this.step = step;
        this.photoUrl = photoUrl;
    }

    @PrePersist
    private void onCreate() {
        this.acquiredAt = LocalDateTime.now();
    }
}