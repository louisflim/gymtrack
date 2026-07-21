import { useEffect, useState } from "react";
import { fetchMyMembership, MembershipCard } from "../../features/membership";
import { createCheckout, confirmMockPayment, fetchMyPayments, fetchPaymentMode, PaymentHistory } from "../../features/payments";
import { fetchActivePlans, PlanPicker } from "../../features/plans";
import { AttendanceHistory } from "../../features/attendance";

function paymentModeNoteFrom(modeInfo) {
  if (!modeInfo) return "";
  if (modeInfo.description) return modeInfo.description;
  if (modeInfo.mode) return `Payment mode: ${modeInfo.mode}`;
  return "";
}

function MemberDashboardPanel({ membership: membershipProp, onMembershipChange, section = "all" }) {
  const [membership, setMembership] = useState(membershipProp);
  const [plans, setPlans] = useState([]);
  const [payments, setPayments] = useState([]);
  const [payStatus, setPayStatus] = useState("");
  const [paying, setPaying] = useState(false);
  const [paymentModeNote, setPaymentModeNote] = useState("");

  const enrolled = membership?.gymId != null;

  const loadData = async () => {
    const [membershipData, planData, paymentData, modeInfo] = await Promise.all([
      fetchMyMembership(),
      fetchActivePlans(),
      fetchMyPayments(),
      fetchPaymentMode().catch(() => null),
    ]);
    setMembership(membershipData);
    setPlans(planData);
    setPayments(paymentData);
    setPaymentModeNote(paymentModeNoteFrom(modeInfo));
    onMembershipChange?.(membershipData);
  };

  useEffect(() => {
    setMembership(membershipProp);
  }, [membershipProp]);

  useEffect(() => {
    loadData().catch(() => {});
  }, []);

  const handleSubscribe = async (planId) => {
    setPaying(true);
    setPayStatus("");
    try {
      const checkout = await createCheckout(planId);
      const isMock = checkout.mockCheckout || checkout.paymentMode === "MOCK";

      if (isMock) {
        // Confirm in-app — avoid /payment/success redirect (404 on static hosts without SPA rewrites).
        setPayStatus("Mock checkout — confirming payment...");
        await confirmMockPayment(checkout.reference);
        setPayStatus("Payment confirmed. Membership activated.");
        await loadData();
        setPaying(false);
        return;
      }

      window.location.href = checkout.checkoutUrl;
    } catch (err) {
      setPayStatus(err.response?.data || "Checkout failed.");
      setPaying(false);
    }
  };

  const showPlans = section === "all" || section === "plans";
  const showActivity = section === "all" || section === "activity";

  return (
    <>
      {showPlans && (
        <>
          <MembershipCard membership={membership} />
          <PlanPicker
            plans={plans}
            onSubscribe={handleSubscribe}
            loading={paying}
            statusMessage={payStatus}
            enrolled={enrolled}
            membershipStatus={membership?.status}
            membershipEndDate={membership?.endDate}
            paymentModeNote={paymentModeNote}
          />
        </>
      )}
      {showActivity && (
        <>
          <PaymentHistory payments={payments} />
          <AttendanceHistory />
        </>
      )}
    </>
  );
}

export default MemberDashboardPanel;
