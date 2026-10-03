import { ref } from 'vue';
import api from '@/services/api.js';

export function useTransactionEdit() {
  const saving = ref(false);
  const saveError = ref(null);
  const saveSuccess = ref(false);

  async function saveTransaction(id, payload) {
    saving.value = true;
    saveError.value = null;
    saveSuccess.value = false;

    try {
      await api.updateTransaction(id, payload);
      saveSuccess.value = true;
      return { success: true };
    } catch (error) {
      console.error('Erro ao salvar transação:', error);
      saveError.value = error.response?.data?.message || 'Erro ao salvar a transação. Tente novamente.';
      return { success: false, error: saveError.value };
    } finally {
      saving.value = false;
    }
  }

  function resetState() {
    saveError.value = null;
    saveSuccess.value = false;
  }

  return {
    saving,
    saveError,
    saveSuccess,
    saveTransaction,
    resetState,
  };
}