// src/services/api.js
import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://192.168.1.102:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

export default {
  async getCattle() {
    return apiClient.get('/boi/listBois');
  },
  async fetchCattleWeightHistory(params){
    return apiClient.get('/boi/bois-pesagens-por-data', {params});
  }
};
