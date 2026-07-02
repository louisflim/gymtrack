import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { createStaffUser } from "../../api/auth";
import { fetchMyQrCode, scanAttendance } from "../../api/qr";
import AttendanceScannerCard from "../../components/dashboard/AttendanceScannerCard";
import CreateStaffForm from "../../components/dashboard/CreateStaffForm";
import DashboardHero from "../../components/dashboard/DashboardHero";
import DashboardSummaryGrid from "../../components/dashboard/DashboardSummaryGrid";
import MemberQrCard from "../../components/dashboard/MemberQrCard";
import QrScanner from "../../components/qr/QrScanner";
import { clearSession, readSession } from "../../utils/session";

const EMPTY_STAFF = { firstName: "", lastName: "", email: "", password: "", confirmPassword: "" };

function Dashboard() {
  const navigate = useNavigate();
  const session = readSession();
  const [qrImage, setQrImage] = useState("");
  const [loadingQr, setLoadingQr] = useState(false);
  const [scannerOpen, setScannerOpen] = useState(false);
  const [scanStatus, setScanStatus] = useState("");
  const [staffForm, setStaffForm] = useState(EMPTY_STAFF);
  const [staffStatus, setStaffStatus] = useState("");
  const [creatingStaff, setCreatingStaff] = useState(false);

  const isMember = session?.role === "MEMBER";
  const canScan = session?.role === "ADMIN" || session?.role === "STAFF";
  const isAdmin = session?.role === "ADMIN";

  useEffect(() => {
    if (!session) {
      navigate("/login", { replace: true });
      return;
    }
    if (!isMember) return;

    const loadQr = async () => {
      setLoadingQr(true);
      try {
        const data = await fetchMyQrCode();
        setQrImage(data.qrImage);
      } catch {
        setQrImage("");
      } finally {
        setLoadingQr(false);
      }
    };
    loadQr();
  }, [session, navigate, isMember]);

  const handleLogout = () => {
    clearSession();
    navigate("/login", { replace: true });
  };

  const handleScan = async (qrPayload) => {
    setScanStatus("Processing scan...");
    try {
      const result = await scanAttendance(qrPayload);
      setScanStatus(`${result.memberName} — ${result.action} at ${new Date(result.timestamp).toLocaleString()}`);
    } catch (err) {
      setScanStatus(err.response?.data || "Scan failed. Try again.");
    }
  };

  const handleStaffChange = (field, value) => {
    setStaffForm((prev) => ({ ...prev, [field]: value }));
    setStaffStatus("");
  };

  const handleCreateStaff = async (e) => {
    e.preventDefault();
    setStaffStatus("");

    if (staffForm.password !== staffForm.confirmPassword) {
      setStaffStatus("Passwords do not match.");
      return;
    }

    setCreatingStaff(true);
    try {
      const created = await createStaffUser(
        staffForm.firstName,
        staffForm.lastName,
        staffForm.email,
        staffForm.password
      );
      setStaffStatus(`Staff account created for ${created.email}.`);
      setStaffForm(EMPTY_STAFF);
    } catch (err) {
      setStaffStatus(err.response?.data || "Failed to create staff account.");
    } finally {
      setCreatingStaff(false);
    }
  };

  if (!session) return null;

  return (
    <div className="dashboard-page">
      <DashboardHero />

      <div className="dashboard-panel">
        <header className="dashboard-topbar">
          <div>
            <p className="dashboard-summary-label">Overview</p>
            <h2 className="dashboard-greeting">Welcome back, {session.firstName}</h2>
            <p className="dashboard-subtitle">Your membership and attendance at a glance.</p>
          </div>
          <button type="button" className="dashboard-logout" onClick={handleLogout}>
            Sign Out
          </button>
        </header>

        <main className="dashboard-content">
          <section className="dashboard-overview-card">
            <DashboardSummaryGrid
              firstName={session.firstName}
              lastName={session.lastName}
              role={session.role}
            />
          </section>

          {isMember && <MemberQrCard qrImage={qrImage} loading={loadingQr} />}
          {canScan && (
            <AttendanceScannerCard
              onOpenScanner={() => setScannerOpen(true)}
              statusMessage={scanStatus}
            />
          )}
          {isAdmin && (
            <CreateStaffForm
              values={staffForm}
              onChange={handleStaffChange}
              onSubmit={handleCreateStaff}
              loading={creatingStaff}
              statusMessage={staffStatus}
            />
          )}
        </main>
      </div>

      {scannerOpen && (
        <QrScanner
          open={scannerOpen}
          onScanSuccess={(text, errorMessage) => {
            setScannerOpen(false);
            if (errorMessage) {
              setScanStatus(errorMessage);
              return;
            }
            if (text) {
              handleScan(text);
            }
          }}
          onClose={() => setScannerOpen(false)}
          statusMessage={scanStatus}
        />
      )}
    </div>
  );
}

export default Dashboard;
