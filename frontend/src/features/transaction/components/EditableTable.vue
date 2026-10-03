<template>
  <div>
    <v-alert v-if="missing.length" type="warning" class="mb-4" variant="tonal">
      Colunas ausentes: <strong>{{ missing.join(', ') }}</strong>
    </v-alert>

    <div class="text-caption mb-2">
      Mostrando {{ localRows.length }} de {{ total }} linhas.
    </div>

    <v-data-table
      :headers="headers"
      :items="localRows"
      :items-per-page="100"
      density="compact"
      class="elevation-1"
      fixed-header
    >
      <template #item="{ item, index, columns }">
        <tr>
          <td
            v-for="col in columns"
            :key="col.key"
            class="py-1"
            :class="{'editable': isEditable(col.key)}"
          >
            <!-- Somente leitura quando não for editável -->
            <template v-if="!isEditable(col.key)">
              <span class="text-high-emphasis">{{ item[col.key] }}</span>
            </template>
            <template v-else>
              <!-- Estado leitura com affordance -->
              <div
                v-if="!isEditing(index, col.key)"
                class="cell-read d-flex align-center gap-1 cursor-pointer"
                @click="startEdit(index, col.key, item[col.key])"
              >
                <span class="text-high-emphasis">{{ item[col.key] }}</span>
                <v-icon size="16">mdi-pencil</v-icon>
              </div>

              <!-- Estado edição -->
              <v-text-field
                v-else
                v-model="draft"
                density="compact"
                hide-details
                variant="plain"
                autofocus
                @keydown.enter.prevent="saveEdit()"
                @keydown.esc.prevent="cancelEdit()"
                @blur="saveEdit()"
              />
            </template>
          </td>
        </tr>
      </template>
    </v-data-table>
  </div>
</template>

<script setup>
import { computed, ref, watch, toRaw } from 'vue'

const emit = defineEmits(['update:data'])

const props = defineProps({
  data: { type: Array, default: () => [] },
  expected: { type: Array, default: () => [] },
  total: { type: Number, default: 0 },
  editable: { type: Array, default: () => [] }, // vazio => todas
})

const localRows = ref(structuredClone(props.data) ?? [])
watch(() => props.data, (val) => { localRows.value = structuredClone(val ?? []) }, { deep: false })

const headers = computed(() => {
  const keys = localRows.value.length ? Object.keys(localRows.value[0]) : []
  return keys.map(k => ({ title: k, key: k }))
})

const missing = computed(() => {
  const keys = localRows.value.length ? Object.keys(localRows.value[0]) : []
  return props.expected.filter(col => !keys.includes(col))
})

const isEditable = (key) => props.editable.length === 0 || props.editable.includes(key)

/* --- Estado de edição por célula --- */
const editingKey = ref(null)     // string "row|col" ou null
const draft = ref('')            // valor temporário
let editRow = -1
let editColKey = ''

const keyOf = (rowIdx, colKey) => `${rowIdx}|${colKey}`
const isEditing = (rowIdx, colKey) => editingKey.value === keyOf(rowIdx, colKey)

function startEdit(rowIdx, colKey, currentValue) {
  editingKey.value = keyOf(rowIdx, colKey)
  editRow = rowIdx
  editColKey = colKey
  draft.value = currentValue ?? ''
}

function saveEdit() {
  if (editingKey.value == null) return
  localRows.value[editRow][editColKey] = draft.value
  const clean = JSON.parse(JSON.stringify(toRaw(localRows.value)))
  emit('update:data', clean)
  editingKey.value = null
}

function cancelEdit() {
  editingKey.value = null
}
</script>

<style scoped>
.cursor-pointer { cursor: pointer; }
td.editable:hover { background: color-mix(in srgb, currentColor 6%, transparent); }
.cell-read { min-height: 32px; }
</style>
