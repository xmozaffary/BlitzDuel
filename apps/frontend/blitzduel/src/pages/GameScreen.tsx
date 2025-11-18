import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import type { QuestionData, ResultData } from "../types/GameData.ts";
import type { Client, IMessage } from "@stomp/stompjs";
import { useWebSocket } from "../hooks/useWebSocket.ts";
import GameOverScreen from "./GameOverScreen";
import { ScoreBoard } from "../components/ScoreBoard.tsx";
import { Spinner } from "../components/Spinner.tsx";

const GameScreen = () => {
  const { lobbyCode } = useParams<{ lobbyCode: string }>();
  const [client, setClient] = useState<Client | null>(null);
  const [currentQuestion, setCurrentQuestion] = useState<QuestionData | null>(
    null
  );
  const [result, setResult] = useState<ResultData | null>(null);
  const [gameOver, setGameOver] = useState(false);
  const { createClient, addLog, logs } = useWebSocket();
  const [playerName, setPlayerName] = useState<string>("");

  const [hostScore, setHostScore] = useState(0);
  const [guestScore, setGuestScore] = useState(0);

  useEffect(() => {
    const name = sessionStorage.getItem("playerName") ?? "Unknown";
    setPlayerName(name);
  }, []);

  useEffect(() => {
    if (!lobbyCode) return;

    const newClient = createClient((client) => {
      addLog(` Connected to game ${lobbyCode}`);
      setClient(client);

      client.subscribe(`/topic/game/${lobbyCode}`, (message: IMessage) => {
        const data = JSON.parse(message.body);
        console.log(data);

        addLog(` Game message: ${data.type || data.status}`);

        if (data.type === "QUESTION") {
          setCurrentQuestion(data as QuestionData);
          setResult(null);
        } else if (data.status === "BOTH_ANSWERED") {
          setResult(data as ResultData);
          setHostScore(data.hostScore);
          setGuestScore(data.guestScore);
          setCurrentQuestion(null);
        } else if (data.status === "GAME_OVER") {
          setGameOver(true);
          setResult(data as ResultData);
        }
      });
    });

    newClient.activate();

    return () => {
      newClient.deactivate();
    };
  }, [lobbyCode]);

  const handleAnswer = (answerIndex: number) => {
    if (!client || !lobbyCode) return;

    client.publish({
      destination: `/app/game/${lobbyCode}/answer`,
      body: JSON.stringify({
        name: playerName,
        answerIndex: answerIndex,
      }),
    });

    addLog(`Send answer: ${answerIndex}`);
  };

  if (gameOver && result) {
    const scores = { host: hostScore, guest: guestScore };
    const playerNames = { host: "host", guest: "guest" };
    return <GameOverScreen scores={scores} playerNames={playerNames} />;
  }

  if (result) {
    return (
      <div className="game-screen">
        <div className="loading-wrapper">
          <Spinner />
        </div>
      </div>
    );
  }

  if (!currentQuestion) {
    return (
      <div className="game-screen">
        <h2>Väntar på att matchen ska starta...</h2>
        <div className="logs">
          {logs.map((log, idx) => (
            <div key={idx}>{log}</div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="game-screen">
      <ScoreBoard playerScore={hostScore} opponentScore={guestScore} />
      <div className="question-content">
        <div className="question-header">
          <span className="question-number">
            Fråga {currentQuestion.currentQuestionIndex + 1} / 10
          </span>
        </div>

        <h2 className="question-text">{currentQuestion.questionText}</h2>

        <div className="options-grid">
          {currentQuestion.options.map((option, index) => (
            <button
              className="option-button"
              key={index}
              onClick={() => handleAnswer(index)}
            >
              {option}
            </button>
          ))}
        </div>
        {/* <div className="logs">
          {logs.map((log, idx) => (
            <div key={idx}>{log}</div>
          ))}
        </div> */}
      </div>
    </div>
  );
};

export default GameScreen;
