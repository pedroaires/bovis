<template>
  <v-dialog :model-value="modelValue" @update:model-value="emit('update:modelValue', $event)" max-width="700px" persistent>
    <v-card>
      <v-card-title class="d-flex align-center justify-space-between bg-secondary">
        <span class="text-h6">
          Editar Transação #{{ form.id }}
        </span>
        <v-btn
          icon="mdi-close"
          variant="text"
          @click="handleClose"
        />
      </v-card-title>

      <v-card-text class="pt-6">
        <v-form ref="formRef" v-model="formValid">
          <v-row>
            <!-- Data -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.data"
                label="Data"
                type="date"
                :rules="[v => !!v || 'Data é obrigatória']"
                variant="outlined"
                density="comfortable"
              />
            </v-col>

            <!-- Operação -->
            <v-col cols="12" md="6">
              <v-select
                v-model="form.operacao"
                label="Operação"
                :items="['COMPRA', 'VENDA']"
                :rules="[v => !!v || 'Operação é obrigatória']"
                variant="outlined"
                density="comfortable"
                readonly
                disabled
              />
            </v-col>

            <!-- Modelo de Transação -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.modeloTransacao"
                label="Modelo de Transação"
                :rules="[v => !!v || 'Modelo é obrigatório']"
                variant="outlined"
                density="comfortable"
              />
            </v-col>

            <!-- Contraparte -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.contraParte"
                :label="form.operacao === 'COMPRA' ? 'Fornecedor' : 'Comprador'"
                variant="outlined"
                density="comfortable"
              />
            </v-col>

            <!-- Valor Total -->
            <v-col cols="12" md="6">
              <v-text-field
                v-model="form.valor"
                label="Valor Total"
                type="number"
                step="0.01"
                prefix="R$"
                :rules="[
                  v => v != null && v !== '' || 'Valor é obrigatório',
                  v => v >= 0 || 'Valor deve ser positivo'
                ]"
                variant="outlined"
                density="comfortable"
              />
            </v-col>

            <!-- Descrição -->
            <v-col cols="12">
              <v-textarea
                v-model="form.descricao"
                label="Descrição"
                rows="3"
                variant="outlined"
                density="comfortable"
              />
            </v-col>
          </v-row>
        </v-form>

        <!-- Mensagens de erro/sucesso -->
        <v-alert v-if="saveError" type="error" variant="tonal" class="mt-4">
          {{ saveError }}
        </v-alert>
        <v-alert v-if="saveSuccess" type="success" variant="tonal" class="mt-4">
          Transação atualizada com sucesso!
        </v-alert>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          variant="text"
          @click="handleClose"
          :disabled="saving"
        >
          Cancelar
        </v-btn>
        <v-btn
          color="primary"
          variant="elevated"
          @click="handleSave"
          :loading="saving"
          :disabled="!formValid"
        >
          Salvar
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useTransactionEdit } from '../composables/useTransactionEdit';

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

const emit = defineEmits(['update:modelValue', 'saved']);

const { saving, saveError, saveSuccess, saveTransaction, resetState } = useTransactionEdit();

const formRef = ref(null);
const formValid = ref(false);

const form = ref({
  id: null,
  data: '',
  operacao: '',
  modeloTransacao: '',
  contraParte: '',
  valor: null,
  descricao: '',
});

// Preenche o formulário quando a transação muda
watch(() => props.transaction, (newTransaction) => {
  if (newTransaction) {
    form.value = {
      id: newTransaction.id,
      data: newTransaction.data,
      operacao: newTransaction.operacao,
      modeloTransacao: newTransaction.modeloTransacao,
      contraParte: newTransaction.contraParte || '',
      valor: newTransaction.valor,
      descricao: newTransaction.descricao || '',
    };
  }
}, { immediate: true });

watch(() => props.modelValue, (isOpen) => {
  if (isOpen) {
    resetState();
  }
});

async function handleSave() {
  const { valid } = await formRef.value.validate();
  if (!valid) return;

  const payload = {
    data: form.value.data,
    modeloTransacao: form.value.modeloTransacao,
    contraParte: form.value.contraParte || null,
    valor: parseFloat(form.value.valor),
    descricao: form.value.descricao || null,
  };

  const result = await saveTransaction(form.value.id, payload);

  if (result.success) {
    
    emit('saved', { payload });

    setTimeout(() => {
      emit('update:modelValue', false);
    }, 1500);
  }
}

function handleClose() {
  resetState();
  emit('update:modelValue', false);
}
</script>