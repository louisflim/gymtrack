import AuthInput from "../../components/auth/AuthInput";
import FormCard from "../../components/auth/FormCard";

function LoginForm({ error, loading, email, password, onEmailChange, onPasswordChange, onSubmit, footer }) {
  return (
    <FormCard
      eyebrow="GymTrack"
      heading="Sign In"
      subheading="Enter your credentials to access your account."
    >
      {error && <div className="auth-error">{error}</div>}

      <form onSubmit={onSubmit}>
        <AuthInput label="Email" type="email" value={email} onChange={onEmailChange} placeholder="you@example.com" required />
        <AuthInput label="Password" type="password" value={password} onChange={onPasswordChange} placeholder="Enter your password" required />
        <button type="submit" className="auth-submit" disabled={loading}>
          {loading ? "Signing In..." : "Sign In"}
        </button>
      </form>

      {footer}
    </FormCard>
  );
}

export default LoginForm;
