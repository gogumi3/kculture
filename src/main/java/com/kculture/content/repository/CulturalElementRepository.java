package com.kculture.content.repository;

import com.kculture.content.domain.CulturalElement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CulturalElementRepository extends JpaRepository<CulturalElement, Long> {

    // 특정 분석에서 추출된 요소들
    List<CulturalElement> findByAnalysis_Id(Long analysisId);
}
