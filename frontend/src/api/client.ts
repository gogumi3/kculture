import { Platform } from 'react-native';

// 백엔드(Spring Boot)가 로컬 8080 포트에서 떠 있다고 가정합니다.
// - 안드로이드 에뮬레이터에서는 10.0.2.2가 "내 컴퓨터의 localhost"를 가리키는 특수 주소예요.
// - iOS 시뮬레이터는 실제 localhost를 그대로 씁니다.
// - 실제 폰(Expo Go 앱)으로 테스트하려면 아래 주소를 컴퓨터의 LAN IP로 바꿔주세요 (예: '192.168.0.12').
const DEV_HOST = Platform.select({ android: '10.0.2.2', default: 'localhost' });

export const API_BASE_URL = `http://${DEV_HOST}:8080/api`;

export class ApiError extends Error {
  status: number;
  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const res = await fetch(`${API_BASE_URL}${path}`, {
    headers: { 'Content-Type': 'application/json', ...(options.headers ?? {}) },
    ...options,
  });

  if (!res.ok) {
    const body = await res.text().catch(() => '');
    // 백엔드 에러 응답은 대부분 { status, message } 형태의 JSON이라, 있으면 message만 뽑아서 보여줘요.
    // (JSON이 아니거나 message가 없으면 원래 텍스트를 그대로 써요.)
    let friendly = body;
    try {
      const parsed = JSON.parse(body);
      if (parsed && typeof parsed.message === 'string' && parsed.message.trim()) {
        friendly = parsed.message;
      }
    } catch {
      // JSON이 아니면 그냥 원문 사용
    }
    throw new ApiError(res.status, friendly || `요청 실패 (${res.status})`);
  }

  // 204 No Content 같은 빈 응답 대비
  const text = await res.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

export const api = {
  get: <T>(path: string) => request<T>(path, { method: 'GET' }),
  post: <T>(path: string, body?: unknown) =>
    request<T>(path, { method: 'POST', body: body !== undefined ? JSON.stringify(body) : undefined }),
  patch: <T>(path: string, body?: unknown) =>
    request<T>(path, { method: 'PATCH', body: body !== undefined ? JSON.stringify(body) : undefined }),
};
