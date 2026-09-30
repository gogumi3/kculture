// 화면 이름과 파라미터 타입을 한 곳에 모아둡니다.
// 백엔드 연동 전까지는 대부분 optional/mock 데이터로 채워져 있어요.

export type RootStackParamList = {
  Onboarding: undefined;
  MainTabs: undefined;
};

// 실제 퀘스트 진행 중일 때 GPS 화면에 필요한 정보 (도착 판정에 쓰는 좌표/반경 포함)
export type QuestGpsParams =
  | {
      questId: number;
      stepId: number;
      placeName: string;
      latitude: number;
      longitude: number;
      arrivalRadius: number;
    }
  | undefined;

// 도착 후 미션(사진 인증/퀴즈) 화면에 필요한 정보
export type MissionParams =
  | {
      questId: number;
      stepId: number;
      placeName: string;
      story?: string | null;
      missionType: 'PHOTO' | 'QUIZ';
      question?: string | null;
      latitude: number;
      longitude: number;
    }
  | undefined;

export type HomeStackParamList = {
  HomeMain: undefined;
  Analysis: { songId?: number; songTitle: string; artist: string } | undefined;
  Place: { elementId?: number; songId?: number; songTitle?: string } | undefined;
  QuestMain: { questId?: number; questTitle?: string } | undefined;
  QuestGps: QuestGpsParams;
  Mission: MissionParams;
};

export type QuestStackParamList = {
  ThemeSelect: undefined;
  QuestMain: { questId?: number; questTitle?: string } | undefined;
  QuestGps: QuestGpsParams;
  Mission: MissionParams;
};

export type MainTabParamList = {
  Home: undefined;
  Quest: undefined;
  Stampbook: undefined;
  Lodging: undefined;
  MyPage: undefined;
};

// QuestMain / QuestGps / Mission 화면은 HomeStack(홈 카드에서 이어하기)과
// QuestStack(테마 선택에서 시작) 양쪽에 모두 등록되는 공용 화면이라, 두 파라미터 목록을 합쳐서 씁니다.
export type QuestFlowParamList = HomeStackParamList & QuestStackParamList;
