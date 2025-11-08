<template>
  <div>
    <h1 class="text-h5 mb-4">{{ title }}</h1>

    <v-text-field
      v-if="enableSearch"
      v-model="search"
      label="Buscar"
      prepend-inner-icon="mdi-magnify"
      class="mb-3"
      density="comfortable"
      clearable
    />

    <v-data-table
      :headers="headers"
      :items="filteredItems"
      :search="enableSearch ? search : undefined"
      :loading="loading"
      item-value="id"
      :show-select="selectable"
      v-model="selected"
      :items-per-page="itemsPerPage"
      :return-object="true"
    >
      <!-- célula customizada: estado -->
      <template #[`item.${estadoKey}`]="{ item }">
        <v-chip :color="getColor(item[estadoKey])" size="small">
          {{ item[estadoKey] }}
        </v-chip>
      </template>
    </v-data-table>

    <slot name="actions" :selected="selected"></slot>
  </div>
</template>
<script setup>
import { computed } from 'vue';
import { useTableFilters } from '../composables/useTableFilters';
import { useTableSelection } from '../composables/useTableSelection';
import { useCattleStatus } from '../composables/useCattleStatus';


const props = defineProps({
  title: { type: String, default: 'Lista de Gado' },
  items: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },

  selectable: { type: Boolean, default: false },
  modelValue: { type: Array, default: () => [] },

  enableSearch: { type: Boolean, default: true },
  filterEstado: { type: String, default: '' },       
  estadoKey: { type: String, default: 'estadoBoi' },  

  itemsPerPage: { type: Number, default: 10 },

  headersProp: { type: Array, default: null },

  statusColorMap: { type: Object, default: () => ({}) },
});

const emit = defineEmits(['update:modelValue']);

const itemsRef = computed(() => props.items);
const { search, filteredItems } = useTableFilters(itemsRef, {
  filterKey: computed(() => props.estadoKey),
  filterValue: computed(() => props.filterEstado),
});

const { selected } = useTableSelection(props, emit);
const { getColor } = useCattleStatus(props.statusColorMap);


const defaultHeaders = [
  { title: 'ID', key: 'id' },
  { title: 'Número', key: 'numero' },
  { title: 'Raça', key: 'raca' },
  { title: 'Estado do Boi', key: 'estadoBoi' },
  { title: 'Data Última Pesagem', key: 'dataUltimaPesagem' },
  { title: 'Último Peso', key: 'peso' },
];

const headers = computed(() => {
  return props.headersProp ?? defaultHeaders;
});


</script>
