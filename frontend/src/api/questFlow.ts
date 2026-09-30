import { api } from './client';
import { ArrivalResponse, QuestProgressResponse } from './types';

// 퀘스트를 시작한다 (이미 시작했으면 기존 진행 상황을 그대로 돌려줌 - 여러 번 호출해도 안전)
export function startQuest(questId: number, userId: number): Promise<QuestProgressResponse> {
  return api.post<QuestProgressResponse>(`/quests/${questId}/start?userId=${userId}`);
}

export function fetchQuestProgress(questId: number, userId: number): Promise<QuestProgressResponse> {
  return api.get<QuestProgressResponse>(`/quests/${questId}/progress?userId=${userId}`);
}

// 현재 위치가 이 단계 장소의 도착 반경 안인지 확인한다.
export function checkArrival(
  questId: number,
  stepId: number,
  userId: number,
  latitude: number,
  longitude: number
): Promise<ArrivalResponse> {
  return api.post<ArrivalResponse>(
    `/quests/${questId}/steps/${stepId}/arrival?userId=${userId}`,
    { latitude, longitude }
  );
}

// 사진(또는 퀴즈 정답)으로 미션을 완료한다. 완료되면 스탬프가 자동으로 찍히고 다음 단계가 열린다.
export function completeMission(
  questId: number,
  stepId: number,
  userId: number,
  payload: { latitude: number; longitude: number; answer?: string; photoUrl?: string }
): Promise<QuestProgressResponse> {
  return api.post<QuestProgressResponse>(
    `/quests/${questId}/steps/${stepId}/complete?userId=${userId}`,
    {
      location: { latitude: payload.latitude, longitude: payload.longitude },
      answer: payload.answer,
      photoUrl: payload.photoUrl,
    }
  );
}
