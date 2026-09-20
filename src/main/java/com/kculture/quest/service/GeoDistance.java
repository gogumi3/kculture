package com.kculture.quest.service;

// 두 GPS 좌표 사이의 거리를 미터로 계산한다.
public final class GeoDistance {
    private GeoDistance() {
    }

    public static double meters(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6_371_000;
        double latitudeDifference = Math.toRadians(lat2 - lat1);
        double longitudeDifference = Math.toRadians(lon2 - lon1);

        double value = Math.pow(Math.sin(latitudeDifference / 2), 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.pow(Math.sin(longitudeDifference / 2), 2);

        return earthRadius * 2 * Math.atan2(Math.sqrt(value), Math.sqrt(1 - value));
    }
}
