import DashboardSection from "./DashboardSection";

function GymQrCard({ qrImage, loading }) {
  return (
    <DashboardSection title="Gym Enrollment QR" className="dashboard-qr-card">
      {qrImage ? (
        <img src={qrImage} alt="Gym enrollment QR" className="dashboard-qr-image" />
      ) : (
        <p className="dashboard-qr-note">
          {loading ? "Loading gym QR code..." : "Unable to load gym QR code."}
        </p>
      )}
      <p className="dashboard-qr-note">
        New members scan this code on their phone to enroll at your gym.
      </p>
    </DashboardSection>
  );
}

export default GymQrCard;
