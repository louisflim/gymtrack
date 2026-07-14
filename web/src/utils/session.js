export function saveSession(data) {
  localStorage.setItem("token", data.token);
  localStorage.setItem("userId", data.userId);
  localStorage.setItem("firstName", data.firstName);
  localStorage.setItem("lastName", data.lastName);
  localStorage.setItem("role", data.role);
  localStorage.setItem("mustChangePassword", data.mustChangePassword ? "true" : "false");
  if (data.gymId != null) localStorage.setItem("gymId", data.gymId);
  else localStorage.removeItem("gymId");
  if (data.gymName) localStorage.setItem("gymName", data.gymName);
  else localStorage.removeItem("gymName");
}

export function clearSession() {
  localStorage.removeItem("token");
  localStorage.removeItem("userId");
  localStorage.removeItem("firstName");
  localStorage.removeItem("lastName");
  localStorage.removeItem("role");
  localStorage.removeItem("gymId");
  localStorage.removeItem("gymName");
  localStorage.removeItem("mustChangePassword");
}

export function readSession() {
  return {
    firstName: localStorage.getItem("firstName") || "User",
    lastName: localStorage.getItem("lastName") || "",
    role: localStorage.getItem("role") || "Member",
    gymName: localStorage.getItem("gymName") || "",
    mustChangePassword: localStorage.getItem("mustChangePassword") === "true",
  };
}

export function setMustChangePassword(value) {
  localStorage.setItem("mustChangePassword", value ? "true" : "false");
}
