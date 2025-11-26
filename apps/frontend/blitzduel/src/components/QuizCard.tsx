interface QuizCardProps {
  title: string;
  imgUrl: string;
  onClick: () => void;
}

export const QuizCard = ({
  title,
  imgUrl,
  onClick,
}: QuizCardProps) => {
  return (
    <div className="quiz-card" onClick={onClick}>
      <img src={imgUrl}></img>
      <h2>{title}</h2>
      <span> 10 frågor</span>
    </div>
  );
};
