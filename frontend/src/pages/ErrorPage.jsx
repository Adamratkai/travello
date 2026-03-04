import {Link} from "react-router-dom";

function ErrorPage() {

    return (
        <div>
            <h1>Something went wrong.</h1>
            <Link to="/">
                Go Back to Home
            </Link>
        </div>
    );
}

export default ErrorPage;