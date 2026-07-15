import DashboardSection from "../../components/dashboard/DashboardSection";
import ChangePasswordForm from "../auth/password/ChangePasswordForm";

function ChangePasswordSection() {
  return (
    <DashboardSection title="Change Password" className="dashboard-staff-card">
      <p className="dashboard-qr-note">
        Update your login password. You will stay signed in after the change.
      </p>
      <ChangePasswordForm currentPlaceholder="Your current password" />
    </DashboardSection>
  );
}

export default ChangePasswordSection;
