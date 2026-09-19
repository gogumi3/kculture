package com.kculture.travel.dto;

import com.kculture.travel.domain.Place;

import java.math.BigDecimal;

// 관광지 응답
public record PlaceResponse(
        Long id,
        String name,
        String regionSido,
        String regionSigungu,
        String category,
        String description,
        String imageUrl,
        BigDecimal latitude,
        BigDecimal longitude,
        String kakaoPlaceId,
        Integer visitorIndex,
        String address
) {
    public static PlaceResponse from(Place p) {
        return new PlaceResponse(
                p.getId(),
                p.getName(),
                p.getRegionSido(),
                p.getRegionSigungu(),
                p.getCategory(),
                p.getDescription(),
                p.getImageUrl(),
                p.getLatitude(),
                p.getLongitude(),
                p.getKakaoPlaceId(),
                p.getVisitorIndex(),
                p.getAddress()
        );
    }
}
