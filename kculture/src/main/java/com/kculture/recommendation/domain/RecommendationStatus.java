package com.kculture.recommendation.domain;

public enum RecommendationStatus {
    ACTIVE,     // 추천 과정 진행중
    CONVERTED,  // 퀘스트로 전환
    EXPIRED     // 만료
}
