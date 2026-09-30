import { api } from './client';
import { QuestProgressResponse, StampResponse } from './types';

// GET /api/users/{userId}/quests
export function fetchUserQuests(userId: number): Promise<QuestProgressResponse[]> {
  return api.get<QuestProgressResponse[]>(`/users/${userId}/quests`);
}

// GET /api/users/{userId}/stamps
export function fetchUserStamps(userId: number): Promise<StampResponse[]> {
  return api.get<StampResponse[]>(`/users/${userId}/stamps`);
}
