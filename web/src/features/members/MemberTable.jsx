import { useState } from "react";
import FilterBar from "../../components/common/FilterBar";
import StatusBadge from "../../components/common/StatusBadge";
import DashboardSection from "../../components/dashboard/DashboardSection";

function MemberTable({ members, plans, onUpdate, onAssignPlan, onDelete }) {
  const [search, setSearch] = useState("");
  const [status, setStatus] = useState("ALL");
  const [editing, setEditing] = useState(null);
  const [assigning, setAssigning] = useState(null);

  const filtered = members.filter((member) => {
    const term = search.toLowerCase();
    const matchesSearch = !term || `${member.firstName} ${member.lastName} ${member.email}`.toLowerCase().includes(term);
    const matchesStatus = status === "ALL" || member.membershipStatus === status;
    return matchesSearch && matchesStatus;
  });

  return (
    <DashboardSection title="Member Management" className="dashboard-staff-card">
      <FilterBar
        search={search}
        onSearchChange={setSearch}
        status={status}
        onStatusChange={setStatus}
        searchPlaceholder="Search members..."
        statusOptions={[
          { value: "ALL", label: "All statuses" },
          { value: "ACTIVE", label: "Active" },
          { value: "EXPIRING_SOON", label: "Expiring Soon" },
          { value: "EXPIRED", label: "Expired" },
          { value: "NONE", label: "None" },
        ]}
      />
      <div className="dashboard-table-wrap">
        <table className="dashboard-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Plan</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 ? (
              <tr>
                <td colSpan="5">
                  {members.length === 0
                    ? "No members enrolled at your gym yet. Ask new members to scan your gym QR code."
                    : "No members match your search or filter."}
                </td>
              </tr>
            ) : (
              filtered.map((member) => (
                <tr key={member.id}>
                  <td>{member.firstName} {member.lastName}</td>
                  <td>{member.email}</td>
                  <td>{member.planName || "—"}</td>
                  <td><StatusBadge status={member.membershipStatus} /></td>
                  <td className="dashboard-actions-cell">
                    <button type="button" className="dashboard-link-button" onClick={() => setEditing(member)}>Edit</button>
                    <button type="button" className="dashboard-link-button" onClick={() => setAssigning(member)}>Assign Plan</button>
                    <button
                      type="button"
                      className="dashboard-link-button dashboard-link-button-danger"
                      onClick={() => {
                        if (window.confirm(`Delete ${member.firstName} ${member.lastName}? This removes their account, memberships, payments, and attendance history.`)) {
                          onDelete(member.id);
                        }
                      }}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {editing && (
        <MemberEditModal member={editing} onClose={() => setEditing(null)} onSave={onUpdate} />
      )}
      {assigning && (
        <AssignPlanModal member={assigning} plans={plans.filter((p) => p.active)} onClose={() => setAssigning(null)} onAssign={onAssignPlan} />
      )}
    </DashboardSection>
  );
}

function MemberEditModal({ member, onClose, onSave }) {
  const [form, setForm] = useState({
    firstName: member.firstName,
    lastName: member.lastName,
    email: member.email,
    active: member.active,
  });
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await onSave(member.id, form);
      onClose();
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="dashboard-modal-overlay">
      <div className="dashboard-modal">
        <h3>Edit Member</h3>
        <form onSubmit={handleSubmit}>
          <div className="auth-field-row">
            <div className="input-group"><label>First Name</label><input value={form.firstName} onChange={(e) => setForm({ ...form, firstName: e.target.value })} required /></div>
            <div className="input-group"><label>Last Name</label><input value={form.lastName} onChange={(e) => setForm({ ...form, lastName: e.target.value })} required /></div>
          </div>
          <div className="input-group"><label>Email</label><input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required /></div>
          <label className="dashboard-checkbox"><input type="checkbox" checked={form.active} onChange={(e) => setForm({ ...form, active: e.target.checked })} /> Active account</label>
          <div className="dashboard-modal-actions">
            <button type="button" className="dashboard-logout" onClick={onClose}>Cancel</button>
            <button type="submit" className="auth-submit" disabled={saving}>{saving ? "Saving..." : "Save"}</button>
          </div>
        </form>
      </div>
    </div>
  );
}

function AssignPlanModal({ member, plans, onClose, onAssign }) {
  const [planId, setPlanId] = useState(plans[0]?.id || "");
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!planId) return;
    setSaving(true);
    try {
      await onAssign(member.id, planId);
      onClose();
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="dashboard-modal-overlay">
      <div className="dashboard-modal">
        <h3>Assign Plan to {member.firstName}</h3>
        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label>Plan</label>
            <select className="dashboard-select" value={planId} onChange={(e) => setPlanId(e.target.value)} required>
              {plans.map((plan) => (
                <option key={plan.id} value={plan.id}>{plan.name} — ₱{Number(plan.price).toFixed(2)}</option>
              ))}
            </select>
          </div>
          <div className="dashboard-modal-actions">
            <button type="button" className="dashboard-logout" onClick={onClose}>Cancel</button>
            <button type="submit" className="auth-submit" disabled={saving || !planId}>{saving ? "Assigning..." : "Assign Plan"}</button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default MemberTable;
