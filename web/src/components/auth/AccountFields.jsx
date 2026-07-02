import AuthInput from "./AuthInput";

function AccountFields({ values, onChange, passwordPlaceholder = "Create password" }) {
  const handle = (field) => (e) => onChange(field, e.target.value);

  return (
    <>
      <div className="auth-field-row">
        <AuthInput label="First Name" value={values.firstName} onChange={handle("firstName")} placeholder="First name" required />
        <AuthInput label="Last Name" value={values.lastName} onChange={handle("lastName")} placeholder="Last name" required />
      </div>
      <AuthInput label="Email" type="email" value={values.email} onChange={handle("email")} placeholder="you@example.com" required />
      <div className="auth-field-row">
        <AuthInput label="Password" type="password" value={values.password} onChange={handle("password")} placeholder={passwordPlaceholder} required minLength={8} />
        <AuthInput label="Confirm" type="password" value={values.confirmPassword} onChange={handle("confirmPassword")} placeholder="Repeat password" required />
      </div>
    </>
  );
}

export default AccountFields;
