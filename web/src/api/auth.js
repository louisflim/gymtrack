import axiosInstance from "./axiosInstance";

export const loginUser = async (email, password) => {
  const response = await axiosInstance.post("/auth/login", {
    email,
    password,
  });
  return response.data;
};

export const registerUser = async (firstName, lastName, email, password, role, gymName) => {
  const payload = {
    firstName,
    lastName,
    email,
    password,
    role,
  };
  if (gymName) {
    payload.gymName = gymName;
  }
  const response = await axiosInstance.post("/auth/register", payload);
  return response.data;
};

export const createStaffUser = async (firstName, lastName, email, password) => {
  const response = await axiosInstance.post("/auth/staff", {
    firstName,
    lastName,
    email,
    password,
    role: "STAFF",
  });
  return response.data;
};