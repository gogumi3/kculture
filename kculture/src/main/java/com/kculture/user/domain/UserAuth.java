package com.kculture.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_auth",     // 연결할 테이블
        uniqueConstraints = {   // 여러 컬럼을 묶는 중복 방지
                @UniqueConstraint(
                        name = "uk_auth_provider", // 제약 이름
                        columnNames = {"provider", "provider_uid"}  // 묶어서 검사할 컬럼
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 인증 수단 자체의 id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    // 이 인증 수단의 사용자
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20,
            columnDefinition = "VARCHAR(20)")
    // 카카오 구글 이메일 중 하나로 로그인
    private AuthProvider provider;

    @Column(name = "provider_uid", length = 191)
    // 소셜 서비스가 발급한 사용자 식별값
    private String providerUid;

    @Column(name = "password_hash", length = 255)
    // 해시 처리한 비밀번호
    private String passwordHash;

    @Column(name = "last_login_at")
    // 마지막 로그인 성공 시간
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    // 인증 수단 등록 시간
    private LocalDateTime createdAt;

    public UserAuth(
            // 어느 사용자의 인증, 로그인 방식, id계정 비밀번호 인지
            User user,
            AuthProvider provider,
            String providerUid,
            String passwordHash
    ) {
        this.user = user;
        this.provider = provider;
        this.providerUid = providerUid;
        this.passwordHash = passwordHash;
    }

    // 로그인 성공 시 마지막 로그인 시간 갱신
    public void markLoginNow() {
        this.lastLoginAt = LocalDateTime.now();
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}