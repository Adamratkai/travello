import axios from "axios";
import {useContext, useMemo} from "react";
import {useNavigate} from "react-router-dom";
import {AuthContext} from "../components/AuthProvider.jsx";

const useAxios = () => {
    const { token, logout } = useContext(AuthContext);
    const navigate = useNavigate();

    return useMemo(() => {
        const instance = axios.create({
            baseURL: "/",
            headers: {"Content-Type": "application/json"},
        });

        instance.interceptors.request.use(
            (config) => {
                if (token) {
                    config.headers.Authorization = `Bearer ${token}`;
                }
                return config;
            },
            (error) => Promise.reject(error)
        );

        instance.interceptors.response.use(
            (response) => response,
            (error) => {
                if (error.response?.status === 401) {
                    console.error("Unauthorized! Redirecting to login...");
                    logout();
                    navigate("/login");
                }
                return Promise.reject(error);
            }
        );

        return instance;
    }, [token, logout, navigate]);
};

export default useAxios;