import { useEffect, useState } from "react";
import { get } from "../services/servicebase.ts";
import { config } from "../config/config.ts";
import { Spinner } from "../components/Spinner.tsx";
import { NotFound } from "./NotFound.tsx";
import { useNavigate, useParams } from "react-router-dom";

interface Quiz {
  id: number;
  title: string;
  description: string;
  totalQuestions: number;
  imgUrl: string;
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
    <div className="quiz-details-page">
      <div className="quiz-details-container">
        <div className="quiz-content">
          <div className="quiz-image-section">
            <button onClick={() => navigate("/")} className="back-btn">
              ← Tillbaka
            </button>
            <div className="quiz-image">
              {quiz.imgUrl ? (
                <img src={quiz.imgUrl} alt={quiz.title} />
              ) : (
                <div className="quiz-image-placeholder">
                  <span className="quiz-icon">🎯</span>
                </div>
              )}
            </div>
          </div>

          <div className="quiz-info-section">
            <div className="quiz-header">
              <h1>{quiz.title}</h1>
              <p className="quiz-description">{quiz.description}</p>
            </div>

            <div className="quiz-meta">
              <div className="meta-item">
                <span className="meta-icon">📝</span>
                <span className="meta-text">10 frågor</span>
              </div>
              <div className="meta-item">
                <span className="meta-icon">⏱️</span>
                <span className="meta-text">
                  5 sekunder per fråga
                </span>
              </div>
            </div>

            <div className="action-buttons">
              <button onClick={createLobby} className="btn-primary">
                <span className="btn-icon">🎮</span>
                Skapa Lobby
              </button>
              <button onClick={joinLobby} className="btn-secondary">
                <span className="btn-icon">🚪</span>
                Gå med i Lobby
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default QuizDetails;
