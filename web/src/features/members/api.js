import axiosInstance from "../../api/axiosInstance";

export const fetchMembers = async (search = "", status = "ALL") => {
  const response = await axiosInstance.get("/members", { params: { search, status } });
  return response.data;
};

export const updateMember = async (id, payload) => {
  const response = await axiosInstance.put(`/members/${id}`, payload);
  return response.data;
};

export const assignPlanToMember = async (memberId, planId) => {
  const response = await axiosInstance.post("/members/assign-plan", { memberId, planId });
  return response.data;
};
