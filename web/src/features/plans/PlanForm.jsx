import DashboardSection from "../../components/dashboard/DashboardSection";

function PlanForm({ values, onChange, onSubmit, submitLabel, loading }) {
  const handle = (field) => (e) => onChange(field, e.target.type === "checkbox" ? e.target.checked : e.target.value);

  return (
    <DashboardSection title="Plan Details" className="dashboard-staff-card">
      <form onSubmit={onSubmit}>
        <div className="input-group">
          <label>Plan Name</label>
          <input value={values.name} onChange={handle("name")} placeholder="Monthly Basic" required />
        </div>
        <div className="auth-field-row">
          <div className="input-group">
            <label>Duration (days)</label>
            <input type="number" min="1" value={values.durationDays} onChange={handle("durationDays")} required />
          </div>
          <div className="input-group">
            <label>Price (PHP)</label>
            <input type="number" min="1" step="0.01" value={values.price} onChange={handle("price")} required />
          </div>
        </div>
        <label className="dashboard-checkbox">
          <input type="checkbox" checked={values.active} onChange={handle("active")} />
          Active plan
        </label>
        <button type="submit" className="auth-submit dashboard-scan-button" disabled={loading}>
          {loading ? "Saving..." : submitLabel}
        </button>
      </form>
    </DashboardSection>
  );
}

export default PlanForm;
