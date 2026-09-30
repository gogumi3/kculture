import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { BadgeKind, badgeColors, badgeLabels } from '../theme/colors';
import { fonts } from '../theme/typography';

type Props = {
  kind: BadgeKind;
  /** 목업처럼 라벨을 덮어써야 할 때(예: 분석 화면 상세 카드) 사용 */
  label?: string;
};

export default function Badge({ kind, label }: Props) {
  return (
    <View style={[styles.badge, { backgroundColor: badgeColors[kind] }]}>
      <Text style={styles.text}>{label ?? badgeLabels[kind]}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  badge: {
    alignSelf: 'flex-start',
    paddingHorizontal: 10,
    paddingVertical: 4,
    borderRadius: 12,
  },
  text: { color: '#fff', fontSize: 11, fontFamily: fonts.display },
});
