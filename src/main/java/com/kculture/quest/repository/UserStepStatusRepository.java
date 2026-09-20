package com.kculture.quest.repository;

import com.kculture.quest.domain.UserStepStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserStepStatusRepository extends JpaRepository<UserStepStatus, Long> {
    Optional<UserStepStatus> findByUserIdAndQuestStepId(Long userId, Long stepId);
    List<UserStepStatus> findByUserIdAndQuestStepQuestIdOrderByQuestStepStepOrderAsc(Long userId, Long questId);
}
