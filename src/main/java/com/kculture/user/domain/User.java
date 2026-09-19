package com.kculture.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity     // jpa Entity로 등록
@Table(name = "users")  // users 테이블과 연결
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // jpa용 기본 생성자
public class User {

    @Id     // 사용자 기본 키
    @GeneratedValue(strategy = GenerationType.IDENTITY) // db 자동 증가
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    // 사용자 이메일 null,중복 허용 안함
    private String email;

    @Column(name = "nickname", length = 50)
    // 화면에 표시할 닉네임
    private String nickname;

    @Column(name = "nationality", length = 50)
    // 국적 (선택 입력, 시기 추천용)
    private String nationality;

    @Column(name = "language_pref", nullable = false, length = 10)
    // 사용할 언어 (기본은 한국어)
    private String languagePref = "ko";

    @Column(name = "provider", length = 20)
    // 주 가입 경로를 나타내는 참고값
    private String provider;

    @Column(name = "created_at", nullable = false, updatable = false)
    // 가입 데이터 생성 시간
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    // 마지막 수정 시간
    private LocalDateTime updatedAt;

    public User(String email, String nickname, String provider) {
        // 전달받은 이메일 닉네임 가입경로 저장
        this.email = email;
        this.nickname = nickname;
        this.provider = provider;
    }

    public User(String email, String nickname, String provider,
                String languagePref, String nationality) {
        this.email = email;
        this.nickname = nickname;
        this.provider = provider;
        if (languagePref != null) {         // 값이 없으면 기본 "ko" 유지
            this.languagePref = languagePref;
        }
        this.nationality = nationality;
    }

    // 설정 화면(S11)에서 닉네임/국적/언어를 부분 수정 (null이면 기존값 유지)
    public void updateSettings(String nickname, String nationality, String languagePref) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (nationality != null) {
            this.nationality = nationality;
        }
        if (languagePref != null) {
            this.languagePref = languagePref;
        }
    }

    @PrePersist
    private void onCreate() {
        LocalDateTime now = LocalDateTime.now(); // 현재 시간을 한 번 구함
        this.createdAt = now;                   // 생성 시간 설정
        this.updatedAt = now;                   // 첫 저장에서 수정 시간도 같은 값
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();   // 수정시 생성 시간 그대로 수정 시간만 갱신
    }
}
