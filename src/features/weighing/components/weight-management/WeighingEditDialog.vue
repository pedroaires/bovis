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

<script>
import api from '@/services/api'; 

export default {
    name: 'WeighingEditDialog',
    props: {
        modelValue: {
            type: Boolean,
            default: false,
        },
        pesagemData: {
            type: Object,
            default: () => ({}),
        },
    },
    emits: ['update:modelValue', 'close', 'pesagem-updated'],
    data() {
        return {
            pesagemForm: {
                id: null,
                numeroBoi: '',
                dataPesagem: null,
                peso: null,
            },
            formValid: false,
            loading: false,
            error: null,
            success: false,
        }
    },
    watch: {
        pesagemData: {
            handler(newVal) {
                if (newVal && newVal.id) {
                    console.log(newVal)
                    this.pesagemForm.id = newVal.id;
                    this.pesagemForm.numeroBoi = newVal.numeroBoi;
                    this.pesagemForm.dataPesagem = newVal.dataPesagem;
                    this.pesagemForm.peso = newVal.peso;
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
        async savePesagem() {
            if (!this.$refs.editFormRef.validate()) return;

            this.loading = true;
            this.error = null;
            this.success = false;

            try {
                const payload = {
                    boiId: this.pesagemData.boiId,
                    data: this.pesagemForm.dataPesagem,
                    peso: this.pesagemForm.peso,
                }
                await api.updateIndividualWeighing(this.pesagemForm.id, payload); 
                
                this.success = true;
                this.$emit('pesagem-updated');
                
                setTimeout(this.closeDialog, 1000); 
            } catch (err) {
                console.error('Erro ao salvar pesagem individual:', err);
                this.error = err.response?.data?.message || 'Erro ao atualizar a pesagem. Tente novamente.';
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
