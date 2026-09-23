-- MV 분석 실패 사유 컬럼 추가
-- 공개 아님/임베드 불가/타임아웃/파싱 실패 등 왜 실패했는지 남겨 디버깅·사용자 안내에 사용 (FAILED 상태의 부가 정보)
ALTER TABLE mv_analysis
    ADD COLUMN fail_reason VARCHAR(255) NULL AFTER finished_at;   -- 분석 실패 사유 (성공 시 NULL)
