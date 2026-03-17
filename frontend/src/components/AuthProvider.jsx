import {createContext, useState, useCallback, useMemo} from "react";
import axios from "axios";
import {jwtDecode} from "jwt-decode";

export const AuthContext = createContext();

export const AuthProvider = ({children}) => {
    const [user, setUser] = useState(() => {
        const t = localStorage.getItem("token");
        return t ? jwtDecode(t).username : null;
    });
    const [token, setToken] = useState(() => localStorage.getItem("token") ?? null);
    const [error, setError] = useState(null);

    const isLoggedIn = token !== null;

    const login = useCallback(async (email, password) => {
        try {
            const response = await axios.post("/api/auth/login", {email, password});
            setUser(response.data.username);
            setToken(response.data.token);
            setError(null);
            localStorage.setItem("token", response.data.token);
            return null;
        } catch (error) {
            setError(error.response?.data?.message ?? "Something went wrong");
            return error;
        }
    }, []);

    const logout = useCallback(() => {
        setUser(null);
        setToken(null);
        localStorage.removeItem("token");
    }, []);

    const register = useCallback(async (username, email, password) => {
        try {
            await axios.post("/api/auth/register", {username, email, password});
            setError(null);
            return null;
        } catch (error) {
            setError(error.response?.data?.message ?? "Something went wrong");
            return error;
        }
    }, []);

    const onPageChange = useCallback(() => {
        setError(null);
    }, []);

    const contextValue = useMemo(() => ({
        user, setUser, login, register, logout, error, token, isLoggedIn, onPageChange
    }), [user, login, register, logout, error, token, isLoggedIn, onPageChange]);

    return (
        <AuthContext.Provider value={contextValue}>
            {children}
        </AuthContext.Provider>
    );
};