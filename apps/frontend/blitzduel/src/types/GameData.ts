export interface QuestionData {
    type: string;
    currentQuestionIndex: number;
    questionText: string;
    options: string[];
}

export interface ResultData {
    status: string;
    correctAnswerIndex: number;
    hostCorrect: boolean;
    guestCorrect: boolean;
    hostScore: number;
    guestScore: number;
}