import { useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

export const ProfilePage = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  if (!user) {
    return null;
  }

  return (
    <div className="profile-page">
      <div className="profile-card">
        <div className="profile-header">
          <img
            src={user.profilePictureUrl}
            alt={user.name}
            className="profile-image"
          />
          <h1>{user.name}</h1>
          <p className="email">{user.email}</p>
        </div>

        <div className="profile-stats">
          <div className="stat-item">
            <span className="stat-value">{user.gamesPlayed}</span>
            <span className="stat-label">Spelade Matcher</span>
          </div>
          <div className="stat-item">
            <span className="stat-value">{user.gamesWon}</span>
            <span className="stat-label">Vunna Matcher</span>
          </div>
          <div className="stat-item">
            <span className="stat-value">{user.totalScore}</span>
            <span className="stat-label">Total Poäng</span>
          </div>
        </div>

        <div className="profile-actions">
          <button onClick={() => navigate("/")} className="btn-secondary">
            Tillbaka till Quiz
          </button>
          <button onClick={handleLogout} className="btn-logout">
            Logga ut
          </button>
        </div>
      </div>
    </div>
  );
};
