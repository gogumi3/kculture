// K-컬처 리얼맵 디자인 토큰 (원색 버전)
// 피그마 색상 코드와 1:1로 맞춰뒀어요. 값을 바꿀 땐 여기만 고치면 앱 전체에 반영됩니다.
export const colors = {
  hanji: '#F6EFDD',
  hanjiDeep: '#ECE0C4',
  brown: '#B99A6C',
  brownSoft: '#DDCDA9',
  ink: '#4A3E2E',
  inkSoft: '#7A6A4E',
  card: '#FBF6EA',
  red: '#CD2E3A',
  redDeep: '#A8232E',
  blue: '#0047A0',
  blueDeep: '#003572',
  amber: '#C79A4B',
  green: '#7C9473',
  canvas: '#221D17',
  canvasLine: '#3A332A',
  label: '#C9BBA0',
  rite: '#9A8AAE', // 의례 배지 (아직 별도 변수명이 없던 색)
} as const;

export type BadgeKind = 'arch' | 'cloth' | 'food' | 'rite' | 'prop';

export const badgeColors: Record<BadgeKind, string> = {
  arch: colors.blue,
  cloth: colors.red,
  food: colors.amber,
  rite: colors.rite,
  prop: colors.green,
};

export const badgeLabels: Record<BadgeKind, string> = {
  arch: '건축',
  cloth: '의상',
  food: '음식',
  rite: '의례',
  prop: '소품',
};
