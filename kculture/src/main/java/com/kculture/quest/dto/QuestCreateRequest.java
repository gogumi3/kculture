package com.kculture.quest.dto;

import com.kculture.quest.domain.MissionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record QuestCreateRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 50) String themeRegion,
        @Size(max = 50) String themeEra,
        @Size(max = 500) String description,
        @NotEmpty List<@Valid StepRequest> steps
) {
    // steps 목록의 순서가 실제 방문 순서가 된다.
    public record StepRequest(
            @NotNull @Positive Long placeId,
            String story,
            @Size(max = 100) String distanceHint,
            @Min(10) @Max(500) int arrivalRadius,
            @NotNull @Valid MissionRequest mission
    ) {
    }

    public record MissionRequest(
            @NotNull MissionType missionType,
            @NotBlank @Size(max = 500) String question,
            @Size(max = 200) String answer
    ) {
    }
}
