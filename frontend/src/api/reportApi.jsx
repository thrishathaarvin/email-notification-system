import axios from "axios";

const BASE_URL = "http://localhost:8080/api";

export const getDashboardData = async () => {
  const response = await axios.get(`${BASE_URL}/reports/dashboard`);
  return response.data;
};

export const getSummaryData = async () => {
  const response = await axios.get(`${BASE_URL}/reports/summary`);
  return response.data;
};
