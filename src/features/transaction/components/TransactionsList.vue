<template>
  <div>
    <h1>Lista De Transações</h1>
    <v-data-table
      :headers="headers"
      :items="transactions"
      :sort-by="sortBy"
      :loading="loading"
      item-value="id"
      hover
      @click:row="(_, {item}) => openTransactionDialog(item)"
    >
      <!-- Coluna de valor formatada -->
      <template #[`item.valor`]="{ item }">
        R$ {{ formatCurrency(item.valor) }}
      </template>

      <!-- Coluna de data formatada -->
      <template #[`item.data`]="{ item }">
        {{ formatDate(item.data) }}
      </template>

      <!-- Ícone de ação -->
      <template #[`item.actions`]="{ item }">
        <v-btn
          icon="mdi-eye"
          variant="text"
          size="small"
          @click.stop="openTransactionDialog(item)"
        />
      </template>
    </v-data-table>

    <!-- Modal de detalhes -->
    <TransactionDetailModal
      v-model="detailDialog"
      :transaction="selectedTransaction"
      :items="items"
      :loading-items="loadingItems"
      :items-error="itemsError"
      @edit="openEditDialog"
    />

    <!-- Modal de edição -->
    <TransactionEditModal
      v-model="editDialog"
      :transaction="selectedTransaction"
      :loading="saving"
      :error="saveError"
      :success="saveSuccess"
      @save="handleSave"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '@/services/api.js';
import TransactionDetailModal from './TransactionDetailModal.vue';
import TransactionEditModal from './TransactionEditModal.vue';
import { useWeightFormatting } from '@/features/weighing/composables/useWeightFormatting';

const { formatDate } = useWeightFormatting();

const headers = [
  { title: 'Data', key: 'data' },
  { title: 'ID', key: 'id' },
  { title: 'Operação', key: 'operacao' },
  { title: 'Modelo', key: 'modeloTransacao' },
  { title: 'Contraparte', key: 'contraParte' },
  { title: 'Valor', key: 'valor' },
  { title: '', key: 'actions', sortable: false, align: 'end' },
];

const transactions = ref([]);
const sortBy = ref([{ key: 'data', order: 'desc' }]);
const loading = ref(false);

const detailDialog = ref(false);
const selectedTransaction = ref(null);
const items = ref([]);
const loadingItems = ref(false);
const itemsError = ref(null);

const editDialog = ref(false);
const saving = ref(false);
const saveError = ref(null);
const saveSuccess = ref(false);

async function fetchTransactions() {
  loading.value = true;
  try {
    const response = await api.getTransactionsList();
    transactions.value = response.data;
  } catch (error) {
    console.error('Erro ao buscar dados do backend:', error);
  } finally {
    loading.value = false;
  }
}

async function openTransactionDialog(item) {
  selectedTransaction.value = item;
  detailDialog.value = true;
  items.value = [];
  loadingItems.value = true;
  itemsError.value = null;

  try {
    const response = await api.getTransacaoById(item.id);
    const transacao = response.data;
    
    items.value = (transacao.itens || []).map(dto => ({
      numero: dto.boi?.numero,
      raca: dto.boi?.raca,
      estadoBoi: dto.boi?.estadoBoi,
      peso: dto.pesagem?.peso,
      valor: dto.valor,
    }));
    
  } catch (error) {
    console.error('Erro ao buscar itens da transação:', error);
    itemsError.value = 'Não foi possível carregar os itens desta transação.';
  } finally {
    loadingItems.value = false;
  }
}

function openEditDialog() {
  saveError.value = null;
  saveSuccess.value = false;
  editDialog.value = true;
}

async function handleSave({ id, payload }) {
  saving.value = true;
  saveError.value = null;
  saveSuccess.value = false;

  try {
    await api.updateTransaction(id, payload);
    
    saveSuccess.value = true;

    // Atualiza a lista
    await fetchTransactions();

    // Atualiza a transação selecionada
    selectedTransaction.value = {
      ...selectedTransaction.value,
      ...payload,
    };

    // Fecha o dialog após 1.5s
    setTimeout(() => {
      editDialog.value = false;
      saveSuccess.value = false;
    }, 1500);

  } catch (error) {
    console.error('Erro ao salvar transação:', error);
    saveError.value = error.response?.data?.message || 'Erro ao salvar a transação. Tente novamente.';
  } finally {
    saving.value = false;
  }
}

function formatCurrency(value) {
  if (value == null) return '0,00';
  return Number(value).toFixed(2).replace('.', ',').replace(/\B(?=(\d{3})+(?!\d))/g, '.');
}

onMounted(async () => {
  await fetchTransactions();
});
</script>

<style scoped>
h1 {
  margin-bottom: 16px;
}

.v-table {
  border: 1px solid rgba(0, 0, 0, 0.12);
}

.v-table thead tr th {
  font-weight: 600;
}

:deep(.v-data-table tbody tr) {
  cursor: pointer;
}

:deep(.v-data-table tbody tr:hover) {
  background-color: rgba(0, 0, 0, 0.04);
}
</style>