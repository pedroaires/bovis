<template>
    <FilteredDataTable
        title="Pesagens Agrupadas por Data"
        :headers="headers"
        :items="groupedWeighings"
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
                <v-col cols="12" sm="6" md="4">
                    <v-text-field
                        v-model="filters.dataPesagemInicio"
                        label="Data Início"
                        type="date"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>

                <v-col cols="12" sm="6" md="4">
                    <v-text-field
                        v-model="filters.dataPesagemFim"
                        label="Data Fim"
                        type="date"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>

                <v-col cols="12" sm="6" md="4">
                    <v-text-field
                        v-model.number="filters.pesoMedioMin"
                        label="Peso Médio Mín. (kg)"
                        type="number"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>
            </v-row>
        </template>

        <!-- Slots personalizados da tabela -->
        <template #[`item.dataPesagem`]="{ item }">
            {{ formatDate(item.dataPesagem) }}
        </template>

        <template #[`item.pesoMedio`]="{ item }">
            {{ formatPeso(item.pesoMedio) }}
        </template>

        <template #[`item.pesoTotal`]="{ item }">
            {{ formatPeso(item.pesoTotal) }}
        </template>

        <template #[`item.actions`]="{ item }">
            <v-btn
                icon="mdi-eye"
                variant="text"
                color="primary"
                size="small"
                title="Ver Detalhes"
                @click="openDetails(item)"
            />
            <v-btn
                icon="mdi-pencil"
                variant="text"
                color="warning"
                size="small"
                title="Editar Data do Grupo"
                @click="openEditGroupDialog(item)"
            />
            <v-btn
                icon="mdi-delete"
                variant="text"
                color="error"
                size="small"
                title="Deletar Grupo"
                @click="confirmDeleteGroup(item)"
            />
        </template>
    </FilteredDataTable>

    <!-- Modal de Detalhes -->
    <WeighingDetailDialog
        v-model="showDetailsDialog"
        :data-pesagem="selectedGroup ? selectedGroup.data : null"
        @close="showDetailsDialog = false"
        @pesagem-edited="fetchGroupedWeighings"
        @pesagem-deleted="fetchGroupedWeighings"
    />

    <!-- Dialog de Edição de Grupo -->
    <WeighingGroupEditDialog
        v-model="showEditGroupDialog"
        :group-data="selectedGroup"
        @close="showEditGroupDialog = false"
        @group-updated="onGroupUpdated"
    />

    <!-- Dialog de Confirmação de Deleção -->
    <v-dialog v-model="showDeleteDialog" max-width="500">
        <v-card>
            <v-card-title class="text-h5">Confirmar Deleção</v-card-title>
            <v-card-text>
                Tem certeza que deseja deletar TODAS as {{ selectedGroup ? selectedGroup.totalBois : 0 }} pesagens da data 
                <strong>{{ selectedGroup ? formatDate(selectedGroup.dataPesagem) : '' }}</strong>?
                Esta ação é irreversível.
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="grey-darken-1" variant="text" @click="showDeleteDialog = false">
                    Cancelar
                </v-btn>
                <v-btn color="error" variant="flat" @click="deleteGroup">
                    Deletar
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
import FilteredDataTable from '@/components/molecules/FilteredDataTable.vue';
import WeighingDetailDialog from '@/components/organisms/WeighingDetailDialog.vue';
import WeighingGroupEditDialog from '@/components/organisms/WeighingGroupEditDialog.vue';
import api from '@/services/api'; 

export default {
    name: 'WeighingGroupView',
    components: {
        FilteredDataTable,
        WeighingDetailDialog,
        WeighingGroupEditDialog,
    },
    data() {
        return {
            headers: [
                { title: 'Data da Pesagem', key: 'data', sortable: true },
                { title: 'Total de Bois', key: 'totalBois', sortable: true },
                { title: 'Peso Médio (kg)', key: 'pesoMedio', sortable: true },
                { title: 'Peso Total (kg)', key: 'pesoTotal', sortable: true },
                { title: 'Ações', key: 'actions', sortable: false, align: 'center' },
            ],
            groupedWeighings: [],
            loading: false,
            options: {
                page: 1,
                itemsPerPage: 10,
                sortBy: [{ key: 'dataPesagem', order: 'desc' }],
            },
            totalElements: 0,
            
            // Filtros
            filters: {
                dataPesagemInicio: null,
                dataPesagemFim: null,
                pesoMedioMin: null,
            },
            filterLabels: {
                dataPesagemInicio: 'Data Início',
                dataPesagemFim: 'Data Fim',
                pesoMedioMin: 'Peso Médio Mín.',
            },

            // Modais
            showDetailsDialog: false,
            showEditGroupDialog: false,
            showDeleteDialog: false,
            selectedGroup: null,
        }
    },
    computed: {
        filterFormatters() {
            return {
                dataPesagemInicio: (value) => this.formatDate(value),
                dataPesagemFim: (value) => this.formatDate(value),
            }
        }
    },
    methods: {
        formatDate(dateString) {
            if (!dateString) return '—';
            const date = new Date(dateString + 'T00:00:00'); 
            return date.toLocaleDateString('pt-BR');
        },

        formatPeso(peso) {
            if (peso === null || peso === undefined) return '—'
            return `${Number(peso).toFixed(2)} kg`
        },

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

        async fetchGroupedWeighings() {
            try {
                this.loading = true
                // TODO: USAR PARAMS PARA FILTRAR
                // const params = this.buildRequestParams()

                const response = await api.fetchGroupedWeighings() 
                
                this.groupedWeighings = response.data
                this.totalElements = response.data.totalElements
            } catch (error) {
                console.error('Erro ao buscar pesagens agrupadas:', error)
                // TODO: Adicionar lógica de notificação de erro aqui
            } finally {
                this.loading = false
            }
        },

        onApplyFilters(newFilters) {
            this.filters = { ...newFilters }
            this.options.page = 1
            this.fetchGroupedWeighings()
        },

        onOptionsUpdate(newOpts) {
            this.options = { ...newOpts }
            this.fetchGroupedWeighings()
        },

        // Ações
        openDetails(group) {
            this.selectedGroup = group
            this.showDetailsDialog = true
        },

        openEditGroupDialog(group) {
            this.selectedGroup = group
            this.showEditGroupDialog = true
        },

        confirmDeleteGroup(group) {
            this.selectedGroup = group
            this.showDeleteDialog = true
        },

        async deleteGroup() {
            if (!this.selectedGroup) return;

            try {
                this.loading = true;
                const dataPesagem = this.selectedGroup.data;
                await api.deleteGroupedWeighing(dataPesagem); 
                
                this.showDeleteDialog = false;
                this.fetchGroupedWeighings(); 
                // TODO: Adicionar lógica de notificação de sucesso aqui
            } catch (error) {
                console.error('Erro ao deletar grupo de pesagens:', error);
                // TODO: Adicionar lógica de notificação de erro aqui
            } finally {
                this.loading = false;
            }
        },

        onGroupUpdated() {
            this.showEditGroupDialog = false;
            this.fetchGroupedWeighings();
            // TODO: Adicionar lógica de notificação de sucesso aqui
        }
    },

    mounted() {
        this.fetchGroupedWeighings()
    }
}
</script>
