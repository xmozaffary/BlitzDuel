import { config } from "../config/config";

export interface UserInfo {
  id: number;
  email: string;
  name: string;
  profilePictureUrl: string;
  gamesPlayed: number;
  gamesWon: number;
  totalScore: number;
}

export const fetchUserInfo = async (): Promise<UserInfo> => {
  const token = localStorage.getItem("jwt");

  if (!token) {
    throw new Error("No JWT token found");
  }

  const response = await fetch(`${config.apiBaseUrl}/api/auth/me`, {
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  });

  return await response.json();
};
