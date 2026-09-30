import React from 'react';
import { Image, ImageBackground, StyleSheet, View, ViewStyle } from 'react-native';
import { images } from '../theme/assets';

export type Pin = { leftPct: number; topPct: number; size?: number };

type Props = {
  pins?: Pin[];
  style?: ViewStyle;
  children?: React.ReactNode;
};

// 실제 카카오맵이 붙기 전까지 쓰는 자리표시자. 목업의 .mapcard와 동일한 배경(bg_mapcard) 사용.
export default function MapPlaceholder({ pins = [], style, children }: Props) {
  return (
    <ImageBackground source={images.mapcard} resizeMode="cover" style={[styles.map, style]} imageStyle={styles.mapImg}>
      {pins.map((p, i) => (
        <Image
          key={i}
          source={images.pinTaegeuk}
          style={[
            styles.pin,
            {
              width: p.size ?? 46,
              height: p.size ?? 46,
              left: `${p.leftPct}%`,
              top: `${p.topPct}%`,
              marginLeft: -(p.size ?? 46) / 2,
              marginTop: -(p.size ?? 46),
            },
          ]}
          resizeMode="contain"
        />
      ))}
      {children}
    </ImageBackground>
  );
}

const styles = StyleSheet.create({
  map: { borderRadius: 22, overflow: 'hidden', position: 'relative' },
  mapImg: { borderRadius: 22 },
  pin: { position: 'absolute' },
});
