import { ref } from "vue";
import api from "@/services/api";

export function useWeighingGroup(){
    const groupedWeighings = ref([]);
    const loading = ref(false);
    const totalElements = ref(0);
    const error = ref(null)

    async function fetchGroupedWeighings(){
        try {
            loading.value = true;
            const response = await api.fetchGroupedWeighings();
            groupedWeighings.value = response.data;
            totalElements.value = response.data.totalElements;
        } catch (e) {
            console.error('Erro ao buscar pesagens agrupadas:', e)
            error.value = `Erro ao buscar pesagens agrupadas: ${e}`
        } finally {
            loading.value = false;
        }
    }
    async function deleteGrouppedWeighing(selectedDate){
        try {
            loading.value = true;
            await api.deleteGroupedWeighing(selectedDate); 
            
            fetchGroupedWeighings();
            
        } catch (e) {
            console.error('Erro ao deletar grupo de pesagens:', e);
            error.value = e.response?.data?.message || 'Erro ao deletar grupo de pesagens.'
        } finally {
            loading.value = false;
        }
    }
    return {
        groupedWeighings,
        loading,
        error,
        fetchGroupedWeighings,
        deleteGrouppedWeighing
    }
}
