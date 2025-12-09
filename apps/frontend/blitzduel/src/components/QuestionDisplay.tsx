import { AnswerButton } from "./AnswerButton";

interface QuestionDisplayProps {
  questionText: string;
  currentQuestionIndex: number;
  options: string[];
  selectedAnswer: number | null;
  answerState: "selected" | "correct" | "incorrect" | null;
  onAnswer: (index: number) => void;
  isDisabled: boolean;
  profilePicture?: string;
  correctAnswerIndex?: number | null;
  opponentAnswerIndex?: number | null;
}

export const QuestionDisplay = ({
  questionText,
  currentQuestionIndex,
  options,
  selectedAnswer,
  answerState,
  onAnswer,
  isDisabled,
  profilePicture,
  correctAnswerIndex,
  opponentAnswerIndex,
}: QuestionDisplayProps) => {
  return (
    <div className="question-content">
      <div className="question-header">
        <span className="question-number">
          Fråga {currentQuestionIndex + 1} / 10
        </span>
      </div>

      <h2 className="question-text">{questionText}</h2>

      <div className="options-grid">
        {options.map((option, index) => (
          <AnswerButton
            key={index}
            option={option}
            index={index}
            onAnswer={onAnswer}
            disabled={selectedAnswer !== null || isDisabled}
            isSelected={selectedAnswer === index}
            answerState={selectedAnswer === index ? answerState : null}
            profilePicture={profilePicture}
            correctAnswerIndex={correctAnswerIndex}
            opponentAnswerIndex={opponentAnswerIndex}
          />
        ))}
      </div>
    </div>
  );
};
