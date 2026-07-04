import axiosInstance from "./axiosInstance";

export const fetchDashboardStats = async () => {
  const response = await axiosInstance.get("/dashboard/stats");
  return response.data;
};
