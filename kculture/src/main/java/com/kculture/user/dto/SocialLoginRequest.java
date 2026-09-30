package com.kculture.user.dto;

import jakarta.validation.constraints.NotBlank;

// 소셜 로그인 요청 (프론트가 카카오/구글 SDK로 받은 값 전달)
public record SocialLoginRequest(
        @NotBlank String provider,     // KAKAO / GOOGLE
        @NotBlank String providerUid,  // 소셜이 발급한 고유 ID
        String email,                  // 제공 안 될 수 있음
        String nickname
) {
}
