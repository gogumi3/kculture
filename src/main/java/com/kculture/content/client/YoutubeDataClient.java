package com.kculture.content.client;

import tools.jackson.databind.JsonNode;
import com.kculture.content.client.dto.YoutubeVideoInfo;
import com.kculture.content.exception.ContentNotFoundException;
import com.kculture.common.exception.ExternalApiException;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.ResourceAccessException;

/**
 * YouTube Data API v3로 곡 메타데이터(제목/채널/썸네일/공개여부)를 가져온다.
 * 영상 자체는 절대 내려받지 않는다 — 공식 메타데이터만 조회한다.
 */
@Component
public class YoutubeDataClient {

    private final RestClient client;
    private final String apiKey;

    public YoutubeDataClient(
            RestClient youtubeRestClient,
            @Value("${youtube.api-key:}") String apiKey
    ) {
        this.client = youtubeRestClient;
        this.apiKey = apiKey;
    }

    public YoutubeVideoInfo fetch(String videoId) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ExternalApiException(
                    HttpStatus.SERVICE_UNAVAILABLE, "YouTube API 키가 설정되지 않았습니다."
            );
        }

        JsonNode resp;
        try {
            resp = client.get()
                    .uri(uri -> uri.path("/videos")
                            .queryParam("part", "snippet,status,contentDetails")
                            .queryParam("id", videoId)
                            .queryParam("key", apiKey)
                            .build())
                    .retrieve()
                    .body(JsonNode.class);
        } catch (ResourceAccessException exception) {
            throw new ExternalApiException(
                    HttpStatus.GATEWAY_TIMEOUT, "YouTube API 응답 시간이 초과되었습니다.", exception
            );
        } catch (RestClientException exception) {
            throw new ExternalApiException(
                    HttpStatus.BAD_GATEWAY, "YouTube API 호출에 실패했습니다.", exception
            );
        }

        JsonNode item = (resp == null) ? null : resp.path("items").path(0);
        if (item == null || item.isMissingNode() || item.isEmpty()) {
            throw new ContentNotFoundException("유튜브 영상을 찾을 수 없습니다: " + videoId);
        }

        JsonNode snippet = item.path("snippet");
        JsonNode status = item.path("status");

        return new YoutubeVideoInfo(
                snippet.path("title").asText(null),
                snippet.path("channelTitle").asText(null),
                snippet.path("thumbnails").path("high").path("url").asText(null),
                status.path("privacyStatus").asText(null),
                status.path("embeddable").asBoolean(false)
        );
    }
}
