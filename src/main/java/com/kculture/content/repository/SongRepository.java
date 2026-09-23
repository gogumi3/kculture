package com.kculture.content.repository;

import com.kculture.content.domain.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByTitleContainingIgnoreCase(String title);

    // 동일 영상 중복 등록 방지 / 재사용
    Optional<Song> findByYoutubeVideoId(String youtubeVideoId);
}
