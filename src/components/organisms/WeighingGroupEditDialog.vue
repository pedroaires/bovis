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

<script>
import api from '@/services/api'; 

export default {
    name: 'WeighingGroupEditDialog',
    props: {
        modelValue: {
            type: Boolean,
            default: false,
        },
        groupData: {
            type: Object,
            default: () => ({}),
        },
    },
    emits: ['update:modelValue', 'close', 'group-updated'],
    data() {
        return {
            novaData: null,
            formValid: false,
            loading: false,
            error: null,
            success: false,
        }
    },
    watch: {
        groupData: {
            handler(newVal) {
                if (newVal && newVal.data) {
                    this.novaData = newVal.data;
                    this.error = null;
                    this.success = false;
                }
            },
            immediate: true,
            deep: true,
        },
        modelValue(val) {
            if (!val) {
                this.error = null;
                this.success = false;
            }
        }
    },
    methods: {
        formatDate(dateString) {
            if (!dateString) return '—';
            const date = new Date(dateString + 'T00:00:00'); 
            return date.toLocaleDateString('pt-BR');
        },
        async saveGroupDate() {
            if (!this.$refs.editFormRef.validate()) return;

            this.loading = true;
            this.error = null;
            this.success = false;
            
            const oldDate = this.groupData.data;

            try {
                await api.updateGroupedWeighingDate(oldDate, this.novaData); 
                
                this.success = true;
                this.$emit('group-updated');
                // Fechar após um pequeno delay para mostrar a mensagem de sucesso
                setTimeout(this.closeDialog, 1000); 
            } catch (err) {
                console.error('Erro ao salvar nova data do grupo:', err);
                this.error = err.response?.data?.message || 'Erro ao atualizar a data do grupo. Tente novamente.';
            } finally {
                this.loading = false;
            }
        },
        closeDialog() {
            this.$emit('update:modelValue', false);
            this.$emit('close');
        }
    }
}
</script>
