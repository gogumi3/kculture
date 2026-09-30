import React, { useState } from 'react';
import {
  Image,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import ScreenContainer from '../components/ScreenContainer';
import PrimaryButton from '../components/PrimaryButton';
import LangToggle from '../components/LangToggle';
import { colors } from '../theme/colors';
import { fonts } from '../theme/typography';
import { images } from '../theme/assets';
import { RootStackParamList } from '../navigation/types';
import { login, signup } from '../api/user';
import { ApiError } from '../api/client';
import { useSession } from '../state/SessionContext';

type Props = NativeStackScreenProps<RootStackParamList, 'Onboarding'>;

type Mode = 'login' | 'signup';

export default function OnboardingScreen({ navigation }: Props) {
  const { setUser } = useSession();
  const [mode, setMode] = useState<Mode>('login');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [nickname, setNickname] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const submit = async () => {
    setError(null);
    if (!email.trim() || !password.trim() || (mode === 'signup' && !nickname.trim())) {
      setError('빈 칸을 모두 입력해주세요.');
      return;
    }

    setSubmitting(true);
    try {
      const user =
        mode === 'login'
          ? await login(email.trim(), password)
          : await signup({ email: email.trim(), password, nickname: nickname.trim(), languagePref: 'ko' });
      setUser(user);
      navigation.replace('MainTabs');
    } catch (e) {
      if (e instanceof ApiError) {
        setError(e.message || '요청에 실패했어요.');
      } else {
        setError('서버에 연결할 수 없어요. 백엔드가 켜져 있는지 확인해주세요.');
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <ScreenContainer contentStyle={styles.content}>
      <KeyboardAvoidingView
        style={styles.flex}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
      >
        <ScrollView
          contentContainerStyle={styles.scroll}
          keyboardShouldPersistTaps="handled"
          showsVerticalScrollIndicator={false}
        >
          <LangToggle style={styles.langToggle} />

          <View style={styles.brandBlock}>
            <Image source={images.pinTaegeuk} style={styles.logo} resizeMode="contain" />
            <Text style={styles.title}>K-컬처 리얼맵</Text>
            <Text style={styles.subtitle}>
              MV 속 한국을{'\n'}실제 지도 위로 데려다드려요
            </Text>
          </View>

          <View style={styles.tabRow}>
            <Text
              style={[styles.tab, mode === 'login' && styles.tabActive]}
              onPress={() => {
                setMode('login');
                setError(null);
              }}
            >
              로그인
            </Text>
            <Text
              style={[styles.tab, mode === 'signup' && styles.tabActive]}
              onPress={() => {
                setMode('signup');
                setError(null);
              }}
            >
              회원가입
            </Text>
          </View>

          <View style={styles.form}>
            {mode === 'signup' && (
              <TextInput
                style={styles.input}
                placeholder="닉네임"
                placeholderTextColor={colors.inkSoft}
                value={nickname}
                onChangeText={setNickname}
                autoCapitalize="none"
              />
            )}
            <TextInput
              style={styles.input}
              placeholder="이메일"
              placeholderTextColor={colors.inkSoft}
              value={email}
              onChangeText={setEmail}
              autoCapitalize="none"
              keyboardType="email-address"
            />
            <TextInput
              style={styles.input}
              placeholder="비밀번호"
              placeholderTextColor={colors.inkSoft}
              value={password}
              onChangeText={setPassword}
              secureTextEntry
            />
            {error && <Text style={styles.error}>{error}</Text>}
          </View>

          <View style={styles.buttons}>
            <PrimaryButton
              title={submitting ? '처리 중...' : mode === 'login' ? '로그인' : '가입하고 시작하기'}
              onPress={submit}
              disabled={submitting}
            />
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </ScreenContainer>
  );
}

const styles = StyleSheet.create({
  content: { paddingTop: 50 },
  flex: { flex: 1 },
  scroll: { flexGrow: 1, justifyContent: 'space-between', gap: 22, paddingBottom: 20 },
  langToggle: { alignSelf: 'flex-end' },
  brandBlock: { alignItems: 'center', marginTop: 24 },
  logo: { width: 64, height: 64 },
  title: { fontFamily: fonts.display, fontSize: 22, color: colors.ink, marginTop: 14 },
  subtitle: {
    color: colors.inkSoft,
    fontSize: 13,
    marginTop: 8,
    lineHeight: 19,
    textAlign: 'center',
    fontFamily: fonts.display,
  },
  tabRow: { flexDirection: 'row', gap: 22, justifyContent: 'center', marginTop: 12 },
  tab: { fontSize: 15, color: colors.inkSoft, fontFamily: fonts.display, paddingBottom: 6 },
  tabActive: { color: colors.ink, borderBottomWidth: 2, borderBottomColor: colors.red },
  form: { gap: 10 },
  input: {
    height: 48,
    borderRadius: 14,
    borderWidth: 1,
    borderColor: colors.brownSoft,
    backgroundColor: colors.card,
    paddingHorizontal: 16,
    fontSize: 14,
    color: colors.ink,
    fontFamily: fonts.display,
  },
  error: { color: colors.red, fontSize: 12.5, fontFamily: fonts.display, marginTop: 2 },
  buttons: { gap: 12 },
});
