import axiosInstance from "../../../api/axiosInstance";

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
