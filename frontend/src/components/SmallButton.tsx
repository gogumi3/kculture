import React from 'react';
import { Image, ImageSourcePropType, Pressable, StyleSheet, Text, View } from 'react-native';
import { colors } from '../theme/colors';
import { fonts } from '../theme/typography';

type Props = {
  title: string;
  onPress?: () => void;
  active?: boolean;
  icon?: ImageSourcePropType;
};

// 목업의 .btn-sm (필터 토글, "이어하기 >" 등 작은 알약 버튼)
export default function SmallButton({ title, onPress, active, icon }: Props) {
  return (
    <Pressable onPress={onPress} style={[styles.btn, active && styles.active]}>
      <Text style={[styles.label, active && styles.activeLabel]}>{title}</Text>
      {icon ? <Image source={icon} style={[styles.icon, active && styles.activeTint]} /> : null}
    </Pressable>
  );
}

const styles = StyleSheet.create({
  btn: {
    height: 38,
    paddingHorizontal: 16,
    borderRadius: 19,
    borderWidth: 1.4,
    borderColor: colors.brown,
    backgroundColor: colors.card,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 6,
  },
  active: { backgroundColor: colors.blue, borderColor: colors.blue },
  label: { fontSize: 13, fontFamily: fonts.display, color: colors.ink },
  activeLabel: { color: colors.hanji },
  icon: { width: 12, height: 12, tintColor: colors.ink },
  activeTint: { tintColor: colors.hanji },
});
