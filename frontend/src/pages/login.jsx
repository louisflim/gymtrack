import { Link } from "react-router-dom";
import "../css/auth.css";

function Login() {
  return (
    <div className="auth-container">
      <div className="auth-card">

        <h1>GymTrack</h1>
        <p>Gym Membership Management System</p>

        <form>

          <div className="input-group">
            <label>Email</label>
            <input
              type="email"
              placeholder="Enter your email"
            />
          </div>

          <div className="input-group">
            <label>Password</label>
            <input
              type="password"
              placeholder="Enter your password"
            />
          </div>

          <button type="submit">
            Login
          </button>

        </form>

        <div className="auth-footer">
          Don't have an account?{" "}
          <Link to="/register">Register</Link>
        </div>

      </div>
    </div>
  );
}

export default Login;