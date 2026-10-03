import api from "@/services/api";
import { ref } from 'vue';
export function useGroupEditWeighing(){
    const loading = ref(false)
    const error = ref(null)
    const success = ref(false)

    async function updateGroupWeighing(oldDate, newDate){
        loading.value = true;
        error.value = null;
        success.value = false;
        
        try {
            await api.updateGroupedWeighingDate(oldDate, newDate); 
            success.value = true;
            return true;
        } catch (err) {
            console.error('Erro ao salvar nova data do grupo:', err);
            error.value = err.response?.data?.message || 'Erro ao atualizar a data do grupo. Tente novamente.';
            return false;
        } finally {
            loading.value = false;
        }

    }
 return {
    loading,
    error,
    success,
    updateGroupWeighing
}
}