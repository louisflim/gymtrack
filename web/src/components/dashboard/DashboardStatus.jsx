function DashboardStatus({ message }) {
  if (!message) return null;
  return <p className="dashboard-scan-status">{message}</p>;
}

export default DashboardStatus;
