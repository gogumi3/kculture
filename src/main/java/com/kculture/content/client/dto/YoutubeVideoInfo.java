package com.kculture.content.client.dto;

/**
 * YouTube Data API에서 뽑아온 곡 메타데이터.
 * privacyStatus/embeddable로 Gemini 분석 가능(공개·임베드) 여부를 판단한다.
 */
public record YoutubeVideoInfo(
        String title,
        String channelTitle,
        String thumbnailUrl,
        String privacyStatus,
        boolean embeddable
) {
    // 공개 + 임베드 가능해야 Gemini URL 분석이 허용된다.
    public boolean isPublicAndEmbeddable() {
        return "public".equalsIgnoreCase(privacyStatus) && embeddable;
    }
}
