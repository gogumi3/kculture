package com.kculture.content.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "translations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_translations",
                        columnNames = {
                                "entity_type",
                                "entity_id",
                                "field_name",
                                "language"
                        }
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 번역 행 자체의 id
    private Long id;

    @Column(name = "entity_type", nullable = false, length = 30)
    // 번역 대상 종류
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    // 번역 대상 데이터 id
    private Long entityId;

    @Column(name = "field_name", nullable = false, length = 50)
    // 번역할 필드
    private String fieldName;

    @Column(name = "language", nullable = false, length = 10)
    // 번역 언어 코드
    private String language;

    @Column(name = "text_value", nullable = false,
            columnDefinition = "TEXT")
    // 실제 번역문
    private String textValue;

    @Column(name = "created_at", nullable = false, updatable = false)
    // 번역 등록 시간
    private LocalDateTime createdAt;

    public Translation(
            String entityType,
            Long entityId,
            String fieldName,
            String language,
            String textValue
    ) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.fieldName = fieldName;
        this.language = language;
        this.textValue = textValue;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 같은 번역 데이터가 다시 들어오면 행을 추가하지 않고 번역문만 수정한다.
    public void updateText(String textValue) {
        this.textValue = textValue;
    }

}