package com.kculture.content.controller;

import com.kculture.content.dto.SongCreateRequest;
import com.kculture.content.dto.SongResponse;
import com.kculture.content.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/songs")
public class SongController {

    private final SongService songService;

    @GetMapping
    public List<SongResponse> getSongs() {
        return songService.findAllSongs();
    }

    // 공개 MV video id로 곡 등록 (메타데이터 자동 수집)
    @PostMapping
    public SongResponse createSong(@Valid @RequestBody SongCreateRequest request) {
        return songService.createFromYoutube(request.youtubeVideoId());
    }

    @GetMapping("/{id}")
    public SongResponse getSong(@PathVariable Long id) {
        return songService.findSong(id);
    }

    @GetMapping("/search")
    public List<SongResponse> searchSongs(
            @RequestParam String keyword
    ) {
        return songService.searchSongs(keyword);
    }
}
