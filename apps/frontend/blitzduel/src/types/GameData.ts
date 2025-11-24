export interface QuestionData {
  type: string;
  currentQuestionIndex: number;
  questionText: string;
  options: string[];
  hostName: string;
  guestName: string;
  timeLimit: number;
  startTime: number;
}

export interface ResultData {
  status: string;
  correctAnswerIndex: number;
  hostCorrect: boolean;
  guestCorrect: boolean;
  hostScore: number;
  guestScore: number;
  hostName: string;
  guestName: string;
}
