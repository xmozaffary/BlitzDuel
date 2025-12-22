import { describe, it, expect } from 'vitest';
import { render } from '@testing-library/react';
import { Spinner } from '../Spinner';

describe('Spinner', () => {
  it('should render spinner element', () => {
    const { container } = render(<Spinner />);
    const spinner = container.querySelector('.spinner');

    expect(spinner).toBeInTheDocument();
  });

  it('should have spinner class', () => {
    const { container } = render(<Spinner />);
    const spinner = container.querySelector('.spinner');

    expect(spinner).toHaveClass('spinner');
  });

  it('should render as a span element', () => {
    const { container } = render(<Spinner />);
    const spinner = container.querySelector('.spinner');

    expect(spinner?.tagName).toBe('SPAN');
  });
});
