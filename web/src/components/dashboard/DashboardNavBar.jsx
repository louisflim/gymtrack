import { NAV_TABS_BY_ROLE } from "../../constants/dashboardUi";

function DashboardNavBar({ role, activeTab, onChange }) {
  const tabs = NAV_TABS_BY_ROLE[role] || NAV_TABS_BY_ROLE.MEMBER;

  return (
    <nav className="dashboard-nav-bar" aria-label="Dashboard sections">
      <div className="dashboard-nav-bar-inner">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            type="button"
            className={`dashboard-nav-item ${activeTab === tab.id ? "is-active" : ""}`}
            onClick={() => onChange(tab.id)}
          >
            <span className="dashboard-nav-icon" aria-hidden="true">
              {tab.icon}
            </span>
            <span className="dashboard-nav-label">{tab.label}</span>
          </button>
        ))}
      </div>
    </nav>
  );
}

export default DashboardNavBar;
