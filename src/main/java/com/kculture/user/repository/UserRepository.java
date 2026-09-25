package com.kculture.user.repository;

import com.kculture.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.Optional;

// 퀘스트 진행 기록이 어느 사용자의 것인지 확인하기 위해 기본 사용자 조회가 필요하다.
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 조회 (로그인/중복확인)
    Optional<User> findByEmail(String email);

    // 이메일 중복 여부 (회원가입 검증)
    boolean existsByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id = :id")
    Optional<User> findByIdForUpdate(@Param("id") Long id);
}
