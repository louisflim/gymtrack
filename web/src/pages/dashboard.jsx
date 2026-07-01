import { Link } from "react-router-dom";
import "../css/auth.css";

function Dashboard() {
  const firstName = localStorage.getItem("firstName") || "User";
  const lastName = localStorage.getItem("lastName") || "";
  const role = localStorage.getItem("role") || "Member";

  return (
    <div className="auth-shell dashboard-shell">
      <div className="auth-brand dashboard-brand">
        <div className="auth-brand-eyebrow">GymTrack Dashboard</div>
        <h1 className="auth-brand-title">
          TRAIN
          <br />
          TRACK
          <br />
          <span>THRIVE</span>
        </h1>
        <p className="auth-brand-tagline">
          Monitor your gym access, membership details, and activity from one
          place.
        </p>
        <div className="auth-brand-footer">
          <div className="auth-brand-stat">
            <span className="auth-brand-stat-value">01</span>
            <span className="auth-brand-stat-label">Profile</span>
          </div>
          <div className="auth-brand-stat">
            <span className="auth-brand-stat-value">02</span>
            <span className="auth-brand-stat-label">Membership</span>
          </div>
          <div className="auth-brand-stat">
            <span className="auth-brand-stat-value">03</span>
            <span className="auth-brand-stat-label">Access</span>
          </div>
        </div>
      </div>

      <div className="auth-form-panel dashboard-panel">
        <div className="auth-form-wrap dashboard-card">
          <div className="auth-form-eyebrow">Overview</div>
          <h2 className="auth-form-heading">Dashboard</h2>
          <p className="auth-form-subheading">
            Signed in as {firstName} {lastName}.
          </p>

          <div className="dashboard-summary">
            <div className="dashboard-summary-item">
              <span className="dashboard-summary-label">Name</span>
              <span className="dashboard-summary-value">
                {firstName} {lastName}
              </span>
            </div>
            <div className="dashboard-summary-item">
              <span className="dashboard-summary-label">Role</span>
              <span className="dashboard-summary-value">{role}</span>
            </div>
            <div className="dashboard-summary-item">
              <span className="dashboard-summary-label">Status</span>
              <span className="dashboard-summary-value">Active</span>
            </div>
          </div>

          <div className="dashboard-actions">
            <Link to="/" className="auth-submit dashboard-link">
              Sign Out
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;