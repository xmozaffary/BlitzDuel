import { useAuth } from "../contexts/AuthContext";
import { useJoinLobby } from "../hooks/useJoinLobby";

const JoinLobby = () => {
  const { user } = useAuth();
  const {
    lobbyCode,
    isJoining,
    joined,
    error,
    hostName,
    joinLobby,
    cancelJoin,
    updateLobbyCode,
  } = useJoinLobby();

  return (
    <div className="lobby-container">
      <div className="lobby-card">
        <h1>Gå med i Lobby</h1>

        {!joined ? (
          <div className="lobby-waiting">
            <div className="players-section">
              <div className="player-card guest joined">
                <img
                  src={user?.profilePictureUrl}
                  alt={user?.name}
                  className="player-avatar"
                />
                <div>
                  <p className="player-name">{user?.name}</p>
                  <p className="player-label">Gäst</p>
                </div>
              </div>

              <div className="vs-badge">VS</div>

              <div className="player-card host waiting">
                <div className="player-avatar-placeholder">?</div>
                <div>
                  <p className="player-name">Värd</p>
                  <p className="player-label">Väntar...</p>
                </div>
              </div>
            </div>

            <div className="lobby-code-section">
              <span className="lobby-code-label">Ange Lobby Kod</span>
              <div className="lobby-code-display">
                <input
                  type="text"
                  placeholder="ABC123"
                  value={lobbyCode}
                  onChange={(e) =>
                    updateLobbyCode(e.target.value.toUpperCase())
                  }
                  disabled={isJoining}
                  className="lobby-code-input"
                  maxLength={6}
                />
              </div>
              {error && <p className="error-message">{error}</p>}
            </div>

            <div className="lobby-actions">
              <button
                onClick={joinLobby}
                disabled={isJoining || !lobbyCode.trim()}
                className="btn-start"
              >
                {isJoining ? "Ansluter..." : "Gå med"}
              </button>
              <button onClick={cancelJoin} className="btn-cancel">
                Avbryt
              </button>
            </div>
          </div>
        ) : (
          <div className="lobby-waiting">
            <div className="players-section">
              <div className="player-card guest joined">
                <img
                  src={user?.profilePictureUrl}
                  alt={user?.name}
                  className="player-avatar"
                />
                <div>
                  <p className="player-name">{user?.name}</p>
                  <p className="player-label">Gäst</p>
                </div>
              </div>

              <div className="vs-badge">VS</div>

              <div className="player-card host joined">
                <div className="player-avatar guest-avatar">
                  {hostName?.charAt(0) || "H"}
                </div>
                <div>
                  <p className="player-name">{hostName || "Värd"}</p>
                  <p className="player-label">Host</p>
                </div>
              </div>
            </div>

            <div className="join-success">
              <div className="success-icon">✅</div>
              <h2>Du har gått med i lobbyn!</h2>
              <p>Väntar på att hosten startar matchen...</p>
              <div className="spinner-container">
                <div className="spinner"></div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default JoinLobby;
