import { useState } from "react";
import AuthInput from "../../../components/auth/AuthInput";
import { getApiError } from "../../../utils/apiError";
import { getPasswordMismatchError } from "../../../utils/authHelpers";
import { setMustChangePassword } from "../../../utils/session";
import { changePassword } from "./api";

/**
 * Reusable password-change form for forced reset and Settings.
 */
function ChangePasswordForm({
  currentPlaceholder = "Current password",
  onSuccess,
  successMessage = "Password updated.",
  submitLabel = "Update Password",
}) {
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [status, setStatus] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setStatus("");

    if (getPasswordMismatchError(newPassword, confirmPassword)) {
      setError("Passwords do not match.");
      return;
    }

    setLoading(true);
    try {
      await changePassword(currentPassword, newPassword, confirmPassword);
      setMustChangePassword(false);
      setCurrentPassword("");
      setNewPassword("");
      setConfirmPassword("");
      setStatus(successMessage);
      onSuccess?.();
    } catch (err) {
      setError(getApiError(err, "We couldn't update your password. Please try again."));
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {error && <div className="auth-error">{error}</div>}
      {status && <p className="dashboard-scan-status">{status}</p>}
      <AuthInput
        label="Current Password"
        type="password"
        value={currentPassword}
        onChange={(e) => setCurrentPassword(e.target.value)}
        placeholder={currentPlaceholder}
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
        {loading ? "Updating..." : submitLabel}
      </button>
    </form>
  );
}

export default ChangePasswordForm;
