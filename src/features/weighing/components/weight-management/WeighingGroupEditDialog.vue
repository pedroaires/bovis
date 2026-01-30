<template>
    <v-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" max-width="500px" persistent>
        <v-card>
            <v-card-title class="d-flex align-center justify-space-between bg-warning">
                <span class="text-h6">
                    Editar Data do Grupo de Pesagens
                </span>
                <v-btn
                    icon="mdi-close"
                    variant="text"
                    @click="closeDialog"
                />
            </v-card-title>

            <v-card-text class="pt-6">
                <v-alert type="info" variant="tonal" class="mb-4">
                    Você está alterando a data de <strong>{{ groupData ? groupData.totalBois : 0 }}</strong> pesagens.
                    A data atual é: <strong>{{ groupData ? formatDate(groupData.dataPesagem) : '—' }}</strong>
                </v-alert>

                <v-form ref="editFormRef" v-model="formValid">
                    <v-row>
                        <v-col cols="12">
                            <v-text-field
                                v-model="novaData"
                                label="Nova Data da Pesagem"
                                type="date"
                                :rules="[v => !!v || 'Nova data é obrigatória']"
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
                    Data do grupo atualizada com sucesso!
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
                    @click="saveGroupDate"
                    :loading="loading"
                    :disabled="!formValid || !novaData || novaData === groupData.dataPesagem"
                >
                    Salvar
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script setup>
import api from '@/services/api'; 
import { watch, ref } from 'vue';
import { formatDate } from '@/shared/utils/formatters/dateFormatter';

const props = defineProps({
    modelValue: {
        type: Boolean,
        default: false,
    },
    groupData: {
        type: Object,
        default: () => ({}),
    },
})

const novaData = ref(null)
const formValid = ref(false)
const loading = ref(false)
const error = ref(null)
const success = ref(false)

const editFormRef = ref(null)

watch(() => props.groupData, (newVal) => {
    if (newVal && newVal.data) {
        novaData.value = newVal.data;
        error.value = null;
        success.value = false;
    }
    
}, {immediate: true, deep: true}
)
watch(() => props.modelValue, (val) => {
    if (!val) {
        error.value = null;
        success.value = false;
    }
})
const emit = defineEmits(['update:modelValue', 'close', 'group-updated'])


async function saveGroupDate() {
    const { valid } = await editFormRef.value.validate() 
    if (!valid) return;

    loading.value = true;
    error.value = null;
    success.value = false;
    
    const oldDate = props.groupData.data;

    try {
        await api.updateGroupedWeighingDate(oldDate, novaData.value); 
        
        success.value = true;
        emit('group-updated');
        // Fechar após um pequeno delay para mostrar a mensagem de sucesso
        setTimeout(closeDialog, 1000); 
    } catch (err) {
        console.error('Erro ao salvar nova data do grupo:', err);
        error.value = err.response?.data?.message || 'Erro ao atualizar a data do grupo. Tente novamente.';
    } finally {
        loading.value = false;
    }
}
function closeDialog() {
    emit('update:modelValue', false);
    emit('close');
}
</script>
