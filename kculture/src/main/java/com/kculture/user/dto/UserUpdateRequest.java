package com.kculture.user.dto;

import jakarta.validation.constraints.Size;

// 설정(S11) 부분 수정 요청 - null 필드는 기존값 유지
public record UserUpdateRequest(
        @Size(max = 50) String nickname,
        @Size(max = 50) String nationality,
        @Size(max = 10) String languagePref
) {
}
