import { useLocation } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import { useLobby } from "../hooks/useLobby";

const CreateLobby = () => {
  const { user } = useAuth();
  const location = useLocation();
  const quizId = location.state?.quizId;

  const {
    lobbyCode,
    isCreating,
    waitingForPlayers,
    guestName,
    startGame,
    copyLobbyCode,
    cancelLobby,
  } = useLobby(quizId, true);

  return (
    <div className="lobby-container">
      <div className="lobby-card">
        {isCreating ? (
          <div className="lobby-creating">
            <h1>Skapar Lobby...</h1>
            <div className="spinner-container">
              <div className="spinner"></div>
            </div>
            <p>Vänta ett ögonblick</p>
          </div>
        ) : (
          <>
            <h1>Din Lobby</h1>

            <div className="lobby-waiting">
              <div className="players-section">
                <div className="player-card host">
                  <img
                    src={user?.profilePictureUrl}
                    alt={user?.name}
                    className="player-avatar"
                  />
                  <div>
                    <p className="player-name">{user?.name}</p>
                    <p className="player-label">Host</p>
                  </div>
                </div>

                <div className="vs-divider">VS</div>

                <div
                  className={`player-card guest ${
                    guestName ? "joined" : "waiting"
                  }`}
                >
                  {guestName ? (
                    <>
                      <div className="player-avatar guest-avatar">
                        {guestName.charAt(0).toUpperCase()}
                      </div>
                      <div>
                        <p className="player-name">{guestName}</p>
                        <p className="player-label">Gäst</p>
                      </div>
                    </>
                  ) : (
                    <>
                      <div className="player-avatar-placeholder">
                        <div className="spinner"></div>
                      </div>
                      <div>
                        <p className="player-name">Väntar...</p>
                        <p className="player-label">Gäst</p>
                      </div>
                    </>
                  )}
                </div>
              </div>

              <div className="lobby-code-section">
                <p className="lobby-code-label">Lobby Kod</p>
                <div className="lobby-code-display">
                  <span className="code">{lobbyCode}</span>
                  <button onClick={copyLobbyCode} className="btn-copy">
                    📋 Kopiera
                  </button>
                </div>
                <p className="lobby-code-hint">
                  Dela denna kod med din motståndare
                </p>
              </div>

              <button
                onClick={startGame}
                className="btn-start"
                disabled={waitingForPlayers}
              >
                {waitingForPlayers
                  ? "Väntar på motståndare..."
                  : "Starta Match"}
              </button>

              <button onClick={cancelLobby} className="btn-cancel">
                Avbryt
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default CreateLobby;
