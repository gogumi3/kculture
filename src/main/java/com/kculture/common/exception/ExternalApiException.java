package com.kculture.common.exception;

import org.springframework.http.HttpStatus;

public class ExternalApiException extends RuntimeException {

    private final HttpStatus status;

    public ExternalApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public ExternalApiException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
