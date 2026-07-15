import axiosInstance from "../../api/axiosInstance";

export const createCheckout = async (planId) => {
  const origin = window.location.origin;
  const response = await axiosInstance.post("/payments/checkout", {
    planId,
    successUrl: `${origin}/payment/success`,
    cancelUrl: `${origin}/payment/cancel`,
  });
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

export const fetchPaymentStatus = async (reference) => {
  const response = await axiosInstance.get("/payments/status", { params: { reference } });
  return response.data;
};

export const confirmMockPayment = async (reference) => {
  const response = await axiosInstance.post("/payments/confirm-mock", null, { params: { reference } });
  return response.data;
};
