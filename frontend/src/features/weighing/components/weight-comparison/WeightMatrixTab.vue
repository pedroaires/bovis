<template>
    <div style="height: 100%; overflow-y: auto;">
        <div class="control-panel">
            <v-container fluid>
                <v-row dense align="center">
                    <v-col cols="12" md="4">
                        <div class="d-flex ga-2 align-center">
                            <v-chip color="primary" variant="flat" size="small">
                                <v-icon start>mdi-cow</v-icon>
                                {{ props.matrixData?.totalBois || 0 }} bois
                            </v-chip>
                            <v-chip color="success" variant="flat" size="small">
                                <v-icon start>mdi-calendar-range</v-icon>
                                {{ props.matrixData?.totalDias || 0 }} dias
                            </v-chip>
                        </div>
                    </v-col>

                    <v-col cols="12" md="8">
                        <v-row dense>
                            <v-col cols="12" sm="3">
                                <v-text-field
                                    v-model="filters.boiNumero"
                                    label="Buscar Boi"
                                    prepend-inner-icon="mdi-magnify"
                                    variant="outlined"
                                    density="compact"
                                    clearable
                                    hide-details
                                />
                            </v-col>
                            <v-col cols="12" sm="3">
                                <v-select
                                    v-model="filters.racas"
                                    label="Raças"
                                    :items="props.availableBreeds"
                                    multiple
                                    chips
                                    variant="outlined"
                                    density="compact"
                                    clearable
                                    hide-details
                                />
                            </v-col>
                            <v-col cols="12" sm="3">
                                <v-select
                                    v-model="filters.gmdFilter"
                                    label="GMD"
                                    :items="gmdFilterOptions"
                                    variant="outlined"
                                    density="compact"
                                    clearable
                                    hide-details
                                />
                            </v-col>
                            <v-col cols="12" sm="3" class="d-flex align-center">
                                <v-switch
                                    v-model="showOnlyComplete"
                                    label="Completos"
                                    color="primary"
                                    density="compact"
                                    hide-details
                                    inset
                                />
                            </v-col>
                        </v-row>
                    </v-col>
                </v-row>
            </v-container>
        </div>

        <div class="legend-bar">
            <v-icon size="small" class="mr-1">mdi-information</v-icon>
            <span class="text-caption">
                GMD: 
                <v-chip size="x-small" color="success" class="mx-1">≥1.2</v-chip>
                <v-chip size="x-small" color="warning" class="mx-1">0.8-1.2</v-chip>
                <v-chip size="x-small" color="error" class="mx-1">&lt;0.8</v-chip>
                kg/dia
            </span>
            <v-spacer />
            <span class="text-caption text-grey">
                Mostrando {{ filteredRows.length }} de {{ props.matrixData.rows.length }} bois
            </span>
        </div>

        <div class="table-scroll-container">
            <table class="matrix-table">
                <thead>
                    <tr>
                        <th class="col-fixed col-boi">Boi</th>
                        <th class="col-fixed col-raca">Raça</th>
                        <th 
                            v-for="(date, index) in props.matrixData.dates" 
                            :key="date"
                            class="col-date"
                        >
                            <div class="date-header-content">
                                <div class="date-main">{{ formatDateShort(date) }}</div>
                                <div v-if="index > 0" class="date-diff">
                                    +{{ calculateDaysBetween(props.matrixData.dates[index - 1], date) }}d
                                </div>
                            </div>
                        </th>
                        <th class="col-summary">GMD Médio</th>
                        <th class="col-summary">Ganho Total</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="row in filteredRows" :key="row.boiId" class="data-row">
                        <td class="col-fixed col-boi">
                            <span class="boi-numero">{{ row.boiNumero }}</span>
                        </td>
                        <td class="col-fixed col-raca">
                            <v-chip size="x-small" variant="tonal">
                                {{ row.raca }}
                            </v-chip>
                        </td>
                        <td 
                            v-for="(peso, index) in row.pesos" 
                            :key="index"
                            class="col-weight"
                            :class="{ 'has-data': peso && peso.peso !== null }"
                        >
                            <div v-if="peso && peso.peso !== null" class="weight-cell-content">
                                <div class="weight-value">{{ formatPeso(peso.peso) }}</div>
                                <div v-if="peso.gmd !== null" class="gmd-badge">
                                    <v-chip 
                                        :color="getGMDColor(peso.gmd)"
                                        size="x-small"
                                        variant="flat"
                                    >
                                        {{ formatGMD(peso.gmd) }}
                                    </v-chip>
                                </div>
                            </div>
                            <div v-else class="no-data">—</div>
                        </td>
                        <td class="col-summary">
                            <v-chip 
                                v-if="row.gmdMedio !== null"
                                :color="getGMDColor(row.gmdMedio)"
                                size="small"
                                variant="flat"
                            >
                                {{ formatGMD(row.gmdMedio) }}
                            </v-chip>
                            <span v-else class="text-grey">—</span>
                        </td>
                        <td class="col-summary">
                            <span 
                                class="gain-value"
                                :class="getGainClass(row.ganhoTotal)"
                            >
                                {{ formatGanhoTotal(row.ganhoTotal) }}
                            </span>
                        </td>
                    </tr>
                    <tr v-if="filteredRows.length === 0">
                        <td :colspan="props.matrixData.dates.length + 4" class="text-center pa-8">
                            <v-icon size="48" color="grey">mdi-filter-remove</v-icon>
                            <div class="text-h6 text-grey mt-2">Nenhum boi encontrado com os filtros aplicados</div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { getGMDColor, getGainClass } from "@/shared/utils/helpers/styleHelpers"
import { formatPeso, formatGanhoTotal, formatGMD } from "@/shared/utils/formatters/weightFormatter"
import { calculateDaysBetween } from '@/shared/utils/calculators/dateCalculator';
import { formatDateShort } from "@/shared/utils/formatters/dateFormatter";


const props = defineProps({
    matrixData: {
        type: Object,
        required: true,
    },
    availableBreeds: {
        type: Array,
        default: () => [],
    },
});

const filters = ref({
    racas: [],
    boiNumero: '',
    gmdFilter: null,
});

const showOnlyComplete = ref(false);

const gmdFilterOptions = [
    { title: 'Excelente (≥ 1.2)', value: 'excelente' },
    { title: 'Regular (0.8-1.2)', value: 'regular' },
    { title: 'Baixo (< 0.8)', value: 'baixo' },
];

const filteredRows = computed(() => {
    if (!props.matrixData?.rows) return [];

    let rows = [...props.matrixData.rows];

    // Filtro por Raças
    if (filters.value.racas.length > 0) {
        rows = rows.filter(row => filters.value.racas.includes(row.raca));
    }

    // Filtro por Número do Boi
    if (filters.value.boiNumero) {
        rows = rows.filter(row => 
            row.boiNumero.toLowerCase().includes(filters.value.boiNumero.toLowerCase())
        );
    }

    // Filtro por GMD
    if (filters.value.gmdFilter) {
        rows = rows.filter(row => {
            const gmd = row.gmdMedio;
            if (gmd === null || gmd === undefined) return false;
            if (filters.value.gmdFilter === 'excelente') return gmd >= 1.2;
            if (filters.value.gmdFilter === 'regular') return gmd >= 0.8 && gmd < 1.2;
            if (filters.value.gmdFilter === 'baixo') return gmd < 0.8;
            return true;
        });
    }

    // Filtro por Completos
    if (showOnlyComplete.value) {
        rows = rows.filter(row => 
            row.pesos.every(p => p !== null && p.peso !== null)
        );
    }

    return rows;
});

</script>

<style>
.control-panel {
    background-color: #f5f5f5; 
    border-bottom: 1px solid #e0e0e0;
    padding-top: 8px; 
    padding-bottom: 8px;
}


</style>