package com.kculture.content.ai;

// [Claude 작성 - 신규 파일]
// 유튜브 Data API v3로 검색어(곡 제목/가수 등)에 맞는 뮤직비디오 1건을 찾는 클라이언트입니다.
// 기존 파일은 전혀 수정하지 않았고, 이 클래스는 AutoAnalysisService에서만 사용됩니다.

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
public class YoutubeSearchClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // application.properties의 app.youtube.api-key 값을 주입받는다.
    @Value("${app.youtube.api-key:}")
    private String apiKey;

    public record YoutubeVideo(String videoId, String title, String channelTitle, String thumbnailUrl) {
    }

    // 검색어로 가장 관련도 높은 (음악 카테고리) 영상 1건을 찾는다. 결과가 없으면 null.
    public YoutubeVideo searchTopVideo(String query) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("YOUTUBE_API_KEY가 설정되어 있지 않습니다.");
        }
        try {
            String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://www.googleapis.com/youtube/v3/search"
                    + "?part=snippet&type=video&maxResults=1&videoCategoryId=10"
                    + "&q=" + encoded + "&key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException(
                        "유튜브 검색 실패 (status=" + response.statusCode() + "): " + response.body());
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode items = root.path("items");
            if (!items.isArray() || items.isEmpty()) {
                return null;
            }

            JsonNode first = items.get(0);
            String videoId = first.path("id").path("videoId").asText(null);
            if (videoId == null || videoId.isBlank()) {
                return null;
            }
            JsonNode snippet = first.path("snippet");
            String title = snippet.path("title").asText(query);
            String channelTitle = snippet.path("channelTitle").asText(null);
            String thumbnailUrl = snippet.path("thumbnails").path("high").path("url")
                    .asText(snippet.path("thumbnails").path("default").path("url").asText(null));

            return new YoutubeVideo(videoId, title, channelTitle, thumbnailUrl);
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException("유튜브 검색 중 오류: " + e.getMessage(), e);
        }
    }
}
