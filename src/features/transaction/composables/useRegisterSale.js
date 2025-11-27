import { useUtils } from "@/shared/composables/useUtils";
import { ref, computed } from 'vue';
import api from "@/services/api";
import { useWeightFormatting } from "@/features/weighing/composables/useWeightFormatting";

export function useRegisterSale(){
    const { parseDecimal } = useUtils();
    const { normalizePeso, normalizeValor } = useWeightFormatting();
    const saleItems = ref([]);
    const error = ref(null);
    const submitting = ref(false);
    const form = ref({
        data: '',
        descricao: '',
        modeloTransacao: 'PESO',
        contraParte: '',
        valorTotal: '',
    })

    function createFromSelection(selected, cattleList){
        const sel = Array.isArray(selected) ? selected.filter(Boolean) : []
        const cattleById = new Map(cattleList.map(c => [c.id, c]))

        saleItems.value = sel.map(s => {
            const cattle = (typeof s === 'object' && s != null) ? s : cattleById.get(s)
            if(!cattle) return null

            return{
                boiId: cattle.id,
                numero: cattle.numero,
                raca: cattle.raca,
                valor: null,
                peso: cattle.peso ?? null,
                valorStr: '',
                pesoStr: cattle.peso != null ? String(cattle.peso) : '',
            }
        }).filter(Boolean)

        return saleItems.value.length > 0
    }

    const totalValue = computed(() => 
        saleItems.value.reduce((acc, item) => acc + (item.valor ?? 0), 0)
    )

    const totalValueFormatted = computed(() => 
        totalValue.value.toFixed(2)
    )

    function buildPayload() {
        const total = form.value.valorTotal !== '' 
            ? parseDecimal(form.value.valorTotal) 
            : totalValue.value

        const vendaBoiDTOS = saleItems.value.map(it => ({
            boiId: it.boiId,
            valor: it.valor,
            pesagemDTO: { peso: it.peso },
        }))

        const payload = {
            valor: total,
            data: String(form.value.data).slice(0, 10),
            descricao: form.value.descricao ?? '',
            modeloTransacao: form.value.modeloTransacao,
            contraParte: form.value.contraParte,
            vendaBoiDTOS,
        }
        return payload
    }

    function clear() {
        form.value = { data: '', descricao: '', modeloTransacao: 'PESO', contraParte: '', valorTotal: '' }
        saleItems.value = []
    }

    async function submit(onSuccess){
        submitting.value = true
        error.value = ''

        try {
            const payload = buildPayload();
            await api.createVenda(payload);
            
            clear()
            if (onSuccess) onSuccess()
        } catch(e){
            console.error('Erro ao cadastrar venda:', e)
            error.value = e
        } finally {
            submitting.value = false
        }
    }
    return {
        form,
        saleItems,
        totalValueFormatted,
        submitting,
        error,
        createFromSelection,
        normalizePeso,
        normalizeValor,
        submit
    }
}