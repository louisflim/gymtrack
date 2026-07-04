import DashboardSection from "./DashboardSection";

function MemberQrCard({ qrImage, loading, note }) {
  return (
    <DashboardSection title="My Member QR" className="dashboard-qr-card">
      {qrImage ? (
        <img src={qrImage} alt="My GymTrack QR" className="dashboard-qr-image" />
      ) : (
        <p className="dashboard-qr-note">
          {loading ? "Generating your QR code..." : "Unable to load your QR code."}
        </p>
      )}
      <p className="dashboard-qr-note">
        {note || "Present this QR code to staff for attendance check-in/check-out."}
      </p>
    </DashboardSection>
  );
}

export default MemberQrCard;
