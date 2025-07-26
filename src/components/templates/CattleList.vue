<template>
    <div>
      <h1>Lista de Gado</h1>
      <v-data-table :headers="headers" :items="cattle" />
    </div>
  </template>
  
  <script>
  import api from '../../services/api.js';
  
  export default {
    name: 'CattleList',
    data() {
        return {
      headers: [
        { title: 'ID', key: 'id' },
        { title: 'Número', key: 'numero' },
        { title: 'Raça', key: 'raca' },
        { title: 'Estado do Boi', key: 'estadoBoi' },
        { title: 'Data Última Pesagem', key: 'dataUltimaPesagem' },
        { title: 'Peso', key: 'peso' },
        
      ],
      cattle: [],
    };
  },
    async created() {
      try {
        const response = await api.getActiveCattleWithWeight();
        this.cattle = response.data;
      } catch (error) {
        console.error('Erro ao buscar dados do backend:', error);
      }
    },
  };
  </script>
  
  <style scoped>
  h1 {
    margin-bottom: 16px;
  }
  </style>
  