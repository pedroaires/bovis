<template>
  <div class="space-y-4">
    <h1 class="text-h5">Cadastro de Compra (Upload)</h1>


    <v-btn color="primary" prepend-icon="mdi-file-upload" @click="showUpload = true">
      Selecionar arquivo
    </v-btn>

    <UploadDialog
      :model-value="showUpload"
      :loading="uploading"
      @update:modelValue="showUpload = $event"
      @preview="onPreviewFile"
      @download-template="onDownloadTemplate"
    />

    <v-card v-if="previewReady" class="mt-4">
      <v-card-title class="text-h6">
        <v-icon class="mr-2">mdi-eye</v-icon>
        Pré-visualização dos Dados
      </v-card-title>
      <v-card-text>
        <v-alert v-if="parseError" type="error" class="mb-4" dismissible @click:close="parseError = null">
          {{ parseError }}
        </v-alert>

        <div v-if="previewRows.length">
          <PreviewTable
            :data="previewRows"
            :expected="expectedColumns"
            :total="totalRows"
            :editable="[]"            
            @update:data="onPreviewEdited"
          />

        </div>
        <div v-else class="text-body-2">Nenhum dado para exibir.</div>
      </v-card-text>
      <v-card-actions>
        <v-spacer />
        <v-btn text @click="resetUpload">Trocar arquivo</v-btn>
        <v-btn color="primary" :disabled="!canConfirm" :loading="uploading" @click="confirmUpload">
          Confirmar Upload
        </v-btn>
      </v-card-actions>
    </v-card>

    <v-snackbar v-model="snackbar.show" :timeout="4000">
      {{ snackbar.text }}
    </v-snackbar>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import * as XLSX from 'xlsx'
import UploadDialog from '@/components/organisms/UploadDialog.vue'
import PreviewTable from '@/components/molecules/EditableTable.vue'
import api from '@/services/api'

const showUpload = ref(false)
const uploading = ref(false)
const snackbar = ref({ show: false, text: '' })

const previewRows = ref([])   // []
const fullData = ref([])      // []
const totalRows = ref(0)
const parseError = ref(null)

const expectedColumns = ['data','fornecedor','numero','raca','valor_boi','peso_transacao']
const previewReady = computed(() => parseError.value !== null || fullData.value.length > 0)
const canConfirm   = computed(() => !parseError.value && fullData.value.length > 0)

const onPreviewFile = async (file) => {
  parseError.value = null
  previewRows.value = []
  fullData.value = []
  totalRows.value = 0

  try {
    const { rows, all } = await parseFileXlsx(file)
    fullData.value = all
    previewRows.value = rows
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
  const json = XLSX.utils.sheet_to_json(ws, { defval: '' }) // Array<Object>
  const norm = json.map(normalizeKeys)
  return { rows: norm.slice(0, 100), all: norm }
}

function normalizeKeys(row) {
  const out = {}
  for (const k of Object.keys(row)) out[String(k).trim()] = row[k]
  return out
}

const confirmUpload = async () => {
  try {
    uploading.value = true
    await api.post('/purchases/upload', { rows: fullData.value })
    snackbar.value = { show: true, text: 'Upload realizado com sucesso.' }
    resetUpload(true)
  } catch {
    snackbar.value = { show: true, text: 'Falha no upload. Verifique os dados.' }
  } finally {
    uploading.value = false
  }
}

const resetUpload = (clear = false) => {
  if (clear) {
    fullData.value = []
    previewRows.value = []
    totalRows.value = 0
  }
  parseError.value = null
  showUpload.value = true
}

const onDownloadTemplate = () => {
  const csv = [
    'data,fornecedor,numero,raca,valor_boi,peso_transacao',
    '2025-09-01,Fornecedor Exemplo,12345,Nelore,3500.00,450.0',
  ].join('\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'template_compra.csv'
  a.click()
  URL.revokeObjectURL(url)
}
function onPreviewEdited(editedPreview) {
  // atualiza a prévia
  previewRows.value = editedPreview

  // propaga para o dataset completo nas mesmas linhas da prévia
  for (let i = 0; i < editedPreview.length; i++) {
    // garante objeto existente
    if (!fullData.value[i]) fullData.value[i] = {}
    Object.assign(fullData.value[i], editedPreview[i])
  }
}

</script>
