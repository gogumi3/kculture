package com.kculture.recommendation.domain;

import com.kculture.content.domain.Song;
import com.kculture.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recommendation_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendationSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 이번 추천 묶음 id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    // 추천 받는 사용자 , 비회원이면 null
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    // 추천을 한 곡
    private Song song;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20,
            columnDefinition = "VARCHAR(20)")
    private RecommendationStatus status = RecommendationStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    // 추천 묶음 생성 시간
    private LocalDateTime createdAt;

    public RecommendationSession(User user, Song song) {
        this.user = user;
        this.song = song;
    }

    // 세션 상태 전환 (예: 퀘스트로 전환 시 CONVERTED)
    public void changeStatus(RecommendationStatus status) {
        this.status = status;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}