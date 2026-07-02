import "../../css/auth.css";

function SplitAuthLayout({ brand, panelClassName = "", cardClassName = "", children }) {
  return (
    <div className={`auth-shell ${panelClassName}`.trim()}>
      {brand}
      <div className={`auth-form-panel ${cardClassName}`.trim()}>{children}</div>
    </div>
  );
}

export default SplitAuthLayout;
