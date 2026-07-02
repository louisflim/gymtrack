import SummaryItem from "./SummaryItem";

function DashboardSummaryGrid({ firstName, lastName, role }) {
  return (
    <div className="dashboard-summary">
      <SummaryItem label="Name" value={`${firstName} ${lastName}`} />
      <SummaryItem label="Role" value={role} />
      <SummaryItem label="Status" value="Active" />
    </div>
  );
}

export default DashboardSummaryGrid;
