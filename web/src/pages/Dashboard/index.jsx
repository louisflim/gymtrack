import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { createStaffUser } from "../../features/auth/staff";
import { fetchMyMembership, MemberOnboardingCard } from "../../features/membership";
import { fetchGymQrCode, fetchMyQrCode, scanAttendance, scanGymQr } from "../../features/attendance";
import AdminDashboardPanel from "../../components/admin/AdminDashboardPanel";
import {
  AttendanceScannerCard,
  GymQrCard,
  MemberQrCard,
  QrScanner,
} from "../../features/attendance";
import DashboardHero from "../../components/dashboard/DashboardHero";
import DashboardNavBar from "../../components/dashboard/DashboardNavBar";
import DashboardSummaryGrid from "../../components/dashboard/DashboardSummaryGrid";
import MemberDashboardPanel from "../../components/member/MemberDashboardPanel";
import { MEMBER_QR_NOTES, STAFF_HOME_HINT } from "../../constants/dashboardUi";
import { clearSession, readSession } from "../../utils/session";
import { getApiError } from "../../utils/apiError";

const EMPTY_STAFF = { firstName: "", lastName: "", email: "", password: "", confirmPassword: "" };

function Dashboard() {
  const navigate = useNavigate();
  const session = readSession();
  const [qrImage, setQrImage] = useState("");
  const [loadingQr, setLoadingQr] = useState(false);
  const [gymQrImage, setGymQrImage] = useState("");
  const [loadingGymQr, setLoadingGymQr] = useState(false);
  const [membership, setMembership] = useState(null);
  const [scannerOpen, setScannerOpen] = useState(false);
  const [scannerMode, setScannerMode] = useState("member");
  const [scanStatus, setScanStatus] = useState("");
  const [memberGymScanStatus, setMemberGymScanStatus] = useState("");
  const [staffForm, setStaffForm] = useState(EMPTY_STAFF);
  const [staffStatus, setStaffStatus] = useState("");
  const [creatingStaff, setCreatingStaff] = useState(false);
  const [staffRefreshKey, setStaffRefreshKey] = useState(0);
  const [activeTab, setActiveTab] = useState("home");

  const isMember = session?.role === "MEMBER";
  const canScan = session?.role === "ADMIN" || session?.role === "STAFF";
  const isAdmin = session?.role === "ADMIN";
  const isStaff = session?.role === "STAFF";
  const showMemberQr =
    membership?.nextStep === "FIRST_CHECK_IN" || membership?.nextStep === "ACTIVE";

  const loadMemberData = useCallback(async () => {
    setLoadingQr(true);
    try {
      const membershipData = await fetchMyMembership();
      setMembership(membershipData);
      const canShowQr =
        membershipData.nextStep === "FIRST_CHECK_IN" || membershipData.nextStep === "ACTIVE";
      if (canShowQr) {
        const qrData = await fetchMyQrCode();
        setQrImage(qrData.qrImage);
      } else {
        setQrImage("");
      }
    } catch {
      setQrImage("");
    } finally {
      setLoadingQr(false);
    }
  }, []);

  const loadGymQr = useCallback(async () => {
    setLoadingGymQr(true);
    try {
      const data = await fetchGymQrCode();
      setGymQrImage(data.qrImage);
    } catch {
      setGymQrImage("");
    } finally {
      setLoadingGymQr(false);
    }
  }, []);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/", { replace: true });
      return;
    }
    if (isMember) loadMemberData();
    if (canScan) loadGymQr();
  }, [navigate, isMember, canScan, loadMemberData, loadGymQr]);

  const handleLogout = () => {
    clearSession();
    navigate("/", { replace: true });
  };

  const handleStaffScan = async (qrPayload) => {
    setScanStatus("Processing scan...");
    try {
      const result = await scanAttendance(qrPayload);
      setScanStatus(
        result.message ||
          `${result.memberName} (${result.planName || "No plan"}) — ${result.membershipStatus} — ${result.action}`
      );
    } catch (err) {
      setScanStatus(err.response?.data || "Scan failed. Try again.");
    }
  };

  const handleMemberGymScan = async (qrPayload) => {
    setMemberGymScanStatus("Processing...");
    try {
      const result = await scanGymQr(qrPayload);
      setMemberGymScanStatus(result.message);
      await loadMemberData();
    } catch (err) {
      setMemberGymScanStatus(err.response?.data || "Scan failed. Try again.");
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
      setStaffRefreshKey((key) => key + 1);
    } catch (err) {
      setStaffStatus(getApiError(err, "Failed to create staff account."));
    } finally {
      setCreatingStaff(false);
    }
  };

  const openScanner = (mode) => {
    setScannerMode(mode);
    setScannerOpen(true);
  };

  return (
    <div className="dashboard-page">
      <DashboardHero />

      <DashboardNavBar role={session.role} activeTab={activeTab} onChange={setActiveTab} />

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
          {activeTab === "home" && (
            <section className="dashboard-overview-card">
              <DashboardSummaryGrid
                firstName={session.firstName}
                lastName={session.lastName}
                role={session.role}
                membershipStatus={membership?.status}
                planName={membership?.planName}
              />
            </section>
          )}

          {isMember && activeTab === "home" && (
            <MemberOnboardingCard
              membership={membership}
              onOpenScanner={() => openScanner("gym")}
              statusMessage={memberGymScanStatus}
            />
          )}

          {isMember && activeTab === "qr" && (
            showMemberQr ? (
              <MemberQrCard
                qrImage={qrImage}
                loading={loadingQr}
                note={
                  membership?.nextStep === "FIRST_CHECK_IN"
                    ? MEMBER_QR_NOTES.FIRST_CHECK_IN
                    : MEMBER_QR_NOTES.ACTIVE
                }
              />
            ) : (
              <p className="dashboard-qr-note">{MEMBER_QR_NOTES.LOCKED}</p>
            )
          )}

          {isMember && (activeTab === "plans" || activeTab === "activity") && (
            <MemberDashboardPanel
              membership={membership}
              onMembershipChange={setMembership}
              section={activeTab}
            />
          )}

          {canScan && activeTab === "qr" && (
            <GymQrCard qrImage={gymQrImage} loading={loadingGymQr} />
          )}

          {isStaff && activeTab === "scan" && (
            <AttendanceScannerCard
              onOpenScanner={() => openScanner("member")}
              statusMessage={scanStatus}
            />
          )}

          {isAdmin && activeTab === "qr" && (
            <AttendanceScannerCard
              onOpenScanner={() => openScanner("member")}
              statusMessage={scanStatus}
            />
          )}

          {isAdmin && activeTab !== "qr" && (
            <AdminDashboardPanel
              staffForm={staffForm}
              staffStatus={staffStatus}
              creatingStaff={creatingStaff}
              adminGymName={session.gymName}
              onStaffChange={handleStaffChange}
              onCreateStaff={handleCreateStaff}
              staffRefreshKey={staffRefreshKey}
              activeTab={activeTab}
              onTabChange={setActiveTab}
            />
          )}

          {isStaff && activeTab === "home" && (
            <p className="dashboard-qr-note">{STAFF_HOME_HINT}</p>
          )}

        </main>
      </div>

      {scannerOpen && (
        <QrScanner
          open={scannerOpen}
          onScanSuccess={(text, errorMessage) => {
            setScannerOpen(false);
            if (errorMessage) {
              if (scannerMode === "gym") setMemberGymScanStatus(errorMessage);
              else setScanStatus(errorMessage);
              return;
            }
            if (text) {
              if (scannerMode === "gym") handleMemberGymScan(text);
              else handleStaffScan(text);
            }
          }}
          onClose={() => setScannerOpen(false)}
          statusMessage={scannerMode === "gym" ? memberGymScanStatus : scanStatus}
        />
      )}
    </div>
  );
}

export default Dashboard;
