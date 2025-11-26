import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { get } from "../services/servicebase.ts";
import { API_BASE_URL } from "../config/api.ts";
import { Spinner } from "../components/Spinner.tsx";
import { QuizCard } from "../components/QuizCard.tsx";

interface Quiz {
  id: number;
  title: string;
  description: string;
  category: string;
  totalQuestions: number;
  createdAt: string;
  imgUrl: string;
}

const Quizzes = () => {
  const [quizzes, setQuizzes] = useState<Quiz[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchQuizzes();
  }, []);

  const fetchQuizzes = async () => {
    try {
      const data = await get<Quiz[]>(`${API_BASE_URL}/quizzes`);
      setQuizzes(data);
    } catch (error) {
      console.error("Failed to fetch Quizzes", error);
    } finally {
      setLoading(false);
    }
  };

  const selectQuiz = (quizId: number) => {
    navigate(`/quiz/${quizId}`);
  };

  if (loading) return <Spinner />;

  return (
    <div className="quiz-selection">
      <h1>välja quiz</h1>
      <div className="quiz-list">
        {quizzes.map((quiz) => {
          return (
            <QuizCard
              key={quiz.id}
              title={quiz.title}
              imgUrl={quiz.imgUrl}
              onClick={() => selectQuiz(quiz.id)}
            />
          );
        })}
      </div>
    </div>
  );
};

export default Quizzes;
