import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import BrandPanel from "../../../components/auth/BrandPanel";
import FormCard from "../../../components/auth/FormCard";
import SplitAuthLayout from "../../../components/layout/SplitAuthLayout";
import { clearSession } from "../../../utils/session";
import ChangePasswordForm from "./ChangePasswordForm";

function ChangePasswordPage() {
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/", { replace: true });
    }
  }, [navigate]);

  const handleSignOut = () => {
    clearSession();
    navigate("/", { replace: true });
  };

  return (
    <SplitAuthLayout
      brand={
        <BrandPanel
          eyebrow="Security"
          titleLines={["SET", "YOUR"]}
          highlight="PASSWORD"
          tagline="For your security, please choose a new password before continuing."
          stats={[
            { value: "01", label: "Current Password" },
            { value: "02", label: "New Password" },
            { value: "03", label: "Continue" },
          ]}
        />
      }
    >
      <FormCard
        eyebrow="GymTrack"
        heading="Reset Password"
        subheading="Your gym owner set a temporary password. Create a new one to continue."
      >
        <ChangePasswordForm
          currentPlaceholder="Temporary password from your gym owner"
          successMessage=""
          onSuccess={() => navigate("/dashboard", { replace: true })}
        />
        <div className="auth-footer">
          <button type="button" className="dashboard-link-button" onClick={handleSignOut}>
            Sign Out
          </button>
        </div>
      </FormCard>
    </SplitAuthLayout>
  );
}

export default ChangePasswordPage;
