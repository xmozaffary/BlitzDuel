import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useAuth } from '../useAuth';

describe('useAuth', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should initialize with no authentication when no token in localStorage', () => {
    const { result } = renderHook(() => useAuth());

    expect(result.current.isAuthenticated).toBe(false);
    expect(result.current.token).toBeNull();
  });

  it('should initialize with authentication when token exists in localStorage', () => {
    localStorage.setItem('jwt', 'test-token-123');

    const { result } = renderHook(() => useAuth());

    expect(result.current.isAuthenticated).toBe(true);
    expect(result.current.token).toBe('test-token-123');
  });

  it('should provide logout function', () => {
    const { result } = renderHook(() => useAuth());

    expect(result.current.logout).toBeDefined();
    expect(typeof result.current.logout).toBe('function');
  });

  it('should clear authentication on logout', () => {
    localStorage.setItem('jwt', 'test-token-123');

    const { result } = renderHook(() => useAuth());

    expect(result.current.isAuthenticated).toBe(true);
    expect(result.current.token).toBe('test-token-123');

    act(() => {
      result.current.logout();
    });

    expect(result.current.isAuthenticated).toBe(false);
    expect(result.current.token).toBeNull();
  });

  it('should remove token from localStorage on logout', () => {
    localStorage.setItem('jwt', 'test-token-123');

    const { result } = renderHook(() => useAuth());

    act(() => {
      result.current.logout();
    });

    expect(localStorage.getItem('jwt')).toBeNull();
  });

  it('should handle logout when already logged out', () => {
    const { result } = renderHook(() => useAuth());

    expect(result.current.isAuthenticated).toBe(false);

    act(() => {
      result.current.logout();
    });

    expect(result.current.isAuthenticated).toBe(false);
    expect(result.current.token).toBeNull();
  });

  it('should read from localStorage only on mount', () => {
    const getItemSpy = vi.spyOn(Storage.prototype, 'getItem');

    const { rerender } = renderHook(() => useAuth());

    expect(getItemSpy).toHaveBeenCalledTimes(1);

    rerender();

    // Should not call getItem again on rerender
    expect(getItemSpy).toHaveBeenCalledTimes(1);

    getItemSpy.mockRestore();
  });
});
