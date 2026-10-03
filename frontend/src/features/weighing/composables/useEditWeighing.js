import { ref } from 'vue';
import api from '@/services/api'
export function useEditWeighing(){
    const pesagemForm = ref({
        id: null,
        numeroBoi: '',
        dataPesagem: null,
        peso:null,
    });

    const loading = ref(false)
    const error = ref(null)
    const success = ref(false)

    function populateForm(pesagemData){
        if(pesagemData && pesagemData.id){
            pesagemForm.value.id = pesagemData.id;
            pesagemForm.value.numeroBoi = pesagemData.numeroBoi;
            pesagemForm.value.dataPesagem = pesagemData.dataPesagem;
            pesagemForm.value.peso = pesagemData.peso;
            error.value = null;
            success.value = false;
        }
    }

    function resetMessages(){
        error.value = null;
        success.value = false;
    }

    async function updateWeighing(boiId){
        loading.value = true;
        error.value = null;
        success.value = false;

        try {
            const payload = {
                boiId,
                data: pesagemForm.value.dataPesagem,
                peso: pesagemForm.value.peso,
            }
            await api.updateIndividualWeighing(pesagemForm.value.id, payload); 
            success.value = true;
            return true;
        } catch (err) {
            console.error('Erro ao salvar pesagem individual:', err);
            error.value = err.response?.data?.message || 'Erro ao atualizar a pesagem. Tente novamente.';
            return false;
        } finally {
            loading.value = false;
        }
    }

    return {
        pesagemForm,
        loading,
        error,
        success,
        populateForm,
        resetMessages,
        updateWeighing
    }
}