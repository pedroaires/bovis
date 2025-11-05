<template>
    <div>
        <!-- Ações Globais -->
        <v-card variant="outlined" class="mb-4">
            <v-card-text class="d-flex align-center justify-space-between">
                <div>
                    <div class="text-h6">Pesagens Agrupadas por Data</div>
                    <div class="text-caption text-grey mt-1">
                        Selecione múltiplas datas na tabela para comparar
                    </div>
                </div>
            </v-card-text>
        </v-card>

        <!-- Tabela com Seleção Múltipla -->
        <v-card variant="outlined">
            <v-card-text>
                <!-- Filtros -->
                <v-expansion-panels class="mb-4">
                    <v-expansion-panel>
                        <v-expansion-panel-title>
                            <v-icon class="mr-2">mdi-filter</v-icon>
                            Filtros
                        </v-expansion-panel-title>
                        <v-expansion-panel-text>
                            <v-row dense>
                                <v-col cols="12" sm="6" md="4">
                                    <v-text-field
                                        v-model="filters.dataPesagemInicio"
                                        label="Data Início"
                                        type="date"
                                        variant="outlined"
                                        density="compact"
                                        clearable
                                        hide-details
                                    />
                                </v-col>
                                <v-col cols="12" sm="6" md="4">
                                    <v-text-field
                                        v-model="filters.dataPesagemFim"
                                        label="Data Fim"
                                        type="date"
                                        variant="outlined"
                                        density="compact"
                                        clearable
                                        hide-details
                                    />
                                </v-col>
                                <v-col cols="12" sm="6" md="4">
                                    <v-text-field
                                        v-model.number="filters.pesoMedioMin"
                                        label="Peso Médio Mín. (kg)"
                                        type="number"
                                        variant="outlined"
                                        density="compact"
                                        clearable
                                        hide-details
                                    />
                                </v-col>
                            </v-row>
                            <v-btn
                                color="primary"
                                class="mt-3"
                                size="small"
                                @click="applyFilters"
                            >
                                Aplicar Filtros
                            </v-btn>
                        </v-expansion-panel-text>
                    </v-expansion-panel>
                </v-expansion-panels>

                <!-- Tabela -->
                <v-data-table
                    v-model="selectedItems"
                    :headers="headers"
                    :items="groupedWeighings"
                    :loading="loading"
                    show-select
                    item-value="data"
                    density="comfortable"
                    :items-per-page="10"
                >
                    <!-- Header customizado com contador de selecionados -->
                    <template #top>
                        <v-toolbar density="compact" flat v-if="selectedItems.length > 0">
                            <v-toolbar-title>
                                <v-chip color="primary" variant="flat">
                                    {{ selectedItems.length }} selecionada(s)
                                </v-chip>
                            </v-toolbar-title>
                            <v-spacer />
                            <v-btn
                                color="grey"
                                variant="text"
                                @click="selectedItems = []"
                            >
                                Limpar Seleção
                            </v-btn>
                        </v-toolbar>
                    </template>

                    <template #[`item.dataPesagem`]="{ item }">
                        <div class="d-flex align-center">
                            {{ formatDate(item.data) }}
                            <v-chip
                                v-if="isInComparison(item.data)"
                                size="x-small"
                                color="primary"
                                class="ml-2"
                            >
                                na comparação
                            </v-chip>
                        </div>
                    </template>

                    <template #[`item.pesoMedio`]="{ item }">
                        {{ formatPeso(item.pesoMedio) }}
                    </template>

                    <template #[`item.pesoTotal`]="{ item }">
                        {{ formatPeso(item.pesoTotal) }}
                    </template>

                    <template #[`item.actions`]="{ item }">
                        <v-btn
                            icon="mdi-eye"
                            variant="text"
                            color="primary"
                            size="small"
                            title="Ver Detalhes"
                            @click="openDetails(item)"
                        />
                        <v-btn
                            icon="mdi-pencil"
                            variant="text"
                            color="warning"
                            size="small"
                            title="Editar Data do Grupo"
                            @click="openEditGroupDialog(item)"
                        />
                        <v-btn
                            icon="mdi-delete"
                            variant="text"
                            color="error"
                            size="small"
                            title="Deletar Grupo"
                            @click="confirmDeleteGroup(item)"
                        />
                    </template>
                </v-data-table>
            </v-card-text>
        </v-card>

        <!-- Barra de Ação Flutuante (aparece quando há seleção) -->
        <v-slide-y-reverse-transition>
            <v-card
                v-if="selectedItems.length >= 2"
                class="floating-action-bar elevation-12"
            >   
                <v-card-text class="pa-0">
                    <!-- Header com contador e botão fechar -->
                    <div class="bar-header">
                        <div class="d-flex align-center ga-2">
                            <v-icon color="primary">mdi-chart-timeline-variant</v-icon>
                            <span class="text-h6 font-weight-bold">
                                Comparando {{ selectedItems.length }} datas
                            </span>
                        </div>
                        <v-btn
                            icon="mdi-close"
                            variant="text"
                            size="small"
                            @click="selectedItems = []"
                        />
                    </div>

                    <!-- Timeline Visual das Datas -->
                    <div class="timeline-container">
                        <div class="timeline-track">
                            <div
                                v-for="(date, index) in sortedSelectedDates"
                                :key="date"
                                class="timeline-item"
                            >
                                <!-- Ponto da timeline -->
                                <div class="timeline-point">
                                    <div class="point-circle">{{ index + 1 }}</div>
                                    <div class="point-date">{{ formatDate(date) }}</div>
                                </div>
                                
                                <!-- Seta conectora (exceto no último) -->
                                <div v-if="index < sortedSelectedDates.length - 1" class="timeline-arrow">
                                    <v-icon size="small" color="primary">mdi-arrow-right</v-icon>
                                    <div class="arrow-label">
                                        +{{ calculateDaysBetween(date, sortedSelectedDates[index + 1]) }}d
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Métricas Principais -->
                    <div class="metrics-row">
                        <div class="metric-card">
                            <v-icon color="primary" size="20">mdi-cow</v-icon>
                            <div class="metric-content">
                                <div class="metric-value">{{ getTotalBois() }}</div>
                                <div class="metric-label">Bois</div>
                            </div>
                        </div>

                        <v-divider vertical />

                        <div class="metric-card">
                            <v-icon color="success" size="20">mdi-calendar-range</v-icon>
                            <div class="metric-content">
                                <div class="metric-value">{{ getTotalDays() }}</div>
                                <div class="metric-label">Dias</div>
                            </div>
                        </div>

                        <v-divider vertical />

                        <div class="metric-card">
                            <v-icon color="info" size="20">mdi-weight-kilogram</v-icon>
                            <div class="metric-content">
                                <div class="metric-value">{{ getAverageWeightSimple() }}</div>
                                <div class="metric-label">Peso Médio</div>
                            </div>
                        </div>

                        <v-divider vertical />

                        <div class="metric-card">
                            <v-icon color="warning" size="20">mdi-chart-line</v-icon>
                            <div class="metric-content">
                                <div class="metric-value">{{ getWeightVariation() }}</div>
                                <div class="metric-label">Variação</div>
                            </div>
                        </div>
                    </div>

                    <!-- Botões de Ação -->
                    <div class="actions-grid">
                        <v-btn
                            color="primary"
                            variant="elevated"
                            size="large"
                            prepend-icon="mdi-table-large"
                            @click="openWeightMatrix"
                            block
                        >
                            Ver Análise Completa
                        </v-btn>
                    </div>
                </v-card-text>
            </v-card>
        </v-slide-y-reverse-transition>

        <!-- Modal de Matriz de Pesos -->
        <WeightMatrixDialog
            v-model="showMatrixDialog"
            :selected-dates="selectedItems"
            @close="showMatrixDialog = false"
        />

        <!-- Modal de Detalhes -->
        <WeighingDetailDialog
            v-model="showDetailsDialog"
            :data-pesagem="selectedGroup ? selectedGroup.data : null"
            @close="showDetailsDialog = false"
            @pesagem-edited="fetchGroupedWeighings"
            @pesagem-deleted="fetchGroupedWeighings"
        />

        <!-- Dialog de Edição de Grupo -->
        <WeighingGroupEditDialog
            v-model="showEditGroupDialog"
            :group-data="selectedGroup"
            @close="showEditGroupDialog = false"
            @group-updated="onGroupUpdated"
        />

        <!-- Dialog de Confirmação de Deleção -->
        <v-dialog v-model="showDeleteDialog" max-width="500">
            <v-card>
                <v-card-title class="text-h5">Confirmar Deleção</v-card-title>
                <v-card-text>
                    Tem certeza que deseja deletar TODAS as {{ selectedGroup ? selectedGroup.totalBois : 0 }} pesagens da data 
                    <strong>{{ selectedGroup ? formatDate(selectedGroup.data) : '' }}</strong>?
                    Esta ação é irreversível.
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="grey-darken-1" variant="text" @click="showDeleteDialog = false">
                        Cancelar
                    </v-btn>
                    <v-btn color="error" variant="flat" @click="deleteGroup">
                        Deletar
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </div>
</template>

<script>
import WeighingDetailDialog from '@/components/organisms/WeighingDetailDialog.vue';
import WeighingGroupEditDialog from '@/components/organisms/WeighingGroupEditDialog.vue';
import WeightMatrixDialog from '@/components/organisms/WeightEvolutionDialog.vue';
import api from '@/services/api'; 

export default {
    name: 'WeighingGroupView',
    components: {
        WeighingDetailDialog,
        WeighingGroupEditDialog,
        WeightMatrixDialog
    },
    data() {
        return {
            headers: [
                { title: 'Data da Pesagem', key: 'data', sortable: true },
                { title: 'Total de Bois', key: 'totalBois', sortable: true },
                { title: 'Peso Médio (kg)', key: 'pesoMedio', sortable: true },
                { title: 'Peso Total (kg)', key: 'pesoTotal', sortable: true },
                { title: 'Ações', key: 'actions', sortable: false, align: 'center' },
            ],
            groupedWeighings: [],
            loading: false,
            totalElements: 0,
            
            // Seleção múltipla
            selectedItems: [],
            
            // Filtros
            filters: {
                dataPesagemInicio: null,
                dataPesagemFim: null,
                pesoMedioMin: null,
            },

            // Modais e Painéis
            showDetailsDialog: false,
            showEditGroupDialog: false,
            showDeleteDialog: false,
            showMatrixDialog: false,
            selectedGroup: null,
        }
    },
    computed:{
        sortedSelectedDates() {
            return [...this.selectedItems].sort();
        }
    },
    methods: {
        formatDate(dateString) {
            if (!dateString) return '—';
            const date = new Date(dateString + 'T00:00:00'); 
            return date.toLocaleDateString('pt-BR');
        },

        formatPeso(peso) {
            if (peso === null || peso === undefined) return '—'
            return `${Number(peso).toFixed(2)} kg`
        },

        async fetchGroupedWeighings() {
            try {
                this.loading = true
                const response = await api.fetchGroupedWeighings() 
                this.groupedWeighings = response.data
                this.totalElements = response.data.totalElements
            } catch (error) {
                console.error('Erro ao buscar pesagens agrupadas:', error)
            } finally {
                this.loading = false
            }
        },

        applyFilters() {
            this.fetchGroupedWeighings();
        },

        isInComparison(date) {
            return this.selectedItems.includes(date);
        },

        removeFromSelection(date) {
            this.selectedItems = this.selectedItems.filter(d => d !== date);
        },

        // Métodos de comparação
        openWeightMatrix() {
            if (this.selectedItems.length < 2) {
                alert('Selecione pelo menos 2 datas para gerar a matriz de evolução');
                return;
            }
            this.showMatrixDialog = true;
        },

        // Ações individuais
        openDetails(group) {
            this.selectedGroup = group
            this.showDetailsDialog = true
        },

        openEditGroupDialog(group) {
            this.selectedGroup = group
            this.showEditGroupDialog = true
        },

        confirmDeleteGroup(group) {
            this.selectedGroup = group
            this.showDeleteDialog = true
        },

        async deleteGroup() {
            if (!this.selectedGroup) return;

            try {
                this.loading = true;
                const dataPesagem = this.selectedGroup.data;
                await api.deleteGroupedWeighing(dataPesagem); 
                
                this.showDeleteDialog = false;
                this.fetchGroupedWeighings(); 
            } catch (error) {
                console.error('Erro ao deletar grupo de pesagens:', error);
            } finally {
                this.loading = false;
            }
        },

        onGroupUpdated() {
            this.showEditGroupDialog = false;
            this.fetchGroupedWeighings();
        },
        getTotalBois() {
            return this.groupedWeighings
                .filter(g => this.selectedItems.includes(g.data))
                .reduce((sum, g) => sum + (g.totalBois || 0), 0);
        },
        calculateDaysBetween(date1, date2) {
            const d1 = new Date(date1 + 'T00:00:00');
            const d2 = new Date(date2 + 'T00:00:00');
            const diffTime = Math.abs(d2 - d1);
            return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        },

        getTotalDays() {
            if (this.sortedSelectedDates.length < 2) return 0;
            const first = this.sortedSelectedDates[0];
            const last = this.sortedSelectedDates[this.sortedSelectedDates.length - 1];
            return this.calculateDaysBetween(first, last);
        },

        getAverageWeightSimple() {
            const selected = this.groupedWeighings.filter(g => 
                this.selectedItems.includes(g.data)
            );
            if (selected.length === 0) return '—';
            const avg = selected.reduce((sum, g) => sum + (g.pesoMedio || 0), 0) / selected.length;
            return `${avg.toFixed(1)} kg`;
        },

        getWeightVariation() {
            const selected = this.groupedWeighings
                .filter(g => this.selectedItems.includes(g.data))
                .sort((a, b) => a.data.localeCompare(b.data));
            
            if (selected.length < 2) return '—';
            
            const first = selected[0].pesoMedio;
            const last = selected[selected.length - 1].pesoMedio;
            const diff = last - first;
            
            return diff >= 0 ? `+${diff.toFixed(1)} kg` : `${diff.toFixed(1)} kg`;
        }
    },

    mounted() {
        this.fetchGroupedWeighings()
    }
}
</script>

<style scoped>
/* Barra Flutuante */
.floating-action-bar {
    position: fixed;
    bottom: 24px;
    left: 50%;
    transform: translateX(-50%);
    z-index: 1000;
    max-width: 900px;
    width: 90%;
    border-radius: 16px !important;
    background: linear-gradient(135deg, #ffffff 0%, #f8f9fa 100%);
}

/* Header da Barra */
.bar-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 20px;
    border-bottom: 2px solid #e0e0e0;
    background: linear-gradient(90deg, #f5f7fa 0%, #ffffff 100%);
}

/* Timeline */
.timeline-container {
    padding: 20px;
    background: #fafbfc;
    overflow-x: auto;
}

.timeline-track {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    min-width: min-content;
}

.timeline-item {
    display: flex;
    align-items: center;
    gap: 8px;
}

.timeline-point {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
}

.point-circle {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: linear-gradient(135deg, #1976D2 0%, #1565C0 100%);
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    font-size: 16px;
    box-shadow: 0 4px 8px rgba(25, 118, 210, 0.3);
    animation: pulse 2s infinite;
}

@keyframes pulse {
    0%, 100% {
        box-shadow: 0 4px 8px rgba(25, 118, 210, 0.3);
    }
    50% {
        box-shadow: 0 4px 16px rgba(25, 118, 210, 0.5);
    }
}

.point-date {
    font-size: 13px;
    font-weight: 600;
    color: #424242;
    white-space: nowrap;
}

.timeline-arrow {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    padding: 0 8px;
}

.arrow-label {
    font-size: 11px;
    color: #666;
    font-weight: 600;
    background: #e3f2fd;
    padding: 2px 8px;
    border-radius: 8px;
}

/* Métricas */
.metrics-row {
    display: flex;
    align-items: center;
    justify-content: space-around;
    padding: 16px 20px;
    gap: 16px;
}

.metric-card {
    display: flex;
    align-items: center;
    gap: 12px;
    flex: 1;
}

.metric-content {
    display: flex;
    flex-direction: column;
    gap: 2px;
}

.metric-value {
    font-size: 20px;
    font-weight: 700;
    color: #1976D2;
    line-height: 1;
}

.metric-label {
    font-size: 11px;
    color: #666;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    font-weight: 600;
}

/* Grid de Ações */
.actions-grid {
    display: grid;
    grid-template-columns: 1fr;
    gap: 12px;
    padding: 16px 20px;
    border-top: 1px solid #e0e0e0;
    background: #ffffff;
}

/* Responsividade */
@media (max-width: 960px) {
    .floating-action-bar {
        max-width: 95%;
        width: 95%;
    }

    .timeline-container {
        padding: 16px 12px;
    }

    .point-circle {
        width: 32px;
        height: 32px;
        font-size: 14px;
    }

    .point-date {
        font-size: 11px;
    }

    .metrics-row {
        flex-wrap: wrap;
        gap: 12px;
    }

    .metric-card {
        flex: 1 1 calc(50% - 12px);
        min-width: 140px;
    }

    .actions-grid {
        grid-template-columns: 1fr;
    }
}

@media (max-width: 600px) {
    .bar-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;
    }

    .timeline-track {
        justify-content: flex-start;
    }

    .metric-value {
        font-size: 18px;
    }
}
</style>