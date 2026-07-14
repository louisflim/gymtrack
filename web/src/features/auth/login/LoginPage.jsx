import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import BrandPanel from "../../../components/auth/BrandPanel";
import SplitAuthLayout from "../../../components/layout/SplitAuthLayout";
import { saveSession } from "../../../utils/session";
import { getApiError } from "../../../utils/apiError";
import { navigateAfterLogin } from "../../../utils/authHelpers";
import { loginUser } from "./api";
import LoginForm from "./LoginForm";

function LoginPage() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const data = await loginUser(email, password);
      saveSession(data);
      navigateAfterLogin(navigate, data);
    } catch (err) {
      if (err.response?.status === 401) {
        setError("Invalid email or password.");
      } else {
        setError(getApiError(err, "Something went wrong. Please try again."));
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <SplitAuthLayout
      brand={
        <BrandPanel
          titleLines={["TRAIN", "TRACK"]}
          highlight="TRANSFORM"
          tagline="One system for memberships, attendance, and payments. Built for gyms that run on discipline, not paperwork."
          stats={[
            { value: "01", label: "Sign In" },
            { value: "02", label: "Scan QR" },
            { value: "03", label: "Train" },
          ]}
        />
      }
    >
      <LoginForm
        error={error}
        loading={loading}
        email={email}
        password={password}
        onEmailChange={(e) => setEmail(e.target.value)}
        onPasswordChange={(e) => setPassword(e.target.value)}
        onSubmit={handleSubmit}
        footer={
          <div className="auth-footer">
            Don't have an account? <Link to="/register">Register</Link>
          </div>
        }
      />
    </SplitAuthLayout>
  );
}

export default LoginPage;
