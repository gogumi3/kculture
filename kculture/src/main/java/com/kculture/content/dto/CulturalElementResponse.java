package com.kculture.content.dto;

import com.kculture.content.domain.CulturalCategory;
import com.kculture.content.domain.CulturalElement;
import java.math.BigDecimal;

// 엔터티 전체 대신 화면에 필요한 문화 요소 정보만 반환한다.
public record CulturalElementResponse(
        Long id,
        CulturalCategory category,
        String name,
        String description,
        Integer timestampSec,
        BigDecimal confidence
) {
    public static CulturalElementResponse from(CulturalElement element) {
        return new CulturalElementResponse(
                element.getId(), element.getCategory(), element.getName(),
                element.getDescription(), element.getTimestampSec(), element.getConfidence()
        );
    }
}
