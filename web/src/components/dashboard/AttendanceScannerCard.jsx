import DashboardSection from "./DashboardSection";
import DashboardStatus from "./DashboardStatus";

function AttendanceScannerCard({ onOpenScanner, statusMessage }) {
  return (
    <DashboardSection title="Attendance Scanner" className="dashboard-scan-card">
      <button type="button" className="auth-submit dashboard-scan-button" onClick={onOpenScanner}>
        Open Camera Scanner
      </button>
      <DashboardStatus message={statusMessage} />
    </DashboardSection>
  );
}

export default AttendanceScannerCard;
