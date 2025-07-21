<template>
    <div>
        <h1>Histórico de Pesagens</h1>
        <!-- Aqui você adicionaria os campos de filtro -->
        <!-- Exemplo de como você chamaria o DataTable -->
        <DataTable :headers="headers" :items="cattleWeightHistory" />

        <!-- Aqui você adicionaria os controles de paginação, se o DataTable não os tiver internamente -->
        <!-- Exemplo: -->
        <div class="pagination-controls">
            <button @click="onPageChange(pagination.page - 1)" :disabled="pagination.page === 0">Anterior</button>
            <span>Página {{ pagination.page + 1 }} de {{ totalPages }}</span>
            <button @click="onPageChange(pagination.page + 1)" :disabled="pagination.page >= totalPages - 1">Próxima</button>
        </div>
    </div>
</template>

<script>
import DataTable from '../organisms/DataTable.vue';
import api from '../../services/api.js';

export default{
    name: 'CattleWeightHistory',
    components: {
        DataTable,
    },
    data(){
        return{
            headers: [
                { title: 'ID do Boi', key: 'id' },
                { title: 'Número do Boi', key: 'numero' },
                { title: 'Raça', key: 'racaNome' },
                { title: 'Estado do Boi', key: 'estadoBoi' },
            ],
            cattleWeightHistory: [],
            allDates: [],
            filters: {
                racaId: null,
                pesoMin: null,
                pesoMax: null,
                dataPesagemInicio: null,
                dataPesagemFim: null,
                dataCompraInicio: null,
                dataCompraFim: null,
                numeroBoi: null,
                estadoBoi: null,
            },
            pagination: {
                page: 0,
                size: 10,
                sortBy: 'numero',
                sortDesc: false,
            },
            totalElements: 0,
            totalPages: 0,
        }
    },
    async created() {
        await this.fetchCattleWeightHistory();
    },
    methods: {
        buildRequestParams() {
            return {
                ...this.filters,
                page: this.pagination.page,
                size: this.pagination.size,
                sort: `${this.pagination.sortBy},${this.pagination.sortDesc ? 'desc' : 'asc'}`,
            }
        },
        extractAllDates(content) {
            const dateSet = new Set()
            content.forEach(boi => {
                if (boi.pesagens) {
                    Object.keys(boi.pesagens).forEach(date => dateSet.add(date))
                }
            })
            return Array.from(dateSet).sort()
        },
        buildHeaders(dates) {
            return [
                { title: 'ID do Boi', key: 'id' },
                { title: 'Número do Boi', key: 'numero' },
                { title: 'Raça', key: 'raca' },
                { title: 'Estado do Boi', key: 'estadoBoi' },
                ...dates.map(date => ({ title: date, key: `peso_${date}` })),
            ]
        },
        buildTableRows(content, dates) {
            return content.map(boi => {
            const row = {
                id: boi.id,
                numero: boi.numero,
                raca: boi.raca,
                estadoBoi: boi.estadoBoi,
            }
            dates.forEach(date => {
                row[`peso_${date}`] = boi.pesagens?.[date]?.peso ?? null
                })
                return row
            })
        },
        async fetchCattleWeightHistory() {
            try {
                const params = this.buildRequestParams()
                const response = await api.fetchCattleWeightHistory(params)
                const content = response.data.content

                const allDates = this.extractAllDates(content)
                this.allDates = allDates
                this.headers = this.buildHeaders(allDates)
                this.cattleWeightHistory = this.buildTableRows(content, allDates)
                this.totalElements = response.data.totalElements
                this.totalPages = response.data.totalPages
            } catch (error) {
                console.error('Erro ao buscar histórico de pesagens:', error)
            }
        },
        onPageChange(newPage) {
            this.pagination.page = newPage;
            this.fetchCattleWeightHistory();
        },
        onSizeChange(newSize) {
            this.pagination.size = newSize;
            this.pagination.page = 0;
            this.fetchCattleWeightHistory();
        },
        onSortChange(sortBy, sortDesc) {
            this.pagination.sortBy = sortBy;
            this.pagination.sortDesc = sortDesc;
            this.fetchCattleWeightHistory();
        },
        applyFilters() {
            this.pagination.page = 0;
            this.fetchCattleWeightHistory();
        },
    },
}
</script>

<style scoped>
h1 {
  margin-bottom: 16px;
}
</style>
