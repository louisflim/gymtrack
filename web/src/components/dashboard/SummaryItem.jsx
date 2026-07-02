function SummaryItem({ label, value }) {
  return (
    <div className="dashboard-summary-item">
      <span className="dashboard-summary-label">{label}</span>
      <span className="dashboard-summary-value">{value}</span>
    </div>
  );
}

export default SummaryItem;
