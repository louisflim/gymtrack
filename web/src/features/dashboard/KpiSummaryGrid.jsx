import SummaryItem from "../../components/dashboard/SummaryItem";

function KpiSummaryGrid({ items = [] }) {
  if (!items.length) return null;

  return (
    <div className="dashboard-summary dashboard-kpi-grid">
      {items.map((item) => (
        <SummaryItem key={item.label} label={item.label} value={item.value} />
      ))}
    </div>
  );
}

export default KpiSummaryGrid;
