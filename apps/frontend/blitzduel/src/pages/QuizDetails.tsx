import { useEffect, useState } from "react";
import { get } from "../services/servicebase.ts";
import { config } from "../config/config.ts";
import { Spinner } from "../components/Spinner.tsx";
import { NotFound } from "./NotFound.tsx";
import { useNavigate, useParams } from "react-router-dom";

interface Quiz {
  id: number;
  name: string;
  description: string;
  questionCount: number;
}

const QuizDetails = () => {
  const { quizId } = useParams<{ quizId: string }>();
  const [quiz, setQuiz] = useState<Quiz | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchQuizDetails();
  }, [quizId]);

  const fetchQuizDetails = async (): Promise<void> => {
    try {
      const data = await get<Quiz>(`${config.apiBaseUrl}/quizzes/${quizId}`);
      setQuiz(data);
    } catch (error) {
      console.error("Failed to fetch quiz", error);
    } finally {
      setLoading(false);
    }
  };

  const createLobby = (): void => {
    navigate("/lobby/create", { state: { quizId } });
  };

  const joinLobby = (): void => {
    navigate("/lobby/join", { state: { quizId } });
  };

  if (loading) return <Spinner />;
  if (!quiz) return <NotFound />;

  return (
    <div className="quiz-details-container">
      <div className="quiz-details-card">
        <button onClick={() => navigate("/")} className="back-button">
          ← Tillbaka
        </button>

        <div className="quiz-info">
          <h1>{quiz.name}</h1>
          <p className="description">{quiz.description}</p>
          <div className="meta-info">
            <span className="question-count">
              📝 {quiz.questionCount} frågor
            </span>
          </div>
        </div>

        <div className="action-buttons">
          <button onClick={createLobby} className="btn-create-lobby">
            Skapa Lobby
          </button>
          <button onClick={joinLobby} className="btn-join-lobby">
            Gå med i Lobby
          </button>
        </div>
      </div>
    </div>
  );
};

export default QuizDetails;
