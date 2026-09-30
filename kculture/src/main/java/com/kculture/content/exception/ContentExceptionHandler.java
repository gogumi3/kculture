package com.kculture.content.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// content.controller 안에서 발생한 예외만 처리한다.
@RestControllerAdvice(basePackages = "com.kculture.content.controller")
public class ContentExceptionHandler {

    @ExceptionHandler(ContentNotFoundException.class)
    public ResponseEntity<ContentErrorResponse> handleNotFound(ContentNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ContentErrorResponse(404, exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ContentErrorResponse> handleBadRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest()
                .body(new ContentErrorResponse(400, exception.getMessage()));
    }
}
