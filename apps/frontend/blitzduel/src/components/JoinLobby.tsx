import { useState } from "react";
import { useWebSocket } from "../hooks/useWebSocket";
import type { IMessage } from "@stomp/stompjs";
import type {JoinLobbyRequest,  LobbyUpdate} from "../types/lobby";
import { Button } from "./Button";
import {useNavigate} from "react-router-dom";

const JoinLobby = () => {
  const [name, setName] = useState<string>("");
  const [joinCode, setJoinCode] = useState<string>("");
  const [joined, setJoined] = useState<boolean>(false);
  const { logs, addLog, createClient } = useWebSocket();
  const navigate = useNavigate();

  const handleJoinLobby = (): void => {
    if (!name || !joinCode) {
      alert("Enter nickname and lobby code!");
      return;
    }

    localStorage.setItem("playerName", name);

    const client = createClient((client) => {
      addLog(`✅ Connected to WebSocket`);

      client.subscribe("/user/queue/lobby", (message: IMessage) => {
        const data: LobbyUpdate = JSON.parse(message.body);
        if (data.status === "FULL") {
          addLog("❌ Lobby is full!");
          client.deactivate();
          return;
        }
      });

      client.subscribe(`/topic/lobby/${joinCode}`, (message: IMessage) => {
        const data: LobbyUpdate = JSON.parse(message.body);
        addLog(`📨 Lobby update: ${JSON.stringify(data)}`);
        setJoined(true);
      });

      client.subscribe(`/topic/lobby/${joinCode}/start`, () => {
          addLog("🎮 Game starting! Navigating...");
          navigate(`/game/${joinCode}`);
      });

      setTimeout(() => {
        const payLoad: JoinLobbyRequest = { name };
        client.publish({
          destination: `/app/lobby/${joinCode}/join`,
          body: JSON.stringify(payLoad),
        });

        addLog(`📤 Sent join request to ${joinCode}`);
      }, 200);
    });
    client.activate();
  };

  return (
    <div className="join-lobby">
      <h2>Join lobby</h2>

      <input
        type="text"
        placeholder="Ditt nickname"
        value={name}
        onChange={(e) => setName(e.target.value)}
      />

      <input
        type="text"
        placeholder="Lobby kod"
        value={joinCode}
        onChange={(e) => setJoinCode(e.target.value)}
      />

      <Button
        text="Joina lobby"
        onClick={handleJoinLobby}
        variant="secondary"
      />

      {joined && (
        <div className="success-massage">✅ Du har joinat lobbyn!</div>
      )}

      <div className="logs">
        <h3>Logs:</h3>
        {logs.map((log, idx) => (
          <div key={idx}>{log}</div>
        ))}
      </div>
    </div>
  );
};

export default JoinLobby;
