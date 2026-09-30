// kculture 백엔드(자바)의 DTO와 1:1로 맞춘 타입들입니다.
// 백엔드 record 필드가 바뀌면 여기도 같이 바꿔주세요.

export type QuestOriginType = 'CURATED' | 'FROM_SONG';
export type MissionType = 'PHOTO' | 'QUIZ';
export type StepStatus = 'LOCKED' | 'UNLOCKED' | 'DONE';
export type QuestProgressStatus = 'IN_PROGRESS' | 'COMPLETED';

export type QuestResponse = {
  id: number;
  title: string;
  themeRegion: string | null;
  themeEra: string | null;
  originType: QuestOriginType;
  description: string | null;
};

export type MissionResponse = {
  id: number;
  missionType: MissionType;
  question: string;
};

export type QuestStepResponse = {
  id: number;
  stepOrder: number;
  placeId: number;
  placeName: string;
  latitude: number;
  longitude: number;
  story: string;
  distanceHint: string | null;
  arrivalRadius: number;
  mission: MissionResponse;
};

export type QuestDetailResponse = {
  quest: QuestResponse;
  steps: QuestStepResponse[];
};

export type StepProgressResponse = {
  stepId: number;
  stepOrder: number;
  status: StepStatus;
  placeName: string;
};

export type QuestProgressResponse = {
  questId: number;
  questTitle: string;
  currentStep: number;
  status: QuestProgressStatus;
  steps: StepProgressResponse[];
};

// ===== content 도메인 =====
export type SongResponse = {
  id: number;
  title: string;
  artist: string;
  thumbnailUrl: string | null;
};

export type AnalysisStatusValue = 'PENDING' | 'RUNNING' | 'DONE' | 'FAILED';

export type AnalysisResponse = {
  id: number;
  songId: number;
  status: AnalysisStatusValue;
  modelName: string;
  startedAt: string | null;
  finishedAt: string | null;
};

export type CulturalCategoryValue = 'ARCHITECTURE' | 'COSTUME' | 'FOOD' | 'ART_RITUAL' | 'PROP';

export type CulturalElementResponse = {
  id: number;
  category: CulturalCategoryValue;
  name: string;
  description: string | null;
  timestampSec: number | null;
  confidence: number | null;
};

// ===== recommendation 도메인 =====
export type MatchResponse = {
  matchId: number;
  elementId: number;
  elementName: string;
  category: string | null;
  placeId: number;
  placeName: string;
  regionSido: string | null;
  regionSigungu: string | null;
  latitude: number | null;
  longitude: number | null;
  imageUrl: string | null;
  kakaoPlaceId: string | null;
  matchScore: number | null;
  reason: string | null;
};

export type SessionPlaceResponse = {
  id: number;
  displayOrder: number;
  chosen: boolean;
  shownAt: string | null;
  placeId: number;
  placeName: string;
  category: string | null;
  regionSido: string | null;
  regionSigungu: string | null;
  description: string | null;
  imageUrl: string | null;
  latitude: number | null;
  longitude: number | null;
  kakaoPlaceId: string | null;
  elementId: number | null;
  elementName: string | null;
  reason: string | null;
};

export type SessionResponse = {
  sessionId: number;
  songId: number;
  songTitle: string;
  status: string | null;
  places: SessionPlaceResponse[];
};

// ===== travel 도메인 =====
export type PlaceResponse = {
  id: number;
  name: string;
  regionSido: string | null;
  regionSigungu: string | null;
  category: string | null;
  description: string | null;
  imageUrl: string | null;
  latitude: number | null;
  longitude: number | null;
  kakaoPlaceId: string | null;
  visitorIndex: number | null;
  address: string | null;
};

export type AccommodationResponse = {
  id: number;
  name: string;
  accomType: string | null;
  priceRange: string | null;
  rating: number | null;
  regionSido: string | null;
  regionSigungu: string | null;
  latitude: number | null;
  longitude: number | null;
  address: string | null;
  phone: string | null;
  kakaoPlaceId: string | null;
  distanceMeters: number | null;
};

// ===== user 도메인 =====
export type UserResponse = {
  id: number;
  email: string;
  nickname: string;
  nationality: string | null;
  languagePref: string | null;
  provider: string | null;
};

export type UserUpdateRequest = {
  nickname?: string;
  nationality?: string;
  languagePref?: string;
};

// ===== quest 진행/스탬프 (MyQuestController) =====
export type StampResponse = {
  id: number;
  stepId: number;
  placeName: string;
  photoUrl: string | null;
  acquiredAt: string;
};

// ===== 퀘스트 진행(GPS/미션) =====
export type ArrivalResponse = {
  arrived: boolean;
  distanceMeters: number;
  arrivalRadius: number;
  story: string | null;
  mission: MissionResponse | null;
};
