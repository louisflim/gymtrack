import StatusBadge from "../../components/common/StatusBadge";
import DashboardSection from "../../components/dashboard/DashboardSection";

function PaymentTable({ payments, title = "Payments" }) {
  return (
    <DashboardSection title={title} className="dashboard-staff-card">
      <div className="dashboard-table-wrap">
        <table className="dashboard-table">
          <thead>
            <tr>
              <th>Member</th>
              <th>Plan</th>
              <th>Amount</th>
              <th>Method</th>
              <th>Status</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            {payments.length === 0 ? (
              <tr><td colSpan="6">No payments yet.</td></tr>
            ) : (
              payments.map((payment) => (
                <tr key={payment.id}>
                  <td>{payment.memberName || "You"}</td>
                  <td>{payment.planName}</td>
                  <td>₱{Number(payment.amount).toFixed(2)}</td>
                  <td>{payment.paymentMethod || "—"}</td>
                  <td><StatusBadge status={payment.status} /></td>
                  <td>{new Date(payment.paidAt || payment.createdAt).toLocaleString()}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </DashboardSection>
  );
}

export default PaymentTable;
