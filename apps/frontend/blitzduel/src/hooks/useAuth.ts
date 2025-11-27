import { useEffect, useState } from "react";

export const useAuth = () => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [token, setToken] = useState<string | null>(null);

    useEffect(() => {
        console.log("locatstorage")
        const jwt = localStorage.getItem('jwt');
        console.log("google log in ska inte vissas")
        setToken(token);
        setIsAuthenticated(true);
    }, [])

    const logout = () => {
        console.log("rensa localstorage")
        localStorage.removeItem('jwt');
        setToken(null);
        setIsAuthenticated(false);
    }

    return {isAuthenticated, token, logout};

}