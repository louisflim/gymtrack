import SummaryItem from "./SummaryItem";
import StatusBadge from "../common/StatusBadge";

function DashboardSummaryGrid({ firstName, lastName, role, membershipStatus, planName }) {
  return (
    <div className="dashboard-summary">
      <SummaryItem label="Name" value={`${firstName} ${lastName}`} />
      <SummaryItem label="Role" value={role} />
      <SummaryItem
        label="Membership"
        value={<StatusBadge status={membershipStatus || "NONE"} />}
      />
      {planName && <SummaryItem label="Plan" value={planName} />}
    </div>
  );
}

export default DashboardSummaryGrid;
