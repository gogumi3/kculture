package com.kculture.quest.domain;

import com.kculture.content.domain.Song;
import com.kculture.recommendation.domain.RecommendationSession;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "quests",
        uniqueConstraints = @UniqueConstraint(name = "uk_quests_session", columnNames = "session_id")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 퀘스트 id
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    // 코스 제목
    private String title;

    @Column(name = "theme_region", length = 50)
    // 지역 테마
    private String themeRegion;

    @Column(name = "theme_era", length = 50)
    // 시대 , 컨텐츠 테마
    private String themeEra;

    @Enumerated(EnumType.STRING)
    @Column(name = "origin_type", nullable = false, length = 20,
            columnDefinition = "VARCHAR(20)")
    private QuestOriginType originType = QuestOriginType.CURATED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id")
    // 코스 생성의 출발 곡 , null 가능
    private Song song;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    // 출발 추천 묶음
    private RecommendationSession session;

    @Column(name = "description", length = 500)
    // 코스 설명
    private String description;

    @Column(name = "is_active", nullable = false,
            columnDefinition = "TINYINT(1)")
    // 현재 사용 가능한 코스인지
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    // 코스 생성 시간
    private LocalDateTime createdAt;

    public Quest(
            String title,
            String themeRegion,
            String themeEra,
            QuestOriginType originType,
            Song song,
            RecommendationSession session,
            String description
    ) {
        this.title = title;
        this.themeRegion = themeRegion;
        this.themeEra = themeEra;
        this.originType = originType;
        this.song = song;
        this.session = session;
        this.description = description;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
