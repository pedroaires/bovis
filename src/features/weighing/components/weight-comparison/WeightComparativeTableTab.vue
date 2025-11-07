<template>
    <div style="height: 100%; overflow-y: auto;">
        <div class="comparative-table-container">
            <div class="control-panel">
                <v-container fluid>
                    <v-row dense align="center">
                        <v-col cols="12" md="4">
                            <v-select
                                v-model="tableSortBy"
                                :items="tableSortOptions"
                                label="Ordenar por"
                                variant="outlined"
                                density="compact"
                                hide-details
                            />
                        </v-col>
                        <v-col cols="12" md="4">
                            <v-select
                                v-model="tableFilterRacas"
                                label="Filtrar Raças"
                                :items="props.availableBreeds"
                                multiple
                                chips
                                variant="outlined"
                                density="compact"
                                hide-details
                            />
                        </v-col>
                        <v-col cols="12" md="4">
                            <v-btn
                                color="success"
                                variant="elevated"
                                block
                                prepend-icon="mdi-file-excel"
                                @click="exportTableToExcel"
                            >
                                Exportar Tabela
                            </v-btn>
                        </v-col>
                    </v-row>
                </v-container>
            </div>

            <v-data-table
                :headers="comparativeHeaders"
                :items="comparativeTableData"
                :items-per-page="15"
                density="comfortable"
                class="elevation-1"
            >
                <template #[`item.boiNumero`]="{ item }">
                    <span class="font-weight-bold text-primary" style="font-size: 1rem;">
                        {{ item.boiNumero }}
                    </span>
                </template>

                <template #[`item.raca`]="{ item }">
                    <v-chip size="small" variant="tonal" color="primary">
                        {{ item.raca }}
                    </v-chip>
                </template>

                <template #[`item.pesoInicial`]="{ item }">
                    <span class="font-weight-medium">
                        {{ formatPesoKg(item.pesoInicial) }}
                    </span>
                </template>

                <template #[`item.pesoFinal`]="{ item }">
                    <span class="font-weight-medium">
                        {{ formatPesoKg(item.pesoFinal) }}
                    </span>
                </template>

                <template #[`item.ganhoTotal`]="{ item }">
                    <v-chip
                        :color="item.ganhoTotal >= 0 ? 'success' : 'error'"
                        size="small"
                        variant="flat"
                    >
                        {{ formatGanhoTotal(item.ganhoTotal) }}
                    </v-chip>
                </template>

                <template #[`item.gmdMedio`]="{ item }">
                    <v-chip
                        :color="getGMDColor(item.gmdMedio)"
                        size="small"
                        variant="flat"
                    >
                        {{ formatGMD(item.gmdMedio) }} kg/dia
                    </v-chip>
                </template>

                <template #[`item.numeroPesagens`]="{ item }">
                    <v-chip 
                        size="small" 
                        variant="outlined"
                        :color="item.numeroPesagens === props.selectedDates.length ? 'success' : 'warning'"
                    >
                        {{ item.numeroPesagens }}/{{ props.selectedDates.length }}
                    </v-chip>
                </template>
            </v-data-table>
        </div>
    </div>
</template>

<script setup>
import api from '@/services/api';
import { ref, computed, defineEmits } from 'vue';
import { useWeightFormatting } from '@/features/weighing/composables/useWeightFormatting';
const emit = defineEmits(['error']);
const props = defineProps({
    matrixData: {
        type: Object,
        required: true,
    },
    availableBreeds: {
        type: Array,
        default: () => [],
    },
    selectedDates: {
        type: Array,
        default: () => [],
    },
});

const {
    formatGMD,
    formatGanhoTotal,
    getGMDColor,
    formatPesoKg
} = useWeightFormatting();
const tableSortBy = ref('gmdMedio');
const tableFilterRacas = ref([]);
const tableSortOptions = ref([
    { title: 'GMD Médio (Maior)', value: 'gmdMedio' },
    { title: 'Ganho Total (Maior)', value: 'ganhoTotal' },
    { title: 'Número do Boi', value: 'boiNumero' },
    { title: 'Raça', value: 'raca' },
]);

const comparativeHeaders = computed(() => {
    return [
        { title: 'Boi', key: 'boiNumero', sortable: true },
        { title: 'Raça', key: 'raca', sortable: true },
        { title: 'Peso Inicial', key: 'pesoInicial', sortable: true },
        { title: 'Peso Final', key: 'pesoFinal', sortable: true },
        { title: 'Ganho Total', key: 'ganhoTotal', sortable: true },
        { title: 'GMD Médio', key: 'gmdMedio', sortable: true },
        { title: 'Pesagens', key: 'numeroPesagens', sortable: true },
    ];
    });

const comparativeTableData = computed(() => {
    if (!props.matrixData?.rows) return [];

    let data = props.matrixData.rows.map(row => ({
        boiId: row.boiId,
        boiNumero: row.boiNumero,
        raca: row.raca,
        pesoInicial: row.pesos[0]?.peso || null,
        pesoFinal: row.pesos[row.pesos.length - 1]?.peso || null,
        ganhoTotal: row.ganhoTotal,
        gmdMedio: row.gmdMedio,
        numeroPesagens: row.pesos.filter(p => p && p.peso !== null).length,
    }));

    // Aplicar filtro de raça
    if (tableFilterRacas.value.length > 0) {
        data = data.filter(item => tableFilterRacas.value.includes(item.raca));
    }

    // Ordenar
    data.sort((a, b) => {
        const key = tableSortBy.value;
        
        if ( key === 'raca') {
            return String(a[key]).localeCompare(String(b[key]));
        }
        
        if (a[key] === null) return 1;
        if (b[key] === null) return -1;

        if (key === 'gmdMedio' || key === 'ganhoTotal') {
            return b[key] - a[key];
        }
        return a[key] - b[key];
    });

    return data;
});

async function exportTableToExcel(){
    try {
        // Exportar apenas os dados da tabela comparativa
        const response = await api.exportComparativeTableToExcel({
            datasSelecionadas: props.selectedDates,
            sortBy: tableSortBy.value,
            racas: tableFilterRacas.value,
        });

        const filename = `tabela_comparativa_${new Date().toISOString().split('T')[0]}.xlsx`;
        api.downloadFile(response.data, filename);
    } catch (error) {
        console.error('Erro ao exportar tabela:', error);
        emit('error', 'Erro ao exportar tabela');
    }
}
</script>