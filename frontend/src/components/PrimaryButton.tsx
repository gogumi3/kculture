import React from 'react';
import { Pressable, StyleSheet, Text } from 'react-native';
import { colors } from '../theme/colors';
import { fonts } from '../theme/typography';

type Props = {
  title: string;
  onPress?: () => void;
  disabled?: boolean;
};

export default function PrimaryButton({ title, onPress, disabled }: Props) {
  return (
    <Pressable
      onPress={onPress}
      disabled={disabled}
      style={({ pressed }) => [styles.btn, pressed && styles.pressed, disabled && styles.disabled]}
    >
      <Text style={styles.label}>{title}</Text>
    </Pressable>
  );
}

const styles = StyleSheet.create({
  btn: {
    height: 54,
    borderRadius: 27,
    backgroundColor: colors.red,
    alignItems: 'center',
    justifyContent: 'center',
    width: '100%',
  },
  pressed: { backgroundColor: colors.redDeep },
  disabled: { opacity: 0.5 },
  label: { color: colors.hanji, fontSize: 16, fontFamily: fonts.display, letterSpacing: 0.2 },
});
