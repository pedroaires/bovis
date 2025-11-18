<template>
  <div>
    <v-alert
      v-if="fetchTransactionsError"
      type="error"
      class="mb-4"
      title="Erro ao carregar"
    >
      Não foi possível buscar os dados de transações. Por favor, tente novamente mais tarde.
    </v-alert>
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
      v-model="showDetailDialog"
      :transaction="selectedTransaction"
      @edit="openEditDialog"
    />

    <!-- Modal de edição -->
    <TransactionEditModal
      v-model="showEditDialog"
      :transaction="selectedTransaction"
      @saved="handleTransactionSaved"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import TransactionDetailModal from './TransactionDetailModal.vue';
import TransactionEditModal from './TransactionEditModal.vue';
import { useWeightFormatting } from '@/features/weighing/composables/useWeightFormatting';
import { useTransactionList } from '../composables/useTransationList';

const { formatDate, formatCurrency } = useWeightFormatting();
const { 
  loading,
  error: fetchTransactionsError,
  transactions,
  fetchTransactions,
} = useTransactionList();

const headers = [
  { title: 'Data', key: 'data' },
  { title: 'ID', key: 'id' },
  { title: 'Operação', key: 'operacao' },
  { title: 'Modelo', key: 'modeloTransacao' },
  { title: 'Contraparte', key: 'contraParte' },
  { title: 'Valor', key: 'valor' },
  { title: '', key: 'actions', sortable: false, align: 'end' },
];

const sortBy = ref([{ key: 'data', order: 'desc' }]);

const showDetailDialog = ref(false);
const showEditDialog = ref(false);
const selectedTransaction = ref(null);

function openTransactionDialog(item) {
  selectedTransaction.value = item;
  showDetailDialog.value = true;
}

function openEditDialog() {
  showEditDialog.value = true;
}

async function handleTransactionSaved({ payload }) {
  // Atualiza a lista
  await fetchTransactions();

  // Atualiza a transação selecionada
  selectedTransaction.value = {
    ...selectedTransaction.value,
    ...payload,
  };
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