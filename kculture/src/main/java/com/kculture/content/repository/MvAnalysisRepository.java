package com.kculture.content.repository;

import com.kculture.content.domain.AnalysisStatus;
import com.kculture.content.domain.MvAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MvAnalysisRepository extends JpaRepository<MvAnalysis, Long> {
    // 한 곡의 분석 이력을 최신순으로 조회한다.
    List<MvAnalysis> findBySongIdOrderByIdDesc(Long songId);

    // 중복 분석 요청을 막기 위해 가장 최근 작업을 확인한다.
    Optional<MvAnalysis> findFirstBySongIdOrderByIdDesc(Long songId);

    // 곡의 최신 분석 (특정 상태, 예: DONE) 조회
    Optional<MvAnalysis> findFirstBySong_IdAndStatusOrderByFinishedAtDesc(Long songId, AnalysisStatus status);
}
