import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { fetchUserInfo } from "../services/fetchUserInfo";

const AuthCallback = () => {
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const handleCallback = async () => {
      try {
        const params = new URLSearchParams(window.location.search);
        const token = params.get("token");

        if (!token) {
          throw new Error("Ingen token mottagen");
        }

        localStorage.setItem("jwt", token);
        await fetchUserInfo();

        navigate("/");
      } catch (err) {
        console.log("Auth callback error:", err);
        setError(
          err instanceof Error ? err.message : "Inloggning misslyckades"
        );

        localStorage.removeItem("jwt");

        setTimeout(() => navigate("/login"), 2000);
      }
    };

    handleCallback();
  }, [navigate]);

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
