import axiosInstance from "../../api/axiosInstance";

export const fetchDashboardStats = async () => {
  const response = await axiosInstance.get("/dashboard/stats");
  return response.data;
};
