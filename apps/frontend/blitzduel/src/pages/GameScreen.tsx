import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import type { QuestionData, ResultData } from "../types/GameData.ts";
import type { Client, IMessage } from "@stomp/stompjs";
import { useWebSocket } from "../hooks/useWebSocket.ts";
import GameOverScreen from "./GameOverScreen";
import { ScoreBoard } from "../components/ScoreBoard.tsx";
import { Spinner } from "../components/Spinner.tsx";
import { AnswerButton } from "../components/AnswerButton.tsx";

const GameScreen = () => {
  const { lobbyCode } = useParams<{ lobbyCode: string }>();
  const [client, setClient] = useState<Client | null>(null);
  const [currentQuestion, setCurrentQuestion] = useState<QuestionData | null>(
    null
  );
  const [result, setResult] = useState<ResultData | null>(null);
  const [gameOver, setGameOver] = useState(false);
  const { createClient, addLog } = useWebSocket();
  const [playerName, setPlayerName] = useState<string>("");

  const [hostScore, setHostScore] = useState(0);
  const [guestScore, setGuestScore] = useState(0);

  const [selectedAnswer, setSelectedAnswer] = useState<number | null>(null);
  const [answerState, setAnswerState] = useState<
    "selected" | "correct" | "incorrect" | null
  >(null);

  const [showResult, setShowResult] = useState(false);

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
        console.log("object: ");
        console.log(data);

        addLog(` Game message: ${data.type || data.status}`);

        if (data.type === "QUESTION") {
          setCurrentQuestion(data as QuestionData);
          setResult(null);
          setSelectedAnswer(null);
          setAnswerState(null);
          setShowResult(false);
        } else if (data.status === "BOTH_ANSWERED") {
          setResult(data as ResultData);

          console.log("BOTH_ANSWERED");
          console.log(data.correctAnswerIndex);

          setHostScore(data.hostScore);
          setGuestScore(data.guestScore);

          console.log("innan is correct : " + selectedAnswer);

          const isCorrect = selectedAnswer === data.correctAnswerIndex;

          console.log("efter is correct : " + selectedAnswer);

          setAnswerState(isCorrect ? "correct" : "incorrect");

          setShowResult(false);

          setTimeout(() => {
            setShowResult(true);
          }, 3000);
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
  }, [lobbyCode, selectedAnswer]);

  const handleAnswer = (answerIndex: number) => {
    if (!client || !lobbyCode || selectedAnswer !== null) return;

    setSelectedAnswer(answerIndex);
    setAnswerState("selected");

    client.publish({
      destination: `/app/game/${lobbyCode}/answer`,
      body: JSON.stringify({
        name: playerName,
        answerIndex: answerIndex,
      }),
    });

    console.log("Du har klickad på: " + answerIndex);
    addLog(`Send answer: ${answerIndex}`);
  };

  if (gameOver && result) {
    const scores = { host: hostScore, guest: guestScore };
    const playerNames = { host: "host", guest: "guest" };
    return <GameOverScreen scores={scores} playerNames={playerNames} />;
  }

  if (result && showResult) {
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
        <div className="waiting-simple">
          <div className="pulse-icon">⚡</div>
          <h2>Matchen startar...</h2>
          <div className="loading-bar"></div>
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
            <AnswerButton
              key={index}
              option={option}
              index={index}
              onAnswer={handleAnswer}
              disabled={selectedAnswer !== null}
              isSelected={selectedAnswer == index}
              answerState={selectedAnswer === index ? answerState : null}
            />
          ))}
        </div>
      </div>
    </div>
  );
};

export default GameScreen;
