<template>
    <v-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" max-width="900px" scrollable>
        <v-card>
            <v-card-title class="d-flex align-center justify-space-between bg-primary">
                <span class="text-h6">
                    Detalhes da Pesagem em: {{ dataPesagem ? formatDate(dataPesagem) : 'Carregando...' }}
                </span>
                <v-btn
                    icon="mdi-close"
                    variant="text"
                    @click="$emit('close')"
                />
            </v-card-title>

            <v-card-text class="pt-4">
                <!-- Loading -->
                <div v-if="loading" class="text-center py-8">
                    <v-progress-circular indeterminate color="primary" size="48" />
                    <div class="mt-3 text-body-2">Carregando pesagens...</div>
                </div>

                <!-- Erro ao carregar -->
                <v-alert v-else-if="error" type="error" variant="tonal" class="mb-4">
                    Erro ao carregar os detalhes da pesagem: {{ error }}
                </v-alert>

                <!-- Lista de itens -->
                <v-card v-else-if="weighings.length" variant="outlined">
                    <v-card-title class="text-subtitle-1">
                        Bois Pesados
                        <v-chip size="small" class="ml-2">{{ weighings.length }}</v-chip>
                    </v-card-title>
                    <v-card-text>
                        <v-table density="comfortable">
                            <thead>
                                <tr>
                                    <th>Número do Boi</th>
                                    <th>Raça</th>
                                    <th class="text-right">Peso (kg)</th>
                                    <th>Medicações</th>
                                    <th class="text-center">Ações</th>
                                </tr>
                            </thead>
<tbody>
                                <tr v-for="pesagem in weighings" :key="pesagem.id">
                                    <td>
                                        <div class="d-flex align-center ga-2">
                                            {{ pesagem.numeroBoi || '-' }}
                                            <v-tooltip 
                                                v-if="pesagem.descricao" 
                                                location="top"
                                            >
                                                <template v-slot:activator="{ props }">
                                                    <v-icon 
                                                        v-bind="props"
                                                        size="small" 
                                                        color="info"
                                                        class="cursor-pointer"
                                                    >
                                                        mdi-information-outline
                                                    </v-icon>
                                                </template>
                                                <div style="max-width: 300px;">
                                                    <strong>Observação:</strong><br>
                                                    {{ pesagem.descricao }}
                                                </div>
                                            </v-tooltip>
                                        </div>
                                    </td>
                                    <td>{{ pesagem.racaBoi || '-' }}</td>
                                    <td class="text-right">{{ formatPeso(pesagem.peso) }}</td>
                                    <td>
                                        <span v-if="!pesagem.medicacoes || pesagem.medicacoes.length === 0" class="text-grey">
                                            Nenhuma
                                        </span>
                                        <div v-else class="d-flex flex-wrap ga-1">
                                            <v-chip 
                                                v-for="(med, index) in pesagem.medicacoes" 
                                                :key="index"
                                                size="x-small"
                                                color="primary"
                                                variant="tonal"
                                            >
                                                {{ med }}
                                            </v-chip>
                                        </div>
                                    </td>
                                    <td class="text-center">
                                        <v-btn
                                            icon="mdi-pencil"
                                            variant="text"
                                            color="warning"
                                            size="small"
                                            title="Editar Pesagem Individual"
                                            @click="openEditDialog(pesagem)"
                                        />
                                        <v-btn
                                            icon="mdi-delete"
                                            variant="text"
                                            color="error"
                                            size="small"
                                            title="Deletar Pesagem Individual"
                                            @click="confirmDeleteIndividual(pesagem)"
                                        />
                                    </td>
                                </tr>
                            </tbody>
                        </v-table>
                    </v-card-text>
                </v-card>

                <!-- Sem itens -->
                <v-alert v-else type="info" variant="tonal">
                    Nenhuma pesagem encontrada para esta data.
                </v-alert>
            </v-card-text>

            <v-card-actions>
                <v-spacer />
                <v-btn color="primary" variant="text" @click="$emit('close')">
                    Fechar
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>

    <!-- Dialog de Edição Individual -->
    <WeighingEditDialog
        v-model="showEditDialog"
        :pesagem-data="selectedWeighing"
        @close="showEditDialog = false"
        @pesagem-updated="onPesagemUpdated"
    />

    <!-- Dialog de Confirmação de Deleção Individual -->
    <v-dialog v-model="showDeleteDialog" max-width="500">
        <v-card>
            <v-card-title class="text-h5">Confirmar Deleção</v-card-title>
            <v-card-text>
                Tem certeza que deseja deletar a pesagem do boi <strong>{{ selectedWeighing ? selectedWeighing.numeroBoi : '' }}</strong> ({{ selectedWeighing ? selectedWeighing.peso : '' }} kg) da data 
                <strong>{{ selectedWeighing ? formatDate(selectedWeighing.dataPesagem) : '' }}</strong>?
                Esta ação é irreversível.
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="grey-darken-1" variant="text" @click="showDeleteDialog = false">
                    Cancelar
                </v-btn>
                <v-btn color="error" variant="flat" @click="deleteIndividual">
                    Deletar
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>
<script setup>
import { watch, ref, toRefs } from 'vue';
import WeighingEditDialog from '@/features/weighing/components/weight-management/WeighingEditDialog.vue'; 
import { useWeightFormatting } from '../../composables/useWeightFormatting';
import api from '@/services/api'; 

const {
    formatDate,
    formatPeso
} = useWeightFormatting();
const props = defineProps({
    modelValue: {
        type: Boolean,
        default: false,
    },
    dataPesagem: {
        type: String, // YYYY-MM-DD
        default: null,
    },
})
const emit = defineEmits(['update:modelValue', 'close', 'pesagem-edited', 'pesagem-deleted'])
const weighings = ref([])
const loading = ref(false)
const error = ref(null)
const showEditDialog = ref(false)
const showDeleteDialog = ref(false)
const selectedWeighing = ref(null)
const { modelValue, dataPesagem } = toRefs(props)
watch(dataPesagem, () => {
    if (modelValue) {
        fetchIndividualWeighings();
    }
})

watch(modelValue, (newVal) => {
    if (newVal && dataPesagem.value) {
        fetchIndividualWeighings();
    } else if (!newVal) {
        weighings.value = [];
        error.value = null;
    }
})

async function fetchIndividualWeighings() {
    if (!dataPesagem.value) return;
    loading.value = true;
    error.value = null;
    try {
        const response = await api.fetchIndividualWeighingsByDate(dataPesagem.value); 
        weighings.value = response.data;
    } catch (err) {
        console.error('Erro ao buscar pesagens individuais:', err);
        error.value = err.message || 'Erro desconhecido.';
    } finally {
        loading.value = false;
    }
}

function openEditDialog(pesagem) {
    const pesagemComData = {
        ...pesagem,
        dataPesagem: dataPesagem.value
    }
    selectedWeighing.value = pesagemComData;
    showEditDialog.value = true;
}

function confirmDeleteIndividual(pesagem) {
    selectedWeighing.value = pesagem;
    showDeleteDialog.value = true;
}

async function deleteIndividual() {
    if (!selectedWeighing.value) return;

    try {
        loading.value = true;
        await api.deleteIndividualWeighing(selectedWeighing.value.id); 
        
        showDeleteDialog.value = false;
        await fetchIndividualWeighings();
        emit('pesagem-deleted'); 
        // TODO: Adicionar lógica de notificação de sucesso aqui
    } catch (error) {
        console.error('Erro ao deletar pesagem individual:', error);
        // TODO: Adicionar lógica de notificação de erro aqui
    } finally {
        loading.value = false;
    }
}

function onPesagemUpdated() {
    showEditDialog.value = false;
    fetchIndividualWeighings(); 
    emit('pesagem-edited'); 
    // TODO: Adicionar lógica de notificação de sucesso aqui
}
</script>
