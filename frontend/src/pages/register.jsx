import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registerUser } from "../api/auth";
import "../css/auth.css";

function Register() {
  const navigate = useNavigate();

  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [role, setRole] = useState("member");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    setLoading(true);

    const backendRole = role === "owner" ? "ADMIN" : "MEMBER";

    try {
      const data = await registerUser(firstName, lastName, email, password, backendRole);

      localStorage.setItem("token", data.token);
      localStorage.setItem("userId", data.userId);
      localStorage.setItem("firstName", data.firstName);
      localStorage.setItem("lastName", data.lastName);
      localStorage.setItem("role", data.role);

      if (data.role === "ADMIN") {
        navigate("/admin/dashboard");
      } else {
        navigate("/member/dashboard");
      }
    } catch (err) {
      if (err.response?.status === 400) {
        setError(err.response.data || "Registration failed. Check your details.");
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
        <div className="auth-brand-eyebrow">New Member</div>
        <h1 className="auth-brand-title">
          JOIN
          <br />
          THE
          <br />
          <span>FLOOR</span>
        </h1>
        <p className="auth-brand-tagline">
          Create your account to manage your membership, track attendance,
          and pay online — all in one place.
        </p>
        <div className="auth-brand-footer">
          <div className="auth-brand-stat">
            <span className="auth-brand-stat-value">01</span>
            <span className="auth-brand-stat-label">Register</span>
          </div>
          <div className="auth-brand-stat">
            <span className="auth-brand-stat-value">02</span>
            <span className="auth-brand-stat-label">Choose Plan</span>
          </div>
          <div className="auth-brand-stat">
            <span className="auth-brand-stat-value">03</span>
            <span className="auth-brand-stat-label">Get QR Code</span>
          </div>
        </div>
      </div>

      <div className="auth-form-panel">
        <div className="auth-form-wrap">
          <div className="auth-form-eyebrow">GymTrack</div>
          <h2 className="auth-form-heading">Create Account</h2>
          <p className="auth-form-subheading">
            Fill in your details to get started.
          </p>

          {error && <div className="auth-error">{error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="auth-field-row">
              <div className="input-group">
                <label>First Name</label>
                <input
                  type="text"
                  placeholder="First name"
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                  required
                />
              </div>

              <div className="input-group">
                <label>Last Name</label>
                <input
                  type="text"
                  placeholder="Last name"
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                  required
                />
              </div>
            </div>

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

            <div className="auth-field-row">
              <div className="input-group">
                <label>Password</label>
                <input
                  type="password"
                  placeholder="Create password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                  minLength={8}
                />
              </div>

              <div className="input-group">
                <label>Confirm</label>
                <input
                  type="password"
                  placeholder="Repeat password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  required
                />
              </div>
            </div>

            <div className="input-group">
              <label>Register As</label>
              <select value={role} onChange={(e) => setRole(e.target.value)}>
                <option value="member">Member</option>
                <option value="owner">Gym Owner</option>
              </select>
            </div>

            <button type="submit" className="auth-submit" disabled={loading}>
              {loading ? "Creating Account..." : "Create Account"}
            </button>
          </form>

          <div className="auth-footer">
            Already have an account? <Link to="/">Sign In</Link>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Register;