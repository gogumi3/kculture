package com.kculture.content.client;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.kculture.content.dto.CulturalElementRequest;
import com.kculture.common.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Gemini에 "공개 YouTube URL"을 그대로 전달해 영상 속 한국 문화 요소를 추출한다.
 * 영상을 다운로드하지 않고 URL만 넘기므로 약관 위반이 없다(공개 영상 한정).
 * 구조화 출력(responseSchema)으로 곧바로 CulturalElementRequest 목록에 매핑한다.
 */
@Component
public class GeminiVideoClient {

    private static final List<String> CATEGORIES =
            List.of("ARCHITECTURE", "COSTUME", "FOOD", "ART_RITUAL", "PROP");

    private static final String PROMPT = """
            이 뮤직비디오(K-pop 공식 MV)에 실제로 화면에 보이는 '한국 문화 요소'만 식별하세요.
            아래 5개 카테고리로만 분류합니다:
            - ARCHITECTURE(건축: 고궁, 한옥, 전통 건물 등)
            - COSTUME(의상: 한복, 전통 장신구 등)
            - FOOD(음식: 전통 음식, 식기 등)
            - ART_RITUAL(예술/의례: 탈춤, 전통 놀이, 제례 등)
            - PROP(소품: 부채, 전통 악기 등)
            각 요소마다 등장 시점(timestampSec, 초 단위 정수), 짧은 한국어 설명(description),
            신뢰도(confidence, 0.0~1.0)를 함께 제시하세요.
            추측·환각은 금지하며, 화면에서 확실히 보이는 것만 포함합니다. JSON 스키마에 맞춰 배열로만 응답하세요.
            """;

    private final RestClient client;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;
    private final BigDecimal minConfidence;

    public GeminiVideoClient(
            RestClient geminiRestClient,
            ObjectMapper objectMapper,
            @Value("${gemini.api-key:}") String apiKey,
            @Value("${gemini.model:gemini-2.5-flash}") String model,
            @Value("${gemini.min-confidence:0.5}") BigDecimal minConfidence
    ) {
        this.client = geminiRestClient;
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.minConfidence = minConfidence;
    }

    public List<CulturalElementRequest> analyze(String videoId) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ExternalApiException(
                    HttpStatus.SERVICE_UNAVAILABLE, "Gemini API 키가 설정되지 않았습니다."
            );
        }
        String videoUrl = "https://www.youtube.com/watch?v=" + videoId;
        Map<String, Object> body = buildRequestBody(videoUrl);

        JsonNode resp;
        try {
            resp = client.post()
                    .uri("/v1beta/models/{model}:generateContent?key={key}", model, apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (ResourceAccessException exception) {
            throw new ExternalApiException(
                    HttpStatus.GATEWAY_TIMEOUT, "Gemini API 응답 시간이 초과되었습니다.", exception
            );
        } catch (RestClientException exception) {
            throw new ExternalApiException(
                    HttpStatus.BAD_GATEWAY, "Gemini API 호출에 실패했습니다.", exception
            );
        }

        String text = (resp == null) ? "" : resp
                .path("candidates").path(0)
                .path("content").path("parts").path(0)
                .path("text").asText("");

        if (text.isBlank()) {
            return List.of();
        }

        List<CulturalElementRequest> elements = parse(text);
        // 신뢰도 임계값 미만은 환각 억제를 위해 제외한다.
        return elements.stream()
                .filter(e -> e.confidence() == null || e.confidence().compareTo(minConfidence) >= 0)
                .toList();
    }

    private List<CulturalElementRequest> parse(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<CulturalElementRequest>>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Gemini 응답 파싱 실패: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> buildRequestBody(String videoUrl) {
        Map<String, Object> itemSchema = Map.of(
                "type", "OBJECT",
                "properties", Map.of(
                        "category", Map.of("type", "STRING", "enum", CATEGORIES),
                        "name", Map.of("type", "STRING"),
                        "description", Map.of("type", "STRING"),
                        "timestampSec", Map.of("type", "INTEGER"),
                        "confidence", Map.of("type", "NUMBER")
                ),
                "required", List.of("category", "name", "timestampSec", "confidence")
        );

        Map<String, Object> responseSchema = Map.of(
                "type", "ARRAY",
                "items", itemSchema
        );

        Map<String, Object> content = Map.of(
                "parts", List.of(
                        Map.of("fileData", Map.of("fileUri", videoUrl)),
                        Map.of("text", PROMPT)
                )
        );

        return Map.of(
                "contents", List.of(content),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "responseSchema", responseSchema
                )
        );
    }
}
