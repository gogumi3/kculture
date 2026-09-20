package com.kculture.quest.repository;

import com.kculture.quest.domain.UserQuestProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserQuestProgressRepository extends JpaRepository<UserQuestProgress, Long> {
    Optional<UserQuestProgress> findByUserIdAndQuestId(Long userId, Long questId);
    List<UserQuestProgress> findByUserIdOrderByStartedAtDesc(Long userId);
}
