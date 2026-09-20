package com.kculture.quest.repository;

import com.kculture.quest.domain.Quest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface QuestRepository extends JpaRepository<Quest, Long> {
    // 현재 사용할 수 있는 퀘스트만 최신순으로 조회한다.
    List<Quest> findByActiveTrueOrderByIdDesc();

    // 같은 추천 세션에서 퀘스트가 중복 생성되는 것을 확인한다.
    Optional<Quest> findBySessionId(Long sessionId);
}
