import { ref, computed, onMounted } from "vue"
import api from "@/services/api"
export function useCattleFilters(){
    const filters = ref({
        racaId: null,
        pesoMin: null,
        pesoMax: null,
        dataPesagemInicio: null,
        dataPesagemFim: null,
        numeroBoi: null,
        estadoBoi: null,
        gmdMin: null,
    })

    const filterLabels = ref({
        numeroBoi: 'Número',
        racaId: 'Raça',
        estadoBoi: 'Estado',
        pesoMin: 'Peso Min',
        pesoMax: 'Peso Max',
        dataPesagemInicio: 'Data Início',
        dataPesagemFim: 'Data Fim',
        gmdMin: 'GMD Min'
    })

    const estadosOptions = ref([
        { title: 'Na Fazenda', value: 'NA_FAZENDA' },
        { title: 'Vendido', value: 'VENDIDO' },
        { title: 'Morto', value: 'MORTO' }
    ])

    const racas = ref([])

    const filterFormatters = computed(() => {
        return {
            racaId: (value) => {
                const raca = racas.value.find(r => r.id === value)
                return raca ? raca.nome : value
            },
            estadoBoi: (value) => {
                const estado = estadosOptions.value.find(e => e.value === value)
                return estado ? estado.title : value
            },
            dataPesagemInicio: (value) => new Date(value).toLocaleDateString('pt-BR'),
            dataPesagemFim: (value) => new Date(value).toLocaleDateString('pt-BR')
        }
    })
    async function fetchRacas() {
        try {
            const response = await api.fetchRacas()
            racas.value = response.data
        } catch (error) {
            console.error('Erro ao buscar raças:', error)
            throw error
        }
    }
    function resetFilters() {
        filters.value = {
            racaId: null,
            pesoMin: null,
            pesoMax: null,
            dataPesagemInicio: null,
            dataPesagemFim: null,
            numeroBoi: null,
            estadoBoi: null,
            gmdMin: null,
        }
    }
    onMounted(() => {
        fetchRacas()
    })

    return {
        filters,
        filterLabels,
        estadosOptions,
        racas,
        filterFormatters,

        fetchRacas,
        resetFilters
    }
}