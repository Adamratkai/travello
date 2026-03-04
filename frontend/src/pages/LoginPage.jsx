import {useContext, useState} from "react";
import {Link} from "react-router-dom";
import {useNavigate} from "react-router";
import {AuthContext} from "../components/AuthProvider.jsx";


const LoginPage = () => {
    const {login, error} = useContext(AuthContext);
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [showPopup, setShowPopup] = useState(false);
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        const error = await login(email, password);
        if (!error) {
            setShowPopup(true);
            setTimeout(() => {
                setShowPopup(false);
                navigate("/trip-list");
            }, 2000);
        }
    };

    return (
        <div>
            <form onSubmit={handleLogin}>
                <fieldset className="fieldset w-xs bg-base-200 border border-base-300 p-4 rounded-box">
                    <legend className="fieldset-legend">Login</legend>
                    {error && <p style={{color: "red"}}>{error}</p>}
                    <label className="fieldset-label">Email</label>
                    <input type="email"
                           required
                           className="input"
                           placeholder="Email"
                           value={email}
                           onChange={(e) => setEmail(e.target.value)}
                    />
                    <label className="fieldset-label">Password</label>
                    <input type="password"
                           required
                           className="input"
                           placeholder="Password"
                           value={password}
                           onChange={(e) => setPassword(e.target.value)}
                    />

                    <Link className="link link-primary" to={"/register"}>Don't have an account? Register here</Link>
                    <button type="submit" className="btn btn-neutral mt-4">Login</button>
                </fieldset>
            </form>
            {showPopup && (
                <dialog id="my_modal_1" className="modal modal-open">
                    <div className="modal-box">
                        <p>Logged in successful! Redirecting...</p>
                    </div>
                </dialog>
            )}
        </div>
    );

}

export default LoginPage;