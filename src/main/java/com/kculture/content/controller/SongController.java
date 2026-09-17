package com.kculture.content.controller;

import com.kculture.content.dto.SongResponse;
import com.kculture.content.service.SongService;
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

    @GetMapping("/{id}")
    public SongResponse getSong(@PathVariable Long id) {
        return songService.findSong(id);
    }

    public List<SongResponse> searchSongs(
            @RequestParam String keyword
    ) {
        return songService.searchSongs(keyword);
    }
}
