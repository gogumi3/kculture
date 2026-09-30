import React, { useEffect, useState } from 'react';
import { ActivityIndicator, ScrollView, StyleSheet, Text, View } from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import ScreenContainer from '../components/ScreenContainer';
import TopBar from '../components/TopBar';
import Card from '../components/Card';
import ProgressBar from '../components/ProgressBar';
import ListRow from '../components/ListRow';
import StampCircle from '../components/StampCircle';
import PrimaryButton from '../components/PrimaryButton';
import MapPlaceholder from '../components/MapPlaceholder';
import { colors } from '../theme/colors';
import { fonts } from '../theme/typography';
import { inProgressQuest, questSteps } from '../data/mock';
import { fetchQuestDetail } from '../api/quest';
import { startQuest } from '../api/questFlow';
import { QuestDetailResponse } from '../api/types';
import { QuestFlowParamList } from '../navigation/types';
import { useSession } from '../state/SessionContext';
import { DEV_USER_ID } from '../api/devUser';

type Props = NativeStackScreenProps<QuestFlowParamList, 'QuestMain'>;

// questId가 있으면(테마 선택 화면에서 진입) 실제 백엔드 데이터(GET /api/quests/{id})를 불러오고,
// 없으면(아직 연동 안 된 다른 진입 경로 - 홈 화면 "이어하기" 등) 기존 목업을 그대로 보여줍니다.
// 진행 상태(몇 단계까지 왔는지)는 로그인 연동 후에 /progress 엔드포인트로 이어서 붙일 예정이라
// 지금은 항상 "1단계"로 표시돼요.
export default function QuestMainScreen({ navigation, route }: Props) {
  const questId = route.params?.questId;
  const { user } = useSession();
  const userId = user?.id ?? DEV_USER_ID;
  const [detail, setDetail] = useState<QuestDetailResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [starting, setStarting] = useState(false);

  useEffect(() => {
    if (questId == null) return;
    setError(null);
    setDetail(null);
    fetchQuestDetail(questId)
      .then(setDetail)
      .catch((e) => setError(e?.message ?? '퀘스트 정보를 불러오지 못했어요'));
  }, [questId]);

  // 퀘스트를 시작하고(이미 시작했으면 그대로), 1단계 GPS 화면으로 이동한다.
  const handleStart = async () => {
    if (questId == null || !detail) {
      navigation.navigate('QuestGps', undefined);
      return;
    }
    setStarting(true);
    setError(null);
    try {
      await startQuest(questId, userId);
      const firstStep = detail.steps[0];
      if (!firstStep || firstStep.latitude == null || firstStep.longitude == null) {
        setError('이 퀘스트에는 GPS 좌표가 있는 장소가 없어요.');
        return;
      }
      navigation.navigate('QuestGps', {
        questId,
        stepId: firstStep.id,
        placeName: firstStep.placeName,
        latitude: Number(firstStep.latitude),
        longitude: Number(firstStep.longitude),
        arrivalRadius: firstStep.arrivalRadius,
      });
    } catch (e: any) {
      setError(e?.message ?? '퀘스트를 시작하지 못했어요');
    } finally {
      setStarting(false);
    }
  };

  const title = detail?.quest.title ?? route.params?.questTitle ?? inProgressQuest.title;
  const steps = detail
    ? detail.steps.map((s) => ({ label: `${s.stepOrder}. ${s.placeName}`, meta: s.distanceHint ?? '' }))
    : questSteps;
  const loading = questId != null && !detail && !error;
  const ready = questId == null || !!detail;

  return (
    <ScreenContainer contentStyle={styles.content}>
      <TopBar title={title} onBack={() => navigation.goBack()} />

      {loading && (
        <View style={styles.center}>
          <ActivityIndicator color={colors.blue} />
        </View>
      )}

      {error && (
        <View style={styles.center}>
          <Text style={styles.errorText}>{error}</Text>
        </View>
      )}

      {ready && (
        <>
          <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={styles.scroll}>
            <MapPlaceholder
              style={styles.map}
              pins={[
                { leftPct: 70, topPct: 34 },
                { leftPct: 18, topPct: 66 },
                { leftPct: 80, topPct: 62 },
              ]}
            />

            <View style={styles.progressBlock}>
              <ProgressBar progress={1 / steps.length} />
              <Text style={styles.progressLabel}>{steps.length}단계 중 1단계</Text>
            </View>

            <Card style={styles.stepsCard}>
              {steps.map((s, i) => (
                <ListRow
                  key={s.label}
                  label={s.label}
                  right={<Text style={styles.stepMeta}>{s.meta}</Text>}
                  last={i === steps.length - 1}
                />
              ))}
            </Card>

            <View style={styles.stampRow}>
              {steps.map((s) => (
                <StampCircle key={s.label} />
              ))}
            </View>
          </ScrollView>

          <PrimaryButton
            title={starting ? '시작하는 중...' : '퀘스트 시작하기'}
            onPress={handleStart}
            disabled={starting}
          />
        </>
      )}
    </ScreenContainer>
  );
}

const styles = StyleSheet.create({
  content: { paddingBottom: 0 },
  scroll: { gap: 16, paddingBottom: 20 },
  map: { height: 216 },
  progressBlock: { gap: 6 },
  progressLabel: { fontSize: 11.5, color: colors.inkSoft, fontFamily: fonts.display },
  stepsCard: { gap: 0, padding: 6 },
  stepMeta: { fontSize: 13, color: colors.inkSoft, fontFamily: fonts.display },
  stampRow: { flexDirection: 'row', justifyContent: 'center', gap: 22, marginTop: 4 },
  center: { flex: 1, alignItems: 'center', justifyContent: 'center', gap: 12 },
  errorText: { color: colors.red, fontFamily: fonts.display, fontSize: 13.5, textAlign: 'center' },
});
