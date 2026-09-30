-- 사용자 국적 컬럼 추가
-- 기획서(2. DB) users.nationality: 국적(선택 입력, 시기 추천용)
ALTER TABLE users
    ADD COLUMN nationality VARCHAR(50) NULL AFTER nickname;
