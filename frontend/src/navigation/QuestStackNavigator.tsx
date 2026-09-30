import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import ThemeSelectScreen from '../screens/ThemeSelectScreen';
import QuestMainScreen from '../screens/QuestMainScreen';
import QuestGpsScreen from '../screens/QuestGpsScreen';
import MissionScreen from '../screens/MissionScreen';
import { QuestStackParamList } from './types';

const Stack = createNativeStackNavigator<QuestStackParamList>();

// 퀘스트 탭: 테마 선택 -> 퀘스트 메인 -> GPS -> 미션. HomeStack과 화면 컴포넌트를 공유합니다.
export default function QuestStackNavigator() {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      <Stack.Screen name="ThemeSelect" component={ThemeSelectScreen} />
      <Stack.Screen name="QuestMain" component={QuestMainScreen} />
      <Stack.Screen name="QuestGps" component={QuestGpsScreen} />
      <Stack.Screen name="Mission" component={MissionScreen} />
    </Stack.Navigator>
  );
}
