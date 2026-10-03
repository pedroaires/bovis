<template>
    <div>
    <h1 class="text-h5 mb-4">{{ title }}</h1>
    <div>
        <slot name="control-panel"
        :item-count="items.length"
        >
        <v-text-field
          v-if="enableSearch"
          v-model="search"
          label="Buscar"
          prepend-inner-icon="mdi-magnify"
          class="mb-3"
          density="comfortable"
          clearable
        />
        </slot>
    </div>

    <v-data-table
      :headers="headers"
      :items="items"
      :loading="loading"
      :search="enableSearch ? search : undefined"
      :item-value="itemValue"
      :show-select="selectable"
      v-model="selected"
      :items-per-page="itemsPerPage"
      :return-object="true"
    >
    <template 
        v-for="(_, slot) in $slots"
        v-slot:[slot]="scope"
    >
        <slot 
            v-if="slot.startsWith('item.') || slot.startsWith('header.')"
            :name="slot" 
            v-bind="scope"></slot>
    </template>
    </v-data-table>

    <slot name="actions" :selected="selected"></slot>
    </div>
</template>
<script setup>
import { useTableSelection } from '../composables/useTableSelection';
const emit = defineEmits(['update:modelValue']);
import { ref } from 'vue';

const search = ref('')
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
    selectable: {
        type: Boolean,
        default: false,
    },
    modelValue:{
        type: Array,
        default: () => []
    },
    enableSearch: { 
        type: Boolean, 
        default: true 
    },
    loading: {
        type: Boolean,
        default: false
    },
    itemsPerPage: {
        type: Number,
        default: 10
    },
    itemValue: {
        type: String,
        default: 'id'
    }
})
const { selected } = useTableSelection(props, emit);
</script>