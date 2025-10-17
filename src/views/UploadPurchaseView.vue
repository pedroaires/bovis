<template>
  <v-container>
    <v-stepper v-model="step">
      <v-stepper-header>
        <v-stepper-item :value="1" title="Selecionar arquivo e pré-visualizar" />
        <v-stepper-item :value="2" title="Dados da transação e itens" />
      </v-stepper-header>

      <v-stepper-window>
        <!-- Passo 1: Upload + Preview -->
        <v-stepper-window-item :value="1">
          <v-card>
            <v-card-text>
              <v-alert
                v-if="parseError"
                type="error"
                class="mb-4"
                dismissible
                @click:close="parseError = null"
              >
                {{ parseError }}
              </v-alert>

              <div class="mb-4">
                <v-btn color="primary" prepend-icon="mdi-file-upload" @click="showUpload = true">
                  Selecionar arquivo
                </v-btn>
                <v-btn class="ml-2" variant="text" @click="onDownloadTemplate">
                  Baixar template CSV
                </v-btn>
              </div>

              <UploadDialog
                :model-value="showUpload"
                :loading="uploading"
                @update:modelValue="showUpload = $event"
                @preview="onPreviewFile"
                @download-template="onDownloadTemplate"
              />

              <v-card v-if="previewReady" variant="tonal" class="mt-2">
                <v-card-title class="text-subtitle-1">
                  <v-icon class="mr-2">mdi-eye</v-icon>
                  Pré-visualização
                </v-card-title>
                <v-card-text>
                  <div v-if="previewRows.length">
                    <PreviewTable
                      :data="previewRows"
                      :expected="expectedItemColumns"
                      :total="totalRows"
                      :editable="['numero','raca','pesoNaTransacao','valorUnitario']"
                      @update:data="onPreviewEdited"
                    />
                    <div class="text-caption mt-2">
                      Campos por item: <b>numero</b>, <b>raca</b>, <b>pesoNaTransacao</b>, <b>valorUnitario</b>.
                      <em>estadoBoi</em> será enviado como <b>NA_FAZENDA</b>.
                    </div>
                  </div>
                  <div v-else class="text-body-2">Nenhum dado para exibir.</div>
                </v-card-text>
              </v-card>
            </v-card-text>

            <v-card-actions>
              <v-spacer />
              <v-btn
                color="primary"
                :disabled="!canGoStep2"
                @click="toStep2"
              >
                Continuar
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-stepper-window-item>

        <!-- Passo 2: Formulário + Itens + Confirmar -->
        <v-stepper-window-item :value="2">
          <v-card>
            <v-card-text>
              <TransacaoForm
                ref="txFormRef"
                v-model="form"
                :contra-parte-label="'Fornecedor'"
                :mostrar-modelo-transacao="false"
                :rules="rules"
              />

              <div class="mt-6">
                <div class="text-subtitle-2 mb-2">
                  Itens da compra (edite se necessário):
                </div>

                <PreviewTable
                  :data="previewRows"
                  :expected="expectedItemColumns"
                  :total="totalRows"
                  :editable="['numero','raca','pesoNaTransacao','valorUnitario']"
                  @update:data="onPreviewEdited"
                />

                <div class="text-right mt-4 text-body-2">
                  Total dos itens (R$): <b>{{ totalItensFormatado }}</b>
                </div>
              </div>
            </v-card-text>

            <v-card-actions>
              <v-btn variant="text" @click="step = 1">Voltar</v-btn>
              <v-spacer />
              <v-btn
                color="primary"
                :loading="uploading"
                :disabled="!canConfirmComputed"
                @click="confirmUpload"
              >
                Confirmar Compra
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-stepper-window-item>
      </v-stepper-window>
    </v-stepper>

    <v-snackbar v-model="snackbar.show" :timeout="4000">
      {{ snackbar.text }}
    </v-snackbar>
  </v-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import * as XLSX from 'xlsx'
import UploadDialog from '@/components/organisms/UploadDialog.vue'
import PreviewTable from '@/components/molecules/EditableTable.vue'
import TransacaoForm from '@/components/molecules/TransacaoForm.vue'
import api from '@/services/api'

// Stepper e UI
const step = ref(1)
const showUpload = ref(false)
const uploading = ref(false)
const snackbar = ref({ show: false, text: '' })
const parseError = ref(null)

// Preview
const previewRows = ref([])
const fullData = ref([])
const totalRows = ref(0)

// Formulário (TransacaoForm)
const txFormRef = ref(null)
const form = ref({
  data: '',
  descricao: '',
  modeloTransacao: 'PESO',
  contraParte: '',
  valorTotal: '',
})
const rules = {
  req: v => (!!v || v === 0) || 'Obrigatório',
  pos: v => (v === '' || Number(v) > 0) || 'Deve ser > 0',
}

// Colunas esperadas
const expectedItemColumns = ['numero','raca','pesoNaTransacao','valorUnitario']

// Estados computados
const previewReady = computed(() => parseError.value !== null || fullData.value.length > 0)
const canGoStep2 = computed(() => !parseError.value && fullData.value.length > 0)
const canConfirmComputed = computed(() => !parseError.value && fullData.value.length > 0)

// Total (client-side) para exibição
const totalItens = computed(() =>
  fullData.value.reduce((acc, it) => acc + (numOrNull(it?.valorUnitario) ?? 0), 0)
)
const totalItensFormatado = computed(() => totalItens.value.toFixed(2))

// Ações Passo 1
const onPreviewFile = async (file) => {
  parseError.value = null
  previewRows.value = []
  fullData.value = []
  totalRows.value = 0

  try {
    const { rows, all } = await parseFileXlsx(file)
    const cols = all.length ? Object.keys(all[0]) : []
    const missing = expectedItemColumns.filter(c => !cols.includes(c))
    if (missing.length) {
      throw new Error(`Colunas ausentes no arquivo: ${missing.join(', ')}`)
    }
    fullData.value = all
    previewRows.value = rows.slice(0, 100)
    totalRows.value = all.length
    showUpload.value = false
  } catch (e) {
    parseError.value = e?.message || 'Falha ao processar o arquivo.'
    showUpload.value = false
  }
}

async function parseFileXlsx(file) {
  if (!(file instanceof File)) throw new Error('Arquivo inválido.')
  const buf = await file.arrayBuffer()
  const wb = XLSX.read(buf, { type: 'array' })
  const sheetName = wb.SheetNames?.[0]
  if (!sheetName) return { rows: [], all: [] }
  const ws = wb.Sheets[sheetName]
  const json = XLSX.utils.sheet_to_json(ws, { defval: '' })
  const norm = json.map(normalizeKeys)
  return { rows: norm.slice(0, 100), all: norm }
}
function normalizeKeys(row) {
  const out = {}
  for (const k of Object.keys(row)) out[String(k).trim()] = row[k]
  return out
}

function onPreviewEdited(editedPreview) {
  // Mantém os 100 primeiros sincronizados
  previewRows.value = editedPreview
  for (let i = 0; i < editedPreview.length; i++) {
    if (!fullData.value[i]) fullData.value[i] = {}
    Object.assign(fullData.value[i], editedPreview[i])
  }
}

function toStep2() {
  if (!canGoStep2.value) return
  step.value = 2
}

const confirmUpload = async () => {
  const ok = await txFormRef.value?.validate()
  if (!ok) return

  try {
    uploading.value = true

    const compraBoiDTOS = fullData.value.map(it => ({
      boiRequestDTO: {
        numero: intOrNull(it.numero),
        raca: (it.raca ?? '').trim(),
        estadoBoi: 'NA_FAZENDA',
      },
      valor: numOrNull(it.valorUnitario),
      pesoNaTransacao: numOrNull(it.pesoNaTransacao),
    }))

    const total =
      form.value.valorTotal !== ''
        ? numOrNull(form.value.valorTotal)
        : compraBoiDTOS.reduce((acc, x) => acc + (x.valor ?? 0), 0)

    const payload = {
      valor: total,
      data: String(form.value.data).slice(0, 10),
      descricao: form.value.descricao ?? '',
      modeloTransacao: form.value.modeloTransacao,
      contraParte: form.value.contraParte ?? '', // Fornecedor
      compraBoiDTOS,
    }

    await api.createCompra(payload)
    snackbar.value = { show: true, text: 'Upload realizado com sucesso.' }
    resetAll()
  } catch (e) {
    console.error('createCompra error:', e?.response?.data || e)
    snackbar.value = { show: true, text: 'Falha no upload. Verifique os dados.' }
  } finally {
    uploading.value = false
  }
}

function numOrNull(v) {
  if (v === null || v === undefined || v === '') return null
  const n = Number(String(v).replace(',', '.'))
  return Number.isFinite(n) ? n : null
}
function intOrNull(v) {
  if (v === null || v === undefined || v === '') return null
  const n = parseInt(String(v).trim(), 10)
  return Number.isFinite(n) ? n : null
}
function resetAll() {
  step.value = 1
  fullData.value = []
  previewRows.value = []
  totalRows.value = 0
  parseError.value = null
  form.value = { data: '', descricao: '', modeloTransacao: 'PESO', contraParte: '', valorTotal: '' }
  showUpload.value = false
}
const onDownloadTemplate = () => {
  const csv = [
    'numero,raca,pesoNaTransacao,valorUnitario',
    '12345,Nelore,450.0,3500.00',
  ].join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'template_itens_compra.csv'
  a.click()
  URL.revokeObjectURL(url)
}
</script>
