<template>
  <v-container>
    <v-stepper v-model="step">
      <v-stepper-header>
        <v-stepper-item :value="1" title="Selecionar bois" />
        <v-stepper-item :value="2" title="Dados da transação" />
      </v-stepper-header>

      <v-alert
        v-if="fetchCattleError"
        type="error"
        class="mb-4"
        title="Erro ao carregar"
      >
        Não foi possível buscar os dados do gado. Por favor, tente novamente mais tarde.
      </v-alert>
      <v-stepper-window>
        <!-- Passo 1: Seleção -->
        <v-stepper-window-item :value="1">
          <v-card>
            <v-card-text>
              <ClientDataTable
              title="Selecionar bois para venda"
                :items="cattle"
                :headers="headers"
                :loading="loading"
                selectable
                v-model="selected"
                >
                <template #actions>
                  <div class="mt-4 text-right">
                    <v-btn color="primary" :disabled="!selected.length" @click="toStep2">
                      Continuar
                    </v-btn>
                  </div>
                </template>
                <template #[`item.estadoBoi`]="{ item }">
                  <v-chip
                  :color="getColor(item.estadoBoi)"
                  size="small">
                    {{ item.estadoBoi }}
                  </v-chip>
                </template>
                <template #[`item.dataUltimaPesagem`]="{ item }">
                  {{ formatDate(item.dataUltimaPesagem) }}
                </template>
              </ClientDataTable>
            </v-card-text>
          </v-card>
        </v-stepper-window-item>

        <!-- Passo 2: Formulário + Itens -->
        <v-stepper-window-item :value="2">
          <v-card>
            <v-card-text>
              <v-alert
                v-if="error"
                type="error"
                class="mb-4"
                dismissible
                @click:close="error = ''"
              >
                {{ error }}
              </v-alert>

              <TransacaoForm
                ref="txFormRef"
                v-model="form"
                :contra-parte-label="'Comprador'"
                :mostrar-modelo-transacao="false"
              />

              <div class="mt-6">
                <div class="text-subtitle-2 mb-2">
                  Bois selecionados (edite valor/peso):
                </div>

                <v-data-table
                  :headers="itemHeaders"
                  :items="saleItems"
                  item-key="boiId"
                  :items-per-page="10"
                >
                  <template #[`item.valor`]="{ item }">
                    <v-text-field
                      v-model="item.valorStr"
                      density="compact"
                      variant="outlined"
                      inputmode="decimal"
                      hide-details
                      @blur="normalizeValor(item)"
                    />
                  </template>

                  <template #[`item.peso`]="{ item }">
                    <v-text-field
                      v-model="item.pesoStr"
                      density="compact"
                      variant="outlined"
                      inputmode="decimal"
                      hide-details
                      @blur="normalizePeso(item)"
                    />
                  </template>
                </v-data-table>

                <div class="text-right mt-4">
                  <div class="text-body-2">
                    Soma dos itens (R$): <b>{{ totalValueFormatted }}</b>
                  </div>
                </div>
              </div>
            </v-card-text>

            <v-card-actions>
              <v-btn variant="text" @click="step = 1">Voltar</v-btn>
              <v-spacer />
              <v-btn
                color="primary"
                :loading="submitting"
                :disabled="!canSubmit"
                @click="handleConfirmVenda"
              >
                Confirmar Venda
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-stepper-window-item>
      </v-stepper-window>
    </v-stepper>

    <v-snackbar v-model="snackbar.show" :timeout="3000">
      {{ snackbar.text }}
    </v-snackbar>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import TransacaoForm from '@/features/transaction/components/TransacaoForm.vue'
import ClientDataTable from '@/shared/components/ClientDataTable.vue'
import { useCattleStatus } from '@/shared/composables/useCattleStatus'
import { formatDate } from '@/shared/utils/formatters/dateFormatter'
import { useActiveCattle } from '@/features/cattle/composables/useActiveCattle';
import { useRegisterSale } from '../composables/useRegisterSale'

const { getColor } = useCattleStatus();


const snackbar = ref({ show: false, text: '' })

const { 
  fetchActiveCattle, 
  loading, 
  cattle,
  error: fetchCattleError
} = useActiveCattle();

const {
  form,
  saleItems,
  totalValueFormatted,
  submitting,
  error,
  createFromSelection,
  normalizePeso,
  normalizeValor,
  submit
} = useRegisterSale();

const step = ref(1)
const selected = ref([])
const headers = [
  { title: 'ID', key: 'id' },
  { title: 'Número', key: 'numero' },
  { title: 'Raça', key: 'raca' },
  { title: 'Estado do Boi', key: 'estadoBoi' },
  { title: 'Data Última Pesagem', key: 'dataUltimaPesagem' },
  { title: 'Último Peso', key: 'peso' },
];
const txFormRef = ref(null)

const itemHeaders = [
  { title: 'ID', key: 'boiId' },
  { title: 'Número', key: 'numero' },
  { title: 'Raça', key: 'raca' },
  { title: 'Valor (R$)', key: 'valor' },
  { title: 'Peso na Transação (kg)', key: 'peso' },
]


onMounted(async () => {
  await fetchActiveCattle()
})


function toStep2() {
  const hasItems = createFromSelection(selected.value, cattle.value)
  if(!hasItems){
    error.value = 'Seleção inválida. Tente selecionar novamente.'
    return
  }
  step.value = 2
}

async function handleConfirmVenda() {
  const ok = await txFormRef.value?.validate()
  if (!ok) return

  await submit(() => {
    snackbar.value = { show: true, text: 'Venda cadastrada com sucesso.' }
    step.value = 1
    selected.value = []
    fetchActiveCattle() 
  })
}

const canSubmit = computed(() => saleItems.value.length > 0)
</script>
