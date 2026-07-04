import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "/api",
  headers: {
    "Content-Type": "application/json",
  },
});

axiosInstance.interceptors.request.use((config) => {
  const isPublicAuthRoute = /^\/auth\/(login|register)$/.test(config.url ?? "");
  const token = localStorage.getItem("token");
  if (token && !isPublicAuthRoute) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default axiosInstance;
