import { useState } from "react";
import { useWebSocket } from "../hooks/useWebSocket";
import type { IMessage } from "@stomp/stompjs";
import type { LobbyRequest, LobbyUpdate } from "../types/lobby";
import { Button } from "./Button";

const JoinLobby = () => {
  const [nickname, setNickname] = useState<string>("");
  const [joinCode, setJoinCode] = useState<string>("");
  const [joined, setJoined] = useState<boolean>(false);
  const { logs, addLog, createClient } = useWebSocket();

  const handleJoinLobby = (): void => {
    if (!nickname || !joinCode) {
      alert("Enter nickname and lobby code!");
      return;
    }

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

      setTimeout(() => {
        const payLoad: LobbyRequest = { nickname };
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
        value={nickname}
        onChange={(e) => setNickname(e.target.value)}
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
