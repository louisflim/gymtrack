import axiosInstance from "../../api/axiosInstance";

export const fetchMyQrCode = async () => {
  const response = await axiosInstance.get("/qr/me");
  const { qrData, qrImageBase64 } = response.data;
  return {
    qrData,
    qrImage: qrImageBase64 ? `data:image/png;base64,${qrImageBase64}` : "",
  };
};

export const fetchGymQrCode = async () => {
  const response = await axiosInstance.get("/qr/gym");
  const { qrData, qrImageBase64 } = response.data;
  return {
    qrData,
    qrImage: qrImageBase64 ? `data:image/png;base64,${qrImageBase64}` : "",
  };
};

export const scanGymQr = async (qrData) => {
  const response = await axiosInstance.post("/attendance/scan-gym", { qrData });
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

export const fetchGymAttendanceLogs = async (search = "", date = "") => {
  const response = await axiosInstance.get("/attendance/gym", {
    params: { search: search || undefined, date: date || undefined },
  });
  return response.data;
};
