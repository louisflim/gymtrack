export function getApiError(err, fallback = "Something went wrong. Please try again.") {
  const data = err?.response?.data;
  if (typeof data === "string" && data.trim()) {
    return data.trim();
  }
  if (data && typeof data === "object" && data.message) {
    return data.message;
  }
  if (err?.code === "ERR_NETWORK" || !err?.response) {
    return "We couldn't connect right now. Check your internet connection and try again.";
  }
  if (err?.response?.status === 401) {
    return "Please sign in again to continue.";
  }
  if (err?.response?.status === 403) {
    return "You don't have permission to do that. Try signing out and signing back in.";
  }
  if (err?.response?.status === 404) {
    return "We couldn't find what you were looking for.";
  }
  if (err?.response?.status >= 500) {
    return "Something went wrong on our side. Please try again in a moment.";
  }
  return fallback;
}
