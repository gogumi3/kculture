package com.kculture.content.repository;

import com.kculture.content.domain.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByTitleContainingIgnoreCase(String title);

    // 동일 영상 중복 등록 방지 / 재사용
    Optional<Song> findByYoutubeVideoId(String youtubeVideoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Song s where s.id = :id")
    Optional<Song> findByIdForUpdate(@Param("id") Long id);
}
