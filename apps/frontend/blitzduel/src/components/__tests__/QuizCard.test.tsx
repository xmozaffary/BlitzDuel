import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { QuizCard } from '../QuizCard';

describe('QuizCard', () => {
  it('should render with title', () => {
    render(
      <QuizCard
        title="Test Quiz"
        imgUrl="https://example.com/image.jpg"
        onClick={() => {}}
      />
    );

    expect(screen.getByText('Test Quiz')).toBeInTheDocument();
  });

  it('should render with image', () => {
    render(
      <QuizCard
        title="Test Quiz"
        imgUrl="https://example.com/image.jpg"
        onClick={() => {}}
      />
    );

    const image = screen.getByRole('img');
    expect(image).toBeInTheDocument();
    expect(image).toHaveAttribute('src', 'https://example.com/image.jpg');
  });

  it('should display "10 frågor" text', () => {
    render(
      <QuizCard
        title="Test Quiz"
        imgUrl="https://example.com/image.jpg"
        onClick={() => {}}
      />
    );

    expect(screen.getByText('10 frågor')).toBeInTheDocument();
  });

  it('should call onClick when clicked', () => {
    const handleClick = vi.fn();
    render(
      <QuizCard
        title="Test Quiz"
        imgUrl="https://example.com/image.jpg"
        onClick={handleClick}
      />
    );

    fireEvent.click(screen.getByText('Test Quiz'));

    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  it('should call onClick when image is clicked', () => {
    const handleClick = vi.fn();
    render(
      <QuizCard
        title="Test Quiz"
        imgUrl="https://example.com/image.jpg"
        onClick={handleClick}
      />
    );

    fireEvent.click(screen.getByRole('img'));

    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  it('should have quiz-card class', () => {
    const { container } = render(
      <QuizCard
        title="Test Quiz"
        imgUrl="https://example.com/image.jpg"
        onClick={() => {}}
      />
    );

    expect(container.querySelector('.quiz-card')).toBeInTheDocument();
  });

  it('should render with different titles', () => {
    const { rerender } = render(
      <QuizCard
        title="First Quiz"
        imgUrl="https://example.com/image.jpg"
        onClick={() => {}}
      />
    );

    expect(screen.getByText('First Quiz')).toBeInTheDocument();

    rerender(
      <QuizCard
        title="Second Quiz"
        imgUrl="https://example.com/image.jpg"
        onClick={() => {}}
      />
    );

    expect(screen.getByText('Second Quiz')).toBeInTheDocument();
    expect(screen.queryByText('First Quiz')).not.toBeInTheDocument();
  });
});
