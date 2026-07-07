import DashboardSection from "../../components/dashboard/DashboardSection";
import StatusBadge from "../../components/common/StatusBadge";

function MembershipCard({ membership }) {
  return (
    <DashboardSection title="My Membership" className="dashboard-staff-card">
      <div className="dashboard-membership-grid">
        <div><span className="dashboard-summary-label">Plan</span><strong>{membership?.planName || "No plan"}</strong></div>
        <div><span className="dashboard-summary-label">Status</span><StatusBadge status={membership?.status || "NONE"} /></div>
        <div><span className="dashboard-summary-label">Valid Until</span><strong>{membership?.endDate || "—"}</strong></div>
      </div>
    </DashboardSection>
  );
}

export default MembershipCard;
