<template>
    <div style="height: 100%; overflow-y: auto;">
        <div class="chart-container">
            <div class="chart-controls">
                <v-container fluid>
                    <v-row dense align="center">
                        <v-col cols="12" md="5">
                            <v-autocomplete
                                v-model="selectedBoisForChart"
                                :items="chartBoiOptions"
                                label="Selecione os bois para visualizar"
                                multiple
                                chips
                                closable-chips
                                variant="outlined"
                                density="compact"
                                prepend-inner-icon="mdi-cow"
                                hint="Selecione até 10 bois para melhor visualização"
                                persistent-hint
                            >
                                <template #chip="{ props, item }">
                                    <v-chip
                                        v-bind="props"
                                        :color="getBoiColor(item.value)"
                                        size="small"
                                    >
                                        {{ item.title }}
                                    </v-chip>
                                </template>
                            </v-autocomplete>
                        </v-col>
                        <v-col cols="12" md="3">
                            <v-select
                                v-model="chartType"
                                :items="chartTypeOptions"
                                label="Tipo de Gráfico"
                                variant="outlined"
                                density="compact"
                                hide-details
                            />
                        </v-col>
                        <v-col cols="6" md="2">
                            <v-btn
                                color="primary"
                                variant="outlined"
                                block
                                prepend-icon="mdi-trophy"
                                @click="selectTopBois"
                                size="small"
                            >
                                Top 10 GMD
                            </v-btn>
                        </v-col>
                        <v-col cols="6" md="2">
                            <v-btn
                                color="error"
                                variant="outlined"
                                block
                                prepend-icon="mdi-alert"
                                @click="selectWorstBois"
                                size="small"
                            >
                                Bottom 10
                            </v-btn>
                        </v-col>
                    </v-row>
                </v-container>
            </div>

            <div v-if="selectedBoisForChart.length > 0" class="chart-area">
                <apexchart
                    ref="weightChart"
                    type="line"
                    height="450"
                    :options="chartOptions"
                    :series="chartSeries"
                />
            </div>
            <div v-else class="chart-empty-state">
                <v-icon size="80" color="grey-lighten-1">mdi-chart-line-variant</v-icon>
                <div class="text-h6 text-grey mt-4">Selecione bois para visualizar o gráfico</div>
                <div class="text-caption text-grey mt-2">
                    Use os botões acima para selecionar rapidamente os melhores ou piores desempenhos
                </div>
            </div>

            <div v-if="selectedBoisForChart.length > 0" class="chart-stats">
                 <v-row>
                    <v-col cols="6" md="3">
                        <v-card variant="outlined" class="stat-card">
                            <v-card-text class="text-center pa-4">
                                <v-icon color="primary" size="32" class="mb-2">mdi-speedometer</v-icon>
                                <div class="text-h4 text-primary font-weight-bold">
                                    {{ chartStats.avgGmd }}
                                </div>
                                <div class="text-caption text-grey text-uppercase mt-1">
                                    GMD Médio (kg/dia)
                                </div>
                            </v-card-text>
                        </v-card>
                    </v-col>
                    <v-col cols="6" md="3">
                        <v-card variant="outlined" class="stat-card">
                            <v-card-text class="text-center pa-4">
                                <v-icon color="success" size="32" class="mb-2">mdi-trophy</v-icon>
                                <div class="text-h4 text-success font-weight-bold">
                                    {{ chartStats.bestGmd }}
                                </div>
                                <div class="text-caption text-grey text-uppercase mt-1">
                                    Melhor GMD
                                </div>
                            </v-card-text>
                        </v-card>
                    </v-col>
                    <v-col cols="6" md="3">
                        <v-card variant="outlined" class="stat-card">
                            <v-card-text class="text-center pa-4">
                                <v-icon color="info" size="32" class="mb-2">mdi-trending-up</v-icon>
                                <div class="text-h4 text-info font-weight-bold">
                                    {{ chartStats.avgGain }}
                                </div>
                                <div class="text-caption text-grey text-uppercase mt-1">
                                    Ganho Médio Total
                                </div>
                            </v-card-text>
                        </v-card>
                    </v-col>
                    <v-col cols="6" md="3">
                        <v-card variant="outlined" class="stat-card">
                            <v-card-text class="text-center pa-4">
                                <v-icon color="warning" size="32" class="mb-2">mdi-sigma</v-icon>
                                <div class="text-h4 text-warning font-weight-bold">
                                    {{ chartStats.stdDev }}
                                </div>
                                <div class="text-caption text-grey text-uppercase mt-1">
                                    Desvio Padrão
                                </div>
                            </v-card-text>
                        </v-card>
                    </v-col>
                </v-row>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue';
import { useWeightFormatting } from '@/composables/useWeightFormatting';
import { useTheme } from 'vuetify';

const props = defineProps({
    matrixData: {
        type: Object,
        required: true,
    },
})

const { 
    formatDateShort,
} = useWeightFormatting();

const selectedBoisForChart = ref([]);
const chartType = ref('line');
const chartTypeOptions = ref([
    {title: 'Linha', value: 'line'},
    {title: 'Área', value: 'area'},
]);
const chartColors = ref([
    '#1976D2', '#388E3C', '#D32F2F', '#7B1FA2', '#F57C00',
    '#0097A7', '#C2185B', '#5D4037', '#FBC02D', '#00796B'
]);
const weightChart = ref(null);
const chartBoiOptions = computed(() => {
    if (!props.matrixData?.rows) return [];
        return props.matrixData.rows.map(row => ({
            title: `${row.boiNumero} - ${row.raca}`,
            value: row.boiId,
        }));
});

const chartSeries = computed(() => {
    if (!selectedBoisForChart.value.length || !props.matrixData) return [];

    const selectedRows = props.matrixData.rows.filter(row =>
        selectedBoisForChart.value.includes(row.boiId)
    );

    return selectedRows.map(row => ({
        name: row.boiNumero,
        data: row.pesos.map(p => p?.peso || null),
    }));
});
const theme = useTheme();
const chartOptions = computed(() => {
    const isDark = theme.global.current.value.dark;
    
    return {
        chart: {
            type: chartType.value,
            height: 450,
            toolbar: { show: true },
            animations: { enabled: true, easing: 'easeinout', speed: 800 },
        },
        colors: chartColors.value.slice(0, selectedBoisForChart.value.length),
        stroke: {
            curve: 'smooth',
            width: chartType.value === 'area' ? 2 : 3,
        },
        fill: {
            type: chartType.value === 'area' ? 'gradient' : 'solid',
            gradient: { shadeIntensity: 1, opacityFrom: 0.7, opacityTo: 0.3 }
        },
        markers: { size: 5, hover: { size: 7 } },
        xaxis: {
            categories: props.matrixData?.dates.map(d => formatDateShort(d)) || [],
            title: { text: 'Data da Pesagem', style: { fontSize: '14px', fontWeight: 600 } },
            labels: { style: { fontSize: '12px' } }
        },
        yaxis: {
            title: { text: 'Peso (kg)', style: { fontSize: '14px', fontWeight: 600 } },
            labels: {
                formatter: (value) => value ? `${value.toFixed(0)} kg` : '',
                style: { fontSize: '12px' }
            }
        },
        tooltip: {
            shared: true,
            intersect: false,
            y: { formatter: (value) => value ? `${value.toFixed(1)} kg` : 'Sem dados' }
        },
        legend: {
            position: 'bottom',
            horizontalAlign: 'center',
            fontSize: '13px',
            markers: { width: 12, height: 12, radius: 6 },
            itemMargin: { horizontal: 10, vertical: 5 }
        },
        grid: { borderColor: isDark ? '#444' : '#e0e0e0', strokeDashArray: 4 },
        theme: { mode: isDark ? 'dark' : 'light' }
    };
        
});

const chartStats = computed(() => {
    if (!selectedBoisForChart.value.length || !props.matrixData) {
        return { avgGmd: '—', bestGmd: '—', avgGain: '—', stdDev: '—' };
    }

    const selectedRows = props.matrixData.rows.filter(row =>
        selectedBoisForChart.value.includes(row.boiId)
    );

    const gmds = selectedRows.map(r => r.gmdMedio).filter(g => g !== null);
    const gains = selectedRows.map(r => r.ganhoTotal).filter(g => g !== null);

    const avgGmd = gmds.length > 0 ? (gmds.reduce((a, b) => a + b, 0) / gmds.length).toFixed(2) : '—';
    const bestGmd = gmds.length > 0 ? Math.max(...gmds).toFixed(2) : '—';
    const avgGain = gains.length > 0 ? `+${(gains.reduce((a, b) => a + b, 0) / gains.length).toFixed(1)} kg` : '—';

    let stdDev = '—';
    if (gmds.length > 1) {
        const mean = gmds.reduce((a, b) => a + b, 0) / gmds.length;
        const variance = gmds.reduce((sum, val) => sum + Math.pow(val - mean, 2), 0) / gmds.length;
        stdDev = Math.sqrt(variance).toFixed(2);
    }

    return { avgGmd, bestGmd, avgGain, stdDev };
});


watch(chartType, () => {
    nextTick(() => {
        if (weightChart.value) { 
            weightChart.value.refresh();
        }
    });
});

function getBoiColor(boiId) {
    const index = selectedBoisForChart.value.indexOf(boiId);
    return chartColors.value[index % chartColors.value.length];
}
function selectTopBois() {
    if (!props.matrixData?.rows) return;
    const sorted = [...props.matrixData.rows]
        .filter(r => r.gmdMedio !== null)
        .sort((a, b) => b.gmdMedio - a.gmdMedio)
        .slice(0, 10);
        
    selectedBoisForChart.value = sorted.map(r => r.boiId);
}

function selectWorstBois() {
    if (!props.matrixData?.rows) return;
    const sorted = [...props.matrixData.rows]
        .filter(r => r.gmdMedio !== null)
        .sort((a, b) => a.gmdMedio - b.gmdMedio)
        .slice(0, 10);
    selectedBoisForChart.value = sorted.map(r => r.boiId);
}



</script>
