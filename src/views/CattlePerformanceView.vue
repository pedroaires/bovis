<template>
    <FilteredDataTable
        title="Desempenho do Gado"
        :headers="headers"
        :items="cattleSummary"
        :total-items="totalElements"
        :loading="loading"
        :filters="filters"
        :filter-labels="filterLabels"
        :filter-formatters="filterFormatters"
        @apply-filters="onApplyFilters"
        @update:options="onOptionsUpdate"
    >
        <!-- Slot de Filtros -->
        <template #filters="{ filters }">
            <v-row dense>
                <v-col cols="12" sm="6" md="3">
                    <v-text-field
                        v-model.number="filters.numeroBoi"
                        label="Número do Boi"
                        type="number"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>

                <v-col cols="12" sm="6" md="3">
                    <v-select
                        v-model="filters.racaId"
                        :items="racas"
                        item-title="nome"
                        item-value="id"
                        label="Raça"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>

                <v-col cols="12" sm="6" md="3">
                    <v-select
                        v-model="filters.estadoBoi"
                        :items="estadosOptions"
                        label="Estado do Boi"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>

                <v-col cols="12" sm="6" md="3">
                    <v-text-field
                        v-model.number="filters.pesoMin"
                        label="Peso Mínimo (kg)"
                        type="number"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>

                <v-col cols="12" sm="6" md="3">
                    <v-text-field
                        v-model.number="filters.pesoMax"
                        label="Peso Máximo (kg)"
                        type="number"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>

                <v-col cols="12" sm="6" md="3">
                    <v-text-field
                        v-model="filters.dataPesagemInicio"
                        label="Pesagem de"
                        type="date"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>

                <v-col cols="12" sm="6" md="3">
                    <v-text-field
                        v-model="filters.dataPesagemFim"
                        label="Pesagem até"
                        type="date"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>

                <v-col cols="12" sm="6" md="3">
                    <v-text-field
                        v-model.number="filters.gmdMin"
                        label="GMD Mínimo (kg/dia)"
                        type="number"
                        step="0.1"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>
            </v-row>
        </template>

        <!-- Slots personalizados da tabela -->
        <template #[`item.gmd`]="{ item }">
            <v-chip
                :color="getGmdColor(item.gmd)"
                variant="flat"
                size="small"
            >
                {{ formatGmd(item.gmd) }}
            </v-chip>
        </template>

        <template #[`item.pesoAtual`]="{ item }">
            {{ formatPeso(item.pesoAtual) }}
        </template>

        <template #[`item.actions`]="{ item }">
            <v-btn
                icon="mdi-chart-line"
                variant="text"
                color="primary"
                size="small"
                @click="openDetails(item)"
            />
        </template>
    </FilteredDataTable>

    <!-- Modal de Detalhes -->
    <CattleDetailModal
        v-model="showDetailsDialog"
        :cattle="selectedCattle"
        @close="showDetailsDialog = false"
    />
</template>

<script>
import FilteredDataTable from '@/components/molecules/FilteredDataTable.vue';
import CattleDetailModal from '@/components/organisms/CattleDetailModal.vue';
import api from '@/services/api';

export default {
    name: 'CattlePerformanceView',
    components: {
        FilteredDataTable,
        CattleDetailModal
    },
    data() {
        return {
            headers: [
                { title: 'Número', key: 'numero', sortable: true },
                { title: 'Raça', key: 'raca', sortable: true },
                { title: 'Estado', key: 'estadoBoi', sortable: true },
                { title: 'Última Pesagem', key: 'ultimaPesagem', sortable: false },
                { title: 'Peso Atual (kg)', key: 'pesoAtual', sortable: false },
                { title: 'GMD (kg/dia)', key: 'gmd', sortable: false },
                { title: 'Ações', key: 'actions', sortable: false, align: 'center' },
            ],
            cattleSummary: [],
            filters: {
                racaId: null,
                pesoMin: null,
                pesoMax: null,
                dataPesagemInicio: null,
                dataPesagemFim: null,
                numeroBoi: null,
                estadoBoi: null,
                gmdMin: null,
            },
            filterLabels: {
                numeroBoi: 'Número',
                racaId: 'Raça',
                estadoBoi: 'Estado',
                pesoMin: 'Peso Min',
                pesoMax: 'Peso Max',
                dataPesagemInicio: 'Data Início',
                dataPesagemFim: 'Data Fim',
                gmdMin: 'GMD Min'
            },
            estadosOptions: [
                { title: 'Na Fazenda', value: 'NA_FAZENDA' },
                { title: 'Vendido', value: 'VENDIDO' },
                { title: 'Morto', value: 'MORTO' }
            ],
            racas: [],
            loading: false,
            options: {
                page: 1,
                itemsPerPage: 10,
                sortBy: [{ key: 'numero', order: 'asc' }],
            },
            totalElements: 0,
            showDetailsDialog: false,
            selectedCattle: null,
        }
    },
    computed: {
        filterFormatters() {
            return {
                racaId: (value) => {
                    const raca = this.racas.find(r => r.id === value)
                    return raca ? raca.nome : value
                },
                estadoBoi: (value) => {
                    const estado = this.estadosOptions.find(e => e.value === value)
                    return estado ? estado.title : value
                },
                dataPesagemInicio: (value) => new Date(value).toLocaleDateString('pt-BR'),
                dataPesagemFim: (value) => new Date(value).toLocaleDateString('pt-BR')
            }
        }
    },
    methods: {
        buildRequestParams() {
            const { page, itemsPerPage, sortBy } = this.options
            let sortParam
            if (Array.isArray(sortBy) && sortBy.length > 0) {
                const sortItem = sortBy[0]
                const key = sortItem.key
                const order = sortItem.order || 'asc'
                sortParam = `${key},${order}`
            }
            return {
                ...this.filters,
                page: page - 1,
                size: itemsPerPage === -1 ? this.totalElements : itemsPerPage,
                ...(sortParam ? { sort: sortParam } : {})
            }
        },

        async fetchCattleSummary() {
            try {
                this.loading = true
                const params = this.buildRequestParams()
                const response = await api.fetchCattlePerformance(params)
                
                this.cattleSummary = response.data.content
                this.totalElements = response.data.totalElements
            } catch (error) {
                console.error('Erro ao buscar sumário de desempenho:', error)
            } finally {
                this.loading = false
            }
        },

        async fetchRacas() {
            try {
                const response = await api.fetchRacas()
                this.racas = response.data
            } catch (error) {
                console.error('Erro ao buscar raças:', error)
            }
        },

        onApplyFilters(newFilters) {
            this.filters = { ...newFilters }
            this.options.page = 1
            this.fetchCattleSummary()
        },

        openDetails(cattle) {
            this.selectedCattle = cattle
            this.showDetailsDialog = true
        },

        onOptionsUpdate(newOpts) {
            this.options = { ...newOpts }
            this.fetchCattleSummary()
        },

        formatPeso(peso) {
            if (peso === null || peso === undefined) return '—'
            return `${Number(peso).toFixed(1)} kg`
        },

        formatGmd(gmd) {
            if (gmd === null || gmd === undefined) return '—'
            return `${Number(gmd).toFixed(2)} kg/dia`
        },

        getGmdColor(gmd) {
            if (gmd === null || gmd === undefined) return 'grey'
            const value = Number(gmd)
            if (value >= 1.2) return 'success'
            if (value >= 0.8) return 'warning'
            return 'error'
        }
    },

    mounted() {
        this.fetchRacas()
        this.fetchCattleSummary()
    }
}
</script>