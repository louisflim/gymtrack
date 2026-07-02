import axiosInstance from "./axiosInstance";

export const fetchMyQrCode = async () => {
  const response = await axiosInstance.get("/qr/me");
  return response.data;
};

export const scanAttendance = async (qrData) => {
  const response = await axiosInstance.post("/attendance/scan", { qrData });
  return response.data;
};

export const fetchMyAttendanceLogs = async () => {
  const response = await axiosInstance.get("/attendance/me");
  return response.data;
};
