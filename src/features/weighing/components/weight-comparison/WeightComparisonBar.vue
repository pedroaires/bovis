<template>
    <v-slide-y-reverse-transition>
        <v-card
            v-if="props.selectedDates.length >= 2"
            class="floating-action-bar elevation-12"
        >   
            <v-card-text class="pa-0">
                <!-- Header com contador e botão fechar -->
                <div class="bar-header">
                    <div class="d-flex align-center ga-2">
                        <v-icon color="primary">mdi-chart-timeline-variant</v-icon>
                        <span class="text-h6 font-weight-bold">
                            Comparando {{ props.selectedDates.length }} datas
                        </span>
                    </div>
                    <v-btn
                        icon="mdi-close"
                        variant="text"
                        size="small"
                        @click="emit('update:selectedDates', [])"
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
                            <div class="metric-value">{{ getTotalBois(props.groupedWeighings, props.selectedDates) }}</div>
                            <div class="metric-label">Bois</div>
                        </div>
                    </div>

                    <v-divider vertical />

                    <div class="metric-card">
                        <v-icon color="success" size="20">mdi-calendar-range</v-icon>
                        <div class="metric-content">
                            <div class="metric-value">{{ getTotalDays(sortedSelectedDates) }}</div>
                            <div class="metric-label">Dias</div>
                        </div>
                    </div>

                    <v-divider vertical />

                    <div class="metric-card">
                        <v-icon color="info" size="20">mdi-weight-kilogram</v-icon>
                        <div class="metric-content">
                            <div class="metric-value">{{ getAverageWeightSimple(props.groupedWeighings, props.selectedDates) }}</div>
                            <div class="metric-label">Peso Médio</div>
                        </div>
                    </div>

                    <v-divider vertical />

                    <div class="metric-card">
                        <v-icon color="warning" size="20">mdi-chart-line</v-icon>
                        <div class="metric-content">
                            <div class="metric-value">{{ getWeightVariation(props.groupedWeighings, props.selectedDates) }}</div>
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
                        @click="emit('open-matrix')"
                        block
                    >
                        Ver Análise Completa
                    </v-btn>
                </div>
            </v-card-text>
        </v-card>
    </v-slide-y-reverse-transition>    


</template>

<script setup>
import { computed } from 'vue';
import { useWeightComparison } from '@/features/weighing/composables/useWeightComparison';
import { useWeightFormatting } from '@/features/weighing/composables/useWeightFormatting';
const props = defineProps({
    selectedDates: {
        type: Object,
        required: true,
    },
    groupedWeighings: {
        type: Object,
        required: true,
    }
});

const {
    formatDate,
} = useWeightFormatting();

const {
    getTotalBois,
    getTotalDays,
    getWeightVariation,
    getAverageWeightSimple,
    calculateDaysBetween
} = useWeightComparison();

const sortedSelectedDates = computed(() => {
    return [...props.selectedDates].sort();
});

const emit = defineEmits(['update:selectedDates', 'open-matrix'])
</script>

<style scoped>
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