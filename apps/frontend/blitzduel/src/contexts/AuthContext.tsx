import {
  createContext,
  useContext,
  useEffect,
  useState,
  type ReactNode,
} from "react";
import { fetchUserInfo, type UserInfo } from "../services/fetchUserInfo";

interface AuthContextType {
  user: UserInfo | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  login: (token: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<UserInfo | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      const token = localStorage.getItem("jwt");

      if (token) {
        try {
          const UserInfo = await fetchUserInfo();
          setUser(UserInfo);
        } catch (error) {
          console.error("Failed to fetch user info:", error);
          localStorage.removeItem("jwt");
        }
      }
      setIsLoading(false);
    };

    initAuth();
  }, []);

  const login = async (token: string) => {
    localStorage.setItem("jwt", token);
    const UserInfo = await fetchUserInfo();
    setUser(UserInfo);
  };

  const logout = () => {
    localStorage.removeItem("jwt");
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isLoading,
        isAuthenticated: !!user,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within AuthProvider");
  }
  return context;
};
