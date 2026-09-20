package com.kculture.content.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mv_analysis")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MvAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 분석 작업 id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    // 하나의 곡에는 여러 분석
    @JoinColumn(name = "song_id", nullable = false)
    // 분석 대상 곡
    private Song song;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    // 분석 진행 단계 처음에는 분석 대기
    private AnalysisStatus status = AnalysisStatus.PENDING;

    @Column(name = "model_name", length = 100)
    // 분석에 사용할 모델 이름
    private String modelName;

    @Column(name = "started_at")
    // 실제 분석 시작 시간
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    // 분석 종료 시간
    private LocalDateTime finishedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    // 분석 작업 등록 시간
    private LocalDateTime createdAt;

    public MvAnalysis(Song song, String modelName) {
        // 어떤 곡을 어떤 모델로 분석할지
        this.song = song;
        this.modelName = modelName;
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 분석을 시작할 때 상태와 시작 시각을 함께 변경한다.
    public void start() {
        this.status = AnalysisStatus.RUNNING;
        this.startedAt = LocalDateTime.now();
    }

    // 분석 성공 여부에 따라 최종 상태와 종료 시각을 저장한다.
    public void finish(boolean success) {
        this.status = success ? AnalysisStatus.DONE : AnalysisStatus.FAILED;
        this.finishedAt = LocalDateTime.now();
    }

}
