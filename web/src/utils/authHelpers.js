export function getPasswordMismatchError(password, confirmPassword) {
  if (password !== confirmPassword) {
    return "Passwords do not match.";
  }
  return null;
}

export function navigateAfterLogin(navigate, data) {
  if (data.mustChangePassword) {
    navigate("/change-password", { replace: true });
  } else {
    navigate("/dashboard");
  }
}
