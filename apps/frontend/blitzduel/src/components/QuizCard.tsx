interface QuizCardProps {
  title: string;
  description: string;
  totalQuestions: number;
  onClick: () => void;
}

export const QuizCard = ({
  title,
  description,
  totalQuestions,
  onClick,
}: QuizCardProps) => {
  return (
    <div className="quiz-card" onClick={onClick}>
      <h2>{title}</h2>
      <p>{description}</p>
      <span>{totalQuestions} frågor</span>
    </div>
  );
};
