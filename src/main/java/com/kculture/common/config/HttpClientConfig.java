package com.kculture.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * 외부 API 호출용 RestClient 빈.
 * RestClient는 spring-web(webmvc 스타터에 포함)에 있으므로 별도 의존성이 필요 없다.
 * JVM 프록시 설정은 실행 환경의 시스템 프로퍼티로 적용된다.
 */
@Configuration
public class HttpClientConfig {

    // Gemini (generativelanguage) 전용
    @Bean
    public RestClient geminiRestClient(
            @Value("${gemini.timeout-seconds:120}") long timeoutSeconds
    ) {
        return RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .requestFactory(requestFactory(timeoutSeconds))
                .build();
    }

    // YouTube Data API v3 전용
    @Bean
    public RestClient youtubeRestClient(
            @Value("${youtube.timeout-seconds:10}") long timeoutSeconds
    ) {
        return RestClient.builder()
                .baseUrl("https://www.googleapis.com/youtube/v3")
                .requestFactory(requestFactory(timeoutSeconds))
                .build();
    }

    // 카카오 로그인 액세스 토큰 검증(사용자 정보 조회) 전용
    @Bean
    public RestClient kakaoRestClient(
            @Value("${oauth.timeout-seconds:10}") long timeoutSeconds
    ) {
        return RestClient.builder()
                .baseUrl("https://kapi.kakao.com")
                .requestFactory(requestFactory(timeoutSeconds))
                .build();
    }

    // 구글 로그인 액세스 토큰 검증(사용자 정보 조회) 전용
    @Bean
    public RestClient googleRestClient(
            @Value("${oauth.timeout-seconds:10}") long timeoutSeconds
    ) {
        return RestClient.builder()
                .baseUrl("https://www.googleapis.com")
                .requestFactory(requestFactory(timeoutSeconds))
                .build();
    }

    private JdkClientHttpRequestFactory requestFactory(long timeoutSeconds) {
        Duration timeout = Duration.ofSeconds(Math.max(timeoutSeconds, 1));
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(timeout);
        return requestFactory;
    }
}
