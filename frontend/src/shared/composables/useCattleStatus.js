export function useCattleStatus(customMap = {}) {
  const defaultMap = {
    NA_FAZENDA: 'success',
    VENDIDO: 'warning',
    ABATIDO: 'error',
  };
  
  const finalMap = { ...defaultMap, ...customMap };

  const getColor = (statusValue) => {
    return finalMap[statusValue] ?? 'default';
  };

  const getIcon = (statusValue) => {
    const icons = {
      NA_FAZENDA: 'mdi-barn',
      VENDIDO: 'mdi-cash',
      ABATIDO: 'mdi-close-octagon',
    };
    return icons[statusValue] ?? 'mdi-help-circle';
  };

  return {
    getColor,
    getIcon,
  };
}