import React, { useEffect, useState } from 'react';
import { ActivityIndicator, ScrollView, StyleSheet, Text, View } from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import ScreenContainer from '../components/ScreenContainer';
import TopBar from '../components/TopBar';
import Card from '../components/Card';
import Badge from '../components/Badge';
import PrimaryButton from '../components/PrimaryButton';
import MapPlaceholder from '../components/MapPlaceholder';
import { colors, BadgeKind } from '../theme/colors';
import { fonts } from '../theme/typography';
import { HomeStackParamList } from '../navigation/types';
import { fetchMatchesForElement, createRecommendationSession, choosePlace } from '../api/recommendation';
import { createQuestFromSession } from '../api/quest';
import { MatchResponse } from '../api/types';
import { useSession } from '../state/SessionContext';
import { DEV_USER_ID } from '../api/devUser';

type Props = NativeStackScreenProps<HomeStackParamList, 'Place'>;

const categoryToBadgeKind: Record<string, BadgeKind> = {
  ARCHITECTURE: 'arch',
  COSTUME: 'cloth',
  FOOD: 'food',
  ART_RITUAL: 'rite',
  PROP: 'prop',
};

export default function PlaceScreen({ navigation, route }: Props) {
  const elementId = route.params?.elementId;
  const songId = route.params?.songId;
  const songTitle = route.params?.songTitle;
  const { user } = useSession();
  const userId = user?.id ?? DEV_USER_ID;

  const [matches, setMatches] = useState<MatchResponse[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [creating, setCreating] = useState(false);
  const [createError, setCreateError] = useState<string | null>(null);

  useEffect(() => {
    if (elementId == null) return;
    setError(null);
    setMatches(null);
    fetchMatchesForElement(elementId)
      .then(setMatches)
      .catch((e) => setError(e?.message ?? '추천 장소를 불러오지 못했어요'));
  }, [elementId]);

  const top = matches && matches.length > 0 ? matches[0] : null;
  const usingReal = elementId != null && !!top;

  const badgeKind: BadgeKind = usingReal && top!.category ? categoryToBadgeKind[top!.category] ?? 'arch' : 'arch';
  const placeName = usingReal ? top!.placeName : '경복궁';
  const description = usingReal
    ? top!.reason ?? ''
    : 'MV 속 궁궐 건축과 전통 어가행렬 요소가 실제 수문장 교대의식이 열리는 경복궁과 매칭되었어요.';

  // 이 곡의 추천 세션을 만들고, 추천된 장소를 전부 코스에 담아 실제 퀘스트로 바꾼다.
  const startQuest = async () => {
    if (songId == null) {
      // 목업 모드(실제 곡 데이터 없음)에서는 예전처럼 그냥 넘어간다.
      navigation.navigate('QuestMain', undefined);
      return;
    }
    setCreating(true);
    setCreateError(null);
    try {
      const session = await createRecommendationSession(songId, userId);
      if (session.places.length === 0) {
        setCreateError('추천된 장소가 없어서 퀘스트를 만들 수 없어요.');
        return;
      }
      await Promise.all(session.places.map((p) => choosePlace(p.id, true)));
      const quest = await createQuestFromSession({
        sessionId: session.sessionId,
        userId,
        title: `${songTitle ?? session.songTitle} 문화 탐방`,
      });
      navigation.navigate('QuestMain', { questId: quest.id, questTitle: quest.title });
    } catch (e: any) {
      setCreateError(e?.message ?? '퀘스트 생성에 실패했어요');
    } finally {
      setCreating(false);
    }
  };

  return (
    <ScreenContainer contentStyle={styles.content}>
      <TopBar title="이 요소와 어울리는 장소" onBack={() => navigation.goBack()} />

      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={styles.scroll}>
        <MapPlaceholder pins={[{ leftPct: 50, topPct: 46 }]} style={styles.map} />

        <View style={styles.dots}>
          <View style={[styles.dot, styles.dotActive]} />
          <View style={styles.dot} />
          <View style={styles.dot} />
        </View>

        {elementId != null && !matches && !error && (
          <View style={styles.center}>
            <ActivityIndicator color={colors.blue} />
          </View>
        )}

        {error && <Text style={styles.errorText}>{error}</Text>}

        {elementId != null && matches && matches.length === 0 && (
          <Text style={styles.errorText}>아직 이 요소에 매칭된 장소가 없어요</Text>
        )}

        {(elementId == null || top) && (
          <Card>
            <Badge kind={badgeKind} />
            <Text style={styles.placeTitle}>{placeName}</Text>
            <Text style={styles.placeDesc}>{description}</Text>
          </Card>
        )}

        {createError && <Text style={styles.errorText}>{createError}</Text>}
      </ScrollView>

      <PrimaryButton
        title={creating ? '퀘스트 만드는 중...' : '이 장소로 퀘스트 시작하기'}
        onPress={startQuest}
        disabled={creating}
      />
    </ScreenContainer>
  );
}

const styles = StyleSheet.create({
  content: { paddingBottom: 0 },
  scroll: { gap: 16, paddingBottom: 20 },
  map: { height: 226 },
  dots: { flexDirection: 'row', justifyContent: 'center', gap: 6 },
  dot: { width: 7, height: 7, borderRadius: 4, backgroundColor: colors.brownSoft },
  dotActive: { backgroundColor: colors.red, width: 16 },
  placeTitle: { fontSize: 19, color: colors.ink, fontFamily: fonts.display, marginTop: 10 },
  placeDesc: { fontSize: 13, color: colors.inkSoft, fontFamily: fonts.display, marginTop: 8, lineHeight: 19 },
  center: { paddingVertical: 20, alignItems: 'center' },
  errorText: { color: colors.red, fontFamily: fonts.display, fontSize: 13.5, textAlign: 'center' },
});
