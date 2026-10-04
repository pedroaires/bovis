import { ref } from "vue";
import api from "@/services/api";
export function useWeighingDetails(){
    const weighings = ref([])
    const loading = ref(false)
    const error = ref(null)

    async function fetchIndividualWeighings(dataPesagem) {
        if(!dataPesagem) return
        loading.value = true;
        error.value = null;
        try {
            const response = await api.fetchIndividualWeighingsByDate(dataPesagem); 
            weighings.value = response.data;
        } catch (err) {
            console.error('Erro ao buscar pesagens individuais:', err);
            error.value = err.message || 'Erro desconhecido.';
        } finally {
            loading.value = false;
        }
    }

    async function deleteIndividual(weighingId) {
        try {
            loading.value = true;
            await api.deleteIndividualWeighing(weighingId); 
            return true;
        } catch (err) {
            console.error('Erro ao deletar pesagem individual:', err);
            error.value = err.response?.data?.message || 'Erro ao deletar a pesagem.';
            return false;
        } finally {
            loading.value = false;
        }
    }

    return{
        weighings,
        loading,
        error,

        fetchIndividualWeighings,
        deleteIndividual
    }
}
