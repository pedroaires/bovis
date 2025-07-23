<template>
    <div>
        <h1>Histórico de Pesagens</h1>
        <BoiFilters :filters="filters" @apply="onFilterApply" />
        <v-data-table-server
        :headers="headers"
        :items="cattleWeightHistory"
        :options="options"
        :items-length="totalElements"
        :loading="loading"
        @update:options="onOptionsUpdate"
        />
    </div>
</template>

<script>
import BoiFilters from '../organisms/BoiFilters.vue';
import api from '../../services/api.js';

export default{
    name: 'CattleWeightHistory',
    components: {
        BoiFilters
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
            loading: false,
    options: {
      page: 1,
      itemsPerPage: 10,
      sortBy: ['numero'],
      sortDesc: [false],
    },
            totalElements: 0,
            
        }
    },
    async created() {
        await this.fetchCattleWeightHistory();
    },
    watch: {
  options: {
    deep: true,
    handler() {
      this.fetchCattleWeightHistory()
    }
  }
},
    methods: {
        buildRequestParams() {
            const { page, itemsPerPage, sortBy, sortDesc } = this.options
            let sortParam
            if (Array.isArray(sortBy) && sortBy.length > 0) {
                const key = sortBy[0]?.key || sortBy[0]
                const desc = Array.isArray(sortDesc) && sortDesc[0]
                sortParam = `${key},${desc ? 'desc' : 'asc'}`
            }
            return {
                ...this.filters,
                page: page - 1,
                size: itemsPerPage,
                ...(sortParam ? { sort: sortParam } : {})
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
                this.loading = true
                const params = this.buildRequestParams()
                const response = await api.fetchCattleWeightHistory(params)
                const content = response.data.content

                const allDates = this.extractAllDates(content)
                this.allDates = allDates
                this.headers = this.buildHeaders(allDates)
                this.cattleWeightHistory = this.buildTableRows(content, allDates)
                this.totalElements = response.data.totalElements
                
            } catch (error) {
                console.error('Erro ao buscar histórico de pesagens:', error)
            } finally {
                this.loading = false
            }
        },
        onFilterApply(newFilters) {
            console.log("aplicando filters");
            this.filters = { ...newFilters }
            this.fetchCattleWeightHistory();
            this.options.page = 1
        },

        onOptionsUpdate(newOpts) {
            this.options = { ...newOpts }
        },
    },
}
</script>

<style scoped>
    h1 {
        margin-bottom: 16px;
    }
</style>
