package com.kculture.common.exception;

public record ApiErrorResponse(int status, String message) {
}
