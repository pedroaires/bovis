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
                @preview="handleUpload"
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
                      :editable="['numero','raca','peso','valorUnitario']"
                      @update:data="onPreviewEdited"
                    />
                    <div class="text-caption mt-2">
                      Campos por item: <b>numero</b>, <b>raca</b>, <b>peso</b>, <b>valorUnitario</b>.
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
              <v-alert
                v-if="submitError"
                type="error"
                class="mb-4"
                dismissible
                @click:close="submitError = null"
              >
                {{ submitError }}
              </v-alert>

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
                  :editable="['numero','raca','peso','valorUnitario']"
                  @update:data="onPreviewEdited"
                />

                <div class="text-right mt-4 text-body-2">
                  Total dos itens (R$): <b>{{ valorTotalItens }}</b>
                </div>
              </div>
            </v-card-text>

            <v-card-actions>
              <v-btn variant="text" @click="step = 1">Voltar</v-btn>
              <v-spacer />
              <v-btn
                color="primary"
                :loading="uploading"
                :disabled="!canConfirm"
                @click="confirmUpload"
              >
                Confirmar Compra
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-stepper-window-item>
      </v-stepper-window>
    </v-stepper>

    <v-snackbar v-model="snackbar.show" :timeout="4000" :color="snackbar.color">
      {{ snackbar.text }}
    </v-snackbar>
  </v-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import UploadDialog from '@/features/transaction/components/UploadDialog.vue'
import PreviewTable from '@/features/transaction/components/EditableTable.vue'
import TransacaoForm from '@/features/transaction/components/TransacaoForm.vue'
import { usePurchaseUpload } from '../composables/useRegisterPurchase'

const {
  expectedItemColumns,
  previewRows,
  totalRows,
  parseError,
  submitError,
  uploading,

  previewReady,
  valorTotalItens,
  canSubmit,

  onPreviewFile,
  onPreviewEdited,
  onDownloadTemplate,
  submitCompra,
  reset
} = usePurchaseUpload()

// Stepper e UI
const step = ref(1)
const showUpload = ref(false)
const snackbar = ref({ show: false, text: '', color: 'success' })

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

const canGoStep2 = computed(() => canSubmit.value)

const canConfirm = computed(() => {
  return canSubmit.value && 
         form.value.data !== '' && 
         form.value.contraParte !== ''
})

const handleUpload = async (file) => {
  await onPreviewFile(file)

  if (!parseError.value) {
    showUpload.value = false
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
    const result = await submitCompra(form.value)
    snackbar.value = { 
      show: true, 
      text: result.message, 
      color: 'success' 
    }
    resetAll()
  } catch (e) {
    snackbar.value = { 
      show: true, 
      text: submitError.value || 'Falha ao criar compra. Verifique os dados.', 
      color: 'error' 
    }
  }
}

function resetAll() {
  step.value = 1
  form.value = { 
    data: '', 
    descricao: '', 
    modeloTransacao: 'PESO', 
    contraParte: '', 
    valorTotal: '' 
  }
  showUpload.value = false
  reset()
}
</script>