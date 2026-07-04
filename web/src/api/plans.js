import axiosInstance from "./axiosInstance";

export const fetchActivePlans = async () => {
  const response = await axiosInstance.get("/plans/active");
  return response.data;
};

export const fetchAllPlans = async () => {
  const response = await axiosInstance.get("/plans");
  return response.data;
};

export const createPlan = async (payload) => {
  const response = await axiosInstance.post("/plans", payload);
  return response.data;
};

export const updatePlan = async (id, payload) => {
  const response = await axiosInstance.put(`/plans/${id}`, payload);
  return response.data;
};
