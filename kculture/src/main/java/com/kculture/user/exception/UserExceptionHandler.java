package com.kculture.user.exception;

// [Claude 작성 - 신규 파일]
// 지금까지 회원가입/로그인 쪽엔 검증 실패(@Valid) 예외를 따로 처리하는 핸들러가 없어서,
// 예를 들어 비밀번호가 8자 미만이거나 닉네임이 비어있을 때 스프링 기본 에러 페이지가 나갔습니다
// (예: {"timestamp":"...","status":400,"error":"Bad Request","path":"/api/users/signup"}).
// 이 핸들러는 quest/content 모듈의 기존 ExceptionHandler들과 같은 패턴으로,
// 어떤 값이 왜 잘못됐는지 한국어 메시지로 알려주도록 추가했습니다. 기존 파일은 건드리지 않았습니다.

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.kculture.user.controller")
public class UserExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<UserErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        FieldError firstError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String message = firstError != null
                ? firstError.getField() + ": " + firstError.getDefaultMessage()
                : "입력값을 확인해주세요.";
        return ResponseEntity.badRequest().body(new UserErrorResponse(400, message));
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<UserErrorResponse> handleBadRequest(RuntimeException exception) {
        return ResponseEntity.badRequest().body(new UserErrorResponse(400, exception.getMessage()));
    }
}
