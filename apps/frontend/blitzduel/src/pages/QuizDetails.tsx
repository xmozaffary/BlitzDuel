import { useEffect, useState } from "react";
import { get } from "../services/servicebase.ts";
import { config } from "../config/config.ts";
import { Spinner } from "../components/Spinner.tsx";
import { NotFound } from "./NotFound.tsx";
import { useNavigate, useParams } from "react-router-dom";
import { Button } from "../components/Button.tsx";

interface Quiz {
  id: number;
  name: string;
  description: string;
  questionCount: number;
}

const QuizDetails = () => {
  const { quizId } = useParams<{ quizId: string }>();
  const [quiz, setQuiz] = useState<Quiz | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchQuizDetails();
  }, [quizId]);

  const fetchQuizDetails = async (): Promise<void> => {
    try {
      const data = await get<Quiz>(`${config.apiBaseUrl}/quizzes/${quizId}`);
      setQuiz(data);
    } catch (error) {
      console.error("Faild to fetch quiz", error);
    } finally {
      setLoading(false);
    }
  };

  const createLobby = (): void => {
    navigate("/lobby/create", { state: { quizId } });
  };

  const joinLobby = (): void => {
    navigate("/lobby/join");
  };

  if (loading) return <Spinner />;
  if (!quiz) return <NotFound />;

  return (
    <div className="quiz-details">
      <h1>{quiz.name}</h1>
      <p>{quiz.description}</p>
      <span>{quiz.questionCount}</span>

      <div className="actions">
        <Button text="Skapa Lobby" onClick={createLobby} />
        <Button text="Joina Lobby" onClick={joinLobby} variant="secondary" />
      </div>
    </div>
  );
};

export default QuizDetails;
