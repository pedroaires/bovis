import api from "@/services/api";
import { ref } from 'vue';
export function useTransactionList(){
    const loading = ref(false);
    const transactions = ref([])
    const error = ref(null)

    async function fetchTransactions() {
        loading.value = true;
        try {
            const response = await api.getTransactionsList();
            transactions.value = response.data;
        } catch (e) {
            console.error('Erro ao buscar dados do backend:', e);
            error.value = e;
        } finally {
            loading.value = false;
        }
    }
    return {
        loading,
        error,
        transactions,
        fetchTransactions
    }
}