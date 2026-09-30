package com.kculture.quest.repository;

import com.kculture.quest.domain.Stamp;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StampRepository extends JpaRepository<Stamp, Long> {
    List<Stamp> findByUserIdOrderByAcquiredAtDesc(Long userId);
    boolean existsByUserIdAndStepId(Long userId, Long stepId);
}
