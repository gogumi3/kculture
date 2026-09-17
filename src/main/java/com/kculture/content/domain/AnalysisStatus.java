package com.kculture.content.domain;

public enum AnalysisStatus {
    PENDING,    // 분석 대기
    RUNNING,    // 분석 진행중
    DONE,       // 분석 정상 완료
    FAILED      // 분석 실패
}
