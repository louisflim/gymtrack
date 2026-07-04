import { useState } from "react";
import DashboardSection from "../dashboard/DashboardSection";

function StaffTable({ staff, onUpdate }) {
  const [editing, setEditing] = useState(null);

  return (
    <DashboardSection title="Staff Accounts" className="dashboard-staff-card">
      <div className="dashboard-table-wrap">
        <table className="dashboard-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {staff.length === 0 ? (
              <tr>
                <td colSpan="4">No staff accounts yet. Create one from the Overview tab.</td>
              </tr>
            ) : (
              staff.map((member) => (
                <tr key={member.id}>
                  <td>{member.firstName} {member.lastName}</td>
                  <td>{member.email}</td>
                  <td>{member.active ? "Active" : "Deactivated"}</td>
                  <td className="dashboard-actions-cell">
                    <button type="button" className="dashboard-link-button" onClick={() => setEditing(member)}>
                      Edit
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {editing && (
        <StaffEditModal
          staff={editing}
          onClose={() => setEditing(null)}
          onSave={onUpdate}
        />
      )}
    </DashboardSection>
  );
}

function StaffEditModal({ staff, onClose, onSave }) {
  const [form, setForm] = useState({
    firstName: staff.firstName,
    lastName: staff.lastName,
    email: staff.email,
    active: staff.active,
  });
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await onSave(staff.id, form);
      onClose();
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="dashboard-modal-overlay">
      <div className="dashboard-modal">
        <h3>Edit Staff</h3>
        <form onSubmit={handleSubmit}>
          <div className="auth-field-row">
            <div className="input-group">
              <label>First Name</label>
              <input value={form.firstName} onChange={(e) => setForm({ ...form, firstName: e.target.value })} required />
            </div>
            <div className="input-group">
              <label>Last Name</label>
              <input value={form.lastName} onChange={(e) => setForm({ ...form, lastName: e.target.value })} required />
            </div>
          </div>
          <div className="input-group">
            <label>Email</label>
            <input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required />
          </div>
          <label className="dashboard-checkbox">
            <input type="checkbox" checked={form.active} onChange={(e) => setForm({ ...form, active: e.target.checked })} />
            Active account
          </label>
          <div className="dashboard-modal-actions">
            <button type="button" className="dashboard-logout" onClick={onClose}>Cancel</button>
            <button type="submit" className="auth-submit" disabled={saving}>{saving ? "Saving..." : "Save"}</button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default StaffTable;
