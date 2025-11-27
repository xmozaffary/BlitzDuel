import { Navigate } from "react-router-dom";

interface ProtectedRouterProps {
    children: React.ReactNode;
}

export const ProtctedRouter = ({children}: ProtectedRouterProps) => {
    const token = localStorage.getItem('jwt');
    
    if (!token){
        return <Navigate to ="/login" replace />;
    }

    return <>{children}</>
}