package com.kculture.user.domain;

import com.kculture.content.domain.Song;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 검색 기록 id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    // 검색한 사용자 , 비회원이면 null
    private User user;

    @Column(name = "keyword", nullable = false, length = 255)
    // 입력한 검색어
    private String keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id")
    // 검색으로 연결된 곡
    private Song song;

    @Column(name = "searched_at", nullable = false, updatable = false)
    // 검색한 시간
    private LocalDateTime searchedAt;

    public SearchHistory(User user, String keyword, Song song) {
        this.user = user;
        this.keyword = keyword;
        this.song = song;
    }

    @PrePersist
    private void onCreate() {
        this.searchedAt = LocalDateTime.now();
    }
}