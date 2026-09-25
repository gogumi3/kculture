package com.kculture.user.service;

import com.kculture.user.client.GoogleAuthClient;
import com.kculture.user.client.KakaoAuthClient;
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

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final KakaoAuthClient kakaoAuthClient;
    private final GoogleAuthClient googleAuthClient;

    // 이메일 회원가입: users + user_auth(LOCAL) 동시 생성
    @Transactional
    public UserResponse signup(SignupRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
        }

        User user = new User(
                email,
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

    // 이메일 로그인: 비밀번호 검증 후 컨트롤러가 JWT와 함께 반환할 프로필 생성
    @Transactional
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(normalizeEmail(request.email()))
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

    // 소셜 로그인: accessToken을 provider(카카오/구글) API로 직접 검증해서 얻은 providerUid/email/닉네임만 신뢰한다.
    // 검증된 계정이 이미 있으면 로그인, 없으면 가입.
    @Transactional
    public UserResponse socialLogin(SocialLoginRequest request) {
        AuthProvider provider = parseProvider(request.provider());
        if (provider == AuthProvider.LOCAL) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "소셜 로그인에서는 LOCAL을 사용할 수 없습니다.");
        }
        VerifiedIdentity identity = verify(provider, request.accessToken());

        // 1) 기존 소셜 계정이면 그대로 로그인
        UserAuth existing = userAuthRepository
                .findByProviderAndProviderUid(provider, identity.providerUid())
                .orElse(null);
        if (existing != null) {
            existing.markLoginNow();
            return UserResponse.from(existing.getUser());
        }

        // 2) 이메일 확보 (소셜이 이메일을 안 줄 수 있어 placeholder 생성 - users.email은 NOT NULL)
        String email = (identity.email() != null && !identity.email().isBlank())
                ? normalizeEmail(identity.email())
                : provider.name().toLowerCase() + "_" + identity.providerUid() + "@social.local";

        // 3) 검증되지 않은 신원만으로 기존 이메일 계정에 연결하면 계정 탈취가 가능하므로 거부한다.
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "이미 가입된 이메일입니다. 기존 계정으로 로그인해 주세요."
            );
        }
        user = new User(
                email,
                identity.nickname(),
                provider.name().toLowerCase(),
                null,
                null
        );
        userRepository.save(user);

        UserAuth auth = new UserAuth(user, provider, identity.providerUid(), null);
        auth.markLoginNow();
        userAuthRepository.save(auth);

        return UserResponse.from(user);
    }

    // provider별 클라이언트로 accessToken을 검증하고, 서버가 확인한 신원 정보만 꺼낸다.
    private VerifiedIdentity verify(AuthProvider provider, String accessToken) {
        return switch (provider) {
            case KAKAO -> {
                var info = kakaoAuthClient.fetch(accessToken);
                yield new VerifiedIdentity(info.providerUid(), info.email(), info.nickname());
            }
            case GOOGLE -> {
                var info = googleAuthClient.fetch(accessToken);
                yield new VerifiedIdentity(info.providerUid(), info.email(), info.nickname());
            }
            case LOCAL -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "지원하지 않는 로그인 방식입니다: " + provider);
        };
    }

    // provider가 검증해서 돌려준, 신뢰 가능한 사용자 신원 정보.
    private record VerifiedIdentity(String providerUid, String email, String nickname) {
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

    private String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }
}
