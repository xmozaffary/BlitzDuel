import { config } from "../config/config";

export const LoginPage = () => {
  const handleGoogleLogin = () => {
    window.location.href = config.oauthUrl;
  };

  return (
    <div className="login-container">
      <h1>Vällkomen till blitzduel</h1>
      <button onClick={handleGoogleLogin} className="google-login-btn">
        Logga in med Google
      </button>
    </div>
  );
};
