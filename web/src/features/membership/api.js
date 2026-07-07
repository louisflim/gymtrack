import axiosInstance from "../../api/axiosInstance";

export const fetchMyMembership = async () => {
  const response = await axiosInstance.get("/membership/me");
  return response.data;
};
