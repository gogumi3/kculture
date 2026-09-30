package com.kculture.quest.dto;

import com.kculture.quest.domain.Mission;
import com.kculture.quest.domain.MissionType;

// 정답은 프론트에 보내면 안 되므로 answer 필드를 넣지 않는다.
public record MissionResponse(Long id, MissionType missionType, String question) {
    public static MissionResponse from(Mission mission) {
        return new MissionResponse(mission.getId(), mission.getMissionType(), mission.getQuestion());
    }
}
