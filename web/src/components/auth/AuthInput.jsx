function AuthInput({ label, type = "text", value, onChange, placeholder, required = false, minLength }) {
  return (
    <div className="input-group">
      <label>{label}</label>
      <input
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        required={required}
        minLength={minLength}
      />
    </div>
  );
}

export default AuthInput;
