import AccountFields from "../../../components/auth/AccountFields";
import DashboardSection from "../../../components/dashboard/DashboardSection";
import DashboardStatus from "../../../components/dashboard/DashboardStatus";

function CreateStaffForm({ values, onChange, onSubmit, loading, statusMessage, adminGymName }) {
  const missingGym = !adminGymName;

  return (
    <DashboardSection title="Create Staff Account" className="dashboard-staff-card">
      {adminGymName ? (
        <p className="dashboard-qr-note">Gym: <strong>{adminGymName}</strong> — new staff will belong to this gym.</p>
      ) : (
        <p className="auth-error dashboard-qr-note">
          Your account isn't connected to a gym yet. Sign out, then create a new account as <strong>Gym Owner</strong> and enter your gym name.
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
