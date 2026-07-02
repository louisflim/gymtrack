function DashboardHero() {
  return (
    <section className="dashboard-hero" aria-hidden="true">
      <div className="dashboard-hero-inner">
        <p className="dashboard-hero-eyebrow">GymTrack Dashboard</p>
        <h1 className="dashboard-hero-title">
          TRAIN
          <br />
          TRACK <span>THRIVE</span>
        </h1>
        <p className="dashboard-hero-tagline">
          Monitor your gym access, membership details, and activity from one place.
        </p>
        <div className="dashboard-hero-stats">
          <div>
            <span className="dashboard-hero-stat-value">01</span>
            <span className="dashboard-hero-stat-label">Profile</span>
          </div>
          <div>
            <span className="dashboard-hero-stat-value">02</span>
            <span className="dashboard-hero-stat-label">Membership</span>
          </div>
          <div>
            <span className="dashboard-hero-stat-value">03</span>
            <span className="dashboard-hero-stat-label">Access</span>
          </div>
        </div>
      </div>
    </section>
  );
}

export default DashboardHero;
