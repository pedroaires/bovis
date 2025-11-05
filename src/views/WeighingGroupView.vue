<template>
    <div>
        <!-- Ações Globais -->
        <v-card variant="outlined" class="mb-4">
            <v-card-text class="d-flex align-center justify-space-between">
                <div>
                    <div class="text-h6">Pesagens Agrupadas por Data</div>
                    <div class="text-caption text-grey mt-1">
                        Selecione múltiplas datas na tabela para comparar
                    </div>
                </div>
            </v-card-text>
        </v-card>

        <!-- Tabela com Seleção Múltipla -->
        <v-card variant="outlined">
            <v-card-text>
                <!-- Filtros -->
                <v-expansion-panels class="mb-4">
                    <v-expansion-panel>
                        <v-expansion-panel-title>
                            <v-icon class="mr-2">mdi-filter</v-icon>
                            Filtros
                        </v-expansion-panel-title>
                        <v-expansion-panel-text>
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
                            <v-btn
                                color="primary"
                                class="mt-3"
                                size="small"
                                @click="applyFilters"
                            >
                                Aplicar Filtros
                            </v-btn>
                        </v-expansion-panel-text>
                    </v-expansion-panel>
                </v-expansion-panels>

                <!-- Tabela -->
                <v-data-table
                    v-model="selectedItems"
                    :headers="headers"
                    :items="groupedWeighings"
                    :loading="loading"
                    show-select
                    item-value="data"
                    density="comfortable"
                    :items-per-page="10"
                >
                    <!-- Header customizado com contador de selecionados -->
                    <template #top>
                        <v-toolbar density="compact" flat v-if="selectedItems.length > 0">
                            <v-toolbar-title>
                                <v-chip color="primary" variant="flat">
                                    {{ selectedItems.length }} selecionada(s)
                                </v-chip>
                            </v-toolbar-title>
                            <v-spacer />
                            <v-btn
                                color="grey"
                                variant="text"
                                @click="selectedItems = []"
                            >
                                Limpar Seleção
                            </v-btn>
                        </v-toolbar>
                    </template>

                    <template #[`item.data`]="{ item }">
                        <div class="d-flex align-center">
                            {{ formatDate(item.data) }}
                            <v-chip
                                v-if="isInComparison(item.data)"
                                size="x-small"
                                color="primary"
                                class="ml-2"
                            >
                                na comparação
                            </v-chip>
                        </div>
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
                </v-data-table>
            </v-card-text>
        </v-card>

        <!-- Barra de Ação Flutuante (aparece quando há seleção) -->
        <WeightComparisonBar
            v-model:selected-dates="selectedItems"
            :grouped-weighings="groupedWeighings"
            @open-matrix="showMatrixDialog = true"
        />
        <!-- Modal de Matriz de Pesos -->
        <WeightEvolutionDialog
            v-model="showMatrixDialog"
            :selected-dates="selectedItems"
            @close="showMatrixDialog = false"
        />

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
                    <strong>{{ selectedGroup ? formatDate(selectedGroup.data) : '' }}</strong>?
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
    </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import WeighingDetailDialog from '@/components/organisms/WeighingDetailDialog.vue';
import WeighingGroupEditDialog from '@/components/organisms/WeighingGroupEditDialog.vue';
import WeightEvolutionDialog from '@/components/organisms/WeightEvolutionDialog.vue';
import WeightComparisonBar from '@/components/organisms/WeightComparisonBar.vue';
import { useWeightFormatting } from '@/composables/useWeightFormatting';
import api from '@/services/api'

const {
    formatDate,
    formatPeso
} = useWeightFormatting();

const headers = ref([
    { title: 'Data da Pesagem', key: 'data', sortable: true },
    { title: 'Total de Bois', key: 'totalBois', sortable: true },
    { title: 'Peso Médio (kg)', key: 'pesoMedio', sortable: true },
    { title: 'Peso Total (kg)', key: 'pesoTotal', sortable: true },
    { title: 'Ações', key: 'actions', sortable: false, align: 'center' },
])
const groupedWeighings = ref([]);
const loading = ref(false);
const totalElements = ref(0);

const selectedItems = ref([])

// Filtros
const filters = ref({
    dataPesagemInicio: null,
    dataPesagemFim: null,
    pesoMedioMin: null,
});

// Modais e Painéis
const showDetailsDialog = ref(false);
const showEditGroupDialog = ref(false);
const showDeleteDialog = ref(false);
const showMatrixDialog = ref(false);
const selectedGroup = ref(null);

async function fetchGroupedWeighings(){
    try {
        loading.value = true;
        const response = await api.fetchGroupedWeighings();
        groupedWeighings.value = response.data;
        totalElements.value = response.data.totalElements;
    } catch (error) {
        console.error('Erro ao buscar pesagens agrupadas:', error)
    } finally {
        loading.value = false;
    }
}
function applyFilters() {
    fetchGroupedWeighings();
}

function isInComparison(date) {
    return selectedItems.value.includes(date);
}

// Ações individuais
function openDetails(group) {
    selectedGroup.value = group
    showDetailsDialog.value = true
}

function openEditGroupDialog(group) {
    selectedGroup.value = group
    showEditGroupDialog.value = true
}

function confirmDeleteGroup(group) {
    selectedGroup.value = group
    showDeleteDialog.value = true
}

async function deleteGroup() {
    if (!selectedGroup.value) return;

    try {
        loading.value = true;
        const dataPesagem = selectedGroup.value.data;
        await api.deleteGroupedWeighing(dataPesagem); 
        
        showDeleteDialog.value = false;
        fetchGroupedWeighings(); 
    } catch (error) {
        console.error('Erro ao deletar grupo de pesagens:', error);
    } finally {
        loading.value = false;
    }
}

function onGroupUpdated() {
    showEditGroupDialog.value = false;
    fetchGroupedWeighings();
}

onMounted(async () => {
    await fetchGroupedWeighings()
} );
</script>

<style scoped>

</style>