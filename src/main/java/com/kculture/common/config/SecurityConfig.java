package com.kculture.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 비밀번호 해시용 인코더 빈.
 * spring-security-crypto만 사용하므로 전체 시큐리티 필터 체인은 동작하지 않고,
 * 기존 API 엔드포인트는 그대로 공개 상태로 유지됩니다.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
