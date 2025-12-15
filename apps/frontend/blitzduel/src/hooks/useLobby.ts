import { useState, useEffect } from "react";
import type { LobbyRequest, LobbyResponse, LobbyUpdate } from "../types/lobby";
import { useWebSocket } from "./useWebSocket";
import type { Client, IMessage } from "@stomp/stompjs";
import { useNavigate } from "react-router-dom";
import { usePlayer } from "../contexts/PlayerContext";
import { useAuth } from "../contexts/AuthContext";
import { playSound } from "../utils/playSound";

export const useLobby = (quizId: number | null, autoCreate = false) => {
  const { user } = useAuth();
  const { setPlayerName, setPlayerRole } = usePlayer();
  const [lobbyCode, setLobbyCode] = useState<string>("");
  const [isCreating, setIsCreating] = useState(false);
  const [waitingForPlayers, setWaitingForPlayers] = useState(false);
  const [guestName, setGuestName] = useState<string | null>(null);
  const { addLog, createClient } = useWebSocket();
  const navigate = useNavigate();
  const [client, setClient] = useState<Client | null>(null);

  const createLobby = () => {
    if (!user || !quizId) {
      return;
    }

    setIsCreating(true);
    setPlayerName(user.name);
    setPlayerRole("host");

    const newClient = createClient((client) => {
      addLog(`✅ Connected to WebSocket`);
      setClient(client);

      client.subscribe("/user/queue/lobby", (message: IMessage) => {
        const data: LobbyResponse = JSON.parse(message.body);
        addLog(`📨 Lobby created: ${data.lobbyCode}`);
        setLobbyCode(data.lobbyCode);
        setIsCreating(false);
        setWaitingForPlayers(true);

        client.subscribe(
          `/topic/lobby/${data.lobbyCode}`,
          (updateMessage: IMessage) => {
            const updateData: LobbyUpdate = JSON.parse(updateMessage.body);
            addLog(`📨 Guest joined: ${JSON.stringify(updateData)}`);

            // Check if status changed to READY (guest joined)
            if (updateData.status === "READY") {
              setWaitingForPlayers(false);
              // You might need to extract guest name from updateData
              // This depends on your backend response structure
              setGuestName(updateData.guestName || "Gäst");
              playSound("tjoho");
            }
          }
        );
      });

      const payload: LobbyRequest = {
        name: user.name,
        quizId: Number(quizId),
        userId: user.id,
      };
      client.publish({
        destination: "/app/lobby/create",
        body: JSON.stringify(payload),
      });
    });

    newClient.activate();
  };

  // Auto-create lobby on mount if autoCreate is true
  useEffect(() => {
    if (autoCreate && user && quizId && !lobbyCode && !isCreating) {
      createLobby();
    }
  }, [autoCreate, user, quizId]);

  const startGame = () => {
    if (!lobbyCode || !client) {
      return;
    }

    navigate(`/game/${lobbyCode}`);

    setTimeout(() => {
      client.publish({
        destination: `/app/game/${lobbyCode}/start`,
        body: JSON.stringify({}),
      });
    }, 500);
  };

  const copyLobbyCode = () => {
    navigator.clipboard.writeText(lobbyCode);
  };

  const cancelLobby = () => {
    if (client) {
      client.deactivate();
    }
    navigate("/");
  };

  return {
    lobbyCode,
    isCreating,
    waitingForPlayers,
    guestName,
    createLobby,
    startGame,
    copyLobbyCode,
    cancelLobby,
  };
};
