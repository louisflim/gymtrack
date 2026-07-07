import PaymentTable from "./PaymentTable";

function PaymentHistory({ payments }) {
  return <PaymentTable payments={payments} title="Payment History" />;
}

export default PaymentHistory;
