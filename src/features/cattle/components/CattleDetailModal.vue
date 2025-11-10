<template>
    <v-dialog :model-value="props.modelValue" @update:model-value="$emit('update:modelValue', $event)" max-width="900px">
        <v-card v-if="cattle">
            <v-card-title class="d-flex justify-space-between align-center">
                <div>
                    <span class="text-h5">Boi #{{ cattle.numero }}</span>
                    <v-chip class="ml-2" size="small">{{ cattle.raca }}</v-chip>
                </div>
                <v-btn
                    icon="mdi-close"
                    variant="text"
                    @click="emit('update:modelValue', false)"
                ></v-btn>
            </v-card-title>

            <v-divider></v-divider>

            <v-card-text>
                <!-- Informações Resumidas -->
                <v-row class="mb-4">
                    <v-col cols="3">
                        <div class="text-caption text-grey">Estado</div>
                        <v-chip :color="getStatusColor(cattle.estadoBoi)" size="small">{{ cattle.estadoBoi }}</v-chip>
                    </v-col>
                    <v-col cols="3">
                        <div class="text-caption text-grey">Peso Atual</div>
                        <div class="text-body-1">{{ formatPeso(cattle.pesoAtual) }}</div>
                    </v-col>
                    <v-col cols="3">
                        <div class="text-caption text-grey">Última Pesagem</div>
                        <div class="text-body-1">{{ formatDate(cattle.ultimaPesagem) }}</div>
                    </v-col>
                    <v-col cols="3">
                        <div class="text-caption text-grey">GMD Médio</div>
                        <div class="text-body-1">
                            <v-chip :color="getGMDColor(cattle.gmd)" size="small">
                                {{ formatGMD(cattle.gmd) }}
                            </v-chip>
                        </div>
                    </v-col>
                </v-row>
                <v-alert v-if="fetchError" type="error" class="mb-4">
                    {{ fetchError }}
                </v-alert>

                <!-- Conteúdo do Histórico -->
                <div v-if="loading" class="text-center py-8">
                    <v-progress-circular indeterminate></v-progress-circular>
                </div>
                
                <div v-else-if="processedData.tableData.length > 0">
                    <!-- Gráfico -->
                    <h3 class="mb-3">Evolução do Peso</h3>
                    <apexchart
                        type="area"
                        height="300"
                        :options="chartOptions"
                        :series="processedData.chartSeries"
                    ></apexchart>

                    <!-- Tabela de Histórico -->
                    <h3 class="mt-6 mb-3">Histórico de Pesagens</h3>
                    <v-table density="compact">
                        <thead>
                            <tr>
                                <th>Data da Pesagem</th>
                                <th>Peso (kg)</th>
                                <th>GMD desde anterior (kg/dia)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="(pesagem, index) in processedData.tableData" :key="index">
                                <td>{{ formatDate(pesagem.data) }}</td>
                                <td>{{ formatPeso(pesagem.peso) }}</td>
                                <td>
                                    <v-chip v-if="pesagem.gmd !== null"
                                    :color="getGMDColor(pesagem.gmd)"
                                    size="small">
                                        {{ formatGMD(pesagem.gmd) }}
                                    </v-chip>
                                    <span v-else class="text-grey">—</span>
                                </td>
                            </tr>
                        </tbody>
                    </v-table>
                </div>

                <div v-else class="text-center py-8 text-grey">
                    Nenhum histórico de pesagens disponível
                </div>
            </v-card-text>
        </v-card>
    </v-dialog>
</template>
<script setup>
import { computed } from 'vue';
import { useWeightFormatting } from '@/features/weighing/composables/useWeightFormatting';
import { useCattleStatus } from '@/shared/composables/useCattleStatus';
import { useWeightHistory } from '../composables/useWeightHistory';
import { useChartConfig } from '../composables/useChartConfig';

const props = defineProps({
    modelValue: {
        type: Boolean, 
        default: false
    },
    cattle: {
        type: Object,
        default: null
    }
});
const cattleId = computed(() => props.cattle?.id);
const {
    formatGMD,
    getGMDColor,
    formatDate,
    formatPeso,
} = useWeightFormatting();

const {
    getColor: getStatusColor
} = useCattleStatus();

const {
    processedData,
    loading,
    fetchError
} = useWeightHistory(cattleId);
const emit = defineEmits(['update:modelValue']);
const chartCategories = computed(() => processedData.value.chartCategories);
const { chartOptions } = useChartConfig(chartCategories);
</script>

<style scoped>
h3 {
    color: rgba(0, 0, 0, 0.87);
    font-weight: 500;
}
</style>