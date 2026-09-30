package com.kculture.quest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MissionCompleteRequest(
        @NotNull @Valid LocationRequest location,
        @Size(max = 200) String answer,
        @Size(max = 500) String photoUrl
) {
}
