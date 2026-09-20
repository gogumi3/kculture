package com.kculture.quest.dto;

import com.kculture.quest.domain.QuestProgressStatus;
import java.util.List;

public record QuestProgressResponse(
        Long questId,
        String questTitle,
        int currentStep,
        QuestProgressStatus status,
        List<StepProgressResponse> steps
) {
}
