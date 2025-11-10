<template>
    <FilteredDataTable
        title="Desempenho do Gado"
        :headers="headers"
        :items="cattleSummary"
        :total-items="totalElements"
        :loading="loading"
        :filters="filters"
        :filter-labels="filterLabels"
        :filter-formatters="filterFormatters"
        @apply-filters="onApplyFilters"
        @update:options="onOptionsUpdate"
    >
        <!-- Slot de Filtros -->
        <template #filters="{ filters }">
            <v-row dense>
                <v-col cols="12" sm="6" md="3">
                    <v-text-field
                        v-model.number="filters.numeroBoi"
                        label="Número do Boi"
                        type="number"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>

                <v-col cols="12" sm="6" md="3">
                    <v-select
                        v-model="filters.racaId"
                        :items="racas"
                        item-title="nome"
                        item-value="id"
                        label="Raça"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>

                <v-col cols="12" sm="6" md="3">
                    <v-select
                        v-model="filters.estadoBoi"
                        :items="estadosOptions"
                        label="Estado do Boi"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>

                <v-col cols="12" sm="6" md="3">
                    <v-text-field
                        v-model.number="filters.pesoMin"
                        label="Peso Mínimo (kg)"
                        type="number"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>

                <v-col cols="12" sm="6" md="3">
                    <v-text-field
                        v-model.number="filters.pesoMax"
                        label="Peso Máximo (kg)"
                        type="number"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>

                <v-col cols="12" sm="6" md="3">
                    <v-text-field
                        v-model="filters.dataPesagemInicio"
                        label="Pesagem de"
                        type="date"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>

                <v-col cols="12" sm="6" md="3">
                    <v-text-field
                        v-model="filters.dataPesagemFim"
                        label="Pesagem até"
                        type="date"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>

                <v-col cols="12" sm="6" md="3">
                    <v-text-field
                        v-model.number="filters.gmdMin"
                        label="GMD Mínimo (kg/dia)"
                        type="number"
                        step="0.1"
                        variant="outlined"
                        density="compact"
                        clearable
                        hide-details
                    />
                </v-col>
            </v-row>
        </template>

        <!-- Slots personalizados da tabela -->
        <template #[`item.gmd`]="{ item }">
            <v-chip
                :color="getGMDColor(item.gmd)"
                variant="flat"
                size="small"
            >
                {{ formatGMD(item.gmd)}} kg/dia
            </v-chip>
        </template>

        <template #[`item.pesoAtual`]="{ item }">
            {{ formatPeso(item.pesoAtual) }}
        </template>

        <template #[`item.actions`]="{ item }">
            <v-btn
                icon="mdi-chart-line"
                variant="text"
                color="primary"
                size="small"
                @click="openDetails(item)"
            />
        </template>
    </FilteredDataTable>

    <!-- Modal de Detalhes -->
    <CattleDetailModal
        v-model="showDetailsDialog"
        :cattle="selectedCattle"
    />
</template>
<script setup>
import FilteredDataTable from '@/features/cattle/components/FilteredDataTable.vue';
import CattleDetailModal from '@/features/cattle/components/CattleDetailModal.vue';
import { useWeightFormatting } from '@/features/weighing/composables/useWeightFormatting';
import { useCattlePerformance } from '../composables/useCattlePerformance';
import { useCattleFilters } from '../composables/useCattleFilters';
import { ref, onMounted } from 'vue';

const {
    formatGMD,
    formatPeso,
    getGMDColor,
} = useWeightFormatting();

const {
    cattleSummary,
    loading,
    totalElements,
    options,
    headers,
    fetchCattleSummary,
    onOptionsUpdate,
} = useCattlePerformance();

const {
    filters,
    filterLabels,
    estadosOptions,
    racas,
    filterFormatters,
} = useCattleFilters();

const showDetailsDialog = ref(false)
const selectedCattle = ref(null)



function onApplyFilters(newFilters) {
    filters.value = { ...newFilters }
    options.value.page = 1
    fetchCattleSummary(filters.value)
}

function openDetails(cattle) {
    selectedCattle.value = cattle
    showDetailsDialog.value = true
}

onMounted(() => {
    fetchCattleSummary()
})
</script>
