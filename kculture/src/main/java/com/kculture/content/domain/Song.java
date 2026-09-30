package com.kculture.content.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "songs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // db에서 사용하는 곡 id
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "artist", nullable = false, length = 255)
    private String artist;

    @Column(name = "youtube_video_id", unique = true, length = 50)
    // 유튜브에서 사용하는 영상
    private String youtubeVideoId;

    @Column(name = "thumbnail_url", length = 500)
    // 썸네일 이미지 주소
    private String thumbnailUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    // 곡 데이터 등록 시간
    private LocalDateTime createdAt;

    public Song(String title, String artist, String youtubeVideoId, String thumbnailUrl) {
        // 곡 제목,가수,공식뮤비,주소
        this.title = title;
        this.artist = artist;
        this.youtubeVideoId = youtubeVideoId;
        this.thumbnailUrl = thumbnailUrl;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
