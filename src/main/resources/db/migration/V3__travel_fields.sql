-- travel 도메인 필드 보강
-- 기획서(2. DB) ③ tourist_spots / ⑦ lodgings + S10 숙박 추천 카드 기준

ALTER TABLE places
    ADD COLUMN description VARCHAR(500) NULL,   -- 장소 설명 (카드용)
    ADD COLUMN image_url   VARCHAR(300) NULL;   -- 대표 이미지 URL

ALTER TABLE accommodations
    ADD COLUMN price_range    VARCHAR(30) NULL,   -- 가격대 (LOW/MID/HIGH 등, 필터용)
    ADD COLUMN rating         DECIMAL(2,1) NULL,  -- 평점 0.0 ~ 5.0
    ADD COLUMN kakao_place_id VARCHAR(50) NULL;   -- 카카오맵 장소 ID (지도 마커 연동)
