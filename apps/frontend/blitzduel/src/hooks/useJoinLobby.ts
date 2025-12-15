import { useState } from "react";
import { useWebSocket } from "./useWebSocket";
import type { IMessage } from "@stomp/stompjs";
import type { JoinLobbyRequest, LobbyUpdate } from "../types/lobby";
import { useNavigate } from "react-router-dom";
import { usePlayer } from "../contexts/PlayerContext";
import { useAuth } from "../contexts/AuthContext";
import { playSound } from "../utils/playSound";

export const useJoinLobby = () => {
  const { user } = useAuth();
  const { setPlayerName, setPlayerRole } = usePlayer();
  const [lobbyCode, setLobbyCode] = useState<string>("");
  const [isJoining, setIsJoining] = useState<boolean>(false);
  const [joined, setJoined] = useState<boolean>(false);
  const [error, setError] = useState<string>("");
  const [hostName, setHostName] = useState<string>("");
  const { addLog, createClient } = useWebSocket();
  const navigate = useNavigate();

  const joinLobby = () => {
    if (!user || !lobbyCode.trim()) {
      setError("Ange en lobby-kod");
      return;
    }

    setIsJoining(true);
    setError("");
    setPlayerName(user.name);
    setPlayerRole("guest");

    const client = createClient((client) => {
      addLog(`✅ Connected to WebSocket`);

      client.subscribe("/user/queue/lobby", (message: IMessage) => {
        const data: LobbyUpdate = JSON.parse(message.body);
        if (data.status === "FULL") {
          setError("Lobbyn är full!");
          setIsJoining(false);
          client.deactivate();
          return;
        }
      });

      client.subscribe(`/topic/lobby/${lobbyCode}`, (message: IMessage) => {
        const data: LobbyUpdate = JSON.parse(message.body);
        addLog(`📨 Lobby update: ${JSON.stringify(data)}`);

        if (data.hostName) {
          setHostName(data.hostName);
        }

        setJoined(true);
        setIsJoining(false);
        playSound("tjoho-v2.wav");
      });

      client.subscribe(`/topic/lobby/${lobbyCode}/start`, () => {
        addLog("🎮 Game starting!");
        navigate(`/game/${lobbyCode}`);
      });

      setTimeout(() => {
        const payload: JoinLobbyRequest = { name: user.name, userId: user.id };
        client.publish({
          destination: `/app/lobby/${lobbyCode}/join`,
          body: JSON.stringify(payload),
        });
        addLog(`📤 Sent join request to ${lobbyCode}`);
      }, 200);
    });

    client.activate();
  };

  const cancelJoin = () => {
    navigate("/");
  };

  const updateLobbyCode = (code: string) => {
    setLobbyCode(code.toUpperCase());
    setError("");
  };

  return {
    lobbyCode,
    isJoining,
    joined,
    error,
    hostName,
    joinLobby,
    cancelJoin,
    updateLobbyCode,
  };
};
