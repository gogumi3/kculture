import { api } from './client';
import { AnalysisResponse, CulturalElementResponse } from './types';

// 한 곡에 대한 분석 이력(보통 가장 최근 것을 씀)
export function fetchAnalyses(songId: number): Promise<AnalysisResponse[]> {
  return api.get<AnalysisResponse[]>(`/songs/${songId}/analyses`);
}

export function fetchAnalysis(analysisId: number): Promise<AnalysisResponse> {
  return api.get<AnalysisResponse>(`/analyses/${analysisId}`);
}

// 분석이 DONE 상태일 때 이 안에 AI가 찾은 문화 요소들이 들어있음
export function fetchElements(analysisId: number): Promise<CulturalElementResponse[]> {
  return api.get<CulturalElementResponse[]>(`/analyses/${analysisId}/elements`);
}

// 아직 분석이 없는 곡일 때 새로 시작 (실제 AI 모델 연동은 별도 작업 필요)
export function createAnalysis(songId: number, modelName = 'external-vlm'): Promise<AnalysisResponse> {
  return api.post<AnalysisResponse>(`/songs/${songId}/analyses?modelName=${encodeURIComponent(modelName)}`);
}

// [자동 연동] 유튜브 영상을 Gemini로 분석해서 한국적 요소 + 장소 매칭까지 자동으로 만든다.
// 시간이 좀 걸릴 수 있어서 (Gemini 호출 포함), 화면에서는 로딩 표시를 따로 해주는 게 좋다.
export function autoAnalyze(songId: number): Promise<AnalysisResponse> {
  return api.post<AnalysisResponse>(`/songs/${songId}/auto-analyze`);
}
