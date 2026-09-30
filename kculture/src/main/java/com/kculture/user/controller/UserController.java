package com.kculture.user.controller;

import com.kculture.user.dto.SignupRequest;
import com.kculture.user.dto.UserResponse;
import com.kculture.user.dto.UserUpdateRequest;
import com.kculture.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 이메일 회원가입
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signup(@Valid @RequestBody SignupRequest request) {
        return userService.signup(request);
    }

    // 프로필 조회
    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    // 설정(닉네임/국적/언어) 수정
    @PatchMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id,
                                   @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }
}
