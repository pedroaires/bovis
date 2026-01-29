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
import api from '@/services/api'; 
import { ref, watch } from 'vue';

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
const pesagemForm = ref({
    id: null,
    numeroBoi: '',
    dataPesagem: null,
    peso: null,
})
const editFormRef = ref(null)
const formValid = ref(false)
const loading = ref(false)
const error = ref(null)
const success = ref(false)

watch(() => props.pesagemData, (newVal) => {
    console.log(newVal)
    if(newVal && newVal.id){
        pesagemForm.value.id = newVal.id;
        pesagemForm.value.numeroBoi = newVal.numeroBoi;
        pesagemForm.value.dataPesagem = newVal.dataPesagem;
        pesagemForm.value.peso = newVal.peso;
        error.value = null;
        success.value = false;
    }
    },
    {
        immediate: true,
        deep: true
    }
)
watch(() => props.modelValue, (newVal) => {
    if (!newVal) {
        error.value = null;
        success.value = false;
    }
})
async function savePesagem() {
    const { valid } = await editFormRef.value.validate()
    if (!valid) return;

    loading.value = true;
    error.value = null;
    success.value = false;

    try {
        const payload = {
            boiId: props.pesagemData.boiId,
            data: pesagemForm.value.dataPesagem,
            peso: pesagemForm.value.peso,
        }
        await api.updateIndividualWeighing(pesagemForm.value.id, payload); 
        
        success.value = true;
        emit('pesagem-updated');
        
        setTimeout(closeDialog, 1000); 
    } catch (err) {
        console.error('Erro ao salvar pesagem individual:', err);
        error.value = err.response?.data?.message || 'Erro ao atualizar a pesagem. Tente novamente.';
    } finally {
        loading.value = false;
    }
}
function closeDialog() {
    emit('update:modelValue', false);
    emit('close');
}
</script>
