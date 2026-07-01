import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { loginUser } from "../api/auth";
import "../css/auth.css";

function Login() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const data = await loginUser(email, password);

      localStorage.setItem("token", data.token);
      localStorage.setItem("userId", data.userId);
      localStorage.setItem("firstName", data.firstName);
      localStorage.setItem("lastName", data.lastName);
      localStorage.setItem("role", data.role);

      navigate("/dashboard");
    } catch (err) {
      if (err.response?.status === 401) {
        setError("Invalid email or password.");
      } else if (err.response?.status === 403) {
        setError(err.response.data || "This account has been deactivated.");
      } else {
        setError("Something went wrong. Try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-shell">
      <div className="auth-brand">
        <div className="auth-brand-eyebrow">Member Access</div>
        <h1 className="auth-brand-title">
          TRAIN
          <br />
          TRACK
          <br />
          <span>TRANSFORM</span>
        </h1>
        <p className="auth-brand-tagline">
          One system for memberships, attendance, and payments. Built for
          gyms that run on discipline, not paperwork.
        </p>
        <div className="auth-brand-footer">
          <div className="auth-brand-stat">
            <span className="auth-brand-stat-value">01</span>
            <span className="auth-brand-stat-label">Sign In</span>
          </div>
          <div className="auth-brand-stat">
            <span className="auth-brand-stat-value">02</span>
            <span className="auth-brand-stat-label">Scan QR</span>
          </div>
          <div className="auth-brand-stat">
            <span className="auth-brand-stat-value">03</span>
            <span className="auth-brand-stat-label">Train</span>
          </div>
        </div>
      </div>

      <div className="auth-form-panel">
        <div className="auth-form-wrap">
          <div className="auth-form-eyebrow">GymTrack</div>
          <h2 className="auth-form-heading">Sign In</h2>
          <p className="auth-form-subheading">
            Enter your credentials to access your account.
          </p>

          {error && <div className="auth-error">{error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="input-group">
              <label>Email</label>
              <input
                type="email"
                placeholder="you@example.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="input-group">
              <label>Password</label>
              <input
                type="password"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <button type="submit" className="auth-submit" disabled={loading}>
              {loading ? "Signing In..." : "Sign In"}
            </button>
          </form>

          <div className="auth-footer">
            Don't have an account? <Link to="/register">Register</Link>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login;