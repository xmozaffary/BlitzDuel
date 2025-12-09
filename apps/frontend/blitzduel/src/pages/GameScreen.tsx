import { useEffect, useState, useRef } from "react";
import { useParams } from "react-router-dom";
import type { QuestionData, ResultData } from "../types/GameData.ts";
import type { Client, IMessage } from "@stomp/stompjs";
import { useWebSocket } from "../hooks/useWebSocket.ts";
import GameOverScreen from "../components/GameOverScreen.tsx";
import { ScoreBoard } from "../components/ScoreBoard.tsx";
import { Spinner } from "../components/Spinner.tsx";
import { QuestionDisplay } from "../components/QuestionDisplay.tsx";
import { usePlayer } from "../contexts/PlayerContext.tsx";
import { QuestionTimer } from "../components/QuestionTimer.tsx";
import { useAuth } from "../contexts/AuthContext";

const GameScreen = () => {
  const { lobbyCode } = useParams<{ lobbyCode: string }>();
  const { user } = useAuth();
  const [client, setClient] = useState<Client | null>(null);
  const [currentQuestion, setCurrentQuestion] = useState<QuestionData | null>(
    null
  );
  const [result, setResult] = useState<ResultData | null>(null);
  const [gameOver, setGameOver] = useState(false);
  const { createClient, addLog } = useWebSocket();
  const { playerName, playerRole } = usePlayer();

  const [hostScore, setHostScore] = useState(0);
  const [guestScore, setGuestScore] = useState(0);

  const [hostName, setHostName] = useState<string>("");
  const [guestName, setGuestName] = useState<string>("");

  const [selectedAnswer, setSelectedAnswer] = useState<number | null>(null);
  const selectedAnswerRef = useRef<number | null>(null);
  const [answerState, setAnswerState] = useState<
    "selected" | "correct" | "incorrect" | null
  >(null);

  const [showResult, setShowResult] = useState(false);

  const [timeIsUp, setTimeIsUp] = useState(false);

  const [remainingTime, setRemainingTime] = useState(5);

  useEffect(() => {
    selectedAnswerRef.current = selectedAnswer;
  }, [selectedAnswer]);

  useEffect(() => {
    if (!lobbyCode) return;

    const newClient = createClient((client) => {
      addLog(` Connected to game ${lobbyCode}`);
      setClient(client);

      client.subscribe(`/topic/game/${lobbyCode}`, (message: IMessage) => {
        const data = JSON.parse(message.body);

        addLog(` Game message: ${data.type || data.status}`);

        if (data.type === "QUESTION") {
          setCurrentQuestion(data as QuestionData);
          setResult(null);
          setSelectedAnswer(null);
          setAnswerState(null);
          setShowResult(false);
          setTimeIsUp(false);
          setRemainingTime(5);

          if (data.hostName) setHostName(data.hostName);
          if (data.guestName) setGuestName(data.guestName);
        } else if (data.type === "TIMER_UPDATE") {
          setRemainingTime(data.remainingTime);

          if (data.remainingTime <= 0) {
            setTimeIsUp(true);
          }
        } else if (data.status === "BOTH_ANSWERED") {
          setResult(data as ResultData);
          setHostScore(data.hostScore);
          setGuestScore(data.guestScore);

          const isCorrect =
            selectedAnswerRef.current === data.correctAnswerIndex;

          setAnswerState(isCorrect ? "correct" : "incorrect");

          setShowResult(false);

          setTimeout(() => {
            setShowResult(true);
          }, 5000);
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
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [lobbyCode]);

  const handleAnswer = (answerIndex: number) => {
    if (!client || !lobbyCode || selectedAnswer !== null || timeIsUp) return;

    setSelectedAnswer(answerIndex);
    setAnswerState("selected");

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
    const playerNames = { host: hostName, guest: guestName };
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

  const isHost = playerRole === "host";
  const myScore = isHost ? hostScore : guestScore;
  const opponentScore = isHost ? guestScore : hostScore;
  const myName = playerName;
  const opponentName = isHost ? guestName || "Gäst" : hostName || "Värd";

  return (
    <div className="game-screen">
      <ScoreBoard
        playerName={myName}
        playerScore={myScore}
        opponentName={opponentName}
        opponentScore={opponentScore}
      />
      <div>
        {currentQuestion.timeLimit && currentQuestion.startTime && (
          <QuestionTimer
            remainingTime={remainingTime}
            onTimeUp={() => {
              console.log("time is up...");
              setTimeIsUp(true);
            }}
          />
        )}

        <QuestionDisplay
          questionText={currentQuestion.questionText}
          currentQuestionIndex={currentQuestion.currentQuestionIndex}
          options={currentQuestion.options}
          selectedAnswer={selectedAnswer}
          answerState={answerState}
          onAnswer={handleAnswer}
          isDisabled={timeIsUp}
          profilePicture={user?.profilePictureUrl}
          correctAnswerIndex={result?.correctAnswerIndex ?? null}
          opponentAnswerIndex={
            result
              ? isHost
                ? result.guestAnswerIndex
                : result.hostAnswerIndex
              : null
          }
        />
      </div>
    </div>
  );
};

export default GameScreen;
