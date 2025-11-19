interface AnswerButtonProps {
  option: string;
  index: number;
  onAnswer: (index: number) => void;
  disabled?: boolean;
  isSelected?: boolean;
  answerState?: "selected" | "correct" | "incorrect" | null;
}

export const AnswerButton = ({
  option,
  index,
  onAnswer,
  disabled = false,
  isSelected = false,
  answerState = null,
}: AnswerButtonProps) => {
  const handleClick = () => {
    if (disabled) return;
    onAnswer(index);
  };

  const getButtonClass = () => {
    if (answerState === "correct") return "answer-button--correct";
    if (answerState === "incorrect") return "answer-button--incorrect";
    if (answerState === "selected") return "answer-button--selected";
    return "";
  };

  return (
    <button
      className={`answer-button ${getButtonClass()}`}
      onClick={handleClick}
      disabled={disabled || isSelected}
    >
      {option}
    </button>
  );
};
