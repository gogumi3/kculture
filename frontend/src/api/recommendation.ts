import { api } from './client';
import { MatchResponse, SessionResponse } from './types';

// 문화 요소 하나에 어울리는 장소 추천 목록 (장소 화면의 근거 카드용)
export function fetchMatchesForElement(elementId: number): Promise<MatchResponse[]> {
  return api.get<MatchResponse[]>(`/recommendations/elements/${elementId}/matches`);
}

export function createRecommendationSession(songId: number, userId?: number): Promise<SessionResponse> {
  return api.post<SessionResponse>('/recommendations/sessions', { songId, userId });
}

export function getRecommendationSession(sessionId: number): Promise<SessionResponse> {
  return api.get<SessionResponse>(`/recommendations/sessions/${sessionId}`);
}

// 추천 장소를 코스에 담기/빼기 (퀘스트 자동 생성 전, 전체를 chosen=true로 만들 때 사용)
export function choosePlace(sessionPlaceId: number, chosen: boolean): Promise<void> {
  return api.patch<void>(`/recommendations/session-places/${sessionPlaceId}/choose?chosen=${chosen}`);
}
