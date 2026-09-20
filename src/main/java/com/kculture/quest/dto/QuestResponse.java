package com.kculture.quest.dto;

import com.kculture.quest.domain.Quest;
import com.kculture.quest.domain.QuestOriginType;

// 퀘스트 목록과 상세 화면에 사용할 기본 정보다.
public record QuestResponse(
        Long id,
        String title,
        String themeRegion,
        String themeEra,
        QuestOriginType originType,
        String description
) {
    public static QuestResponse from(Quest quest) {
        return new QuestResponse(
                quest.getId(), quest.getTitle(), quest.getThemeRegion(), quest.getThemeEra(),
                quest.getOriginType(), quest.getDescription()
        );
    }
}
