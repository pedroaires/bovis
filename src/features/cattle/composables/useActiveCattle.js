import { ref, onMounted } from 'vue'
import api from '@/services/api.js'

export function useActiveCattle() {
  const cattle = ref([])
  const loading = ref(false)
  const error = ref(null)

  const fetchActiveCattle = async () => {
    loading.value = true
    error.value = null
    try {
      const resp = await api.getActiveCattleWithWeight()
      cattle.value = resp.data ?? []
    } catch (e) {
      console.error('Falha ao buscar gado:', e)
      error.value = e
    } finally {
      loading.value = false
    }
  }

  onMounted(fetchActiveCattle)

  return {
    cattle,
    loading,
    error,
    fetchActiveCattle,
  }
}