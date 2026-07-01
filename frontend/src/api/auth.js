import axiosInstance from "./axiosInstance";

export const loginUser = async (email, password) => {
  const response = await axiosInstance.post("/auth/login", {
    email,
    password,
  });
  return response.data;
};

export const registerUser = async (firstName, lastName, email, password, role) => {
  const response = await axiosInstance.post("/auth/register", {
    firstName,
    lastName,
    email,
    password,
    role,
  });
  return response.data;
};