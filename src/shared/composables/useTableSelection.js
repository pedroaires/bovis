import { computed } from 'vue';


export function useTableSelection(props, emit) {
  const selected = computed({
    get: () => props.modelValue || [],
    set: (value) => emit('update:modelValue', value),
  });

  const clearSelection = () => {
    selected.value = [];
  };

  return {
    selected,
    clearSelection,
  };
}