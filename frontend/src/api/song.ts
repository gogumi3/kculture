import { api } from './client';
import { SongResponse } from './types';

export function searchSongs(keyword: string): Promise<SongResponse[]> {
  return api.get<SongResponse[]>(`/songs/search?keyword=${encodeURIComponent(keyword)}`);
}

export function fetchSongs(): Promise<SongResponse[]> {
  return api.get<SongResponse[]>('/songs');
}

export function fetchSong(id: number): Promise<SongResponse> {
  return api.get<SongResponse>(`/songs/${id}`);
}

// [자동 연동] 로컬 DB 검색 결과가 없을 때, 유튜브에서 곡을 찾아 새로 등록한다.
export function autoImportSong(query: string): Promise<SongResponse> {
  return api.post<SongResponse>(`/songs/auto-import?query=${encodeURIComponent(query)}`);
}
