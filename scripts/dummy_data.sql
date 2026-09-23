-- K-Culture 리얼맵: MV 분석 결과 더미 데이터
-- 곡 1개 + 완료된 분석(DONE) 1건 + 문화 요소 3개를 넣는다.
-- Gemini/YouTube API 키가 없어도 GET /api/analyses/{id}/elements 등을 바로 테스트하기 위한 개발용 시드 데이터.
--
-- 몇 번을 다시 실행해도 안전하다(멱등): 같은 youtube_video_id의 기존 더미 데이터가 있으면
-- 먼저 지우고 새로 넣는다. mv_analysis/cultural_elements는 songs를 ON DELETE CASCADE로
-- 참조하므로 songs만 지워도 연결된 데이터가 같이 정리된다.

DELETE FROM songs WHERE youtube_video_id = 'dQw4w9WgXcQ';

INSERT INTO songs (title, artist, youtube_video_id, thumbnail_url, created_at)
VALUES ('Dummy MV Title', 'Dummy Artist', 'dQw4w9WgXcQ', 'https://i.ytimg.com/vi/dQw4w9WgXcQ/hqdefault.jpg', NOW(6));
SET @song_id = LAST_INSERT_ID();

INSERT INTO mv_analysis (song_id, status, model_name, started_at, finished_at, created_at, fail_reason)
VALUES (@song_id, 'DONE', 'gemini', NOW(6), NOW(6), NOW(6), NULL);
SET @analysis_id = LAST_INSERT_ID();

INSERT INTO cultural_elements (analysis_id, category, name, description, timestamp_sec, confidence, created_at)
VALUES
  (@analysis_id, 'ARCHITECTURE', '경복궁', '뮤직비디오 배경으로 등장하는 전통 궁궐', 42, 0.910, NOW(6)),
  (@analysis_id, 'COSTUME', '한복', '무대 의상으로 사용된 전통 한복', 78, 0.850, NOW(6)),
  (@analysis_id, 'FOOD', '떡', '테이블 위 전통 떡', 120, 0.620, NOW(6));

-- 방금 넣은 데이터 확인용
SELECT @song_id AS inserted_song_id, @analysis_id AS inserted_analysis_id;
