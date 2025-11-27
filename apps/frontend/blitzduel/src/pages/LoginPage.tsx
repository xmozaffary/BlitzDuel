import { API_URL } from "../config/environment";

export const LoginPage = () => {
  const handleGoogleLogin = () => {
    window.location.href = `${API_URL}/oauth2/authorization/google`;
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
