package com.kculture.recommendation.dto;

import com.kculture.content.domain.CulturalElement;
import com.kculture.recommendation.domain.ElementPlaceMatch;
import com.kculture.travel.domain.Place;

import java.math.BigDecimal;

// 요소-장소 매칭 응답 (S03/S04 근거 카드용)
public record MatchResponse(
        Long matchId,
        Long elementId,
        String elementName,
        String category,
        Long placeId,
        String placeName,
        String regionSido,
        String regionSigungu,
        BigDecimal latitude,
        BigDecimal longitude,
        String imageUrl,
        String kakaoPlaceId,
        BigDecimal matchScore,
        String reason
) {
    public static MatchResponse from(ElementPlaceMatch m) {
        CulturalElement e = m.getElement();
        Place p = m.getPlace();
        return new MatchResponse(
                m.getId(),
                e.getId(),
                e.getName(),
                e.getCategory() != null ? e.getCategory().name() : null,
                p.getId(),
                p.getName(),
                p.getRegionSido(),
                p.getRegionSigungu(),
                p.getLatitude(),
                p.getLongitude(),
                p.getImageUrl(),
                p.getKakaoPlaceId(),
                m.getMatchScore(),
                m.getReason()
        );
    }
}
