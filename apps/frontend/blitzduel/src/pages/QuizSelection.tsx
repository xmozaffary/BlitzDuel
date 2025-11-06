import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {get} from "../services/servicebase.ts";
import {API_BASE_URL} from "../config/api.ts";
import {Spinner} from "../components/Spinner.tsx";

interface Quiz{
    id: number;
    name: string;
    description: string;
    questionCount: string;
}

const QuizSelection = () =>{

    const [quizzes, setQuizzes] = useState<Quiz[]>([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        fetchQuizzes();
    }, []);

    const fetchQuizzes = async () => {
        try {
            const data = await get<Quiz[]>(`${API_BASE_URL}/quizzes`)
            setQuizzes(data)
        } catch (error) {
            console.error('Failed to fetch Quizzes', error);
        } finally{
            setLoading(false);
        }
    };

    const selectQuiz = (quizId: number) =>{
        navigate(`/quiz/${quizId}`);
    };


    if (loading) return <Spinner />;


    return (
        <div className="quiz-selection">
            <h1>välja quiz</h1>
            <div className="quiz-list">
                {quizzes.map((quiz: Quiz) => (
                    <div key={quiz.id} className="quiz-card" onClick={() => selectQuiz(quiz.id)}>
                        <h2>{quiz.name}</h2>
                        <p>{quiz.description}</p>
                        <span>{quiz.questionCount} frågor</span>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default QuizSelection;