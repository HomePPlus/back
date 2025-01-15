import axios from 'axios';

const api = {
  // 회원가입 관련 API
  registerResident: (data) => {
    return axios.post('/api/users/resident/join', data);
  },
  
  registerInspector: (data) => {
    return axios.post('/api/users/inspector/join', data);
  },

  // 이메일 관련 API
  checkEmail: (email) => {
    return axios.get(`/api/users/check-email?email=${email}`);
  },

  sendVerificationCode: (email) => {
    return axios.post('/api/users/send-verification', { email: email });
  },
  

  verifyEmail: (token) => {
    return axios.get(`/api/users/verify?token=${token}`);
  },

  // 프로필 조회
  getProfile: () => {
    return axios.get('/api/users/profile');
  }
};
export default api;
