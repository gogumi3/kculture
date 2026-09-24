package com.kculture.user.controller;

import com.kculture.common.security.JwtTokenProvider;
import com.kculture.user.dto.AuthResponse;
import com.kculture.user.dto.LoginRequest;
import com.kculture.user.dto.SocialLoginRequest;
import com.kculture.user.dto.UserResponse;
import com.kculture.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    // 이메일 로그인
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return withToken(userService.login(request));
    }

    // 소셜 로그인 (프론트 OAuth 후 provider/uid 전달)
    @PostMapping("/social")
    public AuthResponse socialLogin(@Valid @RequestBody SocialLoginRequest request) {
        return withToken(userService.socialLogin(request));
    }

    private AuthResponse withToken(UserResponse user) {
        return new AuthResponse(
                jwtTokenProvider.createToken(user.id()),
                "Bearer",
                jwtTokenProvider.getExpirationSeconds(),
                user
        );
    }
}
