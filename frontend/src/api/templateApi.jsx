import axios from "axios";

const BASE_URL = "http://localhost:8080/api";

export const getTemplates = async () => {
  const response = await axios.get(`${BASE_URL}/templates`);
  return response.data;
};

export const createTemplate = async (payload) => {
  const response = await axios.post(`${BASE_URL}/templates`, payload);
  return response.data;
};

export async function deleteTemplate(id) {
  const response = await axios.delete(`${BASE_URL}/templates/${id}`);
  return response.data;
};
