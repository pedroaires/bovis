<template>
    <div>
        <div class="d-flex justify-space-between align-center mb-4">
            <h1 class="mb-0">{{ title }}</h1>
        </div>
        <div>
            <TableFilterPanel
            :filters="props.filters"
            :filter-formatters="props.filterFormatters"
            :filter-labels="props.filterLabels"
            @apply-filters="handleApplyFilters"
            >
            <template #filters="{ filters }">
                <slot name="filters" :filters="filters" />
            </template>
            </TableFilterPanel>
        </div>

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
<script setup>
import TableFilterPanel from '@/shared/components/TableFilterPanel.vue'

const props = defineProps({
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
})
const emit = defineEmits(['update:options', 'apply-filters']);

function handleOptionsUpdate(options) {
    emit('update:options', options)
}
function handleApplyFilters(newFilters) {
    emit('apply-filters', newFilters)
}
</script>


<style scoped>
h1 {
    font-size: 1.75rem;
    font-weight: 500;
}
</style>