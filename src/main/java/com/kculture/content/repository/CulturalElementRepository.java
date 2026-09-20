package com.kculture.content.repository;

import com.kculture.content.domain.CulturalElement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CulturalElementRepository extends JpaRepository<CulturalElement, Long> {
    // 영상에 등장한 시간 순서대로 요소를 반환한다.
    List<CulturalElement> findByAnalysisIdOrderByTimestampSecAscIdAsc(Long analysisId);
}
