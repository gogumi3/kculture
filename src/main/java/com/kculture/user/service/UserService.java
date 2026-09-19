package com.kculture.user.service;

import com.kculture.user.domain.AuthProvider;
import com.kculture.user.domain.User;
import com.kculture.user.domain.UserAuth;
import com.kculture.user.dto.LoginRequest;
import com.kculture.user.dto.SignupRequest;
import com.kculture.user.dto.SocialLoginRequest;
import com.kculture.user.dto.UserResponse;
import com.kculture.user.dto.UserUpdateRequest;
import com.kculture.user.repository.UserAuthRepository;
import com.kculture.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;

    // 이메일 회원가입: users + user_auth(LOCAL) 동시 생성
    @Transactional
    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
        }

        User user = new User(
                request.email(),
                request.nickname(),
                "local",
                request.languagePref(),
                request.nationality()
        );
        userRepository.save(user);

        UserAuth auth = new UserAuth(
                user,
                AuthProvider.LOCAL,
                null,
                passwordEncoder.encode(request.password())
        );
        userAuthRepository.save(auth);

        return UserResponse.from(user);
    }

    // 이메일 로그인: 비밀번호 검증 (MVP - 토큰 없이 프로필 반환)
    @Transactional
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));

        UserAuth auth = userAuthRepository.findByUserAndProvider(user, AuthProvider.LOCAL)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));

        if (auth.getPasswordHash() == null
                || !passwordEncoder.matches(request.password(), auth.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        auth.markLoginNow();
        return UserResponse.from(user);
    }

    // 소셜 로그인: 이미 있으면 로그인, 없으면 가입 (프론트가 OAuth 후 provider/uid 전달)
    @Transactional
    public UserResponse socialLogin(SocialLoginRequest request) {
        AuthProvider provider = parseProvider(request.provider());

        // 1) 기존 소셜 계정이면 그대로 로그인
        UserAuth existing = userAuthRepository
                .findByProviderAndProviderUid(provider, request.providerUid())
                .orElse(null);
        if (existing != null) {
            existing.markLoginNow();
            return UserResponse.from(existing.getUser());
        }

        // 2) 이메일 확보 (소셜이 이메일을 안 줄 수 있어 placeholder 생성 - users.email은 NOT NULL)
        String email = (request.email() != null && !request.email().isBlank())
                ? request.email()
                : provider.name().toLowerCase() + "_" + request.providerUid() + "@social.local";

        // 3) 같은 이메일 계정이 있으면 연결, 없으면 신규 생성
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            user = new User(
                    email,
                    request.nickname(),
                    provider.name().toLowerCase(),
                    null,
                    null
            );
            userRepository.save(user);
        }

        UserAuth auth = new UserAuth(user, provider, request.providerUid(), null);
        auth.markLoginNow();
        userAuthRepository.save(auth);

        return UserResponse.from(user);
    }

    // 프로필 조회
    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        return UserResponse.from(findUserOrThrow(id));
    }

    // 설정(닉네임/국적/언어) 수정
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = findUserOrThrow(id);
        user.updateSettings(request.nickname(), request.nationality(), request.languagePref());
        return UserResponse.from(user);
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
    }

    private AuthProvider parseProvider(String provider) {
        try {
            return AuthProvider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "지원하지 않는 로그인 방식입니다: " + provider);
        }
    }
}
