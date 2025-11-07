export interface LobbyResponse {
  code: string;
  status: string;
}

export interface LobbyUpdate {
  players?: string[];
  status: string;
  [key: string]: unknown;
}

export interface LobbyRequest {
  nickname: string;
}
