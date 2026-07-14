import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import BrandPanel from "../../../components/auth/BrandPanel";
import FormCard from "../../../components/auth/FormCard";
import AuthInput from "../../../components/auth/AuthInput";
import SplitAuthLayout from "../../../components/layout/SplitAuthLayout";
import { clearSession, setMustChangePassword } from "../../../utils/session";
import { getApiError } from "../../../utils/apiError";
import { getPasswordMismatchError } from "../../../utils/authHelpers";
import { changePassword } from "./api";

function ChangePasswordPage() {
  const navigate = useNavigate();
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/", { replace: true });
    }
  }, [navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (getPasswordMismatchError(newPassword, confirmPassword)) {
      setError("Passwords do not match.");
      return;
    }

    setLoading(true);
    try {
      await changePassword(currentPassword, newPassword, confirmPassword);
      setMustChangePassword(false);
      navigate("/dashboard", { replace: true });
    } catch (err) {
      setError(getApiError(err, "We couldn't update your password. Please try again."));
    } finally {
      setLoading(false);
    }
  };

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
        {error && <div className="auth-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          <AuthInput
            label="Current Password"
            type="password"
            value={currentPassword}
            onChange={(e) => setCurrentPassword(e.target.value)}
            placeholder="Temporary password from your gym owner"
            required
          />
          <AuthInput
            label="New Password"
            type="password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            placeholder="Choose a new password"
            required
          />
          <AuthInput
            label="Confirm New Password"
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            placeholder="Confirm your new password"
            required
          />
          <button type="submit" className="auth-submit" disabled={loading}>
            {loading ? "Updating..." : "Update Password"}
          </button>
        </form>
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
