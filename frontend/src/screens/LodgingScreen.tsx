import React, { useState } from 'react';
import { Linking, ScrollView, StyleSheet, Text, View } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import ScreenContainer from '../components/ScreenContainer';
import Card from '../components/Card';
import SmallButton from '../components/SmallButton';
import { colors } from '../theme/colors';
import { fonts } from '../theme/typography';

// [Claude 작성] 아고다는 공식 오픈 API가 없어서, 실제 숙소 매물 데이터를 앱 안에
// 그대로 가져와 보여줄 수 없어요 (사용자 확인 완료: "아고다 검색 페이지로 연결되는 카드만").
// 그래서 이 화면은 백엔드 accommodation 데이터를 쓰지 않고, 퀘스트 지역과 숙소 유형을
// 조합해 아고다 "검색 결과" 페이지로 바로 이동하는 딥링크 카드만 최대 3개 보여줘요.
// 실제 가격/평점/사진은 카드에 없고, 눌렀을 때 아고다 사이트에서 직접 확인하는 구조예요.

const GRADIENTS: readonly [string, string][] = [
  ['#DCC79A', '#8FA8C7'],
  ['#C9927E', '#9A8AAE'],
  ['#8FA8C7', '#7C9473'],
];

// 퀘스트 필터 칩 → 아고다에 검색할 지역 키워드 매핑.
// (기존 디자인의 questFilters 문구는 그대로 두고, 검색어만 지역 위주로 단순화했어요.)
const questFilters = ['인천 개항장', '서울 조선시대'] as const;
const REGION_BY_QUEST: Record<(typeof questFilters)[number], string> = {
  '인천 개항장': '인천',
  '서울 조선시대': '서울',
};

// 숙소 유형 최대 3가지 (요청하신 "최대 3개까지" 조건에 맞춤).
const STAY_TYPES = ['호텔', '게스트하우스', '한옥스테이'] as const;

function buildAgodaUrl(region: string, stayType: string): string {
  // [Claude 작성] 아고다 검색 결과 페이지 URL 패턴 (공식 문서 기반이 아닌 추정치라,
  // 실기기 테스트 후 파라미터를 조정해야 할 수도 있어요).
  const text = encodeURIComponent(`${region} ${stayType}`);
  return `https://www.agoda.com/search?text=${text}&currency=KRW`;
}

export default function LodgingScreen() {
  const [quest, setQuest] = useState<(typeof questFilters)[number]>(questFilters[0]);
  const region = REGION_BY_QUEST[quest];

  const openAgoda = (stayType: string) => {
    Linking.openURL(buildAgodaUrl(region, stayType)).catch(() => {
      // 링크를 열지 못해도 앱이 멈추지 않도록 조용히 무시해요.
    });
  };

  return (
    <ScreenContainer edges={['top', 'left', 'right']} contentStyle={styles.content}>
      <Text style={styles.pageTitle}>근처 숙소</Text>

      <View style={styles.filterRow}>
        {questFilters.map((f) => (
          <SmallButton key={f} title={f} active={quest === f} onPress={() => setQuest(f)} />
        ))}
      </View>

      <Text style={styles.notice}>
        실제 숙소 정보·가격·후기는 아고다에서 바로 확인해보세요. 카드를 누르면 아고다 검색 결과로 이동해요.
      </Text>

      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={styles.scroll}>
        {STAY_TYPES.map((stayType, i) => (
          <Card key={stayType} style={styles.lodgingCard} onPress={() => openAgoda(stayType)}>
            <LinearGradient
              colors={GRADIENTS[i % GRADIENTS.length]}
              start={{ x: 0, y: 0 }}
              end={{ x: 1, y: 1 }}
              style={styles.thumb}
            />
            <View style={styles.info}>
              <Text style={styles.title}>
                {region} {stayType}
              </Text>
              <Text style={styles.meta}>아고다에서 검색하기</Text>
            </View>
            <Text style={styles.arrow}>›</Text>
          </Card>
        ))}
      </ScrollView>
    </ScreenContainer>
  );
}

const styles = StyleSheet.create({
  content: { paddingBottom: 0 },
  pageTitle: { fontSize: 19, color: colors.ink, fontFamily: fonts.display },
  filterRow: { flexDirection: 'row', gap: 8, marginTop: 4 },
  notice: {
    fontSize: 12,
    color: colors.inkSoft,
    fontFamily: fonts.display,
    marginTop: 10,
    lineHeight: 17,
  },
  scroll: { gap: 14, paddingTop: 14, paddingBottom: 20 },
  lodgingCard: { flexDirection: 'row', gap: 14, alignItems: 'center' },
  thumb: { width: 64, height: 64, borderRadius: 16 },
  info: { flex: 1, gap: 4 },
  title: { fontSize: 14.5, color: colors.ink, fontFamily: fonts.display },
  meta: { fontSize: 12, color: colors.inkSoft, fontFamily: fonts.display },
  arrow: { fontSize: 22, color: colors.brown },
});
