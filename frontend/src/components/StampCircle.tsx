import React from 'react';
import { Image, StyleSheet, View } from 'react-native';
import { colors } from '../theme/colors';
import { icons } from '../theme/assets';

type Props = {
  /** 채워진 스탬프의 색 (red | blue). 비어있으면 undefined */
  filledColor?: string;
  size?: number;
};

// 채워진 색상에 맞는 진한 테두리 색 (빨강 스탬프는 진한 빨강, 파랑 스탬프는 진한 파랑 테두리)
const DEEP_BORDER: Record<string, string> = {
  [colors.red]: colors.redDeep,
  [colors.blue]: colors.blueDeep,
};

export default function StampCircle({ filledColor, size = 56 }: Props) {
  const filled = !!filledColor;
  const borderColor = filled ? DEEP_BORDER[filledColor as string] ?? colors.redDeep : colors.brownSoft;
  return (
    <View
      style={[
        styles.base,
        { width: size, height: size, borderRadius: size / 2 },
        filled
          ? { backgroundColor: filledColor, borderWidth: 2, borderColor }
          : { borderWidth: 2, borderColor, borderStyle: 'dashed' },
      ]}
    >
      <Image
        source={filled ? icons.stampCream : icons.stamp}
        style={{ width: size * 0.32, height: size * 0.32, tintColor: filled ? colors.card : colors.inkSoft }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  base: { alignItems: 'center', justifyContent: 'center' },
});
