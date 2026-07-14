import axiosInstance from "../../../api/axiosInstance";

export const changePassword = async (currentPassword, newPassword, confirmPassword) => {
  const response = await axiosInstance.post("/auth/change-password", {
    currentPassword,
    newPassword,
    confirmPassword,
  });
  return response.data;
};
