package com.kculture.travel.dto;

import com.kculture.travel.domain.Accommodation;

import java.math.BigDecimal;

// 숙박 응답. distanceMeters는 반경 검색 결과에서만 채워지고, 그 외에는 null.
public record AccommodationResponse(
        Long id,
        String name,
        String accomType,
        String priceRange,
        BigDecimal rating,
        String regionSido,
        String regionSigungu,
        BigDecimal latitude,
        BigDecimal longitude,
        String address,
        String phone,
        String kakaoPlaceId,
        Long distanceMeters
) {
    public static AccommodationResponse from(Accommodation a) {
        return from(a, null);
    }

    public static AccommodationResponse from(Accommodation a, Long distanceMeters) {
        return new AccommodationResponse(
                a.getId(),
                a.getName(),
                a.getAccomType(),
                a.getPriceRange(),
                a.getRating(),
                a.getRegionSido(),
                a.getRegionSigungu(),
                a.getLatitude(),
                a.getLongitude(),
                a.getAddress(),
                a.getPhone(),
                a.getKakaoPlaceId(),
                distanceMeters
        );
    }
}
