import { useNavigate } from "react-router-dom";

function PaymentCancel() {
  const navigate = useNavigate();

  return (
    <div className="dashboard-page">
      <div className="dashboard-panel">
        <h2 className="dashboard-greeting">Payment Cancelled</h2>
        <p className="dashboard-subtitle">No payment was processed.</p>
        <button type="button" className="auth-submit dashboard-scan-button" onClick={() => navigate("/dashboard")}>
          Back to Dashboard
        </button>
      </div>
    </div>
  );
}

export default PaymentCancel;
