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
      @click:row="openTransactionDialog"
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
          @click.stop="openTransactionDialog(null, item)"
        />
      </template>
    </v-data-table>

    <!-- Dialog de detalhes -->
    <v-dialog v-model="dialog" max-width="900px" scrollable>
      <v-card v-if="selectedTransaction">
        <v-card-title class="d-flex align-center justify-space-between bg-primary">
          <span class="text-h6">
            Detalhes da Transação #{{ selectedTransaction.id }}
          </span>
          <v-btn
            icon="mdi-close"
            variant="text"
            @click="dialog = false"
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
                  <div class="font-weight-medium">{{ selectedTransaction.id }}</div>
                </v-col>
                <v-col cols="12" sm="6" md="4">
                  <div class="text-caption text-grey">Data</div>
                  <div class="font-weight-medium">{{ formatDate(selectedTransaction.data) }}</div>
                </v-col>
                <v-col cols="12" sm="6" md="4">
                  <div class="text-caption text-grey">Operação</div>
                  <v-chip
                    :color="selectedTransaction.operacao === 'COMPRA' ? 'success' : 'error'"
                    size="small"
                  >
                    {{ selectedTransaction.operacao }}
                  </v-chip>
                </v-col>
                <v-col cols="12" sm="6" md="4">
                  <div class="text-caption text-grey">Modelo</div>
                  <div class="font-weight-medium">{{ selectedTransaction.modeloTransacao }}</div>
                </v-col>
                <v-col cols="12" sm="6" md="4">
                  <div class="text-caption text-grey">
                    {{ selectedTransaction.operacao === 'COMPRA' ? 'Fornecedor' : 'Comprador' }}
                  </div>
                  <div class="font-weight-medium">{{ selectedTransaction.contraParte || '-' }}</div>
                </v-col>
                <v-col cols="12" sm="6" md="4">
                  <div class="text-caption text-grey">Valor Total</div>
                  <div class="font-weight-bold text-primary">
                    R$ {{ formatCurrency(selectedTransaction.valor) }}
                  </div>
                </v-col>
                <v-col cols="12" v-if="selectedTransaction.descricao">
                  <div class="text-caption text-grey">Descrição</div>
                  <div class="font-weight-medium">{{ selectedTransaction.descricao }}</div>
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
              {{ selectedTransaction.operacao === 'COMPRA' ? 'Bois Comprados' : 'Bois Vendidos' }}
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
                    <th v-if="selectedTransaction.operacao === 'COMPRA'">Estado</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(boi, idx) in items" :key="idx">
                    <td>{{ boi.numero || '-' }}</td>
                    <td>{{ boi.raca || '-' }}</td>
                    <td class="text-right">{{ boi.peso != null ? boi.peso.toFixed(1) : '-' }}</td>
                    <td class="text-right">R$ {{ formatCurrency(boi.valor) }}</td>
                    <td v-if="selectedTransaction.operacao === 'COMPRA'">
                      <v-chip size="x-small" color="success">
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
                    <td v-if="selectedTransaction.operacao === 'COMPRA'"></td>
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
          <v-spacer />
          <v-btn color="primary" variant="text" @click="dialog = false">
            Fechar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script>
import api from '../../services/api.js';

export default {
  name: 'TransactionsList',
  data() {
    return {
      headers: [
        { title: 'Data', key: 'data' },
        { title: 'ID', key: 'id' },
        { title: 'Operação', key: 'operacao' },
        { title: 'Modelo', key: 'modeloTransacao' },
        { title: 'Contraparte', key: 'contraParte' },
        { title: 'Valor', key: 'valor' },
        { title: '', key: 'actions', sortable: false, align: 'end' },
      ],
      transactions: [],
      sortBy: [{ key: 'data', order: 'desc' }],
      loading: false,
      
      // Dialog
      dialog: false,
      selectedTransaction: null,
      items: [],
      loadingItems: false,
      itemsError: null,
    };
  },
  async created() {
    await this.fetchTransactions();
  },
  methods: {
    async fetchTransactions() {
      this.loading = true;
      try {
        const response = await api.getTransactionsList();
        this.transactions = response.data;
      } catch (error) {
        console.error('Erro ao buscar dados do backend:', error);
      } finally {
        this.loading = false;
      }
    },
    
    async openTransactionDialog(event, item) {
      this.selectedTransaction = item;
      this.dialog = true;
      this.items = [];
      this.loadingItems = true;
      this.itemsError = null;

      try {
        let response;
        response = await api.getTransacaoById(item.id)
        const transacao = response.data;
        console.log(transacao);
        
        this.items = (transacao.itens || []).map(dto => ({
          numero: dto.boi?.numero,
          raca: dto.boi?.raca,
          estadoBoi: dto.boi?.estadoBoi,
          peso: dto.pesagem?.peso,
          valor: dto.valor,
        }));
        
      } catch (error) {
        console.error('Erro ao buscar itens da transação:', error);
        this.itemsError = 'Não foi possível carregar os itens desta transação.';
      } finally {
        this.loadingItems = false;
      }
    },

    formatCurrency(value) {
      if (value == null) return '0,00';
      return Number(value).toFixed(2).replace('.', ',').replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    },

    formatDate(dateStr) {
      if (!dateStr) return '-';
      const date = new Date(dateStr + 'T00:00:00');
      return date.toLocaleDateString('pt-BR');
    },

    calculateItemsTotal() {
      const total = this.items.reduce((acc, item) => acc + (item.valor || 0), 0);
      return this.formatCurrency(total);
    },
  },
};
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