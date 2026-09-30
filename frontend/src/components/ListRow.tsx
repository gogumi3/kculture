import React from 'react';
import { Image, ImageSourcePropType, Pressable, StyleSheet, Text, View } from 'react-native';
import { colors } from '../theme/colors';
import { fonts } from '../theme/typography';

type Props = {
  icon?: ImageSourcePropType;
  label: string;
  right?: React.ReactNode;
  onPress?: () => void;
  last?: boolean;
};

export default function ListRow({ icon, label, right, onPress, last }: Props) {
  const Wrapper = onPress ? Pressable : View;
  return (
    <Wrapper onPress={onPress} style={[styles.row, !last && styles.divider]}>
      {icon ? <Image source={icon} style={styles.icon} /> : null}
      <Text style={styles.label}>{label}</Text>
      <View style={styles.spacer} />
      {right}
    </Wrapper>
  );
}

const styles = StyleSheet.create({
  row: { flexDirection: 'row', alignItems: 'center', gap: 12, paddingVertical: 12 },
  divider: { borderBottomWidth: 1, borderBottomColor: colors.brownSoft },
  icon: { width: 16, height: 16, tintColor: colors.inkSoft },
  label: { fontSize: 14, color: colors.ink, fontFamily: fonts.display },
  spacer: { flex: 1 },
});
