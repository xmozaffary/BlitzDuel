interface ScoreBoardProps {
  playerName: string;
  playerScore: number;
  opponentName: string;
  opponentScore: number;
}

export const ScoreBoard = ({
  playerName,
  playerScore,
  opponentName,
  opponentScore,
}: ScoreBoardProps) => {
  return (
    <div className="score-board">
      <h3>Poäng</h3>
      <div className="score-item">
        <span className="player-name">{playerName}</span>
        <span className="score">{playerScore}</span>
      </div>
      <div className="score-item">
        <span className="player-name">{opponentName}</span>
        <span className="score">{opponentScore}</span>
      </div>
    </div>
  );
};
