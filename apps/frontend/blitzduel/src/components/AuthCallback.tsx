import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

const AuthCallback = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const handleCallback = async () => {
      try {
        const params = new URLSearchParams(window.location.search);
        const token = params.get("token");

        if (!token) {
          throw new Error("Ingen token mottagen");
        }

        await login(token);
        navigate("/");
      } catch (err) {
        console.log("Auth callback error:", err);
        setError(
          err instanceof Error ? err.message : "Inloggning misslyckades"
        );

        setTimeout(() => navigate("/login"), 2000);
      }
    };

    handleCallback();
  }, [navigate, login]);

  if (error) {
    return (
      <div className="auth-callback-error">
        <h2>Inloggning misslyckades</h2>
        <p>{error}</p>
        <p>Omdirigerar till inloggning...</p>
      </div>
    );
  }

  return (
    <div className="auth-callback-loading">
      <h2>Loggar in...</h2>
      <p>Vänligen vänta</p>
    </div>
  );
};

export default AuthCallback;
