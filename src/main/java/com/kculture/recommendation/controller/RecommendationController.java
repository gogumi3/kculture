package com.kculture.recommendation.controller;

import com.kculture.recommendation.dto.CreateSessionRequest;
import com.kculture.recommendation.dto.MatchResponse;
import com.kculture.recommendation.dto.SessionPlaceResponse;
import com.kculture.recommendation.dto.SessionResponse;
import com.kculture.recommendation.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    // 곡 검색 → 추천 세션 생성 (지역 분산된 순서 장소들)
    @PostMapping("/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public SessionResponse createSession(@AuthenticationPrincipal Long userId,
                                         @Valid @RequestBody CreateSessionRequest request) {
        return recommendationService.createSession(request.songId(), userId);
    }

    // 세션 조회 (S04 순차 노출용)
    @GetMapping("/sessions/{id}")
    public SessionResponse getSession(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return recommendationService.getSession(id, userId);
    }

    // 세션 상태 전환 (예: 퀘스트 전환 CONVERTED)
    @PatchMapping("/sessions/{id}/status")
    public SessionResponse updateStatus(@AuthenticationPrincipal Long userId,
                                        @PathVariable Long id,
                                        @RequestParam String value) {
        return recommendationService.updateStatus(id, value, userId);
    }

    // 추천 장소 노출 시점 기록
    @PatchMapping("/session-places/{id}/shown")
    public SessionPlaceResponse markShown(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return recommendationService.markShown(id, userId);
    }

    // 코스에 담기/빼기
    @PatchMapping("/session-places/{id}/choose")
    public SessionPlaceResponse choose(@AuthenticationPrincipal Long userId,
                                       @PathVariable Long id,
                                       @RequestParam(defaultValue = "true") boolean chosen) {
        return recommendationService.choosePlace(id, chosen, userId);
    }

    // 요소별 추천 장소 목록
    @GetMapping("/elements/{elementId}/matches")
    public List<MatchResponse> getMatches(@PathVariable Long elementId) {
        return recommendationService.getMatchesForElement(elementId);
    }
}
