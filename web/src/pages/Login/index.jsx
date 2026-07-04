import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { loginUser } from "../../api/auth";
import BrandPanel from "../../components/auth/BrandPanel";
import SplitAuthLayout from "../../components/layout/SplitAuthLayout";
import { saveSession } from "../../utils/session";
import LoginForm from "./LoginForm";

function Login() {
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
      navigate("/dashboard");
    } catch (err) {
      if (err.response?.status === 401) {
        setError("Invalid email or password.");
      } else if (err.response?.status === 403) {
        setError(err.response.data || "This account has been deactivated.");
      } else if (err.code === "ERR_NETWORK") {
        setError("Cannot reach the server. Make sure the backend is running on port 8080.");
      } else {
        setError("Something went wrong. Try again.");
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

export default Login;
