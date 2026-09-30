package com.kculture.quest.dto;

import com.kculture.quest.domain.StepStatus;

// 잠긴 단계에서는 장소 상세 내용을 보내지 않고 순서와 상태만 보여준다.
public record StepProgressResponse(
        Long stepId,
        int stepOrder,
        StepStatus status,
        String placeName
) {
}
