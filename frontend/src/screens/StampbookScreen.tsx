import React, { useEffect, useState } from 'react';
import { ActivityIndicator, Image, ScrollView, StyleSheet, Text, View } from 'react-native';
import ScreenContainer from '../components/ScreenContainer';
import Card from '../components/Card';
import SmallButton from '../components/SmallButton';
import StampCircle from '../components/StampCircle';
import { colors } from '../theme/colors';
import { fonts } from '../theme/typography';
import { icons } from '../theme/assets';
import { stampBooks as mockStampBooks } from '../data/mock';
import { fetchUserQuests } from '../api/progress';
import { useSession } from '../state/SessionContext';
import { DEV_USER_ID } from '../api/devUser';

const STAMP_COLORS = [colors.red, colors.blue] as const;

type Book = { title: string; completed: boolean; stamps: { color: string | null }[] };

export default function StampbookScreen() {
  const { user } = useSession();
  const userId = user?.id ?? DEV_USER_ID;
  const [tab, setTab] = useState<'todo' | 'done'>('todo');
  const [realBooks, setRealBooks] = useState<Book[] | null>(null);
  const [usingReal, setUsingReal] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let alive = true;
    (async () => {
      try {
        const quests = await fetchUserQuests(userId);
        if (!alive) return;
        if (quests.length > 0) {
          const mapped: Book[] = quests.map((q) => ({
            title: q.questTitle,
            completed: q.status === 'COMPLETED',
            stamps: [...q.steps]
              .sort((a, b) => a.stepOrder - b.stepOrder)
              .map((s, i) => ({ color: s.status === 'DONE' ? STAMP_COLORS[i % STAMP_COLORS.length] : null })),
          }));
          setRealBooks(mapped);
          setUsingReal(true);
        }
      } catch {
        // 실패하면 목업으로 대체 (예: userId가 아직 DB에 없음)
      } finally {
        if (alive) setLoading(false);
      }
    })();
    return () => {
      alive = false;
    };
  }, [userId]);

  const books: Book[] =
    usingReal && realBooks
      ? realBooks
      : mockStampBooks.map((b) => ({
          title: b.title,
          completed: false,
          stamps: b.stamps.map((s) => ({
            color: s.color === 'red' ? colors.red : s.color === 'blue' ? colors.blue : null,
          })),
        }));

  const filteredBooks = books.filter((b) => (tab === 'done' ? b.completed : !b.completed));
  const completedBooks = books.filter((b) => b.completed);

  return (
    <ScreenContainer edges={['top', 'left', 'right']} contentStyle={styles.content}>
      <Text style={styles.pageTitle}>나의 스탬프북</Text>

      <View style={styles.tabRow}>
        <SmallButton title="미완료" active={tab === 'todo'} onPress={() => setTab('todo')} />
        <SmallButton title="완료" active={tab === 'done'} onPress={() => setTab('done')} />
      </View>

      {loading ? (
        <ActivityIndicator style={styles.loading} color={colors.inkSoft} />
      ) : (
        <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={styles.scroll}>
          {filteredBooks.length === 0 ? (
            <Text style={styles.emptyText}>표시할 퀘스트가 없어요.</Text>
          ) : (
            filteredBooks.map((book) => (
              <View key={book.title} style={styles.section}>
                <View style={styles.sectionHead}>
                  <Text style={styles.sectionTitle}>{book.title}</Text>
                  <SmallButton title="취소" />
                </View>
                <View style={styles.stampRow}>
                  {book.stamps.map((s, i) => (
                    <StampCircle key={i} filledColor={s.color ?? undefined} />
                  ))}
                </View>
              </View>
            ))
          )}

          <Card style={styles.doneCard}>
            <View style={styles.rowBetween}>
              <Text style={styles.doneTitle}>완료한 퀘스트</Text>
              <Image source={icons.share} style={styles.shareIcon} />
            </View>
            {completedBooks.length === 0 ? (
              <Text style={styles.doneDesc}>
                아직 완료한 퀘스트가 없어요.{'\n'}스탬프를 모아 퀘스트를 해결해보세요!
              </Text>
            ) : (
              <Text style={styles.doneDesc}>{completedBooks.map((b) => b.title).join(', ')}</Text>
            )}
          </Card>
        </ScrollView>
      )}
    </ScreenContainer>
  );
}

const styles = StyleSheet.create({
  content: { paddingBottom: 0 },
  pageTitle: { fontSize: 19, color: colors.ink, fontFamily: fonts.display },
  tabRow: { flexDirection: 'row', gap: 8, marginTop: 4 },
  loading: { marginTop: 24 },
  scroll: { gap: 22, paddingTop: 4, paddingBottom: 20 },
  emptyText: { fontSize: 13, color: colors.inkSoft, fontFamily: fonts.display, marginTop: 8 },
  section: { gap: 12 },
  sectionHead: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' },
  sectionTitle: { fontSize: 14.5, color: colors.ink, fontFamily: fonts.display },
  stampRow: { flexDirection: 'row', gap: 14 },
  doneCard: { marginTop: 4 },
  rowBetween: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' },
  doneTitle: { fontSize: 15, color: colors.ink, fontFamily: fonts.display },
  shareIcon: { width: 18, height: 18, tintColor: colors.inkSoft },
  doneDesc: { fontSize: 13, color: colors.inkSoft, fontFamily: fonts.display, marginTop: 10, lineHeight: 19 },
});
