package com.kculture.travel.controller;

import com.kculture.travel.dto.AccommodationResponse;
import com.kculture.travel.service.AccommodationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accommodations")
public class AccommodationController {

    private final AccommodationService accommodationService;

    // S10 핵심: 좌표 반경 내 숙박 추천 (거리순)
    @GetMapping("/nearby")
    public List<AccommodationResponse> nearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "3000") double radius,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String priceRange,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return accommodationService.findNearby(lat, lng, radius, type, priceRange, limit);
    }

    // 리스트 뷰: 지역/유형 필터
    @GetMapping
    public List<AccommodationResponse> getAccommodations(
            @RequestParam(required = false) String regionSido,
            @RequestParam(required = false) String type
    ) {
        return accommodationService.findByRegion(regionSido, type);
    }

    // 상세
    @GetMapping("/{id}")
    public AccommodationResponse getAccommodation(@PathVariable Long id) {
        return accommodationService.getAccommodation(id);
    }
}
