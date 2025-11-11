import { useState } from "react";
import { Button } from "../components/Button";
import type { LobbyRequest, LobbyResponse, LobbyUpdate } from "../types/lobby";
import { useWebSocket } from "../hooks/useWebSocket";
import type { IMessage } from "@stomp/stompjs";
import {useLocation} from "react-router-dom";

const CreateLobby = () => {
  const [nickname, setNickname] = useState<string>("");
  const [lobbyCode, setLobbyCode] = useState<string>("");
  const { logs, addLog, createClient } = useWebSocket();
  const location = useLocation();
  const quizId = location.state?.quizId;


  const handleCreateLobby = (): void => {
    if (!nickname) {
      alert("Enter nickname");
      return;
    }
    if(!quizId) {
        alert("No quiz selected! Go back and select a quiz.");
        return;
    }

    const client = createClient((client) => {
      addLog(`✅ Connected to WebSocket`);

      client.subscribe("/user/queue/lobby", (message: IMessage) => {
        const data: LobbyResponse = JSON.parse(message.body);
        addLog(`📨 Lobby created: ${data.code} quizId ${data.quizId}`);
        setLobbyCode(data.code);


        client.subscribe(
          `/topic/lobby/${data.code}`,
          (updateMessage: IMessage) => {
            const updateData: LobbyUpdate = JSON.parse(updateMessage.body);
            addLog(`📨 Lobby updated: ${JSON.stringify(updateData)}`);
          }
        );
      });

      const payLoad: LobbyRequest = { nickname, quizId: Number(quizId) };
      client.publish({
        destination: "/app/lobby/create",
        body: JSON.stringify(payLoad),
      });
      addLog(`📤 Sent create lobby request (${nickname})`);
    });

    client.activate();
  };

    const startGame = (): void => {
        console.log("Starta match");
    }


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
      </div><Button text="Starta match" variant="secondary" disabled={true} onClick={startGame} size="large"></Button>
    </div>
  );
};

export default CreateLobby;
