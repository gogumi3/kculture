import React from 'react';
import { Pressable, StyleSheet, Text } from 'react-native';
import { colors } from '../theme/colors';
import { fonts } from '../theme/typography';

type Props = {
  title: string;
  onPress?: () => void;
};

export default function OutlineButton({ title, onPress }: Props) {
  return (
    <Pressable onPress={onPress} style={({ pressed }) => [styles.btn, pressed && styles.pressed]}>
      <Text style={styles.label}>{title}</Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  btn: {
    height: 54,
    borderRadius: 27,
    backgroundColor: 'transparent',
    borderWidth: 1.6,
    borderColor: colors.brown,
    alignItems: 'center',
    justifyContent: 'center',
    width: '100%',
  },
  pressed: { backgroundColor: 'rgba(185,154,108,0.12)' },
  label: { color: colors.ink, fontSize: 16, fontFamily: fonts.display },
});
