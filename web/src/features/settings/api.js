import axiosInstance from "../../api/axiosInstance";

export const deleteAccount = async (password) => {
  const response = await axiosInstance.delete("/auth/account", {
    data: { password },
  });
  return response.data;
};
