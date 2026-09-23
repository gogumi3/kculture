package com.kculture.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * MV 분석을 백그라운드로 실행하기 위한 비동기 설정.
 * 요청 스레드는 즉시 응답(RUNNING)하고, 실제 Gemini 호출은 이 풀에서 처리한다.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "mvAnalysisExecutor")
    public Executor mvAnalysisExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("mv-analysis-");
        executor.initialize();
        return executor;
    }
}
