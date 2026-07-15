import { useState } from "react";
import AuthInput from "../../components/auth/AuthInput";
import ConfirmDialog from "../../components/common/ConfirmDialog";
import DashboardSection from "../../components/dashboard/DashboardSection";
import { getApiError } from "../../utils/apiError";
import { deleteAccount } from "./api";

function DeleteAccountSection({ onDeleted }) {
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const openConfirm = (e) => {
    e.preventDefault();
    setError("");
    if (!password.trim()) {
      setError("Please enter your password to delete your account.");
      return;
    }
    setConfirmOpen(true);
  };

  const handleDelete = async () => {
    setLoading(true);
    setError("");
    try {
      await deleteAccount(password);
      setConfirmOpen(false);
      onDeleted?.();
    } catch (err) {
      setError(getApiError(err, "We couldn't delete your account. Please try again."));
      setConfirmOpen(false);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <DashboardSection title="Delete Account" className="dashboard-staff-card settings-danger-card">
        <p className="dashboard-qr-note">
          Permanently delete your GymTrack account and personal data. This cannot be undone.
        </p>
        {error && <div className="auth-error">{error}</div>}
        <form onSubmit={openConfirm}>
          <AuthInput
            label="Confirm with password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Enter your password"
            required
          />
          <button type="submit" className="auth-submit auth-submit-danger" disabled={loading}>
            Delete Account
          </button>
        </form>
      </DashboardSection>

      {confirmOpen && (
        <ConfirmDialog
          title="Delete your account?"
          message="Your account, memberships, payments, and attendance history will be permanently removed. This cannot be undone."
          confirmLabel="Delete Account"
          cancelLabel="Keep Account"
          danger
          loading={loading}
          onConfirm={handleDelete}
          onClose={() => !loading && setConfirmOpen(false)}
        />
      )}
    </>
  );
}

export default DeleteAccountSection;
