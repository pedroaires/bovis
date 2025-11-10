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
                
                <div v-else-if="weightHistory.length > 0">
                    <!-- Gráfico -->
                    <WeightChart :weight-history="weightHistory" />

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
                            <tr v-for="(pesagem, index) in weightHistory" :key="index">
                                <td>{{ formatDate(pesagem.data) }}</td>
                                <td>{{ formatPeso(pesagem.peso) }}</td>
                                <td>
                                    <span v-if="pesagem.gmd !== null">
                                        {{ formatGMD(pesagem.gmd) }}
                                    </span>
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
import { ref, watch } from 'vue';
import WeightChart from '@/features/cattle/components/WeightChart.vue';
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

const weightHistory = ref([]);
const loading = ref(false);

watch(() => props.modelValue, (newVal) => {
    if (newVal && props.cattle){
        fetchWeightHistory()
    }
})
async function fetchWeightHistory() {
    if(!props.cattle?.id) return
    try {
    loading.value = true
    const response = await api.fetchCattleWeightHistoryById(props.cattle.id)
    
    // Ordenar por data (mais antiga primeiro)
    const pesagens = response.data.sort((a, b) => 
        new Date(a.data) - new Date(b.data)
    )
    
    // Calcular GMD entre pesagens consecutivas
    weightHistory.value = pesagens.map((pesagem, index) => {
        let gmd = null
        
        if (index > 0) {
            const pesoAtual = Number(pesagem.peso)
            const pesoAnterior = Number(pesagens[index - 1].peso)
            const dataAtual = new Date(pesagem.data)
            const dataAnterior = new Date(pesagens[index - 1].data)
            
            const diferencaDias = Math.floor((dataAtual - dataAnterior) / (1000 * 60 * 60 * 24))
            
            if (diferencaDias > 0) {
                gmd = (pesoAtual - pesoAnterior) / diferencaDias
            }
        }
        
        return {
            ...pesagem,
            gmd: gmd
        }
    })
} catch (error) {
    console.error('Erro ao buscar histórico do boi:', error)
} finally {
    loading.value = false
}
    
}

</script>

<style scoped>
h3 {
    color: rgba(0, 0, 0, 0.87);
    font-weight: 500;
}
</style>