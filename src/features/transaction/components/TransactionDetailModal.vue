<template>
  <v-dialog 
    :model-value="modelValue" 
    @update:model-value="emit('update:modelValue', $event)"
    max-width="900px" 
    scrollable
  >
    <v-card v-if="transaction">
      <v-card-title class="d-flex align-center justify-space-between bg-primary">
        <span class="text-h6">
          Detalhes da Transação #{{ transaction.id }}
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
                <div class="font-weight-medium">{{ transaction.id }}</div>
              </v-col>
              <v-col cols="12" sm="6" md="4">
                <div class="text-caption text-grey">Data</div>
                <div class="font-weight-medium">{{ formatDate(transaction.data) }}</div>
              </v-col>
              <v-col cols="12" sm="6" md="4">
                <div class="text-caption text-grey">Operação</div>
                <v-chip
                  :color="transaction.operacao === 'COMPRA' ? 'success' : 'error'"
                  size="small"
                >
                  {{ transaction.operacao }}
                </v-chip>
              </v-col>
              <v-col cols="12" sm="6" md="4">
                <div class="text-caption text-grey">Modelo</div>
                <div class="font-weight-medium">{{ transaction.modeloTransacao }}</div>
              </v-col>
              <v-col cols="12" sm="6" md="4">
                <div class="text-caption text-grey">
                  {{ transaction.operacao === 'COMPRA' ? 'Fornecedor' : 'Comprador' }}
                </div>
                <div class="font-weight-medium">{{ transaction.contraParte || '-' }}</div>
              </v-col>
              <v-col cols="12" sm="6" md="4">
                <div class="text-caption text-grey">Valor Total</div>
                <div class="font-weight-bold text-primary">
                  R$ {{ formatCurrency(transaction.valor) }}
                </div>
              </v-col>
              <v-col cols="12" v-if="transaction.descricao">
                <div class="text-caption text-grey">Descrição</div>
                <div class="font-weight-medium">{{ transaction.descricao }}</div>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>

        <!-- Loading de itens -->
        <div v-if="loadingItems" class="text-center py-8">
          <v-progress-circular indeterminate color="primary" size="48" />
          <div class="mt-3 text-body-2">Carregando itens...</div>
        </div>

        <!-- Erro ao carregar -->
        <v-alert v-else-if="itemsError" type="error" variant="tonal" class="mb-4">
          {{ itemsError }}
        </v-alert>

        <!-- Lista de itens -->
        <v-card v-else-if="items.length" variant="outlined">
          <v-card-title class="text-subtitle-1">
            {{ transaction.operacao === 'COMPRA' ? 'Bois Comprados' : 'Bois Vendidos' }}
            <v-chip size="small" class="ml-2">{{ items.length }}</v-chip>
          </v-card-title>
          <v-card-text>
            <v-table density="comfortable">
              <thead>
                <tr>
                  <th>Número</th>
                  <th>Raça</th>
                  <th class="text-right">Peso (kg)</th>
                  <th class="text-right">Valor (R$)</th>
                  <th v-if="transaction.operacao === 'COMPRA'">Estado</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(boi, idx) in items" :key="idx">
                  <td>{{ boi.numero || '-' }}</td>
                  <td>{{ boi.raca || '-' }}</td>
                  <td class="text-right">{{ boi.peso != null ? boi.peso.toFixed(1) : '-' }}</td>
                  <td class="text-right">R$ {{ formatCurrency(boi.valor) }}</td>
                  <td v-if="transaction.operacao === 'COMPRA'">
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
                  <td v-if="transaction.operacao === 'COMPRA'"></td>
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
import { useWeightFormatting } from '@/features/weighing/composables/useWeightFormatting';
import { useCattleStatus } from '@/shared/composables/useCattleStatus';
const { formatDate } = useWeightFormatting();
const { getColor } = useCattleStatus();
const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  transaction: {
    type: Object,
    default: null
  },
  items: {
    type: Array,
    default: () => []
  },
  loadingItems: {
    type: Boolean,
    default: false
  },
  itemsError: {
    type: String,
    default: null
  }
});

const emit = defineEmits(['update:modelValue', 'edit']);

function formatCurrency(value) {
  if (value == null) return '0,00';
  return Number(value).toFixed(2).replace('.', ',').replace(/\B(?=(\d{3})+(?!\d))/g, '.');
}

function calculateItemsTotal() {
  const total = props.items.reduce((acc, item) => acc + (item.valor || 0), 0);
  return formatCurrency(total);
}
</script>