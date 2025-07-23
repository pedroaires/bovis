<template>
  <v-container>
    <v-data-table-server
      :headers="headers"
      :items="items"
      :items-length="totalItems"
      :options="internalOptions"
      :loading="loading"
      :items-per-page-options="itemsPerPageOptions"
      @update:options="updateOptions"
    />
  </v-container>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  headers: Array,
  items: Array,
  options: Object,
  totalItems: Number,
  loading: Boolean,
  itemsPerPageOptions: {
    type: Array,
    default: () => [10, 20, 50, 100],
  },
})

const emit = defineEmits(['update:options'])

const internalOptions = ref({ ...props.options })

watch(
  () => props.options,
  val => (internalOptions.value = { ...val }),
  { deep: true }
)

function updateOptions(newOpts) {
  emit('update:options', newOpts)
}
</script>
