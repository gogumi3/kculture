package com.kculture.content.client;

import com.kculture.common.exception.ExternalApiException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExternalApiClientConfigurationTest {

    @Test
    void missingYoutubeKeyReturnsServiceUnavailableBeforeNetworkCall() {
        YoutubeDataClient client = new YoutubeDataClient(RestClient.create(), "");

        ExternalApiException exception = assertThrows(
                ExternalApiException.class, () -> client.fetch("video-id")
        );

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    void missingGeminiKeyReturnsServiceUnavailableBeforeNetworkCall() {
        GeminiVideoClient client = new GeminiVideoClient(
                RestClient.create(), new ObjectMapper(), "", "gemini-test", new BigDecimal("0.5")
        );

        ExternalApiException exception = assertThrows(
                ExternalApiException.class, () -> client.analyze("video-id")
        );

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }
}
