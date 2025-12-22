import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { LoginPage } from '../LoginPage';
import { AuthProvider } from '../../contexts/AuthContext';

// Mock the config
vi.mock('../../config/config', () => ({
  config: {
    oauthUrl: 'https://example.com/oauth',
    apiUrl: 'https://api.example.com'
  }
}));

// Mock fetchUserInfo
vi.mock('../../services/fetchUserInfo', () => ({
  fetchUserInfo: vi.fn(() => Promise.resolve(null))
}));

// Mock useNavigate
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

describe('LoginPage Integration Tests', () => {
  beforeEach(() => {
    mockNavigate.mockClear();
    delete (window as any).location;
    (window as any).location = { href: '' };
    localStorage.clear();
  });

  it('should render login page with all elements', () => {
    render(
      <BrowserRouter>
        <AuthProvider>
          <LoginPage />
        </AuthProvider>
      </BrowserRouter>
    );

    expect(screen.getByText('BlitzDuel')).toBeInTheDocument();
    expect(screen.getByText('Testa dina kunskaper i realtid')).toBeInTheDocument();
    expect(screen.getByText('Välkommen!')).toBeInTheDocument();
    expect(screen.getByText('Logga in för att utmana dina vänner i spännande quiz-dueller')).toBeInTheDocument();
    expect(screen.getByText('Fortsätt med Google')).toBeInTheDocument();
    expect(screen.getByText('Genom att logga in godkänner du våra villkor')).toBeInTheDocument();
  });

  it('should have Google login button', () => {
    render(
      <BrowserRouter>
        <AuthProvider>
          <LoginPage />
        </AuthProvider>
      </BrowserRouter>
    );

    const loginButton = screen.getByText('Fortsätt med Google');
    expect(loginButton).toBeInTheDocument();
    expect(loginButton.className).toBe('google-login-btn');
  });

  it('should redirect to OAuth URL when Google login button is clicked', () => {
    render(
      <BrowserRouter>
        <AuthProvider>
          <LoginPage />
        </AuthProvider>
      </BrowserRouter>
    );

    const loginButton = screen.getByText('Fortsätt med Google');
    fireEvent.click(loginButton);

    expect(window.location.href).toBe('https://example.com/oauth');
  });

  it('should display Google SVG icon', () => {
    const { container } = render(
      <BrowserRouter>
        <AuthProvider>
          <LoginPage />
        </AuthProvider>
      </BrowserRouter>
    );

    const svgIcon = container.querySelector('.google-icon');
    expect(svgIcon).toBeInTheDocument();
  });
});
