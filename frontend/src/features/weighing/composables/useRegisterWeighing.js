import { ref , computed} from 'vue';
import api from '@/services/api';

export function useRegisterWeighing(){
    const weighingItems = ref([])
    const error = ref (null)
    const submitting = ref(false)
    const weighingDate = ref(new Date().toISOString().slice(0, 10))
    
    const canSubmit = computed(() => {
        if (!weighingDate.value || weighingItems.value.length === 0) {
            return false
        }
        return weighingItems.value.every(item => item.peso !== null && item.peso > 0)
    })
    function createFromSelection(selected, cattleList){
        const sel = Array.isArray(selected.value) ? selected.value.filter(Boolean) : []
        const byId = new Map(cattleList.value.map(x => [x.id, x]))

        weighingItems.value = sel.map(s => {
            const c = (typeof s === 'object' && s !== null) ? s : byId.get(s)
            if (!c) return null
            return {
            boiId: c.id,
            numero: c.numero,
            raca: c.raca,
            peso: null,
            pesoStr: '',
            descricao: '',
            medicacoes: [],
            }
        }).filter(Boolean)

        return weighingItems.value.length > 0
    }
    function clear(){
        weighingItems.value = []
        weighingDate.value = new Date().toISOString().slice(0, 10)
    }
    function buildPayload(){
        const pesagens = weighingItems.value.map(it => ({
                boiId: it.boiId,
                peso: it.peso,
                descricao: it.descricao || null,
                data: weighingDate.value,
                medicacoes: it.medicacoes,
            }))
        return {
            pesagens: pesagens
        }
    }
    async function submit(onSuccess){
        try {
            submitting.value = true
            error.value = ''
            const payload = buildPayload()
            await api.createWeighingBatch(payload)
            clear()
            if(onSuccess) onSuccess()

        } catch (e) {
            console.error('createWeighing error:', e?.response?.data || e)
            error.value = e?.response?.data?.message || 'Falha ao cadastrar a pesagem.'
        } finally {
            submitting.value = false
        }

    }
    return {
        weighingItems,
        weighingDate,
        submitting,
        error,
        canSubmit,
        createFromSelection,
        submit
    }
}