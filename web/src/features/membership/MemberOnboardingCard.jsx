import { ONBOARDING_STEPS } from "../../constants/dashboardUi";
import DashboardSection from "../../components/dashboard/DashboardSection";
import DashboardStatus from "../../components/dashboard/DashboardStatus";

function MemberOnboardingCard({ membership, onOpenScanner, statusMessage }) {
  const step = membership?.nextStep || "ENROLL_AT_GYM";
  const info = ONBOARDING_STEPS[step] || ONBOARDING_STEPS.ENROLL_AT_GYM;
  const needsScan = step === "ENROLL_AT_GYM";

  return (
    <DashboardSection title={info.title} className="dashboard-staff-card">
      <p className="dashboard-qr-note">{info.body}</p>
      {membership?.gymName && step !== "ENROLL_AT_GYM" && (
        <p className="dashboard-qr-note">
          <strong>Gym:</strong> {membership.gymName}
        </p>
      )}
      {needsScan && (
        <button type="button" className="auth-submit dashboard-scan-button" onClick={onOpenScanner}>
          Scan Gym QR Code
        </button>
      )}
      <DashboardStatus message={statusMessage} />
    </DashboardSection>
  );
}

export default MemberOnboardingCard;
