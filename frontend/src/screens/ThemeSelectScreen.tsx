import React, { useCallback, useEffect, useState } from 'react';
import { ActivityIndicator, ScrollView, StyleSheet, Text, View } from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import ScreenContainer from '../components/ScreenContainer';
import TopBar from '../components/TopBar';
import Card from '../components/Card';
import SmallButton from '../components/SmallButton';
import { colors } from '../theme/colors';
import { fonts } from '../theme/typography';
import { fetchQuests } from '../api/quest';
import { QuestResponse } from '../api/types';
import { QuestStackParamList } from '../navigation/types';

type Props = NativeStackScreenProps<QuestStackParamList, 'ThemeSelect'>;

// 실제 백엔드(GET /api/quests) 연동 버전입니다.
// 원래 목업엔 난이도/소요시간/거리 알약이 있었는데, 백엔드 퀘스트 데이터에는 그 정보가 아직 없어서
// 실제로 오는 값인 지역(themeRegion)/시대(themeEra)를 대신 보여줍니다.
export default function ThemeSelectScreen({ navigation }: Props) {
  const [quests, setQuests] = useState<QuestResponse[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(() => {
    setError(null);
    setQuests(null);
    fetchQuests()
      .then(setQuests)
      .catch((e) => setError(e?.message ?? '퀘스트를 불러오지 못했어요'));
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  return (
    <ScreenContainer contentStyle={styles.content}>
      <TopBar title="테마 퀘스트" onBack={() => navigation.goBack()} />

      {quests === null && !error && (
        <View style={styles.center}>
          <ActivityIndicator color={colors.blue} />
        </View>
      )}

      {error && (
        <View style={styles.center}>
          <Text style={styles.errorText}>{error}</Text>
          <SmallButton title="다시 시도" onPress={load} />
        </View>
      )}

      {quests && quests.length === 0 && (
        <View style={styles.center}>
          <Text style={styles.emptyText}>아직 등록된 퀘스트가 없어요.</Text>
        </View>
      )}

      {quests && quests.length > 0 && (
        <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={styles.scroll}>
          {quests.map((q) => (
            <Card
              key={q.id}
              onPress={() => navigation.navigate('QuestMain', { questId: q.id, questTitle: q.title })}
            >
              <Text style={styles.title}>{q.title}</Text>
              <View style={styles.pillRow}>
                {q.themeRegion ? <SmallButton title={q.themeRegion} /> : null}
                {q.themeEra ? <SmallButton title={q.themeEra} /> : null}
              </View>
            </Card>
          ))}
        </ScrollView>
      )}
    </ScreenContainer>
  );
}

const styles = StyleSheet.create({
  content: { paddingBottom: 0 },
  scroll: { gap: 14, paddingBottom: 20 },
  title: { fontSize: 15.5, color: colors.ink, fontFamily: fonts.display, marginBottom: 10 },
  pillRow: { flexDirection: 'row', gap: 8, flexWrap: 'wrap' },
  center: { flex: 1, alignItems: 'center', justifyContent: 'center', gap: 12 },
  errorText: { color: colors.red, fontFamily: fonts.display, fontSize: 13.5, textAlign: 'center' },
  emptyText: { color: colors.inkSoft, fontFamily: fonts.display, fontSize: 13.5 },
});
