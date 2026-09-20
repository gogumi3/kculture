package com.kculture.user.repository;

import com.kculture.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

// 퀘스트 진행 기록이 어느 사용자의 것인지 확인하기 위해 기본 사용자 조회가 필요하다.
public interface UserRepository extends JpaRepository<User, Long> {
}
