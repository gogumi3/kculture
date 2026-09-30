// 피그마 최종본에서 받은 단일 폰트. App.tsx에서 expo-font로 로드합니다.
export const fonts = {
  display: 'ChosunCentennial',
} as const;

export const textStyles = {
  h1: { fontFamily: fonts.display, fontSize: 24, fontWeight: '700' as const, color: '#4A3E2E' },
  h2: { fontFamily: fonts.display, fontSize: 17, fontWeight: '700' as const, color: '#4A3E2E' },
  title: { fontFamily: fonts.display, fontSize: 19, fontWeight: '700' as const, color: '#4A3E2E' },
  body: { fontFamily: fonts.display, fontSize: 14, color: '#4A3E2E' },
  bodySoft: { fontFamily: fonts.display, fontSize: 13, color: '#7A6A4E' },
  caption: { fontFamily: fonts.display, fontSize: 11.5, color: '#7A6A4E' },
  button: { fontFamily: fonts.display, fontSize: 16, fontWeight: '700' as const },
};
