<template>
    <v-dialog 
        :model-value="props.modelValue" 
        @update:model-value="$emit('update:modelValue', $event)" 
        fullscreen
        transition="dialog-bottom-transition"
    >
        <v-card>
            <v-toolbar color="primary" dark dense>
                <v-icon class="mr-2">mdi-chart-box-multiple</v-icon>
                <v-toolbar-title>Análise de Evolução de Peso</v-toolbar-title>
                <v-spacer />
                
                <v-btn
                    v-if="matrixData"
                    icon="mdi-refresh"
                    variant="text"
                    @click="generateMatrix"
                    title="Atualizar"
                />
                <v-btn
                    icon="mdi-close"
                    variant="text"
                    @click="$emit('close')"
                />
            </v-toolbar>

            <v-card-text class="pa-0" style="height: calc(100vh - 64px); overflow: hidden;">
                <v-tabs
                    v-model="activeTab"
                    bg-color="grey-lighten-4"
                    color="primary"
                    grow
                    height="56"
                >
                    <v-tab value="matrix">
                        <v-icon start>mdi-table-large</v-icon>
                        Matriz de Pesos
                    </v-tab>
                    <v-tab value="chart">
                        <v-icon start>mdi-chart-line</v-icon>
                        Gráfico de Evolução
                    </v-tab>
                    <v-tab value="table">
                        <v-icon start>mdi-table</v-icon>
                        Tabela Comparativa
                    </v-tab>
                </v-tabs>

                <div v-if="loading" class="loading-container">
                    <v-progress-circular indeterminate color="primary" size="64" />
                    <div class="mt-4 text-h6">Gerando análise...</div>
                </div>

                <v-window v-else-if="matrixData" v-model="activeTab" style="height: calc(100% - 56px);">
                    
                    <v-window-item value="matrix" style="height: 100%;">
                        <weight-matrix-tab
                            :matrix-data="matrixData"
                            :available-breeds="availableBreeds"
                        />
                    </v-window-item>

                    <v-window-item value="chart" style="height: 100%;">
                        <weight-evolution-chart-tab
                            :matrix-data="matrixData"
                        />
                    </v-window-item>

                    <v-window-item value="table" style="height: 100%;">
                        <weight-comparative-table-tab
                            :matrix-data="matrixData"
                            :available-breeds="availableBreeds"
                            :selected-dates="props.selectedDates"
                            @error="$emit('error', $event)"
                        />
                    </v-window-item>
                </v-window>

                <div v-else class="initial-state">
                    <v-icon size="120" color="primary" class="mb-4">mdi-chart-box-multiple</v-icon>
                    <div class="text-h5 mb-2">Análise de Evolução de Peso</div>
                    <div class="text-body-1 text-grey mb-6">
                        Analisando <strong>{{ props.selectedDates.length }} datas</strong> selecionadas
                    </div>
                    <v-btn
                        color="primary"
                        size="x-large"
                        prepend-icon="mdi-play"
                        :loading="loading"
                        @click="generateMatrix"
                        elevation="2"
                    >
                        Gerar Análise
                    </v-btn>
                </div>
            </v-card-text>
        </v-card>
    </v-dialog>
</template>

<script setup>
import api from '@/services/api';
import WeightMatrixTab from './WeightEvolution/WeightMatrixTab.vue';
import WeightEvolutionChartTab from './WeightEvolution/WeightEvolutionChartTab.vue';
import WeightComparativeTableTab from './WeightEvolution/WeightComparativeTableTab.vue';
import { watch, ref } from 'vue';

const props = defineProps({
        modelValue: Boolean,
        selectedDates: {
            type: Array,
            default: () => [],
        },
    })
const emit = defineEmits(['update:modelValue', 'close', 'error']);
const loading = ref(false);
const matrixData = ref(null);
const activeTab = ref('matrix');
const availableBreeds = ref([]);


watch(() => props.modelValue, (newVal) => {
    if (newVal && props.selectedDates.length >= 2 && !matrixData.value) {
        generateMatrix();
    }
    if (!newVal) {
        matrixData.value = null;
        activeTab.value = 'matrix';
        loading.value = false;
    }
})

async function generateMatrix(){
    loading.value = true;
    matrixData.value = null;
    
    try {
        const response = await api.generateWeightMatrix({
            datasSelecionadas: props.selectedDates
        });

        matrixData.value = response.data;
        
        if (matrixData.value.rows) {
            availableBreeds.value = [...new Set(
                matrixData.value.rows.map(row => row.raca)
            )].sort();
        }
    } catch (error) {
        console.error('Erro ao gerar matriz:', error);
        emit('error', 'Erro ao gerar matriz. Por favor, tente novamente.');
    } finally {
        loading.value = false;
    }
}
</script>


<style>
.loading-container, .initial-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    text-align: center;
}

.control-panel {
    background-color: #f5f5f5;
    border-bottom: 1px solid #e0e0e0;
    padding-top: 8px;
    padding-bottom: 8px;
}

</style>