package com.kculture.quest.dto;

// 사용자가 장소 반경 안에 들어왔는지 알려주는 응답이다.
public record ArrivalResponse(
        boolean arrived,
        double distanceMeters,
        int arrivalRadius,
        String story,
        MissionResponse mission
) {
}
