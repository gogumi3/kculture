import { api } from './client';
import { QuestDetailResponse, QuestResponse } from './types';

// GET /api/quests
export function fetchQuests(): Promise<QuestResponse[]> {
  return api.get<QuestResponse[]>('/quests');
}

// GET /api/quests/{questId}
export function fetchQuestDetail(questId: number): Promise<QuestDetailResponse> {
  return api.get<QuestDetailResponse>(`/quests/${questId}`);
}

// 추천 세션에서 선택(chosen)된 장소들로 실제 퀘스트를 만든다.
export function createQuestFromSession(input: {
  sessionId: number;
  userId: number;
  title: string;
  description?: string;
}): Promise<QuestResponse> {
  return api.post<QuestResponse>('/quests/from-session', input);
}
