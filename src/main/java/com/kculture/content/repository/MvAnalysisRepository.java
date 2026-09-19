package com.kculture.content.repository;

import com.kculture.content.domain.AnalysisStatus;
import com.kculture.content.domain.MvAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MvAnalysisRepository extends JpaRepository<MvAnalysis, Long> {

    // 곡의 최신 분석 (특정 상태, 예: DONE) 조회
    Optional<MvAnalysis> findFirstBySong_IdAndStatusOrderByFinishedAtDesc(Long songId, AnalysisStatus status);
}
