export function useWeightComparison(){
    const getTotalBois = (groupedWeighings, selectedItems) => {
        groupedWeighings
            .filter(g => selectedItems.includes(g.data))
            .reduce((sum, g) => sum + (g.totalBois || 0), 0);
    };
    const calculateDaysBetween = (date1, date2) => {
        const d1 = new Date(date1 + 'T00:00:00');
        const d2 = new Date(date2 + 'T00:00:00');
        const diffTime = Math.abs(d2 - d1);
        return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    };

    const getTotalDays = (sortedSelectedDates) => {
        if (sortedSelectedDates.length < 2) return 0;
        const first = sortedSelectedDates[0];
        const last = sortedSelectedDates[sortedSelectedDates.length - 1];
        return calculateDaysBetween(first, last);
    };

    const getAverageWeightSimple = (groupedWeighings, selectedItems) => {
        const selected = groupedWeighings.filter(g => 
            selectedItems.includes(g.data)
        );
        if (selected.length === 0) return '—';
        const avg = selected.reduce((sum, g) => sum + (g.pesoMedio || 0), 0) / selected.length;
        return `${avg.toFixed(1)} kg`;
    };

    const getWeightVariation = (groupedWeighings, selectedItems) => {
        const selected = groupedWeighings
            .filter(g => selectedItems.includes(g.data))
            .sort((a, b) => a.data.localeCompare(b.data));
        
        if (selected.length < 2) return '—';
        
        const first = selected[0].pesoMedio;
        const last = selected[selected.length - 1].pesoMedio;
        const diff = last - first;
        
        return diff >= 0 ? `+${diff.toFixed(1)} kg` : `${diff.toFixed(1)} kg`;
    };

    return {
        getTotalBois,
        calculateDaysBetween,
        getTotalDays,
        getAverageWeightSimple,
        getWeightVariation
    }

}