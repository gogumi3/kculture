import React, { useState } from 'react';
import { Image, ScrollView, StyleSheet, Text, TextInput, View } from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import ScreenContainer from '../components/ScreenContainer';
import Card from '../components/Card';
import Chip from '../components/Chip';
import ProgressBar from '../components/ProgressBar';
import SmallButton from '../components/SmallButton';
import ListRow from '../components/ListRow';
import { colors } from '../theme/colors';
import { fonts } from '../theme/typography';
import { icons } from '../theme/assets';
import { popularSearches, recentSearches, inProgressQuest } from '../data/mock';
import { HomeStackParamList } from '../navigation/types';
import { searchSongs, autoImportSong } from '../api/song';

type Props = NativeStackScreenProps<HomeStackParamList, 'HomeMain'>;

export default function HomeScreen({ navigation }: Props) {
  const [query, setQuery] = useState('');
  const [searching, setSearching] = useState(false);
  const [searchError, setSearchError] = useState<string | null>(null);

  // 검색어로 실제 곡을 찾아서 분석 화면으로 이동합니다.
  // DB에 곡이 없으면 유튜브에서 자동으로 찾아 등록해요 (auto-import).
  const runSearch = async (keyword: string) => {
    const trimmed = keyword.trim();
    if (!trimmed) return;
    setSearching(true);
    setSearchError(null);
    try {
      const results = await searchSongs(trimmed);
      const song = results.length > 0 ? results[0] : await autoImportSong(trimmed);
      navigation.navigate('Analysis', { songId: song.id, songTitle: song.title, artist: song.artist });
    } catch (e: any) {
      setSearchError(e?.message ?? '검색에 실패했어요');
    } finally {
      setSearching(false);
    }
  };

  return (
    <ScreenContainer contentStyle={styles.content}>
      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={styles.scroll}>
        <View>
          <Text style={styles.greeting}>OO님,</Text>
          <Text style={styles.greetingSub}>오늘은 어떤 무대를 찾아 떠나볼까요?</Text>
        </View>

        <View style={styles.search}>
          <Image source={icons.search} style={styles.searchIcon} />
          <TextInput
            style={styles.searchInput}
            placeholder="K-pop 곡 제목을 검색해보세요"
            placeholderTextColor={colors.inkSoft}
            value={query}
            onChangeText={setQuery}
            onSubmitEditing={() => runSearch(query)}
            returnKeyType="search"
          />
        </View>
        {searching && <Text style={styles.searchHint}>검색 중...</Text>}
        {searchError && <Text style={styles.searchError}>{searchError}</Text>}

        <View style={styles.section}>
          <Text style={styles.sectionLabel}>인기 검색어</Text>
          <View style={styles.chipRow}>
            {popularSearches.map((label) => (
              <Chip key={label} label={label} onPress={() => runSearch(label)} />
            ))}
          </View>
        </View>

        <Card onPress={() => navigation.navigate('QuestMain', { questTitle: inProgressQuest.title })}>
          <View style={styles.rowBetween}>
            <Text style={styles.cardCaption}>진행 중인 퀘스트</Text>
            <SmallButton title="이어하기 >" />
          </View>
          <Text style={styles.cardTitle}>{inProgressQuest.title}</Text>
          <ProgressBar progress={inProgressQuest.progress} />
          <Text style={styles.cardMeta}>{inProgressQuest.stepLabel}</Text>
        </Card>

        <View style={styles.section}>
          <Text style={styles.sectionLabel}>최근 검색</Text>
          {recentSearches.map((item, i) => (
            <ListRow
              key={item.id}
              icon={icons.clock}
              label={item.label}
              last={i === recentSearches.length - 1}
              onPress={() => runSearch(item.query)}
            />
          ))}
        </View>
      </ScrollView>
    </ScreenContainer>
  );
}

const styles = StyleSheet.create({
  content: { paddingBottom: 0 },
  scroll: { gap: 18, paddingBottom: 24 },
  greeting: { fontSize: 18, color: colors.ink, fontFamily: fonts.display },
  greetingSub: { fontSize: 14, color: colors.inkSoft, marginTop: 2, fontFamily: fonts.display },
  search: {
    height: 52,
    borderRadius: 26,
    backgroundColor: colors.card,
    borderWidth: 1.4,
    borderColor: colors.brown,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    paddingHorizontal: 18,
  },
  searchIcon: { width: 18, height: 18, tintColor: colors.inkSoft },
  searchInput: { flex: 1, color: colors.ink, fontSize: 14.5, fontFamily: fonts.display, padding: 0 },
  searchHint: { fontSize: 12, color: colors.inkSoft, fontFamily: fonts.display, marginTop: -10 },
  searchError: { fontSize: 12, color: colors.red, fontFamily: fonts.display, marginTop: -10 },
  section: { gap: 8 },
  sectionLabel: { fontSize: 12.5, color: colors.inkSoft, fontFamily: fonts.display },
  chipRow: { flexDirection: 'row', gap: 8, flexWrap: 'wrap' },
  rowBetween: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' },
  cardCaption: { fontSize: 13, color: colors.inkSoft, fontFamily: fonts.display },
  cardTitle: { color: colors.ink, marginVertical: 8, fontSize: 15, fontFamily: fonts.display },
  cardMeta: { fontSize: 11.5, color: colors.inkSoft, marginTop: 6, fontFamily: fonts.display },
});
