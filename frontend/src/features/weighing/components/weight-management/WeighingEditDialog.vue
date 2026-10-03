<template>
    <v-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" max-width="500px" persistent>
        <v-card>
            <v-card-title class="d-flex align-center justify-space-between bg-warning">
                <span class="text-h6">
                    Editar Pesagem do Boi #{{ pesagemForm.numeroBoi }}
                </span>
                <v-btn
                    icon="mdi-close"
                    variant="text"
                    @click="closeDialog"
                />
            </v-card-title>

            <v-card-text class="pt-6">
                <v-form ref="editFormRef" v-model="formValid">
                    <v-row>
                        <!-- Data -->
                        <v-col cols="12" md="6">
                            <v-text-field
                                v-model="pesagemForm.dataPesagem"
                                label="Data da Pesagem"
                                type="date"
                                :rules="[v => !!v || 'Data é obrigatória']"
                                variant="outlined"
                                density="comfortable"
                            />
                        </v-col>

                        <!-- Peso -->
                        <v-col cols="12" md="6">
                            <v-text-field
                                v-model.number="pesagemForm.peso"
                                label="Peso (kg)"
                                type="number"
                                step="0.1"
                                :rules="[
                                    v => v != null && v !== '' || 'Peso é obrigatório',
                                    v => v > 0 || 'Peso deve ser positivo'
                                ]"
                                variant="outlined"
                                density="comfortable"
                            />
                        </v-col>
                    </v-row>
                </v-form>

                <!-- Mensagens de erro/sucesso -->
                <v-alert v-if="error" type="error" variant="tonal" class="mt-4">
                    {{ error }}
                </v-alert>
                <v-alert v-if="success" type="success" variant="tonal" class="mt-4">
                    Pesagem atualizada com sucesso!
                </v-alert>
            </v-card-text>

            <v-card-actions>
                <v-spacer />
                <v-btn
                    variant="text"
                    @click="closeDialog"
                    :disabled="loading"
                >
                    Cancelar
                </v-btn>
                <v-btn
                    color="warning"
                    variant="flat"
                    @click="savePesagem"
                    :loading="loading"
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
import { useEditWeighing } from '../../composables/useEditWeighing';
const {
    pesagemForm,
    loading,
    error,
    success,
    populateForm,
    resetMessages,
    updateWeighing
} = useEditWeighing();

const props = defineProps({
    modelValue: {
        type: Boolean,
        default: false,
    },
    pesagemData: {
        type: Object,
        default: () => ({}),
    },
})
    
const emit = defineEmits(['update:modelValue', 'close', 'pesagem-updated'])

const editFormRef = ref(null)
const formValid = ref(false)

watch(() => props.pesagemData, (newVal) => {
    populateForm(newVal);
    },
    {
        immediate: true,
        deep: true
    }
)
watch(() => props.modelValue, (newVal) => {
    if (!newVal) {
        resetMessages();
    }
})
async function savePesagem() {
    const { valid } = await editFormRef.value.validate()
    if (!valid) return;
    const updated = await updateWeighing(props.pesagemData.boiId);
    if (updated){
        emit('pesagem-updated');
        setTimeout(closeDialog, 1000); 
    }
}
function closeDialog() {
    emit('update:modelValue', false);
    emit('close');
}
</script>
