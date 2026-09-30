package com.kculture.recommendation.dto;

import com.kculture.content.domain.CulturalElement;
import com.kculture.recommendation.domain.SessionPlace;
import com.kculture.travel.domain.Place;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 세션 내 추천 장소 1건 (S04 카드). reason은 서비스에서 매칭 조회로 채움.
public record SessionPlaceResponse(
        Long id,
        int displayOrder,
        boolean chosen,
        LocalDateTime shownAt,
        Long placeId,
        String placeName,
        String category,
        String regionSido,
        String regionSigungu,
        String description,
        String imageUrl,
        BigDecimal latitude,
        BigDecimal longitude,
        String kakaoPlaceId,
        Long elementId,
        String elementName,
        String reason
) {
    public static SessionPlaceResponse from(SessionPlace sp, String reason) {
        Place p = sp.getPlace();
        CulturalElement e = sp.getElement();
        return new SessionPlaceResponse(
                sp.getId(),
                sp.getDisplayOrder(),
                sp.isChosen(),
                sp.getShownAt(),
                p.getId(),
                p.getName(),
                p.getCategory(),
                p.getRegionSido(),
                p.getRegionSigungu(),
                p.getDescription(),
                p.getImageUrl(),
                p.getLatitude(),
                p.getLongitude(),
                p.getKakaoPlaceId(),
                e != null ? e.getId() : null,
                e != null ? e.getName() : null,
                reason
        );
    }
}
