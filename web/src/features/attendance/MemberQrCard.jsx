import DashboardSection from "../../components/dashboard/DashboardSection";

function MemberQrCard({ qrImage, loading, note }) {
  return (
    <DashboardSection title="My Member QR" className="dashboard-qr-card">
      {qrImage ? (
        <img src={qrImage} alt="My GymTrack QR" className="dashboard-qr-image" />
      ) : (
        <p className="dashboard-qr-note">
          {loading ? "Generating your QR code..." : "We couldn't load your QR code. Please try again."}
        </p>
      )}
      <p className="dashboard-qr-note">
        {note || "Present this QR code to staff for attendance check-in/check-out."}
      </p>
    </DashboardSection>
  );
}

export default MemberQrCard;
