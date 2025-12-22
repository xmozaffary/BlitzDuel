import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { AnswerButton } from '../AnswerButton';

describe('AnswerButton', () => {
  it('should render with option text', () => {
    render(<AnswerButton option="Answer 1" index={0} onAnswer={() => {}} />);

    expect(screen.getByText('Answer 1')).toBeInTheDocument();
  });

  it('should call onAnswer with index when clicked', () => {
    const handleAnswer = vi.fn();
    render(<AnswerButton option="Answer 1" index={2} onAnswer={handleAnswer} />);

    fireEvent.click(screen.getByText('Answer 1'));

    expect(handleAnswer).toHaveBeenCalledWith(2);
    expect(handleAnswer).toHaveBeenCalledTimes(1);
  });

  it('should be enabled by default', () => {
    render(<AnswerButton option="Answer 1" index={0} onAnswer={() => {}} />);
    const button = screen.getByText('Answer 1');

    expect(button).not.toBeDisabled();
  });

  it('should be disabled when disabled prop is true', () => {
    render(
      <AnswerButton option="Answer 1" index={0} onAnswer={() => {}} disabled={true} />
    );
    const button = screen.getByText('Answer 1');

    expect(button).toBeDisabled();
  });

  it('should be disabled when isSelected is true', () => {
    render(
      <AnswerButton option="Answer 1" index={0} onAnswer={() => {}} isSelected={true} />
    );
    const button = screen.getByText('Answer 1');

    expect(button).toBeDisabled();
  });

  it('should not call onAnswer when disabled', () => {
    const handleAnswer = vi.fn();
    render(
      <AnswerButton option="Answer 1" index={0} onAnswer={handleAnswer} disabled={true} />
    );

    fireEvent.click(screen.getByText('Answer 1'));

    expect(handleAnswer).not.toHaveBeenCalled();
  });

  it('should apply correct class for correct answer state', () => {
    render(
      <AnswerButton
        option="Answer 1"
        index={0}
        onAnswer={() => {}}
        answerState="correct"
      />
    );
    const button = screen.getByText('Answer 1');

    expect(button).toHaveClass('answer-button--correct');
  });

  it('should apply incorrect class for incorrect answer state', () => {
    render(
      <AnswerButton
        option="Answer 1"
        index={0}
        onAnswer={() => {}}
        answerState="incorrect"
      />
    );
    const button = screen.getByText('Answer 1');

    expect(button).toHaveClass('answer-button--incorrect');
  });

  it('should apply selected class for selected answer state', () => {
    render(
      <AnswerButton
        option="Answer 1"
        index={0}
        onAnswer={() => {}}
        answerState="selected"
      />
    );
    const button = screen.getByText('Answer 1');

    expect(button).toHaveClass('answer-button--selected');
  });

  it('should highlight correct answer when correctAnswerIndex matches', () => {
    render(
      <AnswerButton
        option="Answer 1"
        index={2}
        onAnswer={() => {}}
        correctAnswerIndex={2}
      />
    );
    const button = screen.getByText('Answer 1');

    expect(button).toHaveClass('answer-button--correct');
  });

  it('should display profile picture when selected', () => {
    render(
      <AnswerButton
        option="Answer 1"
        index={0}
        onAnswer={() => {}}
        isSelected={true}
        profilePicture="https://example.com/profile.jpg"
      />
    );

    const img = screen.getByAltText('Du');
    expect(img).toBeInTheDocument();
    expect(img).toHaveAttribute('src', 'https://example.com/profile.jpg');
    expect(img).toHaveClass('answer-profile-pic');
  });

  it('should not display profile picture when not selected', () => {
    render(
      <AnswerButton
        option="Answer 1"
        index={0}
        onAnswer={() => {}}
        isSelected={false}
        profilePicture="https://example.com/profile.jpg"
      />
    );

    expect(screen.queryByAltText('Du')).not.toBeInTheDocument();
  });

  it('should display opponent indicator when opponentAnswerIndex matches', () => {
    render(
      <AnswerButton
        option="Answer 1"
        index={1}
        onAnswer={() => {}}
        opponentAnswerIndex={1}
      />
    );

    expect(screen.getByText('👤')).toBeInTheDocument();
    expect(screen.getByText('👤')).toHaveClass('opponent-indicator');
  });

  it('should not display opponent indicator when opponentAnswerIndex does not match', () => {
    render(
      <AnswerButton
        option="Answer 1"
        index={1}
        onAnswer={() => {}}
        opponentAnswerIndex={2}
      />
    );

    expect(screen.queryByText('👤')).not.toBeInTheDocument();
  });

  it('should have answer-button class', () => {
    render(<AnswerButton option="Answer 1" index={0} onAnswer={() => {}} />);
    const button = screen.getByText('Answer 1');

    expect(button).toHaveClass('answer-button');
  });

  it('should prioritize correct state over correctAnswerIndex', () => {
    render(
      <AnswerButton
        option="Answer 1"
        index={0}
        onAnswer={() => {}}
        answerState="incorrect"
        correctAnswerIndex={0}
      />
    );
    const button = screen.getByText('Answer 1');

    // answerState="incorrect" should take priority
    expect(button).toHaveClass('answer-button--incorrect');
  });
});
