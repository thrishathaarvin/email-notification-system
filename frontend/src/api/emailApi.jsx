import axios from "axios";

const BASE_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api";

export const getEmails = async (filters = {}) => {
  const params = {};
  if (filters.status) params.status = filters.status;
  if (filters.recipient) params.recipient = filters.recipient;

  const response = await axios.get(`${BASE_URL}/emails`, { params });
  return response.data;
};

export const deleteEmail = async (id) => {
  await axios.delete(`${BASE_URL}/emails/${id}`);
};


export const sendEmail = async (emailData) => {
  try {
    const response = await axios.post("http://localhost:8080/api/emails/send", emailData);
    return response.data;
  } catch (error) {
    console.error("Error sending email:", error);
    throw error;
  }
};

export const getEmailById = async (id) => {
  const response = await axios.get(`${BASE_URL}/emails/${id}`);
  return response.data;
};

