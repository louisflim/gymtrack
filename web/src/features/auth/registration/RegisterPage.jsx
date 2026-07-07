import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import BrandPanel from "../../../components/auth/BrandPanel";
import SplitAuthLayout from "../../../components/layout/SplitAuthLayout";
import { saveSession } from "../../../utils/session";
import { getApiError } from "../../../utils/apiError";
import { registerUser } from "./api";
import RegisterForm from "./RegisterForm";

const EMPTY_FORM = {
  firstName: "",
  lastName: "",
  email: "",
  password: "",
  confirmPassword: "",
  role: "member",
  gymName: "",
};

function RegisterPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState(EMPTY_FORM);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (field, value) => setForm((prev) => ({ ...prev, [field]: value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");

    if (form.password !== form.confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    if (form.role === "owner" && !form.gymName.trim()) {
      setError("Gym name is required when registering as gym owner.");
      return;
    }

    setLoading(true);
    const backendRole = form.role === "owner" ? "ADMIN" : "MEMBER";

    try {
      const data = await registerUser(
        form.firstName,
        form.lastName,
        form.email,
        form.password,
        backendRole,
        form.role === "owner" ? form.gymName.trim() : undefined
      );
      saveSession(data);
      navigate("/dashboard");
    } catch (err) {
      setError(getApiError(err, "Registration failed. Check your details."));
    } finally {
      setLoading(false);
    }
  };

  return (
    <SplitAuthLayout
      brand={
        <BrandPanel
          eyebrow="New Member"
          titleLines={["JOIN", "THE"]}
          highlight="FLOOR"
          tagline="Create your account to manage your membership, track attendance, and pay online - all in one place."
          stats={[
            { value: "01", label: "Register" },
            { value: "02", label: "Choose Plan" },
            { value: "03", label: "Get QR Code" },
          ]}
        />
      }
    >
      <RegisterForm
        error={error}
        loading={loading}
        values={form}
        onChange={handleChange}
        onSubmit={handleSubmit}
        footer={
          <div className="auth-footer">
            Already have an account? <Link to="/">Sign In</Link>
          </div>
        }
      />
    </SplitAuthLayout>
  );
}

export default RegisterPage;
