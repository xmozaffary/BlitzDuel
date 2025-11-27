import { useEffect, useState } from "react";

export const useAuth = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [token, setToken] = useState<string | null>(null);

  useEffect(() => {
    const jwt = localStorage.getItem("jwt");
    setToken(jwt);
    setIsAuthenticated(!!jwt);
  }, []);

  const logout = () => {
    console.log("rensa localstorage");
    localStorage.removeItem("jwt");
    setToken(null);
    setIsAuthenticated(false);
  };

  return { isAuthenticated, token, logout };
};
