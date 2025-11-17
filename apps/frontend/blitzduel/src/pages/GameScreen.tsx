import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import type {QuestionData, ResultData} from "../types/GameData.ts";
import type { Client, IMessage} from "@stomp/stompjs";
import {useWebSocket} from "../hooks/useWebSocket.ts";

const GameScreen = () =>{
    const {lobbyCode} = useParams<{lobbyCode: string}>();
    const [client, setClient] = useState<Client | null>(null);
    const [currentQuestion, setCurrentQuestion] = useState<QuestionData | null>(null);
    const [result, setResult] = useState<ResultData | null>(null);
    const [gameOver, setGameOver] = useState(false);
    const {createClient, addLog, logs} = useWebSocket();
    const [playerName, setPlayerName] = useState<string>("");


    useEffect(() => {
        const name = sessionStorage.getItem("playerName") ?? "Unknown";
        setPlayerName(name);
    }, []);

    useEffect(() => {
        if(!lobbyCode) return;

        const newClient = createClient((client) => {
            addLog(` Connected to game ${lobbyCode}`);
            setClient(client);

            client.subscribe(`/topic/game/${lobbyCode}`, (message: IMessage) => {
                const data = JSON.parse(message.body);
                console.log(data);

                addLog(` Game message: ${data.type || data.status}`);

                if(data.type === "QUESTION") {
                    setCurrentQuestion(data as QuestionData);
                    setResult(null);
                } else if (data.status === "BOTH_ANSWERED") {
                    setResult(data as ResultData);
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
    },[lobbyCode]);

    const handleAnswer = (answerIndex: number) => {
        if (!client || !lobbyCode) return;

        client.publish({
            destination: `/app/game/${lobbyCode}/answer`,
            body: JSON.stringify({
                name: playerName,
                answerIndex: answerIndex
            }),
        });

        addLog(`Send answer: ${answerIndex}`);

    }

    if(gameOver && result) {
        return (
            <div className="game-screen">
                <h1> Spelet Över!</h1>
                <h2>Slutresultat</h2>
                <p>Host: : {result.hostScore}</p>
                <p>Guest: {result.guestScore}</p>
                <h3>
                    Winner: {" "}
                    {result.hostScore > result.guestScore
                    ? "Host"
                    : result.guestScore > result.hostScore
                    ? "Guest"
                    : "Oavgjort!"}
                </h3>
            </div>
        );
    }

    if(result) {
        return (
            <div className="game-screen">
                <h2>Resultat</h2>
                <p>Svaret: {result.correctAnswerIndex}</p>
                <p>Spelare 1: {result.hostCorrect ? "RÄTT!" : "FEL!"}</p>
                <p>Spelare 2: {result.guestCorrect ? "RÄTT!" : "FEL!"}</p>
                <h3>Poäng</h3>
                <p>Spelare 1: {result.hostScore}</p>
                <p>Spelare 2: {result.guestScore}</p>
                <p>Väntar på nästa frågan...</p>
            </div>
        );
    }

    if(!currentQuestion) {
        return (
            <div className ="game-screen">
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
            <h2>Fråga {currentQuestion.currentQuestionIndex + 1} / 10</h2>
            <h3>{currentQuestion.questionText}</h3>
            <div className="options">
                {currentQuestion.options.map((option, index) => (
                    <button key={index} onClick={() => handleAnswer(index)}>
                    {option}
                        </button>
                ))}
            </div>
             <div className="logs">
                 {logs.map((log, idx) => (
                     <div key={idx}>{log}</div>
                 ))}
             </div>
        </div>
    );
};

export default GameScreen;