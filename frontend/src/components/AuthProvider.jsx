import {createContext, useState} from "react";
import axios from "axios";

export const AuthContext = createContext();

export const AuthProvider = ({children}) => {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(localStorage.getItem("token") || null);
    const [error, setError] = useState(null);
    const [isLoggedIn, setIsLoggedIn] = useState(localStorage.getItem("token") !== null || false);
    const login = async (email, password) => {
        try {
            const response = await axios.post("/api/auth/login", {email, password});
            setUser(response.data.username)
            setToken(response.data.token);
            setIsLoggedIn(true)
            setError(null);
            localStorage.setItem("token", response.data.token);
            return null;
        } catch (error) {
            setError(error.response.data.message);
            return error;
        }
    }

    const logout = () => {
        setUser(null);
        setToken(null);
        setIsLoggedIn(false);
        localStorage.removeItem("token");
    }

    const register = async (username, email, password) => {
        try {
            await axios.post("/api/auth/register", {username, email, password});
            setError(null);
            return null;
        } catch (error) {
            setError(error.response.data.message);
            return error;
        }
    }

    const onPageChange = () => {
        setError(null);
    }

    return (
        <AuthContext.Provider value={{user, setUser, login, register, logout, error, token, isLoggedIn, onPageChange}}>
            {children}
        </AuthContext.Provider>
    )
}