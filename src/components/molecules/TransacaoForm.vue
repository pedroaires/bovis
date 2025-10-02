<template>
  <v-form ref="formRef" v-model="formValid" class="mb-6">
    <div class="grid md:grid-cols-3 gap-4">
      <v-text-field
        v-model="dataProxy"
        label="Data"
        type="date"
        :rules="[rules.req]"
      />
      <v-text-field
        v-model="contraParteProxy"
        :label="contraParteLabel"
        :rules="[rules.req]"
      />
      <v-text-field
        v-model="valorTotalProxy"
        label="Valor Total (R$)"
        inputmode="decimal"
        :rules="[rules.req, rules.pos]"
        :hint="valorTotalHint"
        persistent-hint
      />
      <v-text-field
        v-model="descricaoProxy"
        label="Descrição"
        class="md:col-span-3"
      />
      <v-select
        v-if="mostrarModeloTransacao"
        v-model="modeloTransacaoProxy"
        :items="['PESO','UNITARIO']"
        label="Modelo de Transação"
        class="md:col-span-3"
      />
    </div>
  </v-form>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({ data: '', descricao: '', modeloTransacao: 'PESO', contraParte: '', valorTotal: '' }),
  },
  contraParteLabel: { type: String, default: 'Comprador' },
  mostrarModeloTransacao: { type: Boolean, default: false },
  valorTotalHint: { type: String, default: 'Se vazio, soma os itens' },
  rules: {
    type: Object,
    default: () => ({
      req: v => (!!v || v === 0) || 'Obrigatório',
      pos: v => (v === '' || Number(String(v).replace(',', '.')) > 0) || 'Deve ser > 0',
    }),
  },
})
const emit = defineEmits(['update:modelValue'])

function updateField(partial) {
  emit('update:modelValue', { ...props.modelValue, ...partial })
}

// proxies por campo (sem watchers)
const dataProxy = computed({
  get: () => props.modelValue.data,
  set: v => updateField({ data: v }),
})
const descricaoProxy = computed({
  get: () => props.modelValue.descricao,
  set: v => updateField({ descricao: v }),
})
const modeloTransacaoProxy = computed({
  get: () => props.modelValue.modeloTransacao,
  set: v => updateField({ modeloTransacao: v }),
})
const contraParteProxy = computed({
  get: () => props.modelValue.contraParte,
  set: v => updateField({ contraParte: v }),
})
const valorTotalProxy = computed({
  get: () => props.modelValue.valorTotal,
  set: v => updateField({ valorTotal: v }),
})

const formRef = ref(null)
const formValid = ref(false)

async function validate() {
  const ok = await formRef.value?.validate()
  return !!ok?.valid
}
defineExpose({ validate })
</script>
