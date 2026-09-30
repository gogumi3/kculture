package com.kculture.quest.dto;

import com.kculture.quest.domain.QuestStep;
import java.math.BigDecimal;

public record QuestStepResponse(
        Long id,
        int stepOrder,
        Long placeId,
        String placeName,
        BigDecimal latitude,
        BigDecimal longitude,
        String story,
        String distanceHint,
        int arrivalRadius,
        MissionResponse mission
) {
    public static QuestStepResponse from(QuestStep step, MissionResponse mission) {
        return new QuestStepResponse(
                step.getId(), step.getStepOrder(), step.getPlace().getId(), step.getPlace().getName(),
                step.getPlace().getLatitude(), step.getPlace().getLongitude(), step.getStory(),
                step.getDistanceHint(), step.getArrivalRadius(), mission
        );
    }
}
