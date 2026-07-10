import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { confirmMockPayment, fetchPaymentStatus } from "./api";

function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function PaymentSuccessPage() {
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const [message, setMessage] = useState("Confirming your payment...");

  useEffect(() => {
    const reference = params.get("reference");
    if (!reference) {
      setMessage("Missing payment reference. Redirecting...");
      const timer = setTimeout(() => navigate("/dashboard", { replace: true }), 1500);
      return () => clearTimeout(timer);
    }

    let cancelled = false;

    async function completePayment() {
      try {
        const initial = await fetchPaymentStatus(reference);

        if (initial.mockCheckout && !initial.paid) {
          await confirmMockPayment(reference);
          if (!cancelled) setMessage("Payment confirmed. Activating membership...");
        } else if (!initial.paid) {
          if (!cancelled) setMessage("Waiting for PayMongo confirmation...");
          for (let attempt = 0; attempt < 15 && !cancelled; attempt++) {
            const latest = await fetchPaymentStatus(reference);
            if (latest.paid) {
              if (!cancelled) setMessage("Payment confirmed. Activating membership...");
              break;
            }
            await sleep(2000);
          }
        } else {
          if (!cancelled) setMessage("Payment already confirmed.");
        }
      } catch {
        if (!cancelled) setMessage("We could not confirm your payment yet. Check your dashboard in a moment.");
      } finally {
        if (!cancelled) {
          setTimeout(() => navigate("/dashboard", { replace: true }), 1500);
        }
      }
    }

    completePayment();
    return () => {
      cancelled = true;
    };
  }, [params, navigate]);

  return (
    <div className="dashboard-page">
      <div className="dashboard-panel">
        <h2 className="dashboard-greeting">Payment Successful</h2>
        <p className="dashboard-subtitle">{message}</p>
      </div>
    </div>
  );
}

export default PaymentSuccessPage;
