<template>
    <div>
      <h1>Lista de Gado</h1>
      <DataTable :headers="headers" :items="cattle" />
    </div>
  </template>
  
  <script>
  import DataTable from '../organisms/DataTable.vue';
  import api from '../../services/api.js';
  
  export default {
    name: 'CattleList',
    components: {
      DataTable,
    },
    data() {
        return {
      headers: [
        { title: 'ID', key: 'id' },
        { title: 'Número', key: 'numero' },
        { title: 'Lote ID', key: 'loteId' },
        { title: 'Estado do Boi', key: 'estadoBoi' },
        { title: 'Raça', key: 'raca' },
      ],
      cattle: [],
    };
    },
    async created() {
      try {
        const response = await api.getCattle();
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
  