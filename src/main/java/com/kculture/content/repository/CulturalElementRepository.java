package com.kculture.content.repository;

import com.kculture.content.domain.CulturalElement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CulturalElementRepository extends JpaRepository<CulturalElement, Long> {

    // 영상에 등장한 시간 순서대로 요소 반환
    List<CulturalElement> findByAnalysisIdOrderByTimestampSecAscIdAsc(Long analysisId);

    // 특정 분석에서 추출된 전체 요소 조회
    List<CulturalElement> findByAnalysis_Id(Long analysisId);
}