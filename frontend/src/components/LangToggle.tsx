import React, { useState } from 'react';
import { Pressable, StyleSheet, Text, View, ViewStyle } from 'react-native';
import { colors } from '../theme/colors';
import { fonts } from '../theme/typography';

type Props = { style?: ViewStyle };

export default function LangToggle({ style }: Props) {
  const [lang, setLang] = useState<'ko' | 'en'>('ko');
  return (
    <View style={[styles.wrap, style]}>
      <Pressable onPress={() => setLang('ko')} style={[styles.item, lang === 'ko' && styles.active]}>
        <Text style={[styles.label, lang === 'ko' && styles.activeLabel]}>한국어</Text>
      </Pressable>
      <Pressable onPress={() => setLang('en')} style={[styles.item, lang === 'en' && styles.active]}>
        <Text style={[styles.label, lang === 'en' && styles.activeLabel]}>ENG</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: {
    flexDirection: 'row',
    backgroundColor: colors.card,
    borderWidth: 1,
    borderColor: colors.brownSoft,
    borderRadius: 16,
    overflow: 'hidden',
  },
  item: { paddingVertical: 6, paddingHorizontal: 11 },
  active: { backgroundColor: colors.blue },
  label: { fontSize: 11.5, color: colors.inkSoft, fontFamily: fonts.display },
  activeLabel: { color: colors.hanji },
});
