function BrandPanel({ eyebrow, titleLines, highlight, tagline, stats, className = "" }) {
  return (
    <div className={`auth-brand ${className}`.trim()}>
      {eyebrow ? <div className="auth-brand-eyebrow">{eyebrow}</div> : null}
      <h1 className="auth-brand-title">
        {titleLines.map((line) => (
          <span key={line} className="auth-brand-title-line">
            {line}
          </span>
        ))}
        <span className="auth-brand-title-highlight">{highlight}</span>
      </h1>
      <p className="auth-brand-tagline">{tagline}</p>
      <div className="auth-brand-footer">
        {stats.map((stat) => (
          <div className="auth-brand-stat" key={stat.value + stat.label}>
            <span className="auth-brand-stat-value">{stat.value}</span>
            <span className="auth-brand-stat-label">{stat.label}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default BrandPanel;
