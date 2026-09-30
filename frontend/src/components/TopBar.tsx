import React from 'react';
import { Image, Pressable, StyleSheet, Text, View } from 'react-native';
import { colors } from '../theme/colors';
import { fonts } from '../theme/typography';
import { icons } from '../theme/assets';

type Props = {
  title: string;
  sup?: string;
  onBack?: () => void;
};

export default function TopBar({ title, sup, onBack }: Props) {
  return (
    <View style={styles.row}>
      {onBack ? (
        <Pressable onPress={onBack} style={styles.back}>
          <Image source={icons.back} style={styles.backIcon} />
        </Pressable>
      ) : null}
      <View>
        {sup ? <Text style={styles.sup}>{sup}</Text> : null}
        <Text style={styles.title}>{title}</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  row: { flexDirection: 'row', alignItems: 'center', gap: 12, minHeight: 32 },
  back: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: colors.card,
    borderWidth: 1,
    borderColor: colors.brownSoft,
    alignItems: 'center',
    justifyContent: 'center',
  },
  backIcon: { width: 16, height: 16, tintColor: colors.ink },
  sup: { fontSize: 12, color: colors.inkSoft, fontFamily: fonts.display, marginBottom: 1 },
  title: { fontSize: 17, color: colors.ink, fontFamily: fonts.display },
});
