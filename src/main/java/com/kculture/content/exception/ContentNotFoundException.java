package com.kculture.content.exception;

// 콘텐츠 데이터가 존재하지 않을 때 서비스에서 발생시키는 예외다.
public class ContentNotFoundException extends RuntimeException {
    public ContentNotFoundException(String message) {
        super(message);
    }
}
