package com.kculture.content.service;

import com.kculture.content.domain.Song;
import com.kculture.content.dto.SongResponse;
import com.kculture.content.exception.ContentNotFoundException;
import com.kculture.content.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;

    public List<SongResponse> findAllSongs() {
        return songRepository.findAll().stream()
                // song엔터티에서 곡을 찾아서 객체로 분류
                // song -> <<< song을 오른쪽으로 바꿔라
                // 새로운 객체에 들어갈 값
                // 받은 값을 다시 리스트로 모아서 반환
                .map(song -> new SongResponse(
                        song.getId(),
                        song.getTitle(),
                        song.getArtist(),
                        song.getThumbnailUrl()
                ))
                .toList();
    }

    public SongResponse findSong(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException("곡을 찾을 수 없습니다."));

        return new SongResponse(
                song.getId(),
                song.getTitle(),
                song.getArtist(),
                song.getThumbnailUrl()
        );
    }

    public List<SongResponse> searchSongs(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("검색어를 입력하세요.");
        }
        keyword = keyword.strip();
        return songRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(song -> new SongResponse(
                        song.getId(),
                        song.getTitle(),
                        song.getArtist(),
                        song.getThumbnailUrl()
                ))
                .toList();
    }
}
