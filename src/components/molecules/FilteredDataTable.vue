<template>
    <div>
        <div class="d-flex justify-space-between align-center mb-4">
            <h1 class="mb-0">{{ title }}</h1>
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

        <!-- Tabela -->
        <v-data-table-server
            :headers="headers"
            :items="items"
            :items-length="totalItems"
            :loading="loading"
            @update:options="handleOptionsUpdate"
            class="elevation-1"
        >
            <!-- Repassar todos os slots personalizados -->
            <template v-for="(_, slot) in $slots" v-slot:[slot]="scope">
                <slot :name="slot" v-bind="scope" />
            </template>
        </v-data-table-server>
    </div>
</template>

<script>
export default {
    name: 'FilteredDataTable',
    props: {
        title: {
            type: String,
            required: true
        },
        headers: {
            type: Array,
            required: true
        },
        items: {
            type: Array,
            default: () => []
        },
        totalItems: {
            type: Number,
            default: 0
        },
        loading: {
            type: Boolean,
            default: false
        },
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
    },
    emits: ['update:options', 'apply-filters'],
    data() {
        return {
            showFilters: false,
            internalFilters: { ...this.filters }
        }
    },
    computed: {
        activeFiltersCount() {
            return Object.values(this.internalFilters).filter(v => v !== null && v !== '').length
        },
        activeFilterChips() {
            const chips = {}

            Object.entries(this.internalFilters).forEach(([key, value]) => {
                if (value !== null && value !== '') {
                    const label = this.filterLabels[key] || key
                    let displayValue = value

                    // Usar formatter customizado se existir
                    if (this.filterFormatters[key]) {
                        displayValue = this.filterFormatters[key](value)
                    }

                    chips[key] = { label, value: displayValue }
                }
            })

            return chips
        }
    },
    watch: {
        filters: {
            handler(newVal) {
                this.internalFilters = { ...newVal }
            },
            deep: true
        }
    },
    methods: {
        applyFilters() {
            this.$emit('apply-filters', this.internalFilters)
        },
        clearFilter(key) {
            this.internalFilters[key] = null
            this.applyFilters()
        },
        clearAllFilters() {
            Object.keys(this.internalFilters).forEach(key => {
                this.internalFilters[key] = null
            })
            this.applyFilters()
        },
        handleOptionsUpdate(options) {
            this.$emit('update:options', options)
        }
    }
}
</script>

<style scoped>
h1 {
    font-size: 1.75rem;
    font-weight: 500;
}

.gap-2 {
    gap: 8px;
}
</style>