export function saveSession(data) {
  localStorage.setItem("token", data.token);
  localStorage.setItem("userId", data.userId);
  localStorage.setItem("firstName", data.firstName);
  localStorage.setItem("lastName", data.lastName);
  localStorage.setItem("role", data.role);
}

export function clearSession() {
  localStorage.removeItem("token");
  localStorage.removeItem("userId");
  localStorage.removeItem("firstName");
  localStorage.removeItem("lastName");
  localStorage.removeItem("role");
}

export function readSession() {
  return {
    firstName: localStorage.getItem("firstName") || "User",
    lastName: localStorage.getItem("lastName") || "",
    role: localStorage.getItem("role") || "Member",
  };
}
