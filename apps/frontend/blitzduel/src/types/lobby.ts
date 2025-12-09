export interface LobbyResponse {
  lobbyCode: string;
  status: string;
  quizId: number;
  hostName: string;
  guestName: string | null;
}

export interface LobbyUpdate {
  players?: string[];
  status: string;
  guestName?: string;
  hostName?: string;
  [key: string]: unknown;
}

export interface LobbyRequest {
  name: string;
  quizId: number;
}

export interface JoinLobbyRequest {
  name: string;
}
