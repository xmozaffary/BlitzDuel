import { createContext, useContext, useState, type ReactNode } from "react";

interface PlayerContextType {
  playerName: string;
  setPlayerName: (name: string) => void;
  playerRole: "host" | "guest" | null;
  setPlayerRole: (role: "host" | "guest" | null) => void;
}

const PlayerContext = createContext<PlayerContextType>({
  playerName: "",
  setPlayerName: () => {},
  playerRole: null,
  setPlayerRole: () => {},
});

export const PlayerProvider = ({ children }: { children: ReactNode }) => {
  const [playerName, setPlayerName] = useState<string>("");
  const [playerRole, setPlayerRole] = useState<"host" | "guest" | null>(null);

  return (
    <PlayerContext.Provider
      value={{ playerName, setPlayerName, playerRole, setPlayerRole }}
    >
      {children}
    </PlayerContext.Provider>
  );
};

// eslint-disable-next-line react-refresh/only-export-components
export const usePlayer = () => {
  const context = useContext(PlayerContext);
  if (!context) {
    throw new Error("usePlayer must be used within PlayerProvider");
  }
  return context as PlayerContextType;
};
