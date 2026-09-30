import React, { createContext, useContext, useMemo, useState } from 'react';
import { UserResponse } from '../api/types';

// 로그인/회원가입 후 받은 사용자 정보를 앱 전체에서 쓸 수 있게 해주는 간단한 세션 상태예요.
// (AsyncStorage 등 영구 저장은 아직 없어서, 앱을 완전히 재시작하면 다시 로그인해야 해요.)
type SessionContextValue = {
  user: UserResponse | null;
  setUser: (user: UserResponse | null) => void;
};

const SessionContext = createContext<SessionContextValue | undefined>(undefined);

export function SessionProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<UserResponse | null>(null);
  const value = useMemo(() => ({ user, setUser }), [user]);
  return <SessionContext.Provider value={value}>{children}</SessionContext.Provider>;
}

export function useSession() {
  const ctx = useContext(SessionContext);
  if (!ctx) {
    throw new Error('useSession은 SessionProvider 안에서만 사용할 수 있어요.');
  }
  return ctx;
}
