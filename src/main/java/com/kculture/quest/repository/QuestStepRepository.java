package com.kculture.quest.repository;

import com.kculture.quest.domain.QuestStep;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface QuestStepRepository extends JpaRepository<QuestStep, Long> {
    List<QuestStep> findByQuestIdOrderByStepOrderAsc(Long questId);
    Optional<QuestStep> findByQuestIdAndStepOrder(Long questId, int stepOrder);
}
