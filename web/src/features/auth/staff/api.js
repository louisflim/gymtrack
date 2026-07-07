import axiosInstance from "../../../api/axiosInstance";

export const createStaffUser = async (firstName, lastName, email, password) => {
  const response = await axiosInstance.post("/auth/staff", {
    firstName,
    lastName,
    email,
    password,
  });
  return response.data;
};
