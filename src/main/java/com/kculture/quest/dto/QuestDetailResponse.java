package com.kculture.quest.dto;

import java.util.List;

// 퀘스트 기본 정보와 순서대로 정렬된 단계를 함께 반환한다.
public record QuestDetailResponse(QuestResponse quest, List<QuestStepResponse> steps) {
}
