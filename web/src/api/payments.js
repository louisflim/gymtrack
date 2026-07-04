import axiosInstance from "./axiosInstance";

export const createCheckout = async (planId) => {
  const response = await axiosInstance.post("/payments/checkout", { planId });
  return response.data;
};

export const fetchMyPayments = async () => {
  const response = await axiosInstance.get("/payments/me");
  return response.data;
};

export const fetchAllPayments = async () => {
  const response = await axiosInstance.get("/payments");
  return response.data;
};

export const confirmMockPayment = async (reference) => {
  const response = await axiosInstance.post("/payments/confirm-mock", null, { params: { reference } });
  return response.data;
};
