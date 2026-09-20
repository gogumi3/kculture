package com.kculture.content.repository;

import com.kculture.content.domain.AnalysisStatus;
import com.kculture.content.domain.MvAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MvAnalysisRepository extends JpaRepository<MvAnalysis, Long> {

    // 한 곡의 분석 이력을 최신순으로 조회
    List<MvAnalysis> findBySongIdOrderByIdDesc(Long songId);

    // 가장 최근 분석 조회
    Optional<MvAnalysis> findFirstBySongIdOrderByIdDesc(Long songId);

    // 특정 상태(DONE 등)의 가장 최근 분석 조회
    Optional<MvAnalysis> findFirstBySong_IdAndStatusOrderByFinishedAtDesc(
            Long songId,
            AnalysisStatus status
    );
}