package com.kculture.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 이메일 회원가입 요청
public record SignupRequest(
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8, max = 64) String password,
        @NotBlank @Size(max = 50) String nickname,
        String nationality,     // 선택
        String languagePref     // 선택 (없으면 ko)
) {
}
