<template>
    <div>
    <div class="d-flex justify-space-between align-center mb-4">
            <v-btn
                :prepend-icon="showFilters ? 'mdi-filter-off' : 'mdi-filter'"
                variant="outlined"
                @click="showFilters = !showFilters"
            >
                {{ showFilters ? 'Ocultar Filtros' : 'Filtros' }}
                <v-badge
                    v-if="activeFiltersCount > 0"
                    :content="activeFiltersCount"
                    color="primary"
                    inline
                    class="ml-2"
                />
            </v-btn>
        </div>

        <!-- Chips de Filtros Ativos -->
        <div v-if="activeFiltersCount > 0 && !showFilters" class="mb-3">
            <v-chip
                v-for="(filter, key) in activeFilterChips"
                :key="key"
                closable
                class="mr-2 mb-2"
                size="small"
                @click:close="clearFilter(key)"
            >
                {{ filter.label }}: {{ filter.value }}
            </v-chip>
            <v-btn
                variant="text"
                size="small"
                color="error"
                @click="clearAllFilters"
            >
                Limpar todos
            </v-btn>
        </div>

        <!-- Painel de Filtros Expansível -->
        <v-expand-transition>
            <v-card v-show="showFilters" class="mb-4" variant="outlined">
                <v-card-text>
                    <slot name="filters" :filters="internalFilters" />

                    <v-divider class="my-3" />

                    <div class="d-flex justify-end gap-2">
                        <v-btn variant="text" @click="clearAllFilters">
                            Limpar
                        </v-btn>
                        <v-btn color="primary" @click="applyFilters">
                            Aplicar Filtros
                        </v-btn>
                    </div>
                </v-card-text>
            </v-card>
        </v-expand-transition>
    </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
const props = defineProps({
    filters: {
        type: Object,
        default: () => ({})
    },
    filterLabels: {
        type: Object,
        default: () => ({})
    },
    filterFormatters: {
        type: Object,
        default: () => ({})
    }
})

const emit = defineEmits(['apply-filters'])

const showFilters = ref(false)
const internalFilters = ref({ ...props.filters });

const activeFiltersCount = computed(() => {
    return Object.values(internalFilters.value).filter(v => v !== null && v !== '').length
})
const activeFilterChips = computed(() => {
    const chips = {}

    Object.entries(internalFilters.value).forEach(([key, value]) => {
        if (value !== null && value !== '') {
            const label = props.filterLabels[key] || key
            let displayValue = value

            // Usar formatter customizado se existir
            if (props.filterFormatters[key]) {
                displayValue = props.filterFormatters[key](value)
            }

            chips[key] = { label, value: displayValue }
        }
    })

    return chips
})
watch(props.filters, (newVal) => {
    console.log(props.filters)
    internalFilters.value = { ...newVal }
}, {deep: true})

function applyFilters() {
    emit('apply-filters', internalFilters.value)
}
function clearFilter(key) {
    internalFilters.value[key] = null
    applyFilters()
}
function clearAllFilters() {
    Object.keys(internalFilters.value).forEach(key => {
        internalFilters.value[key] = null
    })
    applyFilters()
}
</script>

<style scoped>
.gap-2 {
    gap: 8px;
}
</style>