import AccountFields from "../../components/auth/AccountFields";
import FormCard from "../../components/auth/FormCard";

function RegisterForm({ error, loading, values, onChange, onSubmit, footer }) {
  return (
    <FormCard
      eyebrow="GymTrack"
      heading="Create Account"
      subheading="Fill in your details to get started."
    >
      {error && <div className="auth-error">{error}</div>}

      <form onSubmit={onSubmit}>
        <AccountFields values={values} onChange={onChange} />
        <div className="input-group">
          <label>Register As</label>
          <select value={values.role} onChange={(e) => onChange("role", e.target.value)}>
            <option value="member">Member</option>
            <option value="owner">Gym Owner</option>
          </select>
        </div>
        {values.role === "owner" && (
          <div className="input-group">
            <label>Gym Name</label>
            <input
              value={values.gymName}
              onChange={(e) => onChange("gymName", e.target.value)}
              placeholder="e.g. Chuchu Gym"
              required
            />
          </div>
        )}
        <button type="submit" className="auth-submit" disabled={loading}>
          {loading ? "Creating Account..." : "Create Account"}
        </button>
      </form>

      {footer}
    </FormCard>
  );
}

export default RegisterForm;
