import React, { useEffect, useRef, useState } from 'react';
import { ActivityIndicator, StyleSheet, Text, View } from 'react-native';
import * as Location from 'expo-location';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import ScreenContainer from '../components/ScreenContainer';
import TopBar from '../components/TopBar';
import Card from '../components/Card';
import MapPlaceholder from '../components/MapPlaceholder';
import { colors } from '../theme/colors';
import { fonts } from '../theme/typography';
import { QuestFlowParamList } from '../navigation/types';
import { checkArrival } from '../api/questFlow';
import { useSession } from '../state/SessionContext';
import { DEV_USER_ID } from '../api/devUser';

type Props = NativeStackScreenProps<QuestFlowParamList, 'QuestGps'>;

export default function QuestGpsScreen({ navigation, route }: Props) {
  const params = route.params;
  const { user } = useSession();
  const userId = user?.id ?? DEV_USER_ID;

  const [permissionError, setPermissionError] = useState<string | null>(null);
  const [distance, setDistance] = useState<number | null>(null);
  const [checking, setChecking] = useState(false);
  const arrivedRef = useRef(false);

  // GPS 화면은 지도를 꽉 채워야 해서 하단 탭바를 잠깐 숨깁니다.
  useEffect(() => {
    const parent = navigation.getParent();
    parent?.setOptions({ tabBarStyle: { display: 'none' } });
    return () => parent?.setOptions({ tabBarStyle: undefined });
  }, [navigation]);

  // 실제 퀘스트 진행 중일 때만 위치를 계속 감시해서 도착 여부를 확인한다.
  useEffect(() => {
    if (!params) return; // 목업 모드에서는 위치 감시 안 함
    let subscription: Location.LocationSubscription | null = null;
    let cancelled = false;

    (async () => {
      const { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== 'granted') {
        setPermissionError('위치 권한을 허용해야 퀘스트를 진행할 수 있어요.');
        return;
      }

      subscription = await Location.watchPositionAsync(
        { accuracy: Location.Accuracy.High, timeInterval: 4000, distanceInterval: 5 },
        async (position) => {
          if (cancelled || arrivedRef.current || checking) return;
          setChecking(true);
          try {
            const result = await checkArrival(
              params.questId,
              params.stepId,
              userId,
              position.coords.latitude,
              position.coords.longitude
            );
            if (cancelled) return;
            setDistance(Math.round(result.distanceMeters));
            if (result.arrived) {
              arrivedRef.current = true;
              subscription?.remove();
              navigation.replace('Mission', {
                questId: params.questId,
                stepId: params.stepId,
                placeName: params.placeName,
                story: result.story,
                missionType: result.mission?.missionType ?? 'PHOTO',
                question: result.mission?.question,
                latitude: params.latitude,
                longitude: params.longitude,
              });
            }
          } catch (e: any) {
            if (!cancelled) setPermissionError(e?.message ?? '위치 확인 중 문제가 생겼어요');
          } finally {
            if (!cancelled) setChecking(false);
          }
        }
      );
    })();

    return () => {
      cancelled = true;
      subscription?.remove();
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [params?.questId, params?.stepId]);

  if (!params) {
    // 목업 모드: 실제 좌표 없이 진입한 경우 (원본 디자인 그대로)
    return (
      <ScreenContainer contentStyle={styles.content}>
        <TopBar title="인천 개항장 퀘스트 GPS" onBack={() => navigation.goBack()} />
        <Card>
          <Text style={styles.cardTitle}>차이나 타운까지 430m</Text>
          <Text style={styles.cardSub}>도착하면 이야기가 자동으로 열려요</Text>
        </Card>
        <MapPlaceholder style={styles.map} pins={[{ leftPct: 70, topPct: 32 }]}>
          <View style={styles.meDot}>
            <View style={styles.meDotInner} />
          </View>
        </MapPlaceholder>
        <Card>
          <View style={styles.rowBetween}>
            <Text style={styles.questTag}>QUEST 02</Text>
            <Text style={styles.questTag}>2/3</Text>
          </View>
          <Text style={styles.questStatus}>100년 컨 인천으로 이동 중...</Text>
        </Card>
      </ScreenContainer>
    );
  }

  return (
    <ScreenContainer contentStyle={styles.content}>
      <TopBar title={`${params.placeName}로 이동 중`} onBack={() => navigation.goBack()} />

      <Card>
        <Text style={styles.cardTitle}>
          {distance != null ? `${params.placeName}까지 약 ${distance}m` : `${params.placeName}(으)로 이동해주세요`}
        </Text>
        <Text style={styles.cardSub}>도착하면 이야기가 자동으로 열려요</Text>
      </Card>

      <MapPlaceholder style={styles.map} pins={[{ leftPct: 70, topPct: 32 }]}>
        <View style={styles.meDot}>
          <View style={styles.meDotInner} />
        </View>
      </MapPlaceholder>

      <Card>
        {permissionError ? (
          <Text style={styles.errorText}>{permissionError}</Text>
        ) : (
          <View style={styles.rowBetween}>
            <Text style={styles.questStatus}>{params.placeName}로 이동 중...</Text>
            {checking && <ActivityIndicator size="small" color={colors.blue} />}
          </View>
        )}
      </Card>
    </ScreenContainer>
  );
}

const styles = StyleSheet.create({
  content: { paddingBottom: 22, gap: 16 },
  map: { flex: 1 },
  rowBetween: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' },
  cardTitle: { fontSize: 14.5, color: colors.ink, fontFamily: fonts.display },
  cardSub: { fontSize: 12, color: colors.inkSoft, fontFamily: fonts.display, marginTop: 4 },
  questTag: { fontSize: 12, color: colors.inkSoft, fontFamily: fonts.display, letterSpacing: 0.5 },
  questStatus: { fontSize: 14, color: colors.ink, fontFamily: fonts.display, marginTop: 6 },
  errorText: { color: colors.red, fontFamily: fonts.display, fontSize: 13.5 },
  meDot: {
    position: 'absolute',
    left: '20%',
    top: '66%',
    width: 22,
    height: 22,
    marginLeft: -11,
    marginTop: -11,
    borderRadius: 11,
    backgroundColor: 'rgba(0,71,160,0.25)',
    alignItems: 'center',
    justifyContent: 'center',
  },
  meDotInner: {
    width: 12,
    height: 12,
    borderRadius: 6,
    backgroundColor: colors.blue,
    borderWidth: 2,
    borderColor: colors.hanji,
  },
});
