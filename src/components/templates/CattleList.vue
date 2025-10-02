// ===== templates/CattleList.vue (refatorado para reutilização) =====
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
      :items="filteredCattle"
      :search="enableSearch ? search : undefined"
      :loading="loading"
      item-key="id"
      :show-select="selectable"
      v-model:selected="internalSelected"
      :items-per-page="itemsPerPage"
    >
    <template #[`item.estadoBoi`]="{ item }">
      <v-chip :color="item.estadoBoi === 'NA_FAZENDA' ? 'success' : 'default'" size="small">
        {{ item.estadoBoi }}
      </v-chip>
    </template>
    </v-data-table>

    <slot name="actions" :selected="internalSelected"></slot>
  </div>
</template>

<script>
import api from '../../services/api.js'

export default {
  name: 'CattleList',
  props: {
    title: { type: String, default: 'Lista de Gado' },
    selectable: { type: Boolean, default: false },
    enableSearch: { type: Boolean, default: true },
    filterEstado: { type: String, default: '' }, // ex: 'NA_FAZENDA'
    // two-way binding opcional para seleção externa
    modelValue: { type: Array, default: () => [] },
    itemsPerPage: { type: Number, default: 10 },
  },
  emits: ['update:modelValue'],
  data() {
    return {
      loading: false,
      search: '',
      cattle: [],
      headers: [
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
    internalSelected: {
      get() { return this.modelValue },
      set(v) { this.$emit('update:modelValue', v) },
    },
    filteredCattle() {
      if (!this.filterEstado) return this.cattle
      return this.cattle.filter(x => x.estadoBoi === this.filterEstado)
    },
  },
  async created() {
    this.loading = true
    try {
      const response = await api.getActiveCattleWithWeight()
      this.cattle = response.data || []
    } catch (error) {
      console.error('Erro ao buscar dados do backend:', error)
    } finally {
      this.loading = false
    }
  },
}
</script>

<style scoped>
</style>
