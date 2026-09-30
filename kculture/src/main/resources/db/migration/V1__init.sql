-- K-Culture: 최초 테이블 생성
-- Flyway V1 migration

-- 1. 사용자
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) NULL,
    language_pref VARCHAR(10) NOT NULL DEFAULT 'ko',
    provider VARCHAR(20) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 2. 로그인 / 인증 수단
CREATE TABLE user_auth (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    provider VARCHAR(20) NOT NULL,
    provider_uid VARCHAR(191) NULL,
    password_hash VARCHAR(255) NULL,
    last_login_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_auth_provider (provider, provider_uid),
    KEY idx_auth_user (user_id),

    CONSTRAINT fk_auth_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 3. 곡과 공식 MV
CREATE TABLE songs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    artist VARCHAR(255) NOT NULL,
    youtube_video_id VARCHAR(50) NULL,
    thumbnail_url VARCHAR(500) NULL,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    KEY idx_songs_title (title),
    UNIQUE KEY uk_songs_youtube (youtube_video_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 4. MV 분석 실행 이력
CREATE TABLE mv_analysis (
    id BIGINT NOT NULL AUTO_INCREMENT,
    song_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    model_name VARCHAR(100) NULL,
    started_at DATETIME(6) NULL,
    finished_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    KEY idx_mv_analysis_song (song_id),

    CONSTRAINT fk_mv_analysis_song
        FOREIGN KEY (song_id)
        REFERENCES songs(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 5. MV에서 추출한 한국적 요소
CREATE TABLE cultural_elements (
    id BIGINT NOT NULL AUTO_INCREMENT,
    analysis_id BIGINT NOT NULL,
    category VARCHAR(30) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NULL,
    timestamp_sec INT NULL,
    confidence DECIMAL(4,3) NULL,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    KEY idx_elements_analysis (analysis_id),
    KEY idx_elements_category (category),

    CONSTRAINT fk_elements_analysis
        FOREIGN KEY (analysis_id)
        REFERENCES mv_analysis(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 6. 실제 방문 장소
CREATE TABLE places (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    region_sido VARCHAR(50) NULL,
    region_sigungu VARCHAR(50) NULL,
    category VARCHAR(50) NULL,
    latitude DECIMAL(10,7) NULL,
    longitude DECIMAL(10,7) NULL,
    kakao_place_id VARCHAR(50) NULL,
    visitor_index INT NULL,
    address VARCHAR(300) NULL,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    KEY idx_places_region (region_sido, region_sigungu),
    KEY idx_places_category (category),
    KEY idx_places_geo (latitude, longitude)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 7. 한국적 요소와 장소 연결
CREATE TABLE element_place_match (
    id BIGINT NOT NULL AUTO_INCREMENT,
    element_id BIGINT NOT NULL,
    place_id BIGINT NOT NULL,
    match_score DECIMAL(4,3) NULL,
    reason VARCHAR(500) NULL,
    display_order INT NOT NULL DEFAULT 0,
    region_bonus DECIMAL(4,3) NULL,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    KEY idx_match_element (element_id),
    KEY idx_match_place (place_id),

    CONSTRAINT fk_match_element
        FOREIGN KEY (element_id)
        REFERENCES cultural_elements(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_match_place
        FOREIGN KEY (place_id)
        REFERENCES places(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 8. 한 번의 곡 검색에서 시작한 추천 묶음
CREATE TABLE recommendation_sessions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NULL,
    song_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    KEY idx_recsession_user (user_id),
    KEY idx_recsession_song (song_id),

    CONSTRAINT fk_recsession_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_recsession_song
        FOREIGN KEY (song_id)
        REFERENCES songs(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 9. 추천 묶음에 포함된 장소
CREATE TABLE session_places (
    id BIGINT NOT NULL AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    place_id BIGINT NOT NULL,
    element_id BIGINT NULL,
    display_order INT NOT NULL DEFAULT 0,
    is_chosen TINYINT(1) NOT NULL DEFAULT 0,
    shown_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    KEY idx_sessplace_session (session_id, display_order),
    KEY idx_sessplace_place (place_id),

    CONSTRAINT fk_sessplace_session
        FOREIGN KEY (session_id)
        REFERENCES recommendation_sessions(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_sessplace_place
        FOREIGN KEY (place_id)
        REFERENCES places(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_sessplace_element
        FOREIGN KEY (element_id)
        REFERENCES cultural_elements(id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 10. 퀘스트 코스
CREATE TABLE quests (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    theme_region VARCHAR(50) NULL,
    theme_era VARCHAR(50) NULL,
    origin_type VARCHAR(20) NOT NULL DEFAULT 'CURATED',
    song_id BIGINT NULL,
    session_id BIGINT NULL,
    description VARCHAR(500) NULL,
    is_active TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    KEY idx_quests_region (theme_region),
    KEY idx_quests_session (session_id),

    CONSTRAINT fk_quests_song
        FOREIGN KEY (song_id)
        REFERENCES songs(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_quests_session
        FOREIGN KEY (session_id)
        REFERENCES recommendation_sessions(id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 11. 퀘스트 안의 방문 단계
CREATE TABLE quest_steps (
    id BIGINT NOT NULL AUTO_INCREMENT,
    quest_id BIGINT NOT NULL,
    place_id BIGINT NOT NULL,
    step_order INT NOT NULL,
    story TEXT NULL,
    distance_hint VARCHAR(100) NULL,
    arrival_radius INT NOT NULL DEFAULT 50,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_step_order (quest_id, step_order),
    KEY idx_steps_place (place_id),

    CONSTRAINT fk_steps_quest
        FOREIGN KEY (quest_id)
        REFERENCES quests(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_steps_place
        FOREIGN KEY (place_id)
        REFERENCES places(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 12. 단계별 미션
CREATE TABLE missions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    step_id BIGINT NOT NULL,
    mission_type VARCHAR(20) NOT NULL,
    question VARCHAR(500) NULL,
    answer VARCHAR(200) NULL,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_missions_step (step_id),

    CONSTRAINT fk_missions_step
        FOREIGN KEY (step_id)
        REFERENCES quest_steps(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 13. 사용자별 퀘스트 전체 진행 상태
CREATE TABLE user_quest_progress (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    quest_id BIGINT NOT NULL,
    current_step INT NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    started_at DATETIME(6) NOT NULL,
    completed_at DATETIME(6) NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_progress_user_quest (user_id, quest_id),
    KEY idx_progress_quest (quest_id),

    CONSTRAINT fk_progress_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_progress_quest
        FOREIGN KEY (quest_id)
        REFERENCES quests(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 14. 사용자별 각 단계의 잠금 / 완료 상태
CREATE TABLE user_step_status (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    quest_step_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'LOCKED',
    unlocked_at DATETIME(6) NULL,
    done_at DATETIME(6) NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_stepstatus (user_id, quest_step_id),
    KEY idx_stepstatus_step (quest_step_id),

    CONSTRAINT fk_stepstatus_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_stepstatus_step
        FOREIGN KEY (quest_step_id)
        REFERENCES quest_steps(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 15. 획득한 스탬프
CREATE TABLE stamps (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    step_id BIGINT NOT NULL,
    photo_url VARCHAR(500) NULL,
    acquired_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_stamps_user_step (user_id, step_id),
    KEY idx_stamps_user (user_id),

    CONSTRAINT fk_stamps_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_stamps_step
        FOREIGN KEY (step_id)
        REFERENCES quest_steps(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 16. 숙박 시설
CREATE TABLE accommodations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    accom_type VARCHAR(50) NULL,
    region_sido VARCHAR(50) NULL,
    region_sigungu VARCHAR(50) NULL,
    latitude DECIMAL(10,7) NULL,
    longitude DECIMAL(10,7) NULL,
    address VARCHAR(300) NULL,
    phone VARCHAR(30) NULL,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    KEY idx_accom_geo (latitude, longitude),
    KEY idx_accom_region (region_sido, region_sigungu)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 17. 다국어 번역
CREATE TABLE translations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    entity_type VARCHAR(30) NOT NULL,
    entity_id BIGINT NOT NULL,
    field_name VARCHAR(50) NOT NULL,
    language VARCHAR(10) NOT NULL,
    text_value TEXT NOT NULL,
    created_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uk_translations (
        entity_type,
        entity_id,
        field_name,
        language
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 18. 검색 기록
CREATE TABLE search_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NULL,
    keyword VARCHAR(255) NOT NULL,
    song_id BIGINT NULL,
    searched_at DATETIME(6) NOT NULL,

    PRIMARY KEY (id),
    KEY idx_search_user (user_id),
    KEY idx_search_keyword (keyword),

    CONSTRAINT fk_search_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_search_song
        FOREIGN KEY (song_id)
        REFERENCES songs(id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;