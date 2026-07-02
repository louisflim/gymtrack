function DashboardSection({ title, className = "", children }) {
  const isCard = className.includes("-card");
  const sectionClass = isCard ? className : `dashboard-section ${className}`.trim();

  return (
    <div className={sectionClass}>
      {title && <span className="dashboard-summary-label">{title}</span>}
      {children}
    </div>
  );
}

export default DashboardSection;
