import React from "react";

interface GameOverScreenProps {
  scores: { host: number; guest: number };
  playerNames: { host: string; guest: string };
}

const GameOverScreen: React.FC<GameOverScreenProps> = ({
  scores,
  playerNames,
}) => {
  const winner =
    scores.host > scores.guest
      ? playerNames.host
      : scores.guest > scores.host
      ? playerNames.guest
      : null;

  return (
    <div className="game-screen game-over">
      <div className="game-over-container">
        <div className="confetti"></div>
        <div className="confetti"></div>
        <div className="confetti"></div>
        <div className="confetti"></div>
        <div className="confetti"></div>

        <h1 className="game-over-title">Spelet Över!</h1>

        {winner ? (
          <div className="winner-section">
            <div className="trophy-icon">🏆</div>
            <h2 className="winner-text">Vinnare</h2>
            <div className="winner-name"> {winner}</div>
          </div>
        ) : (
          <div className="winner-section">
            <div className="trophy-icon">🤝</div>
            <h2 className="winner-text"> Oavgjort!</h2>
          </div>
        )}

        <div className="final-scores">
          <h3> Slutresultat</h3>
          <div className="final-scores-grid">
            <div
              className={`final-score-box ${
                winner === playerNames.host ? "winner" : ""
              }`}
            >
              <span className="final-player">{playerNames.host || "Host"}</span>
              <span className="final-score">{scores.host}</span>
              <span className="final-label">poäng</span>
            </div>
            <div className="vs-divider">VS</div>
            <div
              className={`final-score-box ${
                winner === playerNames.guest ? "winner" : ""
              }`}
            ></div>
          </div>
        </div>

        <div className="game-over-actions">
          <button
            className="btn-primary"
            onClick={() => (window.location.href = "/")}
          >
            Tillbaka till start
          </button>
        </div>
      </div>
    </div>
  );
};

export default GameOverScreen;
