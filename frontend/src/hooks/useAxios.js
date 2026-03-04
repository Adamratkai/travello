import axios from "axios";
import {useContext} from "react";
import {useNavigate} from "react-router-dom";
import {AuthContext} from "../components/AuthProvider.jsx";

const useAxios = () => {
    const {token, logout} = useContext(AuthContext);
    const navigate = useNavigate();

    const axiosInstance = axios.create({
        baseURL: "/",
        headers: {"Content-Type": "application/json"},
    });
    axiosInstance.interceptors.request.use(
        (config) => {
            if (token) {
                config.headers["Authorization"] = `Bearer ${token}`;
            }
            return config;
        },
        (error) => Promise.reject(error)
    );

    axiosInstance.interceptors.response.use(
        (response) => response,
        (error) => {
            if (error.response && error.response.status === 401) {
                console.error("Unauthorized! Redirecting to login...");
                logout();
                navigate("/login");
            }
            return Promise.reject(error);
        }
    );

    return axiosInstance;
};

export default useAxios;
