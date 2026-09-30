package com.kculture.content.ai;

// [Claude 작성 - 신규 파일]
// Gemini API에 유튜브 영상 URL을 직접 넘겨서, 영상 속 "한국적 요소"(건축/의상/음식/의례/소품)를
// 구조화된 JSON으로 추출해오는 클라이언트입니다. 기존 파일은 전혀 수정하지 않았습니다.

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kculture.content.domain.CulturalCategory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class GeminiVisionClient {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.gemini.api-key:}")
    private String apiKey;

    // 필요하면 application.properties에 app.gemini.model=... 을 추가해 모델을 바꿀 수 있다.
    // [Claude 수정] 원래: @Value("${app.gemini.model:gemini-2.5-flash}")
    // gemini-2.5-flash가 2026-09-29부로 신규 사용자에게 제공 중단(404)되어,
    // 구글 API가 안내한 gemini-3.8-flash로 기본값을 바꿨습니다.
    // application.properties의 app.gemini.model 값이 있으면 그게 우선 적용됩니다.
    @Value("${app.gemini.model:gemini-3.8-flash}")
    private String model;

    private static final String PROMPT = """
            너는 K-pop 뮤직비디오에서 "한국적인 문화 요소"를 찾아내는 분석가야.
            이 유튜브 영상을 보고, 영상에 등장하는 한국적 요소를 최대 5개까지 찾아서
            아래 JSON 스키마에 맞는 JSON 배열로만 답해. 다른 설명은 절대 붙이지 마.

            각 요소는 다음 5개 카테고리 중 하나여야 해:
            - ARCHITECTURE (건축: 고궁, 한옥, 사찰 등 건축물)
            - COSTUME (의상: 한복, 전통 복식)
            - FOOD (음식: 전통 음식, 시장 음식)
            - ART_RITUAL (예술/의례: 전통 무용, 제례, 국악, 민속놀이)
            - PROP (소품: 전통 공예품, 부채, 도자기 등 소품)

            각 요소에 대해:
            - category: 위 5개 중 하나
            - name: 요소 이름 (예: "경복궁 스타일 고궁", "전통 한복")
            - description: 1~2문장 설명
            - timestampSec: 영상에서 처음 등장한 시점(초 단위 정수, 모르면 0)
            - confidence: 0.0~1.0 사이 신뢰도
            """;

    public record ElementDraft(
            CulturalCategory category, String name, String description,
            Integer timestampSec, BigDecimal confidence
    ) {
    }

    // 유튜브 영상 URL을 넘겨서 한국적 요소 목록을 받아온다. 실패 시 예외를 던진다.
    public List<ElementDraft> analyzeVideo(String youtubeUrl) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY가 설정되어 있지 않습니다.");
        }
        try {
            ObjectNode body = objectMapper.createObjectNode();
            ArrayNode contents = body.putArray("contents");
            ObjectNode content = contents.addObject();
            ArrayNode parts = content.putArray("parts");

            ObjectNode fileDataPart = parts.addObject();
            ObjectNode fileData = fileDataPart.putObject("file_data");
            fileData.put("file_uri", youtubeUrl);

            parts.addObject().put("text", PROMPT);

            ObjectNode generationConfig = body.putObject("generationConfig");
            generationConfig.put("responseMimeType", "application/json");
            generationConfig.set("responseSchema", buildResponseSchema());

            String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                    + model + ":generateContent?key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            objectMapper.writeValueAsString(body), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalStateException(
                        "Gemini 분석 실패 (status=" + response.statusCode() + "): " + response.body());
            }

            JsonNode root = objectMapper.readTree(response.body());
            String text = root.path("candidates").path(0).path("content")
                    .path("parts").path(0).path("text").asText(null);
            if (text == null || text.isBlank()) {
                throw new IllegalStateException("Gemini 응답에서 분석 결과를 찾지 못했습니다: " + response.body());
            }

            return parseElements(text);
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException("Gemini 호출 중 오류: " + e.getMessage(), e);
        }
    }

    private ObjectNode buildResponseSchema() {
        ObjectNode itemSchema = objectMapper.createObjectNode();
        itemSchema.put("type", "OBJECT");
        ObjectNode properties = itemSchema.putObject("properties");

        ObjectNode categoryProp = properties.putObject("category");
        categoryProp.put("type", "STRING");
        ArrayNode categoryEnum = categoryProp.putArray("enum");
        for (CulturalCategory c : CulturalCategory.values()) {
            categoryEnum.add(c.name());
        }

        properties.putObject("name").put("type", "STRING");
        properties.putObject("description").put("type", "STRING");
        properties.putObject("timestampSec").put("type", "INTEGER");
        properties.putObject("confidence").put("type", "NUMBER");

        ArrayNode required = itemSchema.putArray("required");
        required.add("category").add("name").add("description").add("timestampSec").add("confidence");

        ObjectNode arraySchema = objectMapper.createObjectNode();
        arraySchema.put("type", "ARRAY");
        arraySchema.set("items", itemSchema);
        return arraySchema;
    }

    private List<ElementDraft> parseElements(String jsonText) throws IOException {
        JsonNode array = objectMapper.readTree(jsonText);
        List<ElementDraft> drafts = new ArrayList<>();
        if (!array.isArray()) {
            return drafts;
        }
        for (JsonNode item : array) {
            try {
                CulturalCategory category = CulturalCategory.valueOf(item.path("category").asText());
                String name = item.path("name").asText();
                if (name == null || name.isBlank()) {
                    continue;
                }
                String description = item.path("description").asText(null);
                Integer timestampSec = item.path("timestampSec").isMissingNode()
                        ? 0 : Math.max(0, item.path("timestampSec").asInt(0));
                BigDecimal confidence = clampConfidence(item.path("confidence").asDouble(0.7));
                drafts.add(new ElementDraft(category, name.strip(), description, timestampSec, confidence));
            } catch (IllegalArgumentException e) {
                // 카테고리 값이 이상하면(스키마를 어긴 경우) 그 항목만 건너뛴다.
            }
        }
        return drafts;
    }

    private BigDecimal clampConfidence(double value) {
        double clamped = Math.max(0.0, Math.min(1.0, value));
        return BigDecimal.valueOf(Math.round(clamped * 1000) / 1000.0);
    }
}
