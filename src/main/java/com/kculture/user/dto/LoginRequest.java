package com.kculture.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// 이메일 로그인 요청
public record LoginRequest(
        @Email @NotBlank String email,
        @NotBlank String password
) {
}
