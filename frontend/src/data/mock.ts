// 백엔드 연동 전까지 화면을 채우는 목업 데이터.
// 실제 연동 시 이 파일의 형태를 참고해서 API 응답을 매핑하면 됩니다.

export const popularSearches = ['대취타', 'Burning Up', 'Ditto'];

export const recentSearches = [
  { id: '1', label: 'Agust D - 대취타', query: '대취타' },
  { id: '2', label: 'MEOVV - Burning UP', query: 'Burning UP' },
  { id: '3', label: 'NewJeans - Ditto', query: 'Ditto' },
];

export const inProgressQuest = {
  title: '인천 개항장 퀘스트',
  progress: 0.4,
  stepLabel: '2단계 중 1단계 완료',
};

// 분석 화면 상단의 배지 5개. 디자인 원본에 '의례' 대신 '건축'이 두 번 들어간 오타가 있어
// 그대로 재현합니다(kind는 색상용 rite를 쓰고 label만 '건축'으로 덮어씀).
export const analysisTopBadges = [
  { kind: 'arch' as const },
  { kind: 'cloth' as const },
  { kind: 'food' as const },
  { kind: 'rite' as const, label: '건축' },
  { kind: 'prop' as const },
];

export const culturalElements = [
  { kind: 'arch' as const, label: '궁궐 건축', confidence: 96 },
  { kind: 'cloth' as const, label: '전통 어가행렬 복식', confidence: 91 },
  { kind: 'prop' as const, label: '전통 어가행렬 복식', confidence: 88 },
];

export const themeQuests = [
  { id: 'q1', title: '서울 조선시대 퀘스트', difficulty: '난이도 중', duration: '1.5시간', distance: '3.2km' },
  { id: 'q2', title: '인천 개항장 퀘스트', difficulty: '난이도 하', duration: '1시간', distance: '1.8km' },
  { id: 'q3', title: '경주 신라 퀘스트', difficulty: '난이도 중', duration: '2시간', distance: '4.5km' },
  { id: 'q4', title: '부산 바다 퀘스트', difficulty: '난이도 하', duration: '1.5시간', distance: '2.9km' },
];

export const questSteps = [
  { label: '1. 개항장', meta: '시작 지점' },
  { label: '2. 차이나타운', meta: '430m' },
  { label: '3. 신포시장', meta: '잠김' },
];

export const stampBooks = [
  { title: '인천 개항장 퀘스트', stamps: [{ color: 'red' as const }, { color: 'blue' as const }, { color: null }] },
  {
    title: '서울 조선시대 퀘스트',
    stamps: [{ color: null }, { color: null }, { color: null }, { color: null }, { color: null }],
  },
];

export const lodgings = [
  {
    id: 'l1',
    title: '개항장 한옥스테이',
    meta: '퀘스트 동선 350m · ★4.7',
    price: '10만원대',
    gradient: ['#DCC79A', '#8FA8C7'] as const,
  },
  {
    id: 'l2',
    title: '차이나타운 게스트하우스',
    meta: '퀘스트 동선 180m · ★4.5',
    price: '5만원대',
    gradient: ['#C9927E', '#9A8AAE'] as const,
  },
];
