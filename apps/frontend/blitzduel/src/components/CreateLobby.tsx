import { useState } from "react";
import { Button } from "../components/Button";
import type { LobbyRequest, LobbyResponse, LobbyUpdate } from "../types/lobby";
import { useWebSocket } from "../hooks/useWebSocket";
import type { Client, IMessage } from "@stomp/stompjs";
import {useLocation, useNavigate} from "react-router-dom";

const CreateLobby = () => {
  const [name, setName] = useState<string>("");
  const [lobbyCode, setLobbyCode] = useState<string>("");
  const { logs, addLog, createClient } = useWebSocket();
  const location = useLocation();
  const navigate = useNavigate();
  const quizId = location.state?.quizId;
  const [client, setClient] = useState<Client | null>(null);



    const handleCreateLobby = (): void => {
    if (!name) {
      alert("Enter nickname");
      return;
    }
    localStorage.setItem("playerName", name);

    if(!quizId) {
        alert("No quiz selected! Go back and select a quiz.");
        return;
    }

    const newClient = createClient((client) => {
      addLog(`✅ Connected to WebSocket`);

        setClient(client);

      client.subscribe("/user/queue/lobby", (message: IMessage) => {
        const data: LobbyResponse = JSON.parse(message.body);
        addLog(`📨 Lobby created: ${data.lobbyCode} quizId ${data.quizId}`);
        setLobbyCode(data.lobbyCode);


        client.subscribe(
          `/topic/lobby/${data.lobbyCode}`,
          (updateMessage: IMessage) => {
            const updateData: LobbyUpdate = JSON.parse(updateMessage.body);
            addLog(`📨 Lobby updated: ${JSON.stringify(updateData)}`);
          }
        );
      });

      const payLoad: LobbyRequest = { name, quizId: Number(quizId) };
      client.publish({
        destination: "/app/lobby/create",
        body: JSON.stringify(payLoad),
      });
      addLog(`📤 Sent create lobby request (${name})`);
    });

    newClient.activate();
  };

    const startGame = (): void => {
        if (!lobbyCode){
            alert("No lobby code");
            return;
        }

        if (!client){
            alert("No client found!");
            return;
        }

        navigate(`/game/${lobbyCode}`)

        setTimeout(() => {
            client.publish({
                destination: `/app/game/${lobbyCode}/start`,
                body: JSON.stringify({}),
            })
            addLog("Starting game...");
        }, 500)
    };

    return (
    <div className="create-lobby">
      <h1>Skapa Lobby</h1>
      <input
        type="text"
        placeholder="Ditt nickname"
        value={name}
        onChange={(e) => setName(e.target.value)}
      />

      <Button text="Skapa lobby" onClick={handleCreateLobby}></Button>

      {lobbyCode && <div className="lobby-code">Din kod: {lobbyCode}</div>}

      <div className="logs">
        <h3>Logs:</h3>
        {logs.map((log, idx) => (
          <div key={idx}>{log}</div>
        ))}
      </div><Button text="Starta match" variant="secondary"  onClick={startGame} size="large"></Button>
    </div>
  );
};

export default CreateLobby;
