<template>
    <div>
        <h1>Lista De Transações</h1>
        <v-data-table
            :headers="headers" :items="transactions"  :sort-by="sortBy"/>
    </div>
</template>
<script>
import api from '../../services/api.js';

export default{
    name: 'TransactionsList',
    data(){
        return{
            headers: [
                { title: 'Data', key: 'data'},
                { title: 'ID', key: 'id'},
                { title: 'Modelo da Transacao', key: 'modeloTransacao'},
                { title: 'Operação', key: 'operacao'},
                { title: 'Contraparte', key: 'contraParte'},
                { title: 'Valor', key: 'valor'},
            ],
            transactions: [],
            sortBy: [{key: 'data', order: 'desc'}]
        };
    },
    async created(){
        try{
            const response = await api.getTransactionsList();
            this.transactions = response.data;

        } catch (error){
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