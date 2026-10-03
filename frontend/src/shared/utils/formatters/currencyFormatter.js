import { parseDecimal } from "../parsers/numberParser";

export function normalizeValor(item) {
    item.valor = parseDecimal(item.valorStr)
    item.valorStr = item.valor != null ? String(item.valor) : ''
}
export function formatCurrency(value) {
    if (value == null) return '0,00';
    return Number(value).toFixed(2).replace('.', ',').replace(/\B(?=(\d{3})+(?!\d))/g, '.');
}