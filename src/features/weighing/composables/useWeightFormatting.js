import { useUtils } from "@/shared/composables/useUtils";

export function useWeightFormatting() {
    const { parseDecimal } = useUtils();   
    const formatDate = (dateString) => {
        if (!dateString) return '—';
        const date = new Date(dateString + 'T00:00:00'); 
        return date.toLocaleDateString('pt-BR');
    };

    const formatDateShort = (dateString) => {
        if (!dateString) return '—';
        // Adiciona T00:00:00 para garantir que a data seja interpretada como local
        const date = new Date(dateString + 'T00:00:00'); 
        return date.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: '2-digit' });
    };
    
    const formatPeso = (peso) => {
        if (peso === null || peso === undefined) return '—';
        return `${Number(peso).toFixed(1)}`;
    };

    const formatPesoKg = (peso) => {
        if (peso === null || peso === undefined) return '—';
        return `${Number(peso).toFixed(1)} kg`;
    };

    const formatGMD = (gmd) => {
        if (gmd === null || gmd === undefined) return '—';
        return `${Number(gmd).toFixed(2)}`;
    };

    const formatGanhoTotal = (ganho) => {
        if (ganho === null || ganho === undefined) return '—';
        const valor = Number(ganho).toFixed(1);
        return ganho >= 0 ? `+${valor} kg` : `${valor} kg`;
    };

    const calculateDaysBetween = (date1, date2) => {
        const d1 = new Date(date1 + 'T00:00:00');
        const d2 = new Date(date2 + 'T00:00:00');
        const diffTime = Math.abs(d2 - d1);
        return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    };

    const getGMDColor = (gmd) => {
        if (gmd === null || gmd === undefined) return 'grey';
        if (gmd >= 1.2) return 'success';
        if (gmd >= 0.8) return 'warning';
        return 'error';
    };

    const getGainClass = (ganho) => {
        if (ganho === null || ganho === undefined) return '';
        return ganho >= 0 ? 'text-success' : 'text-error';
    }

    function formatCurrency(value) {
        if (value == null) return '0,00';
        return Number(value).toFixed(2).replace('.', ',').replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    }
    function normalizePeso(item) {
        item.peso = parseDecimal(item.pesoStr)
        item.pesoStr = item.peso != null ? String(item.peso).replace('.', ',') : ''
    }
    function normalizeValor(item) {
        item.valor = parseDecimal(item.valorStr)
        item.valorStr = item.valor != null ? String(item.valor) : ''
    }

    
    return {
        formatDate,
        formatDateShort,
        formatPeso,
        formatPesoKg,
        formatGMD,
        formatGanhoTotal,
        calculateDaysBetween,
        getGMDColor,
        getGainClass,
        formatCurrency,
        normalizePeso,
        normalizeValor
    };
}