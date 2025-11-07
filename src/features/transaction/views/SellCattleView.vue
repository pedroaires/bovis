<template>
  <v-container>
    <v-stepper v-model="step">
      <v-stepper-header>
        <v-stepper-item :value="1" title="Selecionar bois" />
        <v-stepper-item :value="2" title="Dados da transação" />
      </v-stepper-header>

      <v-stepper-window>
        <!-- Passo 1: Seleção -->
        <v-stepper-window-item :value="1">
          <v-card>
            <v-card-text>
              <CattleList
                title="Selecionar bois para venda"
                :items="cattle"
                :loading="loading"
                selectable
                v-model="selected"
                filter-estado="NA_FAZENDA"
              >
                <template #actions>
                  <div class="mt-4 text-right">
                    <v-btn color="primary" :disabled="!selected.length" @click="toStep2">
                      Continuar
                    </v-btn>
                  </div>
                </template>
              </CattleList>
            </v-card-text>
          </v-card>
        </v-stepper-window-item>

        <!-- Passo 2: Formulário + Itens -->
        <v-stepper-window-item :value="2">
          <v-card>
            <v-card-text>
              <v-alert
                v-if="errorMsg"
                type="error"
                class="mb-4"
                dismissible
                @click:close="errorMsg = ''"
              >
                {{ errorMsg }}
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
                  :items="sellItems"
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
                    Soma dos itens (R$): <b>{{ sumItemsFormatted }}</b>
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
                @click="submitVenda"
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
import CattleList from '@/shared/components/CattleList.vue'
import api from '@/services/api.js'
import TransacaoForm from '@/features/transaction/components/TransacaoForm.vue'

const step = ref(1)
const loading = ref(false)
const cattle = ref([])
const selected = ref([])

const submitting = ref(false)
const snackbar = ref({ show: false, text: '' })
const errorMsg = ref('')
const sellItems = ref([])

const form = ref({
  data: '',
  descricao: '',
  modeloTransacao: 'PESO',
  contraParte: '',
  valorTotal: '',
})

const txFormRef = ref(null)

const itemHeaders = [
  { title: 'ID', key: 'boiId' },
  { title: 'Número', key: 'numero' },
  { title: 'Raça', key: 'raca' },
  { title: 'Valor (R$)', key: 'valor' },
  { title: 'Peso na Transação (kg)', key: 'peso' },
]


onMounted(async () => {
  await fetchCattle()
})

async function fetchCattle() {
  loading.value = true
  try {
    const resp = await api.getActiveCattleWithWeight()
    cattle.value = resp.data ?? []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function toStep2() {
  const sel = Array.isArray(selected.value) ? selected.value.filter(Boolean) : []
  const byId = new Map(cattle.value.map(x => [x.id, x]))
  sellItems.value = sel.map(s => {
    const c = (typeof s === 'object' && s !== null) ? s : byId.get(s)
    if (!c) return null
    return {
      boiId: c.id,
      numero: c.numero,
      raca: c.raca,
      valor: null,
      peso: c.peso ?? null,
      valorStr: '',
      pesoStr: c.peso != null ? String(c.peso) : '',
    }
  }).filter(Boolean)

  if (!sellItems.value.length) {
    errorMsg.value = 'Seleção inválida. Tente selecionar novamente.'
    return
  }

  step.value = 2
}
function parseDecimal(s) {
  if (s === null || s === undefined || s === '') return null
  const n = Number(String(s).replace(',', '.'))
  return Number.isFinite(n) ? n : null
}

function normalizeValor(item) {
  item.valor = parseDecimal(item.valorStr)
  item.valorStr = item.valor != null ? String(item.valor) : ''
}

function normalizePeso(item) {
  item.peso = parseDecimal(item.pesoStr)
  item.pesoStr = item.peso != null ? String(item.peso) : ''
}

const sumItems = computed(() =>
  sellItems.value.reduce((acc, it) => acc + (it.valor ?? 0), 0)
)
const sumItemsFormatted = computed(() => sumItems.value.toFixed(2))

const canSubmit = computed(() => sellItems.value.length > 0)

async function submitVenda() {
  const ok = await txFormRef.value?.validate()
  if (!ok) return

  try {
    submitting.value = true
    errorMsg.value = ''

    const total = form.value.valorTotal !== ''
      ? parseDecimal(form.value.valorTotal)
      : sumItems.value

    const vendaBoiDTOS = sellItems.value.map(it => ({
      boiId: it.boiId,
      valor: it.valor,
      pesagemDTO: {
        peso: it.peso
      },
    }))

    const payload = {
      valor: total,
      data: String(form.value.data).slice(0, 10),
      descricao: form.value.descricao ?? '',
      modeloTransacao: form.value.modeloTransacao,
      contraParte: form.value.contraParte,
      vendaBoiDTOS,
    }

    await api.createVenda(payload)
    snackbar.value = { show: true, text: 'Venda cadastrada com sucesso.' }

    // reset e recarrega a lista
    step.value = 1
    selected.value = []
    sellItems.value = []
    form.value = { data: '', descricao: '', modeloTransacao: 'PESO', contraParte: '', valorTotal: '' }
    await fetchCattle()
  } catch (e) {
    console.error('createVenda error:', e?.response?.data || e)
    errorMsg.value = e?.response?.data?.message || 'Falha ao cadastrar a venda.'
  } finally {
    submitting.value = false
  }
}
</script>
