import { ref } from 'vue';
import api from "@/services/api";

export function useCattlePerformance(
) {
    const options = ref({
        page: 1,
        itemsPerPage: 10,
        sortBy: [{ key: 'numero', order: 'asc'}],
    })
    const loading = ref(true);
    const cattleSummary = ref([])
    const totalElements = ref(0)
    const headers = ref([
        { title: 'Número', key: 'numero', sortable: true },
        { title: 'Raça', key: 'raca', sortable: true },
        { title: 'Estado', key: 'estadoBoi', sortable: true },
        { title: 'Última Pesagem', key: 'ultimaPesagem', sortable: false },
        { title: 'Peso Atual (kg)', key: 'pesoAtual', sortable: false },
        { title: 'GMD (kg/dia)', key: 'gmd', sortable: false },
        { title: 'Ações', key: 'actions', sortable: false, align: 'center' },
    ])
    function buildRequestParams(filters) {
        const { page, itemsPerPage, sortBy } = options.value
        let sortParam
        if (Array.isArray(sortBy) && sortBy.length > 0) {
            const sortItem = sortBy[0]
            const key = sortItem.key
            const order = sortItem.order || 'asc'
            sortParam = `${key},${order}`
        }
        return {
            ...filters,
            page: page - 1,
            size: itemsPerPage === -1 ? totalElements.value : itemsPerPage,
            ...(sortParam ? { sort: sortParam } : {})
        }
    }
    async function fetchCattleSummary(filters = {}) {
        try {
            loading.value = true
            const params = buildRequestParams(filters)
            const response = await api.fetchCattlePerformance(params)
            
            cattleSummary.value = response.data.content
            totalElements.value = response.data.totalElements
        } catch (e) {
            console.error('Erro ao buscar sumário de desempenho:', e)
            throw e
        } finally {
            loading.value = false
        }
    }

    function onOptionsUpdate(newOpts, filters = {}){
        options.value = { ... newOpts }
        fetchCattleSummary(filters)
    }

    return {
        cattleSummary,
        loading,
        totalElements,
        options,
        headers,

        fetchCattleSummary,
        onOptionsUpdate
    }
}