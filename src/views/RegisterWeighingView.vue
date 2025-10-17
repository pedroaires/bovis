<template>
  <v-container>
    <v-stepper v-model="step">
      <v-stepper-header>
        <v-stepper-item :value="1" title="Selecionar bois" />
        <v-stepper-item :value="2" title="Dados da pesagem" />
      </v-stepper-header>

      <v-stepper-window>
        <v-stepper-window-item :value="1">
          <v-card>
            <v-card-text>
              <CattleList
                title="Selecionar bois para pesagem"
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

              <v-row>
                <v-col cols="12" sm="4">
                  <v-text-field
                    v-model="weighingDate"
                    label="Data da Pesagem"
                    type="date"
                    variant="outlined"
                    :max="today"
                  />
                </v-col>
              </v-row>

              <div class="mt-6">
                <div class="text-subtitle-2 mb-2">
                  Bois selecionados (informe os dados da pesagem):
                </div>

                <v-data-table
                  :headers="itemHeaders"
                  :items="weighingItems"
                  item-key="boiId"
                  :items-per-page="-1"
                  hide-default-footer
                >
                  <template #[`item.peso`]="{ item }">
                    <v-text-field
                      v-model="item.pesoStr"
                      density="compact"
                      variant="outlined"
                      inputmode="decimal"
                      hide-details
                      placeholder="Ex: 450,5"
                      @blur="normalizePeso(item)"
                    />
                  </template>

                  <template #[`item.descricao`]="{ item }">
                    <v-text-field
                      v-model="item.descricao"
                      density="compact"
                      variant="outlined"
                      hide-details
                      placeholder="Opcional"
                    />
                  </template>

                  <template #[`item.medicacoes`]="{ item }">
                    <v-combobox
                      v-model="item.medicacoes"
                      chips
                      multiple
                      deletable-chips
                      density="compact"
                      variant="outlined"
                      hide-details
                      placeholder="Digite e pressione Enter"
                    />
                  </template>
                </v-data-table>
              </div>
            </v-card-text>

            <v-card-actions>
              <v-btn variant="text" @click="step = 1">Voltar</v-btn>
              <v-spacer />
              <v-btn
                color="primary"
                :loading="submitting"
                :disabled="!canSubmit"
                @click="submitWeighing"
              >
                Confirmar Pesagem
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-stepper-window-item>
      </v-stepper-window>
    </v-stepper>

    <v-snackbar v-model="snackbar.show" :timeout="3000" color="success">
      {{ snackbar.text }}
    </v-snackbar>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import CattleList from '@/components/templates/CattleList.vue'
import api from '@/services/api.js'

const step = ref(1)
const loading = ref(false)
const cattle = ref([])
const selected = ref([]) // IDs dos bois selecionados

const submitting = ref(false)
const snackbar = ref({ show: false, text: '' })
const errorMsg = ref('')
const weighingItems = ref([])
const weighingDate = ref(new Date().toISOString().slice(0, 10))
const today = new Date().toISOString().slice(0, 10)

const itemHeaders = [
  { title: 'ID', key: 'boiId', sortable: false },
  { title: 'Número', key: 'numero', sortable: false },
  { title: 'Raça', key: 'raca', sortable: false },
  { title: 'Peso (kg)', key: 'peso', sortable: false, width: '150px' },
  { title: 'Descrição', key: 'descricao', sortable: false, width: '200px' },
  { title: 'Medicações Aplicadas', key: 'medicacoes', sortable: false },
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
    errorMsg.value = 'Falha ao buscar os dados do gado.'
  } finally {
    loading.value = false
  }
}

function toStep2() {
  const sel = Array.isArray(selected.value) ? selected.value.filter(Boolean) : []
  const byId = new Map(cattle.value.map(x => [x.id, x]))

  weighingItems.value = sel.map(s => {
    const c = (typeof s === 'object' && s !== null) ? s : byId.get(s)
    if (!c) return null
    return {
      boiId: c.id,
      numero: c.numero,
      raca: c.raca,
      peso: null,
      pesoStr: '',
      descricao: '',
      medicacoes: [],
    }
  }).filter(Boolean)

  if (!weighingItems.value.length) {
    errorMsg.value = 'Seleção inválida. Tente selecionar novamente.'
    step.value = 1
    return
  }

  step.value = 2
}

function parseDecimal(s) {
  if (s === null || s === undefined || String(s).trim() === '') return null
  const n = Number(String(s).replace(',', '.'))
  return Number.isFinite(n) && n > 0 ? n : null
}

function normalizePeso(item) {
  item.peso = parseDecimal(item.pesoStr)
  item.pesoStr = item.peso != null ? String(item.peso).replace('.', ',') : ''
}

const canSubmit = computed(() => {
  if (!weighingDate.value || weighingItems.value.length === 0) {
    return false
  }
  // Garante que todos os bois selecionados tenham um peso válido
  return weighingItems.value.every(item => item.peso !== null && item.peso > 0)
})

async function submitWeighing() {
  if (!canSubmit.value) return

  try {
    submitting.value = true
    errorMsg.value = ''

    const pesagens = weighingItems.value.map(it => ({
      boiId: it.boiId,
      peso: it.peso,
      descricao: it.descricao || null,
      data: weighingDate.value,
      medicacoes: it.medicacoes,
    }))
    console.log(pesagens);
    await api.createWeighingBatch({pesagens: pesagens})
    

    snackbar.value = { show: true, text: 'Pesagem(ns) cadastrada(s) com sucesso!' }
    resetForm()
    await fetchCattle()

  } catch (e) {
    console.error('createWeighing error:', e?.response?.data || e)
    errorMsg.value = e?.response?.data?.message || 'Falha ao cadastrar a pesagem.'
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  step.value = 1
  selected.value = []
  weighingItems.value = []
  weighingDate.value = new Date().toISOString().slice(0, 10)
}
</script>