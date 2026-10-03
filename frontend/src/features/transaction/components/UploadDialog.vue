<template>
  <v-dialog v-model="model" max-width="900px">
    <v-card>
      <v-card-title class="text-h6">
        <v-icon class="mr-2">mdi-file-upload</v-icon>
        Upload de Tabela de Compra
      </v-card-title>

      <v-card-text>
        <v-file-input
          v-model="files"
          label="Selecione o arquivo"
          :accept="accept"
          prepend-icon="mdi-file-table"
          show-size
          chips
          :multiple="false"
          :rules="fileRules"
          clearable
        />

        <v-alert v-if="error" type="error" class="mt-4" dismissible @click:close="error = null">
          {{ error }}
        </v-alert>

        <v-alert type="info" class="mt-4" variant="tonal">
          <strong>Formatos aceitos:</strong> Excel (.xlsx, .xls) e CSV (.csv)<br>
          <strong>Colunas esperadas:</strong> data, fornecedor, numero, raca, valor_boi, peso_transacao, etc.
        </v-alert>

        <v-card class="mt-4" variant="outlined">
          <v-card-title class="text-subtitle-1 pb-2">
            <v-icon class="mr-2">mdi-download</v-icon>
            Template de Exemplo
          </v-card-title>
          <v-card-text class="pt-0">
            <p class="text-body-2 mb-3">Baixe um template com colunas corretas e dados de exemplo.</p>
            <v-btn color="primary" variant="outlined" prepend-icon="mdi-file-download" size="small" @click="$emit('download-template')">
              Baixar Template CSV
            </v-btn>
          </v-card-text>
        </v-card>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn text @click="close">Cancelar</v-btn>
        <v-btn color="primary" :disabled="!firstFile || loading" :loading="loading" @click="preview">
          Pré-visualizar
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  loading: { type: Boolean, default: false },
})
const emit = defineEmits(['update:modelValue', 'preview', 'download-template'])

const model = computed({
  get: () => props.modelValue,
  set: (v) => emit('update:modelValue', v),
})

const files = ref(null)           
const error = ref(null)

const accept = '.csv,text/csv,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'

const allowedTypes = new Set([
  'text/csv',
  'application/csv',
  'application/vnd.ms-excel',
  'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
])


const firstFile = computed(() => {
  const v = files.value
  return Array.isArray(v) ? v[0] : v // se vier array, pega o primeiro; senão, usa o próprio File
})



const fileRules = [
  (value) => {
    const f = Array.isArray(value) ? value[0] : value
    if (!f) return 'Selecione um arquivo'
    return allowedTypes.has(f.type) || /\.(csv|xlsx|xls)$/i.test(f.name || '') || 'Apenas CSV ou Excel são suportados'
  },
  (value) => {
    const f = Array.isArray(value) ? value[0] : value
    if (!f) return true
    return f.size < 10 * 1024 * 1024 || 'O arquivo deve ter menos de 10MB'
  },
]

const close = () => {
  model.value = false
  files.value = []
  error.value = null
}

const preview = () => {
  const f = firstFile.value
  if (!f) return
  emit('preview', f)  // emite somente o primeiro File
}
</script>
