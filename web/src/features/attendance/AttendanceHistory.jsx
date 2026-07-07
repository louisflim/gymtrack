import { useEffect, useState } from "react";
import DashboardSection from "../../components/dashboard/DashboardSection";
import { fetchMyAttendanceLogs } from "./api";
import AttendanceLogList from "./AttendanceLogList";

function AttendanceHistory() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchMyAttendanceLogs()
      .then(setLogs)
      .catch(() => setLogs([]))
      .finally(() => setLoading(false));
  }, []);

  return (
    <DashboardSection title="Attendance History" className="dashboard-attendance-card">
      {loading ? (
        <p className="dashboard-qr-note">Loading attendance...</p>
      ) : (
        <AttendanceLogList logs={logs} />
      )}
    </DashboardSection>
  );
}

export default AttendanceHistory;
