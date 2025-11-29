import { Navigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

interface RequireAuthProps {
  children: React.ReactNode;
}

export const RequiredAuth = ({ children }: RequireAuthProps) => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="auth-loading">
        <h2>Laddar...</h2>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};
