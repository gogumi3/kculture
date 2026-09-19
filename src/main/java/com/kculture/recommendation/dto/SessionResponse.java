package com.kculture.recommendation.dto;

import com.kculture.recommendation.domain.RecommendationSession;

import java.util.List;

// 추천 세션 응답 (세션 메타 + 순서대로 추천 장소들)
public record SessionResponse(
        Long sessionId,
        Long songId,
        String songTitle,
        String status,
        List<SessionPlaceResponse> places
) {
    public static SessionResponse of(RecommendationSession session, List<SessionPlaceResponse> places) {
        return new SessionResponse(
                session.getId(),
                session.getSong().getId(),
                session.getSong().getTitle(),
                session.getStatus() != null ? session.getStatus().name() : null,
                places
        );
    }
}
