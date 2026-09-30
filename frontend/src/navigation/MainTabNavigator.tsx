import React from 'react';
import { Image, StyleSheet, Text, View } from 'react-native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import HomeStackNavigator from './HomeStackNavigator';
import QuestStackNavigator from './QuestStackNavigator';
import StampbookScreen from '../screens/StampbookScreen';
import LodgingScreen from '../screens/LodgingScreen';
import MyPageScreen from '../screens/MyPageScreen';
import { colors } from '../theme/colors';
import { fonts } from '../theme/typography';
import { icons } from '../theme/assets';
import { MainTabParamList } from './types';

const Tab = createBottomTabNavigator<MainTabParamList>();

const tabIcons: Record<keyof MainTabParamList, any> = {
  Home: icons.home,
  Quest: icons.quest,
  Stampbook: icons.stamp,
  Lodging: icons.bed,
  MyPage: icons.user,
};

const tabLabels: Record<keyof MainTabParamList, string> = {
  Home: '홈',
  Quest: '퀘스트',
  Stampbook: '스탬프북',
  Lodging: '숙박',
  MyPage: '마이',
};

function TabIcon({ routeName, focused }: { routeName: keyof MainTabParamList; focused: boolean }) {
  return (
    <View style={styles.tabItem}>
      <Image
        source={tabIcons[routeName]}
        style={[styles.icon, { tintColor: focused ? colors.blue : colors.inkSoft }]}
      />
      <Text style={[styles.label, { color: focused ? colors.blue : colors.inkSoft }]}>{tabLabels[routeName]}</Text>
    </View>
  );
}

// 하단 탭바 5개: 홈/퀘스트는 각각 자체 스택을 갖고, 나머지 3개는 단일 화면입니다.
export default function MainTabNavigator() {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        headerShown: false,
        tabBarShowLabel: false,
        tabBarStyle: styles.tabBar,
        tabBarIcon: ({ focused }) => <TabIcon routeName={route.name} focused={focused} />,
      })}
    >
      <Tab.Screen name="Home" component={HomeStackNavigator} />
      <Tab.Screen name="Quest" component={QuestStackNavigator} />
      <Tab.Screen name="Stampbook" component={StampbookScreen} />
      <Tab.Screen name="Lodging" component={LodgingScreen} />
      <Tab.Screen name="MyPage" component={MyPageScreen} />
    </Tab.Navigator>
  );
}

const styles = StyleSheet.create({
  tabBar: {
    height: 68,
    paddingTop: 8,
    backgroundColor: colors.card,
    borderTopWidth: 1,
    borderTopColor: colors.brownSoft,
  },
  tabItem: { alignItems: 'center', gap: 4 },
  icon: { width: 22, height: 22 },
  label: { fontSize: 10.5, fontFamily: fonts.display },
});
