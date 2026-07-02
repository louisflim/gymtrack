function FormCard({ eyebrow, heading, subheading, children, className = "" }) {
  return (
    <div className={`auth-form-wrap ${className}`.trim()}>
      <div className="auth-form-eyebrow">{eyebrow}</div>
      <h2 className="auth-form-heading">{heading}</h2>
      <p className="auth-form-subheading">{subheading}</p>
      {children}
    </div>
  );
}

export default FormCard;
