import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import HomeScreen from '../screens/HomeScreen';
import AnalysisScreen from '../screens/AnalysisScreen';
import PlaceScreen from '../screens/PlaceScreen';
import QuestMainScreen from '../screens/QuestMainScreen';
import QuestGpsScreen from '../screens/QuestGpsScreen';
import MissionScreen from '../screens/MissionScreen';
import { HomeStackParamList } from './types';

const Stack = createNativeStackNavigator<HomeStackParamList>();

// 홈 탭 안에서 검색 -> 분석 -> 장소 -> 퀘스트 진행 흐름으로 이어지는 스택.
// 탭바가 계속 보여야 해서(디자인 기준) 별도 Stack.Navigator를 Tab.Screen 안에 둡니다.
export default function HomeStackNavigator() {
  return (
    <Stack.Navigator screenOptions={{ headerShown: false }}>
      <Stack.Screen name="HomeMain" component={HomeScreen} />
      <Stack.Screen name="Analysis" component={AnalysisScreen} />
      <Stack.Screen name="Place" component={PlaceScreen} />
      <Stack.Screen name="QuestMain" component={QuestMainScreen} />
      <Stack.Screen name="QuestGps" component={QuestGpsScreen} />
      <Stack.Screen name="Mission" component={MissionScreen} />
    </Stack.Navigator>
  );
}
