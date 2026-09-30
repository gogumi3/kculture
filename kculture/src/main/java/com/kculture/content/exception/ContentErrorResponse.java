package com.kculture.content.exception;

// 프론트에 전달할 에러 JSON 구조다.
public record ContentErrorResponse(int status, String message) {
}
