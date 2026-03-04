import {Link} from "react-router-dom";
import {useContext} from "react";
import {AuthContext} from "./AuthProvider.jsx";

function Navbar() {
    const {isLoggedIn, logout, onPageChange} = useContext(AuthContext);
    return (
        <nav className="navbar bg-base-300 shadow-sm sticky top-0 z-50">
            <div className="navbar-start">
                <Link onClick={() => onPageChange()} to="/" className="btn btn-ghost text-xl">
                    Home
                </Link>
                <Link onClick={() => onPageChange()} to="/trip-list" className="btn btn-ghost text-xl">
                    Trip
                </Link>
            </div>
            <div className="navbar-end dropdown dropdown-end">
                <div tabIndex={0} role="button" className="btn btn-ghost btn-circle avatar">
                    <div className="w-10 rounded-full">
                        <img
                            alt="Tailwind CSS Navbar component"
                            src="https://img.daisyui.com/images/stock/photo-1534528741775-53994a69daeb.webp"/>
                    </div>
                </div>
                <ul
                    tabIndex={0}
                    className="menu menu-md dropdown-content bg-base-100 rounded-box z-1 mt-3 w-52 p-2 shadow">

                    {!isLoggedIn ? (<>
                            <li><Link onClick={() => onPageChange()} to="/login">Login</Link></li>
                            <li><Link onClick={() => onPageChange()} to="/register">Register</Link></li>
                        </>) :
                        (<li>
                            <button onClick={() => logout()}>Logout</button>
                        </li>)}
                </ul>
            </div>
        </nav>
    );
}

export default Navbar;