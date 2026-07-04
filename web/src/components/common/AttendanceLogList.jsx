import { formatDateTime } from "../../utils/formatters";

function AttendanceLogList({ logs = [], showMember = false, emptyMessage = "No attendance records yet." }) {
  if (!logs.length) {
    return <p className="dashboard-qr-note">{emptyMessage}</p>;
  }

  return (
    <ul className="dashboard-attendance-list">
      {logs.map((log, index) => (
        <li key={log.id ?? `${log.checkInTime}-${index}`} className="dashboard-attendance-item">
          {showMember && log.memberName && <strong>{log.memberName}</strong>}
          <span>Check-in: {formatDateTime(log.checkInTime)}</span>
          <span>Check-out: {formatDateTime(log.checkOutTime)}</span>
        </li>
      ))}
    </ul>
  );
}

export default AttendanceLogList;
