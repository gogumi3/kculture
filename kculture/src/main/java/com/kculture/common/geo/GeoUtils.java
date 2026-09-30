package com.kculture.common.geo;

import java.math.BigDecimal;

/**
 * 위경도 거리 계산 유틸.
 * - haversineMeters: 두 좌표 사이 실제 거리(m)
 * - boundingBox: 반경을 감싸는 위경도 사각형(1차 DB 필터용)
 * quest 도착 판정 반경 등에서도 재사용 가능.
 */
public final class GeoUtils {

    private static final double EARTH_RADIUS_M = 6_371_000.0; // 지구 반경(m)

    private GeoUtils() {
    }

    // 두 좌표 사이 거리(m) - Haversine 공식
    public static double haversineMeters(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_M * c;
    }

    // 반경(m)을 감싸는 위경도 사각형 [minLat, maxLat, minLng, maxLng]
    // DB에서 BETWEEN으로 1차 필터한 뒤 haversineMeters로 정확히 걸러내는 용도
    public static BoundingBox boundingBox(double lat, double lng, double radiusMeters) {
        double latDelta = Math.toDegrees(radiusMeters / EARTH_RADIUS_M);
        double cos = Math.cos(Math.toRadians(lat));
        // 위도가 높을수록 경도 1도의 실제 거리가 짧아짐 → 보정. 극점 부근 0 나눗셈 방지.
        double lngDelta = Math.toDegrees(radiusMeters / (EARTH_RADIUS_M * Math.max(cos, 1e-6)));
        return new BoundingBox(
                BigDecimal.valueOf(lat - latDelta),
                BigDecimal.valueOf(lat + latDelta),
                BigDecimal.valueOf(lng - lngDelta),
                BigDecimal.valueOf(lng + lngDelta)
        );
    }

    // 바운딩박스 결과 (min/max 위경도)
    public record BoundingBox(
            BigDecimal minLat,
            BigDecimal maxLat,
            BigDecimal minLng,
            BigDecimal maxLng
    ) {
    }
}
