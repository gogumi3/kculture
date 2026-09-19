package com.kculture.user.dto;

import com.kculture.user.domain.User;

// 사용자 응답 (비밀번호/인증정보는 노출하지 않음)
public record UserResponse(
        Long id,
        String email,
        String nickname,
        String nationality,
        String languagePref,
        String provider
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getNationality(),
                user.getLanguagePref(),
                user.getProvider()
        );
    }
}
