const apiBaseUrl = import.meta.env.VITE_API_BASE_URL;
const wsUrl = import.meta.env.VITE_WS_URL;

// Validate required environment variables
if (!apiBaseUrl) {
  throw new Error("VITE_API_BASE_URL is required");
}

if (!wsUrl) {
  throw new Error("VITE_WS_URL is required");
}

export const config = {
  apiBaseUrl,
  wsUrl,
  oauthUrl: apiBaseUrl.replace("/api", "/oauth2/authorization/google"),
} as const;
