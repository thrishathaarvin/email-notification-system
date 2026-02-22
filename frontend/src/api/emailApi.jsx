import axios from "axios";

const BASE_URL = "http://localhost:8080/api";

// this will fetch mails from backend, filtering is optional
export const getEmails = async (filters = {}) => {
  const params = {};
  
  if (filters.status) {
    params.status = filters.status;
  }

  const response = await axios.get(`${BASE_URL}/emails`, { params });
  return response.data;
};

//deletes mail
export const deleteEmail = async (id) => {
  await axios.delete(`${BASE_URL}/emails/${id}`);
};


//sends mail
export const sendEmail = async (emailData) => {
  try {
    const response = await axios.post("http://localhost:8080/api/emails/send", emailData);
    return response.data;
  } catch (error) {
    console.error("Error sending email:", error);
    throw error;
  }
};

//to fetch a single mail, email details page
export const getEmailById = async (id) => {
  const response = await axios.get(`${BASE_URL}/emails/${id}`);
  return response.data;
};

