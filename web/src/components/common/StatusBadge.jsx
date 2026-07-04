const STATUS_CLASS = {
  ACTIVE: "status-badge status-active",
  EXPIRING_SOON: "status-badge status-expiring",
  EXPIRED: "status-badge status-expired",
  NONE: "status-badge status-none",
  PAID: "status-badge status-active",
  PENDING: "status-badge status-expiring",
  FAILED: "status-badge status-expired",
  CANCELLED: "status-badge status-none",
};

function formatStatus(status) {
  return (status || "NONE").replaceAll("_", " ");
}

function StatusBadge({ status }) {
  const key = status || "NONE";
  return <span className={STATUS_CLASS[key] || STATUS_CLASS.NONE}>{formatStatus(key)}</span>;
}

export default StatusBadge;
