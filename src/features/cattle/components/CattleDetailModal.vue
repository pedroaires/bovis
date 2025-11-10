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
                    @click="emit('close')"
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
import { ref, watch, computed } from 'vue';
import { useWeightFormatting } from '@/features/weighing/composables/useWeightFormatting';
import { useCattleStatus } from '@/shared/composables/useCattleStatus';
import api from '@/services/api.js';

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

const {
    formatGMD,
    getGMDColor,
    formatDate,
    formatPeso
} = useWeightFormatting();

const {
    getColor: getStatusColor
} = useCattleStatus();

const emit = defineEmits(['update:modelValue', 'close']);
const loading = ref(false);
const rawWeightHistory = ref([]);

watch(() => props.modelValue, (newVal) => {
    if (newVal && props.cattle){
        fetchWeightHistory()
    }
})
async function fetchWeightHistory() {
    if(!props.cattle?.id) return
    try {
        loading.value = true;
        const response = await api.fetchCattleWeightHistoryById(props.cattle.id);
        
        rawWeightHistory.value = response.data.sort((a, b) => 
            new Date(a.data) - new Date(b.data)
        );
    } catch (error) {
        console.error('Erro ao buscar histórico do boi:', error);
        rawWeightHistory.value = []; 
    } finally {
        loading.value = false;
    }
    
}

const processedData = computed(() => {
    if (!rawWeightHistory.value || rawWeightHistory.value.length === 0) {
        return {
            tableData: [],
            chartSeries: [],
            chartCategories: []
        };
    }

    const tableData = [];
    const chartCategories = [];
    const chartWeights = [];

    for (let i = 0; i < rawWeightHistory.value.length; i++) {
        const pesagem = rawWeightHistory.value[i];
        const pesoAtual = Number(pesagem.peso);
        let gmd = null;

        if (i > 0) {
            const pesoAnterior = Number(rawWeightHistory.value[i - 1].peso);
            const dataAtual = new Date(pesagem.data);
            const dataAnterior = new Date(rawWeightHistory.value[i - 1].data);
            
            const diferencaDias = Math.floor((dataAtual - dataAnterior) / (1000 * 60 * 60 * 24));
            
            if (diferencaDias > 0) {
                gmd = (pesoAtual - pesoAnterior) / diferencaDias;
            }
        }
        
        tableData.push({
            ...pesagem,
            gmd: gmd
        });
        
        const d = new Date(pesagem.data);
        chartCategories.push(d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' }));
        chartWeights.push(pesoAtual);
    }

    return {
        tableData: tableData,
        chartSeries: [{ name: 'Peso', data: chartWeights }],
        chartCategories: chartCategories
    };
});

const chartOptions = computed(() => ({
    chart: {
        type: 'area',
        height: 300,
        toolbar: { show: false },
        tooltip: {
            x: { format: 'dd/MM' }
        }
    },
    fill: {
        type: 'gradient',
        gradient: {
            shadeIntensity: 1,
            opacityFrom: 0.7,
            opacityTo: 0.1,
            stops: [0, 90, 100]
        }
    },
    stroke: {
        curve: 'smooth',
        width: 2
    },
    markers: {
        size: 4,
        strokeWidth: 2,
        strokeColors: '#fff'
    },
    xaxis: {
        categories: processedData.value.chartCategories, 
        labels: {
            rotate: -45,
            style: { colors: '#666', fontSize: '12px' }
        }
    },
    yaxis: {
        title: {
            text: 'Peso (kg)',
            style: { color: '#666' }
        },
        labels: {
            formatter: (value) => `${value.toFixed(0)} kg`,
            style: { colors: '#666' }
        }
    },
    grid: {
        borderColor: '#e0e0e0',
        strokeDashArray: 4
    },
    legend: {
        show: false
    }
}));
</script>

<style scoped>
h3 {
    color: rgba(0, 0, 0, 0.87);
    font-weight: 500;
}
</style>