interface ScoreBoardProps {
  playerScore: number;
  opponentScore: number;
}

export const ScoreBoard = ({ playerScore, opponentScore }: ScoreBoardProps) => {
  return (
    <div className="score-board">
      <h3>Poäng</h3>
      <div className="score-item">
        <span className="player-name">Du:</span>
        <span className="score">{playerScore}</span>
      </div>
      <div className="score-item">
        <span className="player-name">Motståandare:</span>
        <span className="score">{opponentScore}</span>
      </div>
    </div>
  );
};
