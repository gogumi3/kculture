package com.kculture.quest.repository;

import com.kculture.quest.domain.UserQuestProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface UserQuestProgressRepository extends JpaRepository<UserQuestProgress, Long> {
    Optional<UserQuestProgress> findByUserIdAndQuestId(Long userId, Long questId);
    List<UserQuestProgress> findByUserIdOrderByStartedAtDesc(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select p from UserQuestProgress p
            where p.user.id = :userId and p.quest.id = :questId
            """)
    Optional<UserQuestProgress> findByUserIdAndQuestIdForUpdate(
            @Param("userId") Long userId,
            @Param("questId") Long questId
    );
}
