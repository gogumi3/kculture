import React from 'react';
import { StyleSheet, View } from 'react-native';
import { colors } from '../theme/colors';

type Props = { progress: number }; // 0~1

export default function ProgressBar({ progress }: Props) {
  const pct = Math.max(0, Math.min(1, progress)) * 100;
  return (
    <View style={styles.track}>
      <View style={[styles.fill, { width: `${pct}%` }]} />
    </View>
  );
}

const styles = StyleSheet.create({
  track: { height: 8, borderRadius: 8, backgroundColor: colors.brownSoft, overflow: 'hidden' },
  fill: { height: '100%', backgroundColor: colors.red, borderRadius: 8 },
});
