// src/services/api.js
import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://192.168.1.101:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

export default {
  getCattle() {
    return apiClient.get('/boi/listBois');
  },
};
