import DashboardSection from "../../components/dashboard/DashboardSection";
import FilterBar from "../../components/common/FilterBar";
import AttendanceLogList from "./AttendanceLogList";

function AttendanceLogTable({ logs, search, onSearchChange, date, onDateChange }) {
  return (
    <DashboardSection title="Attendance Logs" className="dashboard-attendance-card">
      <FilterBar
        search={search}
        onSearchChange={onSearchChange}
        searchPlaceholder="Search by member name or email..."
        date={date}
        onDateChange={onDateChange}
      />
      <AttendanceLogList
        logs={logs}
        showMember
        emptyMessage="No attendance logs match your filters."
      />
    </DashboardSection>
  );
}

export default AttendanceLogTable;
