import StatusBadge from "../common/StatusBadge";
import DashboardSection from "../dashboard/DashboardSection";

function PlanList({ plans, onEdit }) {
  return (
    <DashboardSection title="Subscription Plans" className="dashboard-staff-card">
      <div className="dashboard-table-wrap">
        <table className="dashboard-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Duration</th>
              <th>Price</th>
              <th>Status</th>
              <th />
            </tr>
          </thead>
          <tbody>
            {plans.length === 0 ? (
              <tr><td colSpan="5">No plans yet.</td></tr>
            ) : (
              plans.map((plan) => (
                <tr key={plan.id}>
                  <td>{plan.name}</td>
                  <td>{plan.durationDays} days</td>
                  <td>₱{Number(plan.price).toFixed(2)}</td>
                  <td><StatusBadge status={plan.active ? "ACTIVE" : "EXPIRED"} /></td>
                  <td>
                    <button type="button" className="dashboard-link-button" onClick={() => onEdit(plan)}>
                      Edit
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </DashboardSection>
  );
}

export default PlanList;
