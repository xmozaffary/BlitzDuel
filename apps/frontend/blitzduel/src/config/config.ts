export const config = {
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL,
  wsUrl: import.meta.env.VITE_WS_URL,
  oauthUrl: import.meta.env.VITE_API_BASE_URL?.replace(
    "/api",
    "/oauth2/authorization/google"
  ),
} as const;

// Validate required environment variables
if (!config.apiBaseUrl) {
  throw new Error("VITE_API_BASE_URL is required");
}

if (!config.wsUrl) {
  throw new Error("VITE_WS_URL is required");
}
