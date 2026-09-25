package com.kculture.content.dto;

public record SongResponse(
        // 레코드 사용시 필드 생성자 getter setter equals()
        // hashCode(), toString() 등 만들어줌으로 코드 줄이기 용
        // 한번 들어온 값을 바꾸지 못함

        Long id,
        String title,
        String artist,
        String youtubeVideoId,
        String thumbnailUrl
) {
}
