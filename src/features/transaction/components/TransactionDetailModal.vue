<template>
  <v-dialog 
    :model-value="modelValue" 
    @update:model-value="emit('update:modelValue', $event)"
    max-width="900px" 
    scrollable
  >
    <v-card v-if="transactionDetailed">
      <v-card-title class="d-flex align-center justify-space-between bg-primary">
        <span class="text-h6">
          Detalhes da Transação #{{ transactionDetailed.id }}
        </span>
        <v-btn
          icon="mdi-close"
          variant="text"
          @click="emit('update:modelValue', false)"
        />
      </v-card-title>

      <v-card-text class="pt-4">
        <!-- Informações gerais -->
        <v-card variant="tonal" class="mb-4">
          <v-card-title class="text-subtitle-1">
            Informações Gerais
          </v-card-title>
          <v-card-text>
            <v-row dense>
              <v-col cols="12" sm="6" md="4">
                <div class="text-caption text-grey">ID</div>
                <div class="font-weight-medium">{{ transactionDetailed.id }}</div>
              </v-col>
              <v-col cols="12" sm="6" md="4">
                <div class="text-caption text-grey">Data</div>
                <div class="font-weight-medium">{{ formatDate(transactionDetailed.data) }}</div>
              </v-col>
              <v-col cols="12" sm="6" md="4">
                <div class="text-caption text-grey">Operação</div>
                <v-chip
                  :color="transactionDetailed.operacao === 'COMPRA' ? 'success' : 'error'"
                  size="small"
                >
                  {{ transactionDetailed.operacao }}
                </v-chip>
              </v-col>
              <v-col cols="12" sm="6" md="4">
                <div class="text-caption text-grey">Modelo</div>
                <div class="font-weight-medium">{{ transactionDetailed.modeloTransacao }}</div>
              </v-col>
              <v-col cols="12" sm="6" md="4">
                <div class="text-caption text-grey">
                  {{ transactionDetailed.operacao === 'COMPRA' ? 'Fornecedor' : 'Comprador' }}
                </div>
                <div class="font-weight-medium">{{ transactionDetailed.contraParte || '-' }}</div>
              </v-col>
              <v-col cols="12" sm="6" md="4">
                <div class="text-caption text-grey">Valor Total</div>
                <div class="font-weight-bold text-primary">
                  R$ {{ formatCurrency(transactionDetailed.valor) }}
                </div>
              </v-col>
              <v-col cols="12" v-if="transactionDetailed.descricao">
                <div class="text-caption text-grey">Descrição</div>
                <div class="font-weight-medium">{{ transactionDetailed.descricao }}</div>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>

        <!-- Loading de itens -->
        <div v-if="loading" class="text-center py-8">
          <v-progress-circular indeterminate color="primary" size="48" />
          <div class="mt-3 text-body-2">Carregando itens...</div>
        </div>

        <!-- Erro ao carregar -->
        <v-alert v-else-if="error" type="error" variant="tonal" class="mb-4">
          {{ error }}
        </v-alert>

        <!-- Lista de itens -->
        <v-card v-else-if="transactionDetailed.itens.length" variant="outlined">
          <v-card-title class="text-subtitle-1">
            {{ transactionDetailed.operacao === 'COMPRA' ? 'Bois Comprados' : 'Bois Vendidos' }}
            <v-chip size="small" class="ml-2">{{ transactionDetailed.itens.length }}</v-chip>
          </v-card-title>
          <v-card-text>
            <v-table density="comfortable">
              <thead>
                <tr>
                  <th>Número</th>
                  <th>Raça</th>
                  <th class="text-right">Peso (kg)</th>
                  <th class="text-right">Valor (R$)</th>
                  <th v-if="transactionDetailed.operacao === 'COMPRA'">Estado</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(boi, idx) in transactionDetailed.itens" :key="idx">
                  <td>{{ boi.numero || '-' }}</td>
                  <td>{{ boi.raca || '-' }}</td>
                  <td class="text-right">{{ boi.peso != null ? boi.peso.toFixed(1) : '-' }}</td>
                  <td class="text-right">R$ {{ formatCurrency(boi.valor) }}</td>
                  <td v-if="transactionDetailed.operacao === 'COMPRA'">
                    <v-chip size="x-small" :color="getColor(boi.estadoBoi)">
                      {{ boi.estadoBoi || '-' }}
                    </v-chip>
                  </td>
                </tr>
              </tbody>
              <tfoot>
                <tr>
                  <td colspan="3" class="text-right font-weight-bold">Total dos Itens:</td>
                  <td class="text-right font-weight-bold text-primary">
                    R$ {{ calculateItemsTotal() }}
                  </td>
                  <td v-if="transactionDetailed.operacao === 'COMPRA'"></td>
                </tr>
              </tfoot>
            </v-table>
          </v-card-text>
        </v-card>

        <!-- Sem itens -->
        <v-alert v-else type="info" variant="tonal">
          Nenhum item encontrado para esta transação.
        </v-alert>
      </v-card-text>

      <v-card-actions>
        <v-btn 
          color="primary" 
          variant="tonal"
          prepend-icon="mdi-pencil"
          @click="emit('edit')"
        >
          Editar
        </v-btn>
        <v-spacer />
        <v-btn color="primary" variant="text" @click="emit('update:modelValue', false)">
          Fechar
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { formatDate } from "@/shared/utils/formatters/dateFormatter"
import { formatCurrency } from '@/shared/utils/formatters/currencyFormatter';
import { useCattleStatus } from '@/shared/composables/useCattleStatus';
import { useTransactionDetails } from '../composables/useTransactionDetails';
const { getColor } = useCattleStatus();
import { computed } from 'vue';

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  transaction: {
    type: Object,
    default: null
  }
});


const transactionIdRef = computed(() => props.transaction?.id)
const {
  loading,
  error,
  transactionDetailed
} = useTransactionDetails(transactionIdRef);

  const emit = defineEmits(['update:modelValue', 'edit']);


function calculateItemsTotal() {
  if (!transactionDetailed.value?.itens) return formatCurrency(0);
  const total = transactionDetailed.value.itens.reduce((acc, item) => acc + (item.valor || 0), 0);
  return formatCurrency(total);
}
</script>