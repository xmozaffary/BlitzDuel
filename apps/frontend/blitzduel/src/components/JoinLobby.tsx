import { useAuth } from "../contexts/AuthContext";
import { useJoinLobby } from "../hooks/useJoinLobby";

const JoinLobby = () => {
  const { user } = useAuth();
  const {
    lobbyCode,
    isJoining,
    joined,
    error,
    joinLobby,
    cancelJoin,
    updateLobbyCode,
  } = useJoinLobby();

  return (
    <div className="lobby-container">
      <div className="lobby-card">
        <h1>Gå med i Lobby</h1>

        {!joined ? (
          <div className="join-lobby-form">
            <div className="user-info">
              <img
                src={user?.profilePictureUrl}
                alt={user?.name}
                className="user-avatar"
              />
              <div>
                <p className="user-name">{user?.name}</p>
                <p className="user-role">Gäst</p>
              </div>
            </div>

            <div className="input-group">
              <label htmlFor="lobbyCode">Lobby Kod</label>
              <input
                id="lobbyCode"
                type="text"
                placeholder="Ange lobby-kod"
                value={lobbyCode}
                onChange={(e) => updateLobbyCode(e.target.value)}
                disabled={isJoining}
                className="lobby-code-input"
                maxLength={6}
              />
              {error && <p className="error-message">{error}</p>}
            </div>

            <button
              onClick={joinLobby}
              disabled={isJoining || !lobbyCode.trim()}
              className="btn-join"
            >
              {isJoining ? "Ansluter..." : "Gå med"}
            </button>

            <button onClick={cancelJoin} className="btn-cancel">
              Avbryt
            </button>
          </div>
        ) : (
          <div className="join-success">
            <div className="success-icon">✅</div>
            <h2>Du har gått med i lobbyn!</h2>
            <p>Väntar på att hosten startar matchen...</p>
            <div className="spinner-container">
              <div className="spinner"></div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default JoinLobby;
