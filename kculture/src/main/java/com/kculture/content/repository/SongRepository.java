package com.kculture.content.repository;

import com.kculture.content.domain.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findByTitleContainingIgnoreCase(String title);

    // [Claude 추가] 유튜브에서 이미 가져온 영상인지 확인해서 곡 중복 생성을 막기 위해 사용
    // (AutoAnalysisService.ensureSongForQuery에서 사용)
    Optional<Song> findByYoutubeVideoId(String youtubeVideoId);
}
