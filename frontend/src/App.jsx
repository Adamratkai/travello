import './App.css'
import Navbar from "./components/Navbar.jsx";
import {createBrowserRouter, Navigate, RouterProvider} from "react-router";
import HomePage from "./pages/HomePage.jsx";
import ErrorPage from "./pages/ErrorPage.jsx";
import TripPage from "./pages/TripPage.jsx";
import LoginPage from "./pages/LoginPage.jsx";
import RegisterPage from "./pages/RegisterPage.jsx";
import {useContext} from "react";
import {AuthContext, AuthProvider} from "./components/AuthProvider.jsx";
import TripListPage from "./pages/TripListPage.jsx";
import Footer from "./components/Footer.jsx";
import {MarkersProvider} from "./components/MarkersContext.jsx";

function Layout({children}) {
    return (
        <div className="flex flex-col min-h-screen">
            <Navbar/>
            <div
                className="bg-cover bg-center flex items-center justify-center text-white text-center bg-[url(/home_1.jpg)] text-center justify-center items-center flex flex-grow">
                {children}
            </div>
            <Footer/>
        </div>
    );
}

const ProtectedRoute = ({children}) => {
    const {token} = useContext(AuthContext);
    if (!token) {
        return <Navigate to="/login"/>;
    } else {
        return children;
    }
}
const router = createBrowserRouter([
    {path: "/", element: <Layout><HomePage/></Layout>},
    {
        path: "/trip-editor/:tripId",
        element: <ProtectedRoute><Layout><MarkersProvider><TripPage/></MarkersProvider></Layout></ProtectedRoute>
    },
    {path: "/trip-list", element: <ProtectedRoute><Layout><TripListPage/></Layout></ProtectedRoute>},
    {path: "*", element: <ErrorPage/>},
    {path: "/login", element: <Layout><LoginPage/></Layout>},
    {path: "/register", element: <Layout><RegisterPage/></Layout>},
]);

function App() {
    return (
        <AuthProvider>
            <RouterProvider router={router}></RouterProvider>
        </AuthProvider>
    );
}

export default App
