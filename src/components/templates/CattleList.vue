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
      item-key="id"
      :show-select="selectable"
      v-model:selected="internalSelected"
      :items-per-page="itemsPerPage"
    >
      <!-- célula customizada: estado -->
      <template #[`item.${estadoKey}`]="{ item }">
        <v-chip :color="statusColor(item[estadoKey])" size="small">
          {{ item[estadoKey] }}
        </v-chip>
      </template>
    </v-data-table>

    <slot name="actions" :selected="internalSelected"></slot>
  </div>
</template>

<script>
export default {
  name: 'CattleList',
  props: {
    title: { type: String, default: 'Lista de Gado' },
    items: { type: Array, default: () => [] },
    loading: { type: Boolean, default: false },

    // seleção
    selectable: { type: Boolean, default: false },
    modelValue: { type: Array, default: () => [] }, 

    // busca/filtro
    enableSearch: { type: Boolean, default: true },
    filterEstado: { type: String, default: '' },        // ex: 'NA_FAZENDA'
    estadoKey: { type: String, default: 'estadoBoi' },  // desacoplado do nome do campo

    // UI
    itemsPerPage: { type: Number, default: 10 },

    // headers (permite custom externo; default mantém os seus)
    headersProp: { type: Array, default: null },

    // mapa de cores por estado (desacoplado)
    statusColorMap: {
      type: Object,
      default: () => ({
        NA_FAZENDA: 'success',
        VENDIDO: 'warning',
        ABATIDO: 'error',
      }),
    },
  },
  emits: ['update:modelValue'],
  data() {
    return {
      search: '',
      defaultHeaders: [
        { title: 'ID', key: 'id' },
        { title: 'Número', key: 'numero' },
        { title: 'Raça', key: 'raca' },
        { title: 'Estado do Boi', key: 'estadoBoi' },
        { title: 'Data Última Pesagem', key: 'dataUltimaPesagem' },
        { title: 'Último Peso', key: 'peso' },
      ],
    }
  },
  computed: {
    headers() {
      return this.headersProp ?? this.defaultHeaders
    },
    internalSelected: {
      get() { return this.modelValue },
      set(v) { this.$emit('update:modelValue', v) },
    },
    filteredItems() {
      if (!this.filterEstado) return this.items
      return this.items.filter(x => x?.[this.estadoKey] === this.filterEstado)
    },
  },
  methods: {
    statusColor(value) {
      return this.statusColorMap?.[value] ?? 'default'
    },
  },
}
</script>

<style scoped>
</style>
