import { useState } from "react";
import { Button } from "../components/Button";
import type { LobbyRequest, LobbyResponse, LobbyUpdate } from "../types/lobby";
import { useWebSocket } from "../hooks/useWebSocket";
import type { IMessage } from "@stomp/stompjs";

const CreateLobby = () => {
  const [nickname, setNickname] = useState<string>("");
  const [lobbyCode, setLobbyCode] = useState<string>("");
  const { logs, addLog, createClient } = useWebSocket();

  const handleCreateLobby = (): void => {
    if (!nickname) {
      alert("Enter nickname");
      return;
    }

    const client = createClient((client) => {
      addLog(`✅ Connected to WebSocket`);

      client.subscribe("/user/queue/lobby", (message: IMessage) => {
        const data: LobbyResponse = JSON.parse(message.body);
        addLog(`📨 Lobby created: ${data.code}`);
        setLobbyCode(data.code);

        client.subscribe(
          `/topic/lobby/${data.code}`,
          (updateMessage: IMessage) => {
            const updateData: LobbyUpdate = JSON.parse(updateMessage.body);
            addLog(`📨 Lobby updated: ${JSON.stringify(updateData)}`);
          }
        );
      });

      const payLoad: LobbyRequest = { nickname };
      client.publish({
        destination: "/app/lobby/create",
        body: JSON.stringify(payLoad),
      });
      addLog(`📤 Sent create lobby request (${nickname})`);
    });

    client.activate();
  };

  return (
    <div className="create-lobby">
      <h1>Skapa Lobby</h1>
      <input
        type="text"
        placeholder="Ditt nickname"
        value={nickname}
        onChange={(e) => setNickname(e.target.value)}
      />

      <Button text="Skapa lobby" onClick={handleCreateLobby}></Button>

      {lobbyCode && <div className="lobby-code">Din koe: {lobbyCode}</div>}

      <div className="logs">
        <h3>Logs:</h3>
        {logs.map((log, idx) => (
          <div key={idx}>{log}</div>
        ))}
      </div>
    </div>
  );
};

export default CreateLobby;
