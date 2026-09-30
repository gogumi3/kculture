package com.kculture.travel.controller;

import com.kculture.travel.dto.PlaceResponse;
import com.kculture.travel.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService placeService;

    // 목록 (지역/카테고리 optional 필터)
    @GetMapping
    public List<PlaceResponse> getPlaces(
            @RequestParam(required = false) String regionSido,
            @RequestParam(required = false) String category
    ) {
        return placeService.findPlaces(regionSido, category);
    }

    // 이름 검색
    @GetMapping("/search")
    public List<PlaceResponse> searchPlaces(@RequestParam String keyword) {
        return placeService.searchPlaces(keyword);
    }

    // 상세
    @GetMapping("/{id}")
    public PlaceResponse getPlace(@PathVariable Long id) {
        return placeService.getPlace(id);
    }
}
