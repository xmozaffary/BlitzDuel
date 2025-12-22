import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { ScoreBoard } from '../ScoreBoard';

describe('ScoreBoard', () => {
  it('should render title', () => {
    render(
      <ScoreBoard
        playerName="Player 1"
        playerScore={5}
        opponentName="Player 2"
        opponentScore={3}
      />
    );

    expect(screen.getByText('Poäng')).toBeInTheDocument();
  });

  it('should render player name and score', () => {
    render(
      <ScoreBoard
        playerName="Alice"
        playerScore={7}
        opponentName="Bob"
        opponentScore={4}
      />
    );

    expect(screen.getByText('Alice')).toBeInTheDocument();
    expect(screen.getByText('7')).toBeInTheDocument();
  });

  it('should render opponent name and score', () => {
    render(
      <ScoreBoard
        playerName="Alice"
        playerScore={7}
        opponentName="Bob"
        opponentScore={4}
      />
    );

    expect(screen.getByText('Bob')).toBeInTheDocument();
    expect(screen.getByText('4')).toBeInTheDocument();
  });

  it('should render both players with zero scores', () => {
    render(
      <ScoreBoard
        playerName="Player 1"
        playerScore={0}
        opponentName="Player 2"
        opponentScore={0}
      />
    );

    expect(screen.getByText('Player 1')).toBeInTheDocument();
    expect(screen.getByText('Player 2')).toBeInTheDocument();
    const scores = screen.getAllByText('0');
    expect(scores).toHaveLength(2);
  });

  it('should update scores when props change', () => {
    const { rerender } = render(
      <ScoreBoard
        playerName="Player 1"
        playerScore={3}
        opponentName="Player 2"
        opponentScore={2}
      />
    );

    expect(screen.getByText('3')).toBeInTheDocument();
    expect(screen.getByText('2')).toBeInTheDocument();

    rerender(
      <ScoreBoard
        playerName="Player 1"
        playerScore={5}
        opponentName="Player 2"
        opponentScore={4}
      />
    );

    expect(screen.getByText('5')).toBeInTheDocument();
    expect(screen.getByText('4')).toBeInTheDocument();
  });

  it('should have score-board class', () => {
    const { container } = render(
      <ScoreBoard
        playerName="Player 1"
        playerScore={5}
        opponentName="Player 2"
        opponentScore={3}
      />
    );

    expect(container.querySelector('.score-board')).toBeInTheDocument();
  });

  it('should render score items with correct classes', () => {
    const { container } = render(
      <ScoreBoard
        playerName="Player 1"
        playerScore={5}
        opponentName="Player 2"
        opponentScore={3}
      />
    );

    const scoreItems = container.querySelectorAll('.score-item');
    expect(scoreItems).toHaveLength(2);

    const playerNames = container.querySelectorAll('.player-name');
    expect(playerNames).toHaveLength(2);

    const scores = container.querySelectorAll('.score');
    expect(scores).toHaveLength(2);
  });

  it('should handle long player names', () => {
    render(
      <ScoreBoard
        playerName="Very Long Player Name Here"
        playerScore={10}
        opponentName="Another Very Long Name"
        opponentScore={8}
      />
    );

    expect(screen.getByText('Very Long Player Name Here')).toBeInTheDocument();
    expect(screen.getByText('Another Very Long Name')).toBeInTheDocument();
  });
});
