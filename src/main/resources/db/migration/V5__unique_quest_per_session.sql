-- 하나의 추천 세션은 하나의 퀘스트로만 변환할 수 있다.
-- session_id가 NULL인 CURATED 퀘스트는 MySQL UNIQUE 규칙상 여러 건 허용된다.
ALTER TABLE quests
    ADD CONSTRAINT uk_quests_session UNIQUE (session_id);
