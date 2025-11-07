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
  },
  async createWeighingBatch(weighingRequestList) {
    return apiClient.post('/pesagem/cadastraLotePesagem', weighingRequestList)
  },
  async fetchCattlePerformance(params) {
    return apiClient.get('/boi/performance-summary', {params})
  },
  async fetchCattleWeightHistoryById(boiId) {
    return apiClient.get(`/boi//${boiId}/weight-history`);
  },
  async fetchRacas(){
    return apiClient.get('raca/listRacas');
  },
  async getTransacaoById(transacaoId) {
    return apiClient.get(`/transacao/${transacaoId}/detalhes`)
  },
  async updateTransaction(transacaoId, transacaoRequest) {
    return apiClient.put(`transacao/editaTransacao/${transacaoId}`, transacaoRequest)
  },
  async fetchGroupedWeighings() {
    return apiClient.get(`pesagem/agrupadas`)
  },
  async fetchIndividualWeighingsByDate(date) {
    return apiClient.get(`pesagem/agrupadas/${date}`)
  },
  async updateGroupedWeighingDate(oldDate, newDate){
    return apiClient.put(`pesagem/agrupadas/${oldDate}`, {novaData: newDate})
  },
  async updateIndividualWeighing(pesagemId, payload){
    return apiClient.put(`pesagem/editaPesagem/${pesagemId}`, payload)
  },
  async deleteGroupedWeighing(data){
    return apiClient.delete(`pesagem/agrupadas/${data}`)
  },
  async generateWeightMatrix(request) {
        return apiClient.post('pesagem/matriz', request);
    },
  
};
