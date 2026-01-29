import { parseDecimal } from "../parsers/numberParser";

export const formatPeso = (peso) => {
    if (peso === null || peso === undefined) return '—';
    return `${Number(peso).toFixed(1)}`;
};

export const formatPesoKg = (peso) => {
    if (peso === null || peso === undefined) return '—';
    return `${Number(peso).toFixed(1)} kg`;
};

export const formatGMD = (gmd) => {
    if (gmd === null || gmd === undefined) return '—';
    return `${Number(gmd).toFixed(2)}`;
};

export const formatGanhoTotal = (ganho) => {
    if (ganho === null || ganho === undefined) return '—';
    const valor = Number(ganho).toFixed(1);
    return ganho >= 0 ? `+${valor} kg` : `${valor} kg`;
};
export function normalizePeso(item) {
    item.peso = parseDecimal(item.pesoStr)
    item.pesoStr = item.peso != null ? String(item.peso).replace('.', ',') : ''
}