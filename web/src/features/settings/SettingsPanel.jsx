import ChangePasswordSection from "./ChangePasswordSection";
import DeleteAccountSection from "./DeleteAccountSection";

/**
 * Shared Settings screen for all roles (member, staff, admin).
 * No theme toggles — account security only.
 */
function SettingsPanel({ onAccountDeleted }) {
  return (
    <div className="settings-panel">
      <ChangePasswordSection />
      <DeleteAccountSection onDeleted={onAccountDeleted} />
    </div>
  );
}

export default SettingsPanel;
