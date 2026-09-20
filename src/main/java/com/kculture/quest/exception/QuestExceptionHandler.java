package com.kculture.quest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// quest.controller에서 발생한 퀘스트 관련 예외만 공통 처리한다.
@RestControllerAdvice(basePackages = "com.kculture.quest.controller")
public class QuestExceptionHandler {

    @ExceptionHandler(QuestNotFoundException.class)
    public ResponseEntity<QuestErrorResponse> handleNotFound(QuestNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new QuestErrorResponse(404, exception.getMessage()));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<QuestErrorResponse> handleBadRequest(RuntimeException exception) {
        return ResponseEntity.badRequest()
                .body(new QuestErrorResponse(400, exception.getMessage()));
    }
}
