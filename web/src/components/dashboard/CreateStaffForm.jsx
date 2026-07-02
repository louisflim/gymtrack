import AccountFields from "../auth/AccountFields";
import DashboardSection from "./DashboardSection";
import DashboardStatus from "./DashboardStatus";

function CreateStaffForm({ values, onChange, onSubmit, loading, statusMessage }) {
  return (
    <DashboardSection title="Create Staff Account" className="dashboard-staff-card">
      <form onSubmit={onSubmit}>
        <AccountFields values={values} onChange={onChange} passwordPlaceholder="Staff password" />
        <button type="submit" className="auth-submit dashboard-scan-button" disabled={loading}>
          {loading ? "Creating Staff..." : "Create Staff"}
        </button>
      </form>
      <DashboardStatus message={statusMessage} />
    </DashboardSection>
  );
}

export default CreateStaffForm;
