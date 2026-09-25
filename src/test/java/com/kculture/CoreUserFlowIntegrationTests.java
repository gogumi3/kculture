package com.kculture;

import com.kculture.content.client.GeminiVideoClient;
import com.kculture.content.client.YoutubeDataClient;
import com.kculture.content.client.dto.YoutubeVideoInfo;
import com.kculture.content.domain.CulturalCategory;
import com.kculture.content.domain.CulturalElement;
import com.kculture.content.dto.CulturalElementRequest;
import com.kculture.content.repository.CulturalElementRepository;
import com.kculture.quest.repository.StampRepository;
import com.kculture.quest.repository.UserQuestProgressRepository;
import com.kculture.recommendation.domain.ElementPlaceMatch;
import com.kculture.recommendation.repository.ElementPlaceMatchRepository;
import com.kculture.travel.domain.Accommodation;
import com.kculture.travel.domain.Place;
import com.kculture.travel.repository.AccommodationRepository;
import com.kculture.travel.repository.PlaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CoreUserFlowIntegrationTests {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired CulturalElementRepository elementRepository;
    @Autowired PlaceRepository placeRepository;
    @Autowired AccommodationRepository accommodationRepository;
    @Autowired ElementPlaceMatchRepository matchRepository;
    @Autowired UserQuestProgressRepository progressRepository;
    @Autowired StampRepository stampRepository;

    @MockitoBean YoutubeDataClient youtubeDataClient;
    @MockitoBean GeminiVideoClient geminiVideoClient;

    @Test
    void completeUserJourneyAndAuthorizationBoundaries() throws Exception {
        when(youtubeDataClient.fetch(anyString())).thenReturn(new YoutubeVideoInfo(
                "테스트 MV", "테스트 아티스트", "https://example.com/thumb.jpg", "public", true
        ));
        when(geminiVideoClient.analyze("video-123")).thenReturn(List.of(
                new CulturalElementRequest(
                        CulturalCategory.ARCHITECTURE,
                        "한옥",
                        "영상에 등장한 전통 한옥",
                        12,
                        new BigDecimal("0.900")
                ),
                new CulturalElementRequest(
                        CulturalCategory.FOOD,
                        "한식",
                        "영상에 등장한 한국 음식",
                        28,
                        new BigDecimal("0.850")
                )
        ));

        signup("owner@example.com", "owner");
        String ownerToken = login("owner@example.com");

        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));

        MvcResult songResult = mockMvc.perform(post("/api/songs")
                        .header("Authorization", bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"youtubeVideoId\":\"video-123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.youtubeVideoId").value("video-123"))
                .andReturn();
        long songId = json(songResult).path("id").asLong();

        mockMvc.perform(get("/api/songs/search").param("keyword", "테스트"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(songId));

        MvcResult analysisResult = mockMvc.perform(post("/api/songs/{songId}/analyses/run", songId)
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isOk())
                .andReturn();
        long analysisId = json(analysisResult).path("id").asLong();
        awaitAnalysisDone(analysisId);

        MvcResult elementsResult = mockMvc.perform(get("/api/analyses/{analysisId}/elements", analysisId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("한옥"))
                .andExpect(jsonPath("$[1].name").value("한식"))
                .andReturn();
        long firstElementId = json(elementsResult).path(0).path("id").asLong();
        long secondElementId = json(elementsResult).path(1).path("id").asLong();
        CulturalElement firstElement = elementRepository.findById(firstElementId).orElseThrow();
        CulturalElement secondElement = elementRepository.findById(secondElementId).orElseThrow();

        Place firstPlace = placeRepository.save(new Place(
                "북촌 한옥마을", "서울", "종로구", "한옥",
                new BigDecimal("37.5826000"), new BigDecimal("126.9831000"),
                "kakao-1", 100, "서울 종로구"
        ));
        Place secondPlace = placeRepository.save(new Place(
                "광장시장", "서울", "종로구", "음식",
                new BigDecimal("37.5700000"), new BigDecimal("126.9990000"),
                "kakao-2", 200, "서울 종로구 창경궁로"
        ));
        matchRepository.save(new ElementPlaceMatch(
                firstElement, firstPlace, new BigDecimal("0.900"), "한옥 요소와 관련된 장소", 1,
                new BigDecimal("0.100")
        ));
        matchRepository.save(new ElementPlaceMatch(
                secondElement, secondPlace, new BigDecimal("0.850"), "한식 요소와 관련된 장소", 1,
                new BigDecimal("0.200")
        ));

        MvcResult sessionResult = mockMvc.perform(post("/api/recommendations/sessions")
                        .header("Authorization", bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"songId\":" + songId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.places.length()").value(2))
                .andReturn();
        JsonNode sessionJson = json(sessionResult);
        long sessionId = sessionJson.path("sessionId").asLong();
        long firstSessionPlaceId = sessionJson.path("places").path(0).path("id").asLong();
        long secondSessionPlaceId = sessionJson.path("places").path(1).path("id").asLong();

        mockMvc.perform(patch("/api/recommendations/session-places/{id}/choose", firstSessionPlaceId)
                        .header("Authorization", bearer(ownerToken))
                        .param("chosen", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chosen").value(true));
        mockMvc.perform(patch("/api/recommendations/session-places/{id}/choose", secondSessionPlaceId)
                        .header("Authorization", bearer(ownerToken))
                        .param("chosen", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chosen").value(true));

        String questBody = "{\"sessionId\":" + sessionId
                + ",\"title\":\"나의 한옥 퀘스트\",\"description\":\"통합 테스트\"}";
        MvcResult questResult = mockMvc.perform(post("/api/quests/from-session")
                        .header("Authorization", bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(questBody))
                .andExpect(status().isOk())
                .andReturn();
        long questId = json(questResult).path("id").asLong();

        mockMvc.perform(post("/api/quests/from-session")
                        .header("Authorization", bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(questBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(questId));

        signup("other@example.com", "other");
        String otherToken = login("other@example.com");
        mockMvc.perform(get("/api/recommendations/sessions/{id}", sessionId)
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/quests/{id}", questId)
                        .header("Authorization", bearer(otherToken)))
                .andExpect(status().isForbidden());

        MvcResult progressResult = mockMvc.perform(post("/api/quests/{id}/start", questId)
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andReturn();
        long firstStepId = json(progressResult).path("steps").path(0).path("stepId").asLong();
        long secondStepId = json(progressResult).path("steps").path(1).path("stepId").asLong();

        mockMvc.perform(post("/api/quests/{id}/start", questId)
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isOk());
        assertEquals(1, progressRepository.count());

        mockMvc.perform(get("/api/quests/{id}/progress", questId)
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.steps[0].status").value("UNLOCKED"));

        String location = "{\"latitude\":37.5826000,\"longitude\":126.9831000}";
        mockMvc.perform(post("/api/quests/{questId}/steps/{stepId}/arrival", questId, firstStepId)
                        .header("Authorization", bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.arrived").value(true));

        String completion = "{\"location\":" + location
                + ",\"photoUrl\":\"https://example.com/photo.jpg\"}";
        mockMvc.perform(post("/api/quests/{questId}/steps/{stepId}/complete", questId, firstStepId)
                        .header("Authorization", bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(completion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.steps[0].status").value("DONE"))
                .andExpect(jsonPath("$.steps[1].status").value("UNLOCKED"));

        mockMvc.perform(post("/api/quests/{questId}/steps/{stepId}/complete", questId, firstStepId)
                        .header("Authorization", bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(completion))
                .andExpect(status().isOk());
        assertEquals(1, stampRepository.count());

        String secondLocation = "{\"latitude\":37.5700000,\"longitude\":126.9990000}";
        mockMvc.perform(post("/api/quests/{questId}/steps/{stepId}/arrival", questId, secondStepId)
                        .header("Authorization", bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondLocation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.arrived").value(true));

        String secondCompletion = "{\"location\":" + secondLocation
                + ",\"photoUrl\":\"https://example.com/photo-2.jpg\"}";
        mockMvc.perform(post("/api/quests/{questId}/steps/{stepId}/complete", questId, secondStepId)
                        .header("Authorization", bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondCompletion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.steps[1].status").value("DONE"));
        mockMvc.perform(post("/api/quests/{questId}/steps/{stepId}/complete", questId, secondStepId)
                        .header("Authorization", bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondCompletion))
                .andExpect(status().isOk());
        assertEquals(2, stampRepository.count());

        mockMvc.perform(get("/api/users/me/stamps")
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/quests/{id}/progress", questId))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/recommendations/sessions/{id}", 999999L)
                        .header("Authorization", bearer(ownerToken)))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/accommodations/nearby")
                        .param("lat", "91")
                        .param("lng", "126")
                        .param("limit", "20"))
                .andExpect(status().isBadRequest());
        Accommodation accommodation = accommodationRepository.save(new Accommodation(
                "테스트 호텔", "HOTEL", "서울", "종로구",
                new BigDecimal("37.5826000"), new BigDecimal("126.9831000"),
                "서울 종로구 테스트로 1", "02-0000-0000"
        ));
        mockMvc.perform(get("/api/accommodations/nearby")
                        .param("lat", "37.5826000")
                        .param("lng", "126.9831000")
                        .param("radius", "100")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(accommodation.getId()));
        mockMvc.perform(post("/api/recommendations/sessions")
                        .header("Authorization", bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"songId\":0}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(options("/api/users/me")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    private void signup(String email, String nickname) throws Exception {
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"password123\","
                                + "\"nickname\":\"" + nickname + "\"}"))
                .andExpect(status().isCreated());
    }

    private String login(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn();
        return json(result).path("accessToken").asText();
    }

    private void awaitAnalysisDone(long analysisId) throws Exception {
        for (int attempt = 0; attempt < 50; attempt++) {
            MvcResult result = mockMvc.perform(get("/api/analyses/{id}", analysisId))
                    .andExpect(status().isOk())
                    .andReturn();
            String status = json(result).path("status").asText();
            if ("DONE".equals(status)) {
                return;
            }
            if ("FAILED".equals(status)) {
                throw new AssertionError("MV analysis failed: " + json(result).path("failReason").asText());
            }
            Thread.sleep(100);
        }
        throw new AssertionError("MV analysis did not finish within 5 seconds");
    }

    private JsonNode json(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsByteArray());
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
