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
  async getActiveCattleWithWeight() {
    return apiClient.get('/boi/ativos-com-peso');
  },
  async fetchCattleWeightHistory(params){
    return apiClient.get('/boi/bois-pesagens-por-data', {params});
  },
  async getTransactionsList(){
    return apiClient.get('/transacao/listTransacoes')
  },
  async createCompra(compraRequest) {
    return apiClient.post('/transacao/cadastraCompra', compraRequest)
  },
  async createVenda(vendaRequest) {
    return apiClient.post('/transacao/cadastraVenda', vendaRequest)
  }
};
