import { api } from './client';
import { UserResponse, UserUpdateRequest } from './types';

export function fetchUser(id: number): Promise<UserResponse> {
  return api.get<UserResponse>(`/users/${id}`);
}

export function updateUser(id: number, patch: UserUpdateRequest): Promise<UserResponse> {
  return api.patch<UserResponse>(`/users/${id}`, patch);
}

// 로그인/회원가입 폼은 아직 디자인에 없어서 함수만 준비해뒀어요 (화면에서는 아직 안 씀).
export function login(email: string, password: string): Promise<UserResponse> {
  return api.post<UserResponse>('/auth/login', { email, password });
}

export function signup(input: {
  email: string;
  password: string;
  nickname: string;
  nationality?: string;
  languagePref?: string;
}): Promise<UserResponse> {
  return api.post<UserResponse>('/users/signup', input);
}
