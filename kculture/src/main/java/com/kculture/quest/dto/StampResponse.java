package com.kculture.quest.dto;

import com.kculture.quest.domain.Stamp;
import java.time.LocalDateTime;

public record StampResponse(
        Long id,
        Long stepId,
        String placeName,
        String photoUrl,
        LocalDateTime acquiredAt
) {
    public static StampResponse from(Stamp stamp) {
        return new StampResponse(
                stamp.getId(), stamp.getStep().getId(), stamp.getStep().getPlace().getName(),
                stamp.getPhotoUrl(), stamp.getAcquiredAt()
        );
    }
}
