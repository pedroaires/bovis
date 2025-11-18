export function useUtils(){
    function parseDecimal(s) {
        if (s === null || s === undefined || s === '') return null
        const n = Number(String(s).replace(',', '.'))
        return Number.isFinite(n) ? n : null
    }
    return{
        parseDecimal
    }
}