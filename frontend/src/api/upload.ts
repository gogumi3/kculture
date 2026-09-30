import { API_BASE_URL } from './client';

// 미션 인증 사진을 백엔드(로컬 디스크)에 업로드하고, 다시 접근 가능한 URL을 돌려받는다.
// api.post(JSON 전용)와는 별도로, multipart/form-data로 직접 fetch를 호출한다.
export async function uploadPhoto(localUri: string): Promise<string> {
  const filename = localUri.split('/').pop() ?? `photo_${Date.now()}.jpg`;
  const extMatch = /\.(\w+)$/.exec(filename);
  const ext = (extMatch?.[1] ?? 'jpg').toLowerCase();
  const mimeType = ext === 'jpg' ? 'jpeg' : ext;

  const formData = new FormData();
  // React Native의 fetch는 이 { uri, name, type } 형태를 파일 업로드로 특별 취급합니다.
  formData.append('file', {
    uri: localUri,
    name: filename,
    type: `image/${mimeType}`,
  } as unknown as Blob);

  const res = await fetch(`${API_BASE_URL}/uploads`, {
    method: 'POST',
    body: formData,
    // Content-Type은 절대 직접 지정하지 않습니다 (boundary가 자동으로 붙어야 해요).
  });

  if (!res.ok) {
    const text = await res.text().catch(() => '');
    throw new Error(text || `사진 업로드 실패 (${res.status})`);
  }

  const data = (await res.json()) as { url: string };
  return data.url;
}
