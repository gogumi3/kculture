package com.kculture.user.repository;

import com.kculture.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 조회 (로그인/중복확인)
    Optional<User> findByEmail(String email);

    // 이메일 중복 여부 (회원가입 검증)
    boolean existsByEmail(String email);
}
