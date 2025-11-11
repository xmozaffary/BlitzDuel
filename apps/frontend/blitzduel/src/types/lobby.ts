export interface LobbyResponse {
  code: string;
  status: string;
  quizId: number;
  player1: string;
  player2: string | null;

}

export interface LobbyUpdate {
  players?: string[];
  status: string;
  [key: string]: unknown;
}

export interface LobbyRequest {
  nickname: string;
  quizId: number;
}
