import React from 'react';
import { ImageBackground, StyleSheet, View, ViewStyle } from 'react-native';
import { SafeAreaView, Edge } from 'react-native-safe-area-context';
import { colors } from '../theme/colors';
import { images } from '../theme/assets';

type Props = {
  children: React.ReactNode;
  /** 하단 탭바가 있는 화면은 bottom 세이프에어리어를 탭바 컴포넌트가 처리하므로 false로 둡니다. */
  edges?: Edge[];
  contentStyle?: ViewStyle;
  /** 지도 화면(GPS)처럼 좌우 패딩 없이 꽉 채워야 할 때 true */
  noPadding?: boolean;
};

// 한지 질감 배경 + 세이프에어리어를 깔아주는 공통 화면 래퍼
export default function ScreenContainer({ children, edges = ['top', 'left', 'right'], contentStyle, noPadding }: Props) {
  return (
    <ImageBackground source={images.hanjiTexture} resizeMode="cover" style={styles.bg}>
      <SafeAreaView style={styles.safe} edges={edges}>
        <View style={[noPadding ? styles.noPad : styles.content, contentStyle]}>{children}</View>
      </SafeAreaView>
    </ImageBackground>
  );
}

const styles = StyleSheet.create({
  bg: { flex: 1, backgroundColor: colors.hanji },
  safe: { flex: 1 },
  content: { flex: 1, paddingHorizontal: 22, paddingTop: 26, paddingBottom: 22, gap: 18 },
  noPad: { flex: 1 },
});
