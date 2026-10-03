import { ref, computed } from 'vue';


export function useTableFilters(items, options = {}) {
  const search = ref('');
  const filterField = ref(options.filterField || '');
  const filterValue = ref(options.filterValue || '');

  const filteredItems = computed(() => {
    let result = items.value;
    
    if (filterField.value && filterValue.value) {
      result = result.filter(x => 
        x?.[filterField.value] === filterValue.value
      );
    }
    
    return result;
  });

  return {
    search,
    filterField,
    filterValue,
    filteredItems,
  };
}