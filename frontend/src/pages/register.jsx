import { Link } from "react-router-dom";
import "../css/auth.css";

function Register() {
  return (
    <div className="auth-container">
      <div className="auth-card">

        <h1>Create Account</h1>
        <p>Join GymTrack</p>

        <form>

          <div className="input-group">
            <label>Full Name</label>
            <input
              type="text"
              placeholder="Enter your full name"
            />
          </div>

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
              placeholder="Create a password"
            />
          </div>

          <div className="input-group">
            <label>Confirm Password</label>
            <input
              type="password"
              placeholder="Confirm your password"
            />
          </div>

          <div className="input-group">
            <label>Register As</label>
            <select>
              <option value="member">Member</option>
              <option value="owner">Gym Owner</option>
            </select>
          </div>

          <button type="submit">
            Create Account
          </button>

        </form>

        <div className="auth-footer">
          Already have an account?{" "}
          <Link to="/">Login</Link>
        </div>

      </div>
    </div>
  );
}

export default Register;