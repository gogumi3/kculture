import React, { useEffect, useState } from 'react';
import { ActivityIndicator, ScrollView, StyleSheet, Text, View } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import ScreenContainer from '../components/ScreenContainer';
import TopBar from '../components/TopBar';
import Card from '../components/Card';
import Badge from '../components/Badge';
import PrimaryButton from '../components/PrimaryButton';
import { colors, BadgeKind } from '../theme/colors';
import { fonts } from '../theme/typography';
import { analysisTopBadges, culturalElements } from '../data/mock';
import { HomeStackParamList } from '../navigation/types';
import { fetchAnalyses, fetchElements, autoAnalyze } from '../api/analysis';
import { CulturalElementResponse, CulturalCategoryValue } from '../api/types';

type Props = NativeStackScreenProps<HomeStackParamList, 'Analysis'>;

// 백엔드 CulturalCategory(영문 enum) -> 화면 Badge 색상 종류로 바꾸는 매핑입니다.
const categoryToBadgeKind: Record<CulturalCategoryValue, BadgeKind> = {
  ARCHITECTURE: 'arch',
  COSTUME: 'cloth',
  FOOD: 'food',
  ART_RITUAL: 'rite',
  PROP: 'prop',
};

const categoryLabel: Record<CulturalCategoryValue, string> = {
  ARCHITECTURE: '건축',
  COSTUME: '의상',
  FOOD: '음식',
  ART_RITUAL: '의례',
  PROP: '소품',
};

export default function AnalysisScreen({ navigation, route }: Props) {
  const songId = route.params?.songId;
  const artist = route.params?.artist ?? 'Agust D';
  const songTitle = route.params?.songTitle ?? '대취타';

  const [elements, setElements] = useState<CulturalElementResponse[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [analyzing, setAnalyzing] = useState(false);

  useEffect(() => {
    if (songId == null) return;
    setError(null);
    setElements(null);
    setAnalyzing(false);
    (async () => {
      try {
        const analyses = await fetchAnalyses(songId);
        let target = analyses.find((a) => a.status === 'DONE');

        if (!target) {
          // 완료된 분석이 없으면 AI(유튜브+Gemini) 자동 분석을 새로 돌린다. 시간이 좀 걸릴 수 있다.
          setAnalyzing(true);
          target = await autoAnalyze(songId);
        }

        const els = await fetchElements(target.id);
        setElements(els);
      } catch (e: any) {
        setError(e?.message ?? '분석 결과를 불러오지 못했어요');
      } finally {
        setAnalyzing(false);
      }
    })();
  }, [songId]);

  // songId가 없거나(방어적) 실제 데이터가 아직 없으면 기존 목업으로 보여줍니다.
  const usingReal = songId != null && !!elements && elements.length > 0;
  const badgeItems: { kind: BadgeKind; label?: string }[] = usingReal
    ? elements!.map((el) => ({ kind: categoryToBadgeKind[el.category], label: categoryLabel[el.category] }))
    : analysisTopBadges;
  const detailItems = usingReal
    ? elements!.map((el) => ({
        kind: categoryToBadgeKind[el.category],
        label: el.name,
        confidence: el.confidence != null ? Math.round(el.confidence * 100) : null,
      }))
    : culturalElements.map((el) => ({ ...el, confidence: el.confidence as number | null }));

  return (
    <ScreenContainer contentStyle={styles.content}>
      <TopBar sup={artist} title={songTitle} onBack={() => navigation.goBack()} />

      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={styles.scroll}>
        <LinearGradient
          colors={['#E0CB9E', '#C99B80']}
          start={{ x: 0, y: 0 }}
          end={{ x: 1, y: 1 }}
          style={styles.thumb}
        >
          <View style={styles.playCircle}>
            <View style={styles.playTriangle} />
          </View>
        </LinearGradient>

        {songId != null && !elements && !error && (
          <View style={styles.center}>
            <ActivityIndicator color={colors.blue} />
            {analyzing && (
              <Text style={styles.analyzingText}>
                AI가 뮤직비디오를 분석하고 있어요...{'\n'}최대 1분 정도 걸릴 수 있어요
              </Text>
            )}
          </View>
        )}

        {error && <Text style={styles.errorText}>{error}</Text>}

        {(songId == null || elements) && (
          <>
            <Text style={styles.sectionLabel}>AI가 찾은 한국척 요소</Text>
            <View style={styles.badgeRow}>
              {badgeItems.map((b, i) => (
                <Badge key={i} kind={b.kind} label={b.label} />
              ))}
            </View>

            <Card style={styles.detailCard}>
              {detailItems.map((el, i) => (
                <View key={i} style={[styles.detailRow, i !== detailItems.length - 1 && styles.detailDivider]}>
                  <Badge kind={el.kind} />
                  <Text style={styles.detailLabel}>{el.label}</Text>
                  <View style={{ flex: 1 }} />
                  {el.confidence != null && <Text style={styles.detailConfidence}>신뢰도 {el.confidence}%</Text>}
                </View>
              ))}
            </Card>
          </>
        )}
      </ScrollView>

      <PrimaryButton
        title="장소 추천 보러가기"
        onPress={() =>
          navigation.navigate('Place', {
            elementId: usingReal ? elements![0].id : undefined,
            songId: usingReal ? songId : undefined,
            songTitle,
          })
        }
      />
    </ScreenContainer>
  );
}

const styles = StyleSheet.create({
  content: { paddingBottom: 0 },
  scroll: { gap: 16, paddingBottom: 20 },
  thumb: {
    height: 172,
    borderRadius: 22,
    alignItems: 'center',
    justifyContent: 'center',
  },
  playCircle: {
    width: 60,
    height: 60,
    borderRadius: 30,
    backgroundColor: 'rgba(255,255,255,0.85)',
    alignItems: 'center',
    justifyContent: 'center',
  },
  playTriangle: {
    width: 0,
    height: 0,
    marginLeft: 4,
    borderTopWidth: 12,
    borderBottomWidth: 12,
    borderLeftWidth: 18,
    borderTopColor: 'transparent',
    borderBottomColor: 'transparent',
    borderLeftColor: colors.red,
  },
  sectionLabel: { fontSize: 14.5, color: colors.ink, fontFamily: fonts.display },
  badgeRow: { flexDirection: 'row', gap: 8, flexWrap: 'wrap' },
  detailCard: { gap: 0, padding: 6 },
  detailRow: { flexDirection: 'row', alignItems: 'center', gap: 10, paddingHorizontal: 10, paddingVertical: 13 },
  detailDivider: { borderBottomWidth: 1, borderBottomColor: colors.brownSoft },
  detailLabel: { fontSize: 14, color: colors.ink, fontFamily: fonts.display },
  detailConfidence: { fontSize: 12.5, color: colors.inkSoft, fontFamily: fonts.display },
  center: { paddingVertical: 20, alignItems: 'center', gap: 10 },
  analyzingText: { color: colors.inkSoft, fontFamily: fonts.display, fontSize: 12.5, textAlign: 'center' },
  errorText: { color: colors.red, fontFamily: fonts.display, fontSize: 13.5, textAlign: 'center' },
});
