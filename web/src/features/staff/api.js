import axiosInstance from "../../api/axiosInstance";

export const fetchStaff = async () => {
  const response = await axiosInstance.get("/staff");
  return response.data;
};

export const updateStaff = async (id, payload) => {
  const response = await axiosInstance.put(`/staff/${id}`, payload);
  return response.data;
};

export const deleteStaff = async (id) => {
  await axiosInstance.delete(`/staff/${id}`);
};
