import React, { useState } from 'react';
import { ActivityIndicator, Image, Pressable, ScrollView, StyleSheet, Text, TextInput, View } from 'react-native';
import * as ImagePicker from 'expo-image-picker';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import ScreenContainer from '../components/ScreenContainer';
import TopBar from '../components/TopBar';
import Card from '../components/Card';
import PrimaryButton from '../components/PrimaryButton';
import { colors } from '../theme/colors';
import { fonts } from '../theme/typography';
import { icons } from '../theme/assets';
import { QuestFlowParamList } from '../navigation/types';
import { completeMission } from '../api/questFlow';
import { fetchQuestDetail } from '../api/quest';
import { uploadPhoto } from '../api/upload';
import { useSession } from '../state/SessionContext';
import { DEV_USER_ID } from '../api/devUser';

type Props = NativeStackScreenProps<QuestFlowParamList, 'Mission'>;

// 목업 원본 CTA 문구 "퀘스트 시작하기"를 그대로 사용합니다(완료 버튼이 아님) - 실제 데이터가 없을 때만.
export default function MissionScreen({ navigation, route }: Props) {
  const params = route.params;
  const { user } = useSession();
  const userId = user?.id ?? DEV_USER_ID;

  const [photoUri, setPhotoUri] = useState<string | null>(null);
  const [answer, setAnswer] = useState('');
  const [busy, setBusy] = useState(false);
  const [busyLabel, setBusyLabel] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [done, setDone] = useState(false);

  const pickPhoto = async () => {
    setError(null);
    const { status } = await ImagePicker.requestCameraPermissionsAsync();
    if (status !== 'granted') {
      setError('카메라 권한을 허용해야 사진을 찍을 수 있어요.');
      return;
    }
    const result = await ImagePicker.launchCameraAsync({ quality: 0.6 });
    if (!result.canceled && result.assets && result.assets.length > 0) {
      setPhotoUri(result.assets[0].uri);
    }
  };

  // 다음 단계가 있으면 그 단계 GPS 화면으로, 없으면(퀘스트 완료) 스탬프북으로 이동한다.
  const goNextOrFinish = async () => {
    if (!params) {
      navigation.popToTop();
      return;
    }
    try {
      const detail = await fetchQuestDetail(params.questId);
      const currentIndex = detail.steps.findIndex((s) => s.id === params.stepId);
      const nextStep = currentIndex >= 0 ? detail.steps[currentIndex + 1] : undefined;

      if (nextStep && nextStep.latitude != null && nextStep.longitude != null) {
        navigation.replace('QuestGps', {
          questId: params.questId,
          stepId: nextStep.id,
          placeName: nextStep.placeName,
          latitude: Number(nextStep.latitude),
          longitude: Number(nextStep.longitude),
          arrivalRadius: nextStep.arrivalRadius,
        });
        return;
      }
    } catch {
      // 다음 단계 조회에 실패해도 일단 완료 흐름은 진행한다.
    }
    setDone(true);
  };

  const submitMission = async () => {
    if (!params) {
      navigation.popToTop();
      return;
    }
    if (params.missionType === 'PHOTO' && !photoUri) {
      setError('먼저 사진을 찍어주세요.');
      return;
    }
    if (params.missionType === 'QUIZ' && !answer.trim()) {
      setError('정답을 입력해주세요.');
      return;
    }

    setError(null);
    setBusy(true);
    try {
      let photoUrl: string | undefined;
      if (params.missionType === 'PHOTO' && photoUri) {
        setBusyLabel('사진 업로드 중...');
        photoUrl = await uploadPhoto(photoUri);
      }

      setBusyLabel('미션 완료 처리 중...');
      await completeMission(params.questId, params.stepId, userId, {
        latitude: params.latitude,
        longitude: params.longitude,
        answer: params.missionType === 'QUIZ' ? answer.trim() : undefined,
        photoUrl,
      });

      setBusyLabel('다음 장소 확인 중...');
      await goNextOrFinish();
    } catch (e: any) {
      setError(e?.message ?? '미션 완료에 실패했어요');
    } finally {
      setBusy(false);
      setBusyLabel('');
    }
  };

  if (done) {
    return (
      <ScreenContainer contentStyle={styles.content}>
        <TopBar title="퀘스트 완료!" onBack={() => navigation.popToTop()} />
        <Card style={styles.missionCard}>
          <Text style={styles.storyText}>모든 장소를 다 둘러봤어요. 스탬프북에서 확인해보세요!</Text>
        </Card>
        <PrimaryButton
          title="스탬프북으로 가기"
          onPress={() => navigation.getParent()?.navigate('Stampbook' as never)}
        />
      </ScreenContainer>
    );
  }

  const title = params?.placeName ? `${params.placeName} 도착!` : '차이나타운 도착!';
  const story =
    params?.story ??
    '100여 년 전, 이곳엔 청나라 상인들이 오가며 만든 거리가 있었어요. 오늘날 인천 차이나 타운의 골목마다 그 흔적이 남아있답니다.';

  return (
    <ScreenContainer contentStyle={styles.content}>
      <TopBar title={title} onBack={() => navigation.goBack()} />

      <ScrollView showsVerticalScrollIndicator={false} contentContainerStyle={styles.scroll}>
        <Card>
          <Text style={styles.cardLabel}>장소 이야기</Text>
          <Text style={styles.storyText}>{story}</Text>
        </Card>

        {(!params || params.missionType === 'PHOTO') && (
          <Card style={styles.missionCard}>
            <Text style={styles.missionLabel}>사진으로 미션을 인증해보세요</Text>
            {photoUri ? (
              <Image source={{ uri: photoUri }} style={styles.preview} />
            ) : (
              <Pressable style={styles.camCircle} onPress={params ? pickPhoto : undefined}>
                <Image source={icons.cam} style={styles.camIcon} />
              </Pressable>
            )}
            {photoUri && params && (
              <Text style={styles.retakeText} onPress={pickPhoto}>
                다시 찍기
              </Text>
            )}
          </Card>
        )}

        {params?.missionType === 'QUIZ' && (
          <Card style={styles.missionCard}>
            <Text style={styles.missionLabel}>{params.question ?? '정답을 입력해주세요'}</Text>
            <TextInput
              style={styles.answerInput}
              placeholder="정답"
              placeholderTextColor={colors.inkSoft}
              value={answer}
              onChangeText={setAnswer}
            />
          </Card>
        )}

        {error && <Text style={styles.errorText}>{error}</Text>}
        {busy && (
          <View style={styles.busyRow}>
            <ActivityIndicator color={colors.blue} />
            <Text style={styles.busyText}>{busyLabel}</Text>
          </View>
        )}
      </ScrollView>

      <PrimaryButton
        title={params ? (busy ? '처리 중...' : '미션 인증하기') : '퀘스트 시작하기'}
        onPress={params ? submitMission : () => navigation.popToTop()}
        disabled={busy}
      />
    </ScreenContainer>
  );
}

const styles = StyleSheet.create({
  content: { paddingBottom: 0 },
  scroll: { gap: 16, paddingBottom: 20 },
  cardLabel: { fontSize: 12.5, color: colors.inkSoft, fontFamily: fonts.display },
  storyText: { fontSize: 13.5, color: colors.ink, fontFamily: fonts.display, marginTop: 8, lineHeight: 20 },
  missionCard: { alignItems: 'center', paddingVertical: 28, gap: 18 },
  missionLabel: { fontSize: 14.5, color: colors.ink, fontFamily: fonts.display, textAlign: 'center' },
  camCircle: {
    width: 64,
    height: 64,
    borderRadius: 32,
    borderWidth: 2,
    borderStyle: 'dashed',
    borderColor: colors.brownSoft,
    alignItems: 'center',
    justifyContent: 'center',
  },
  camIcon: { width: 24, height: 24, tintColor: colors.inkSoft },
  preview: { width: 160, height: 160, borderRadius: 16 },
  retakeText: { color: colors.blue, fontFamily: fonts.display, fontSize: 12.5 },
  answerInput: {
    height: 46,
    width: '100%',
    borderRadius: 12,
    borderWidth: 1,
    borderColor: colors.brownSoft,
    backgroundColor: colors.card,
    paddingHorizontal: 14,
    fontFamily: fonts.display,
    color: colors.ink,
  },
  errorText: { color: colors.red, fontFamily: fonts.display, fontSize: 13, textAlign: 'center' },
  busyRow: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: 10 },
  busyText: { color: colors.inkSoft, fontFamily: fonts.display, fontSize: 12.5 },
});
