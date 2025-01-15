import axios from 'axios';

const API_BASE_URL = '/api/users';

export const api = {
  registerResident: (data) => axios.post(`${API_BASE_URL}/resident/join`, data),
  registerInspector: (data) => axios.post(`${API_BASE_URL}/inspector/join`, data),
  getProfile: () => axios.get(`${API_BASE_URL}/profile`),
  verifyEmail: (token) => axios.get(`${API_BASE_URL}/verify?token=${token}`),
  checkEmail: (email) => axios.get(`${API_BASE_URL}/check-email?email=${email}`),
  sendVerificationCode: (email) => axios.post(`${API_BASE_URL}/send-verification`, { email }),
};
