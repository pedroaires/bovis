import api from "@/services/api";
import { computed, ref, watch } from 'vue';
import { calculateDaysBetween } from "@/shared/utils/calculators/dateCalculator";

export function useWeightHistory(cattleIdRef){
    const loading = ref(false);
    const rawWeightHistory = ref([]);
    const fetchError = ref(null);
    watch(cattleIdRef, (newId) => {
        if (newId) {
            fetchWeightHistory(newId);
        } else {
            rawWeightHistory.value = [];
        }
    }, { immediate: true });

    async function fetchWeightHistory(cattleId) {
        if(!cattleId) return
        try {
            loading.value = true;
            rawWeightHistory.value = [];
            const response = await api.fetchCattleWeightHistoryById(cattleId);
            
            rawWeightHistory.value = response.data.sort((a, b) => 
                new Date(a.data) - new Date(b.data)
            );
        } catch (error) {
            console.error('Erro ao buscar histórico do boi:', error);
            fetchError.value = 'Fala ao carregar o histórico do Boi: ';
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
                const dataAtual = pesagem.data;
                const dataAnterior = rawWeightHistory.value[i - 1].data;
                
                const diferencaDias = calculateDaysBetween(dataAtual, dataAnterior)
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
    })

    return {
        processedData,
        loading,
        fetchError
    }
} 