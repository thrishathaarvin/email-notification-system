import axios from "axios";

const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

export const getDashboardData = async () => {
  const response = await axios.get(`${BASE_URL}/reports/dashboard`);
  return response.data;
};
