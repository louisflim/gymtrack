import axiosInstance from "./axiosInstance";

export const fetchMyMembership = async () => {
  const response = await axiosInstance.get("/membership/me");
  return response.data;
};
