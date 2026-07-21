import DashboardSection from "../../components/dashboard/DashboardSection";
import DashboardStatus from "../../components/dashboard/DashboardStatus";

function hasActiveMembership(status) {
  return status === "ACTIVE" || status === "EXPIRING_SOON";
}

function PlanPicker({
  plans,
  onSubscribe,
  loading,
  statusMessage,
  enrolled,
  membershipStatus,
  membershipEndDate,
  paymentModeNote,
}) {
  const alreadySubscribed = hasActiveMembership(membershipStatus);

  return (
    <DashboardSection title="Choose a Plan" className="dashboard-staff-card">
      {paymentModeNote && !alreadySubscribed && (
        <p className="dashboard-scan-status">{paymentModeNote}</p>
      )}
      <div className="dashboard-plan-grid">
        {!enrolled ? (
          <p className="dashboard-qr-note">
            Scan the gym QR code at the front desk first to see available subscription plans.
          </p>
        ) : alreadySubscribed ? (
          <p className="dashboard-qr-note">
            You already have an active membership
            {membershipEndDate ? ` until ${membershipEndDate}` : ""}. You can subscribe again after it
            expires.
          </p>
        ) : plans.length === 0 ? (
          <p className="dashboard-qr-note">No active plans available at your gym yet.</p>
        ) : (
          plans.map((plan) => (
            <div key={plan.id} className="dashboard-plan-card">
              <h4>{plan.name}</h4>
              <p>₱{Number(plan.price).toFixed(2)}</p>
              <p className="dashboard-qr-note">{plan.durationDays} days access</p>
              <button
                type="button"
                className="auth-submit dashboard-scan-button"
                disabled={loading}
                onClick={() => onSubscribe(plan.id)}
              >
                {loading ? "Processing..." : "Subscribe & Pay"}
              </button>
            </div>
          ))
        )}
      </div>
      <DashboardStatus message={statusMessage} />
    </DashboardSection>
  );
}

export default PlanPicker;
