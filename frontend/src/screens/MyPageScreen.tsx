import React, { useEffect, useState } from 'react';
import { Image, Pressable, StyleSheet, Text, View } from 'react-native';
import { CommonActions, useNavigation } from '@react-navigation/native';
import ScreenContainer from '../components/ScreenContainer';
import Card from '../components/Card';
import ListRow from '../components/ListRow';
import LangToggle from '../components/LangToggle';
import { colors } from '../theme/colors';
import { fonts } from '../theme/typography';
import { icons } from '../theme/assets';
import { fetchUser } from '../api/user';
import { fetchUserQuests, fetchUserStamps } from '../api/progress';
import { useSession } from '../state/SessionContext';
import { DEV_USER_ID } from '../api/devUser';

export default function MyPageScreen() {
  const navigation = useNavigation();
  const { user: sessionUser, setUser } = useSession();
  const userId = sessionUser?.id ?? DEV_USER_ID;

  const [name, setName] = useState(sessionUser?.nickname ?? 'OOO');
  const [nationality, setNationality] = useState(sessionUser?.nationality ?? '대한민국');
  const [meta, setMeta] = useState('스탬프 2개 · 완료 퀘스트 0개');

  useEffect(() => {
    let alive = true;
    (async () => {
      try {
        const [fetchedUser, stamps, quests] = await Promise.all([
          fetchUser(userId),
          fetchUserStamps(userId).catch(() => []),
          fetchUserQuests(userId).catch(() => []),
        ]);
        if (!alive) return;
        setName(fetchedUser.nickname);
        if (fetchedUser.nationality) setNationality(fetchedUser.nationality);
        const completedCount = quests.filter((q) => q.status === 'COMPLETED').length;
        setMeta(`스탬프 ${stamps.length}개 · 완료 퀘스트 ${completedCount}개`);
      } catch {
        // 실패하면 기존(로그인 응답 혹은 목업) 표시를 그대로 유지
      }
    })();
    return () => {
      alive = false;
    };
  }, [userId]);

  // 로그인 화면(온보딩)이 루트 스택의 최상단이라, 부모(루트) 내비게이션으로 올라가서 리셋해요.
  const handleLogout = () => {
    setUser(null);
    const root = navigation.getParent() ?? navigation;
    root.dispatch(CommonActions.reset({ index: 0, routes: [{ name: 'Onboarding' }] }));
  };

  return (
    <ScreenContainer edges={['top', 'left', 'right']} contentStyle={styles.content}>
      <Text style={styles.pageTitle}>마이페이지</Text>

      <Card style={styles.profileCard}>
        <View style={styles.avatar}>
          <Image source={icons.user} style={styles.avatarIcon} />
        </View>
        <View>
          <Text style={styles.name}>{name}</Text>
          <Text style={styles.meta}>{meta}</Text>
        </View>
      </Card>

      <Card style={styles.menuCard}>
        <ListRow label="언어" right={<LangToggle />} />
        <ListRow label="국적" right={<Text style={styles.value}>{nationality}</Text>} />
        <ListRow label="알림" last right={<Image source={icons.chev} style={styles.chev} />} />
      </Card>

      <Pressable onPress={handleLogout} style={styles.logoutButton} hitSlop={8}>
        <Text style={styles.logoutText}>로그아웃</Text>
      </Pressable>
    </ScreenContainer>
  );
}

const styles = StyleSheet.create({
  content: { paddingBottom: 0, gap: 16 },
  pageTitle: { fontSize: 19, color: colors.ink, fontFamily: fonts.display },
  profileCard: { flexDirection: 'row', alignItems: 'center', gap: 14 },
  avatar: {
    width: 52,
    height: 52,
    borderRadius: 26,
    backgroundColor: colors.hanjiDeep,
    alignItems: 'center',
    justifyContent: 'center',
  },
  avatarIcon: { width: 24, height: 24, tintColor: colors.inkSoft },
  name: { fontSize: 15.5, color: colors.ink, fontFamily: fonts.display },
  meta: { fontSize: 12.5, color: colors.inkSoft, fontFamily: fonts.display, marginTop: 3 },
  menuCard: { gap: 0, padding: 6 },
  value: { fontSize: 14, color: colors.inkSoft, fontFamily: fonts.display },
  chev: { width: 14, height: 14, tintColor: colors.inkSoft },
  logoutButton: { alignSelf: 'center', marginTop: 4, paddingVertical: 8, paddingHorizontal: 12 },
  logoutText: { fontSize: 13.5, color: colors.red, fontFamily: fonts.display },
});
