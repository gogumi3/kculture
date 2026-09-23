package com.kculture.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 공개 MV의 youtube video id로 곡을 등록한다. 제목/가수/썸네일은 YouTube Data API로 자동 수집한다.
public record SongCreateRequest(
        @NotBlank @Size(max = 50) String youtubeVideoId
) {
}
