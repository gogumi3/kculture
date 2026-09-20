package com.kculture.quest.repository;

import com.kculture.quest.domain.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    Optional<Mission> findByStepId(Long stepId);
}
