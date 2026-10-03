import api from "@/services/api";
import { ref, watch, computed } from 'vue'


export function useTransactionDetails(transactionId){
    const rawTransactionDetail = ref(null)
    const loading = ref(false);
    const error = ref(null);

    watch(transactionId, (newId) => {
        if(newId) {
            fetchTransactionDetails(newId);
        } else {
            rawTransactionDetail.value = null
        }
    }, {immediate: true});
    const transactionDetailed = computed(() => {
        if (!rawTransactionDetail.value) return null;
    
        const { itens, ...transactionData } = rawTransactionDetail.value;
    
    return {
        ...transactionData, // Todos os campos da transação
        itens: (itens || []).map(dto => ({
            numero: dto.boi.numero,
            raca: dto.boi.raca,
            estadoBoi: dto.boi.estadoBoi,
            peso: dto.pesagem?.peso,
            valor: dto.valor,
        }))
    };
    })
    async function fetchTransactionDetails(transactionId){
        try {
            loading.value = true;
            const response = await api.getTransacaoById(transactionId);
            
            rawTransactionDetail.value = response.data;
            
        } catch (e) {
            console.error('Erro ao buscar itens da transação:', e);
            error.value = 'Não foi possível carregar os detalhes desta transação.';
        } finally {
            loading.value = false;
        }
    }
    return {
        loading,
        error,
        transactionDetailed,
        fetchTransactionDetails
    }
}