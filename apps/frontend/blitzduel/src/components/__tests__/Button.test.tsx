import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { Button } from '../Button';

describe('Button', () => {
  it('should render with text', () => {
    render(<Button text="Click me" onClick={() => {}} />);
    expect(screen.getByText('Click me')).toBeInTheDocument();
  });

  it('should call onClick when clicked', () => {
    const handleClick = vi.fn();
    render(<Button text="Click me" onClick={handleClick} />);

    fireEvent.click(screen.getByText('Click me'));

    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  it('should apply primary variant by default', () => {
    render(<Button text="Click me" onClick={() => {}} />);
    const button = screen.getByText('Click me');

    expect(button).toHaveClass('button-primary');
  });

  it('should apply secondary variant when specified', () => {
    render(<Button text="Click me" onClick={() => {}} variant="secondary" />);
    const button = screen.getByText('Click me');

    expect(button).toHaveClass('button-secondary');
  });

  it('should apply ready variant when specified', () => {
    render(<Button text="Click me" onClick={() => {}} variant="ready" />);
    const button = screen.getByText('Click me');

    expect(button).toHaveClass('button-ready');
  });

  it('should be enabled by default', () => {
    render(<Button text="Click me" onClick={() => {}} />);
    const button = screen.getByText('Click me');

    expect(button).not.toBeDisabled();
  });

  it('should be disabled when disabled prop is true', () => {
    render(<Button text="Click me" onClick={() => {}} disabled={true} />);
    const button = screen.getByText('Click me');

    expect(button).toBeDisabled();
  });

  it('should not call onClick when disabled', () => {
    const handleClick = vi.fn();
    render(<Button text="Click me" onClick={handleClick} disabled={true} />);

    fireEvent.click(screen.getByText('Click me'));

    expect(handleClick).not.toHaveBeenCalled();
  });

  it('should have button class', () => {
    render(<Button text="Click me" onClick={() => {}} />);
    const button = screen.getByText('Click me');

    expect(button).toHaveClass('button');
  });
});
