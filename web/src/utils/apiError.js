export function getApiError(err, fallback = "Something went wrong. Try again.") {
  const data = err?.response?.data;
  if (typeof data === "string" && data.trim()) {
    return data.trim();
  }
  if (data && typeof data === "object" && data.message) {
    return data.message;
  }
  if (err?.code === "ERR_NETWORK") {
    return "Cannot reach the server. Make sure the backend is running on port 8080.";
  }
  if (err?.response?.status === 403) {
    return "You do not have permission to perform this action. Try signing out and back in.";
  }
  return fallback;
}
