import AccountFields from "../auth/AccountFields";
import DashboardSection from "./DashboardSection";
import DashboardStatus from "./DashboardStatus";

function CreateStaffForm({ values, onChange, onSubmit, loading, statusMessage, adminGymName }) {
  const missingGym = !adminGymName;

  return (
    <DashboardSection title="Create Staff Account" className="dashboard-staff-card">
      {adminGymName ? (
        <p className="dashboard-qr-note">Gym: <strong>{adminGymName}</strong> — new staff will belong to this gym.</p>
      ) : (
        <p className="auth-error dashboard-qr-note">
          Your admin account is not linked to a gym. Sign out and register again as <strong>Gym Owner</strong> with a gym name.
        </p>
      )}
      <form onSubmit={onSubmit}>
        <AccountFields values={values} onChange={onChange} passwordPlaceholder="Staff password" />
        <button type="submit" className="auth-submit dashboard-scan-button" disabled={loading || missingGym}>
          {loading ? "Creating Staff..." : "Create Staff"}
        </button>
      </form>
      <DashboardStatus message={statusMessage} />
    </DashboardSection>
  );
}

export default CreateStaffForm;
