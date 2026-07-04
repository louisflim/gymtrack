import { useCallback, useEffect, useState } from "react";
import { fetchMembers, assignPlanToMember, updateMember } from "../../api/members";
import { fetchAllPayments } from "../../api/payments";
import { createPlan, fetchAllPlans, updatePlan } from "../../api/plans";
import { fetchDashboardStats } from "../../api/dashboard";
import { fetchStaff, updateStaff } from "../../api/staff";
import { fetchGymAttendanceLogs } from "../../api/qr";
import { formatCurrency } from "../../utils/formatters";
import MemberTable from "./MemberTable";
import PaymentTable from "./PaymentTable";
import PlanForm from "./PlanForm";
import PlanList from "./PlanList";
import StaffTable from "./StaffTable";
import AttendanceLogTable from "./AttendanceLogTable";
import CreateStaffForm from "../dashboard/CreateStaffForm";
import KpiSummaryGrid from "../common/KpiSummaryGrid";

const EMPTY_PLAN = { name: "", durationDays: 30, price: "999", active: true };

function AdminDashboardPanel({
  staffForm,
  staffStatus,
  creatingStaff,
  adminGymName,
  onStaffChange,
  onCreateStaff,
  staffRefreshKey = 0,
  activeTab = "home",
  onTabChange,
}) {
  const tab = activeTab;
  const setTab = onTabChange || (() => {});
  const [plans, setPlans] = useState([]);
  const [members, setMembers] = useState([]);
  const [payments, setPayments] = useState([]);
  const [staff, setStaff] = useState([]);
  const [stats, setStats] = useState(null);
  const [attendanceLogs, setAttendanceLogs] = useState([]);
  const [attendanceSearch, setAttendanceSearch] = useState("");
  const [attendanceDate, setAttendanceDate] = useState("");
  const [planForm, setPlanForm] = useState(EMPTY_PLAN);
  const [editingPlanId, setEditingPlanId] = useState(null);
  const [planStatus, setPlanStatus] = useState("");
  const [savingPlan, setSavingPlan] = useState(false);
  const [loadError, setLoadError] = useState("");

  const loadData = useCallback(async () => {
    try {
      const [planData, memberData, paymentData, staffData, statsData] = await Promise.all([
        fetchAllPlans(),
        fetchMembers("", "ALL"),
        fetchAllPayments(),
        fetchStaff(),
        fetchDashboardStats(),
      ]);
      setPlans(planData);
      setMembers(Array.isArray(memberData) ? memberData : []);
      setPayments(paymentData);
      setStaff(staffData);
      setStats(statsData);
      setLoadError("");
    } catch (err) {
      setLoadError(err.response?.data || "Failed to load admin data.");
    }
  }, []);

  const loadAttendance = useCallback(async () => {
    try {
      const logs = await fetchGymAttendanceLogs(attendanceSearch, attendanceDate);
      setAttendanceLogs(logs);
    } catch (err) {
      setLoadError(err.response?.data || "Failed to load attendance logs.");
    }
  }, [attendanceSearch, attendanceDate]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  useEffect(() => {
    if (tab === "members") {
      fetchMembers("", "ALL")
        .then((data) => setMembers(Array.isArray(data) ? data : []))
        .catch((err) => setLoadError(err.response?.data || "Failed to load members."));
    }
  }, [tab]);

  useEffect(() => {
    if (tab === "attendance") {
      loadAttendance();
    }
  }, [tab, loadAttendance]);

  useEffect(() => {
    if (staffRefreshKey > 0) {
      loadData();
    }
  }, [staffRefreshKey, loadData]);

  const kpiItems = stats
    ? [
        { label: "Total Members", value: stats.totalMembers },
        { label: "Active Subscriptions", value: stats.activeSubscriptions },
        { label: "Expired", value: stats.expiredMemberships },
        { label: "Today's Check-ins", value: stats.todayCheckIns },
        { label: "Total Collected", value: formatCurrency(stats.totalPaymentsCollected) },
      ]
    : [];

  const handlePlanChange = (field, value) => {
    setPlanForm((prev) => ({ ...prev, [field]: value }));
    setPlanStatus("");
  };

  const handlePlanSubmit = async (e) => {
    e.preventDefault();
    setSavingPlan(true);
    setPlanStatus("");
    try {
      const payload = {
        name: planForm.name,
        durationDays: Number(planForm.durationDays),
        price: Number(planForm.price),
        active: planForm.active,
      };
      if (editingPlanId) {
        await updatePlan(editingPlanId, payload);
        setPlanStatus("Plan updated.");
      } else {
        await createPlan(payload);
        setPlanStatus("Plan created.");
      }
      setPlanForm(EMPTY_PLAN);
      setEditingPlanId(null);
      await loadData();
    } catch (err) {
      setPlanStatus(err.response?.data || "Failed to save plan.");
    } finally {
      setSavingPlan(false);
    }
  };

  const handleEditPlan = (plan) => {
    setEditingPlanId(plan.id);
    setPlanForm({
      name: plan.name,
      durationDays: plan.durationDays,
      price: String(plan.price),
      active: plan.active,
    });
    setTab("plans");
  };

  const handleMemberUpdate = async (id, payload) => {
    await updateMember(id, payload);
    await loadData();
  };

  const handleAssignPlan = async (memberId, planId) => {
    await assignPlanToMember(memberId, planId);
    await loadData();
  };

  const handleStaffUpdate = async (id, payload) => {
    await updateStaff(id, payload);
    await loadData();
  };

  return (
    <>
      {loadError && <p className="dashboard-scan-status">{loadError}</p>}

      {tab === "home" && (
        <>
          <KpiSummaryGrid items={kpiItems} />
          <CreateStaffForm
            values={staffForm}
            onChange={onStaffChange}
            onSubmit={onCreateStaff}
            loading={creatingStaff}
            statusMessage={staffStatus}
            adminGymName={adminGymName}
          />
        </>
      )}

      {tab === "plans" && (
        <>
          <PlanList plans={plans} onEdit={handleEditPlan} />
          <PlanForm
            values={planForm}
            onChange={handlePlanChange}
            onSubmit={handlePlanSubmit}
            submitLabel={editingPlanId ? "Update Plan" : "Create Plan"}
            loading={savingPlan}
          />
          {planStatus && <p className="dashboard-scan-status">{planStatus}</p>}
        </>
      )}

      {tab === "members" && (
        <MemberTable
          members={members}
          plans={plans}
          onUpdate={handleMemberUpdate}
          onAssignPlan={handleAssignPlan}
        />
      )}

      {tab === "staff" && (
        <StaffTable staff={staff} onUpdate={handleStaffUpdate} />
      )}

      {tab === "attendance" && (
        <AttendanceLogTable
          logs={attendanceLogs}
          search={attendanceSearch}
          onSearchChange={setAttendanceSearch}
          date={attendanceDate}
          onDateChange={setAttendanceDate}
        />
      )}

      {tab === "payments" && <PaymentTable payments={payments} />}
    </>
  );
}

export default AdminDashboardPanel;
