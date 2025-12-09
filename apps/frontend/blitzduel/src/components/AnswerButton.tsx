interface AnswerButtonProps {
  option: string;
  index: number;
  onAnswer: (index: number) => void;
  disabled?: boolean;
  isSelected?: boolean;
  answerState?: "selected" | "correct" | "incorrect" | null;
  profilePicture?: string;
  correctAnswerIndex?: number | null;
  opponentAnswerIndex?: number | null;
}

export const AnswerButton = ({
  option,
  index,
  onAnswer,
  disabled = false,
  isSelected = false,
  answerState = null,
  profilePicture,
  correctAnswerIndex,
  opponentAnswerIndex,
}: AnswerButtonProps) => {
  const handleClick = () => {
    if (disabled) return;
    onAnswer(index);
  };

  const getButtonClass = () => {
    if (answerState === "correct") return "answer-button--correct";
    if (answerState === "incorrect") return "answer-button--incorrect";
    if (correctAnswerIndex === index) return "answer-button--correct";
    if (answerState === "selected") return "answer-button--selected";
    return "";
  };

  const isOpponentAnswer = opponentAnswerIndex === index;

  return (
    <button
      className={`answer-button ${getButtonClass()}`}
      onClick={handleClick}
      disabled={disabled || isSelected}
    >
      {option}
      {isSelected && profilePicture && (
        <img src={profilePicture} alt="Du" className="answer-profile-pic" />
      )}

      {isOpponentAnswer && <span className="opponent-indicator">👤</span>}
    </button>
  );
};
