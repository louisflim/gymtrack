import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { confirmMockPayment } from "./api";

function PaymentSuccessPage() {
  const navigate = useNavigate();
  const [params] = useSearchParams();

  useEffect(() => {
    const reference = params.get("reference");
    if (!reference) return;

    confirmMockPayment(reference)
      .catch(() => {})
      .finally(() => {
        setTimeout(() => navigate("/dashboard", { replace: true }), 1500);
      });
  }, [params, navigate]);

  return (
    <div className="dashboard-page">
      <div className="dashboard-panel">
        <h2 className="dashboard-greeting">Payment Successful</h2>
        <p className="dashboard-subtitle">Your membership is being activated. Redirecting to dashboard...</p>
      </div>
    </div>
  );
}

export default PaymentSuccessPage;
