package com.kculture.user.repository;

import com.kculture.user.domain.AuthProvider;
import com.kculture.user.domain.User;
import com.kculture.user.domain.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

    // 소셜 로그인: provider + 소셜 고유 ID로 인증 수단 조회
    Optional<UserAuth> findByProviderAndProviderUid(AuthProvider provider, String providerUid);

    // 이메일 로그인: 특정 사용자의 LOCAL 인증 수단 조회
    Optional<UserAuth> findByUserAndProvider(User user, AuthProvider provider);
}
