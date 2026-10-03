import { computed, ref } from "vue"
import * as XLSX from 'xlsx';
import { parseDecimal, parseInteger } from "@/shared/utils/parsers/numberParser";
import api from "@/services/api";

async function parseFileXlsx(file) {
    if (!(file instanceof File)) throw new Error('Arquivo inválido.')
    const buf = await file.arrayBuffer()
    const wb = XLSX.read(buf, { type: 'array' })
    const sheetName = wb.SheetNames?.[0]
    if (!sheetName) return { rows: [], all: [] }
    const ws = wb.Sheets[sheetName]
    const json = XLSX.utils.sheet_to_json(ws, { defval: '' })
    const norm = json.map(normalizeKeys)
    return { rows: norm.slice(0, 100), all: norm }
}

function normalizeKeys(row) {
    const out = {}
    for (const k of Object.keys(row)) out[String(k).trim()] = row[k]
    return out
}

function validateItemData(item, utils) {
    const errors = []
    
    if (!item.numero || item.numero === '') {
        errors.push('numero obrigatório')
    }
    if (!item.raca || item.raca === '') {
        errors.push('raca obrigatória')
    }
    if (!item.peso || utils.parseDecimal(item.peso) <= 0) {
        errors.push('peso inválido')
    }
    if (!item.valorUnitario || utils.parseDecimal(item.valorUnitario) <= 0) {
        errors.push('valorUnitario inválido')
    }
    
    return errors
}

export function usePurchaseUpload() {
    
    // Estado do arquivo
    const previewRows = ref([])
    const fullData = ref([])
    const totalRows = ref(0)
    const parseError = ref(null)
    
    // Estado do submit
    const uploading = ref(false)
    const submitError = ref(null)

    const expectedItemColumns = ['numero', 'raca', 'peso', 'valorUnitario']

    const previewReady = computed(() => parseError.value !== null || fullData.value.length > 0)
    
    const valorTotalItens = computed(() =>
        fullData.value.reduce((acc, it) => acc + (parseDecimal(it?.valorUnitario) ?? 0), 0).toFixed(2)
    )

    const canSubmit = computed(() => 
        !parseError.value && 
        !submitError.value && 
        fullData.value.length > 0 && 
        !uploading.value
    )

    const onPreviewFile = async (file) => {
        parseError.value = null
        previewRows.value = []
        fullData.value = []
        totalRows.value = 0

        try {
            const { rows, all } = await parseFileXlsx(file)
            const cols = all.length ? Object.keys(all[0]) : []
            const missing = expectedItemColumns.filter(c => !cols.includes(c))
            
            if (missing.length) {
                throw new Error(`Colunas ausentes no arquivo: ${missing.join(', ')}`)
            }
            
            const validationErrors = []
            all.forEach((item, idx) => {
                const itemErrors = validateItemData(item, { parseDecimal, parseInteger })
                if (itemErrors.length) {
                    validationErrors.push(`Linha ${idx + 2}: ${itemErrors.join(', ')}`)
                }
            })
            
            if (validationErrors.length > 0) {
                throw new Error(`Erros de validação:\n${validationErrors.slice(0, 5).join('\n')}${validationErrors.length > 5 ? `\n... e mais ${validationErrors.length - 5} erros` : ''}`)
            }
            
            fullData.value = all
            previewRows.value = rows.slice(0, 100)
            totalRows.value = all.length
            
        } catch (e) {
            parseError.value = e?.message || 'Falha ao processar o arquivo.'
        }
    }
    
    function onPreviewEdited(editedPreview) {
        // Atualiza apenas as linhas que existem no preview
        editedPreview.forEach((editedRow, index) => {
            if (fullData.value[index]) {
                fullData.value[index] = { ...fullData.value[index], ...editedRow }
            }
        })
        previewRows.value = editedPreview
    }

    const onDownloadTemplate = () => {
        const csv = [
            'numero,raca,peso,valorUnitario',
            '12345,Nelore,450.0,3500.00',
        ].join('\n')
        const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
        const url = URL.createObjectURL(blob)
        const a = document.createElement('a')
        a.href = url
        a.download = 'template_itens_compra.csv'
        a.click()
        URL.revokeObjectURL(url)
    }

    const submitCompra = async (formData) => {
        if (!canSubmit.value) {
            throw new Error('Dados inválidos para submissão')
        }

        submitError.value = null
        uploading.value = true

        try {
            const compraBoiDTOS = fullData.value.map(it => ({
                boiRequestDTO: {
                    numero: parseInteger(it.numero),
                    raca: (it.raca ?? '').trim(),
                    estadoBoi: 'NA_FAZENDA',
                },
                valor: parseDecimal(it.valorUnitario),
                pesagemDTO: {
                    peso: parseDecimal(it.peso)
                }
            }))

            const total = formData.valorTotal !== '' && formData.valorTotal !== null
                ? parseDecimal(formData.valorTotal)
                : compraBoiDTOS.reduce((acc, x) => acc + (x.valor ?? 0), 0)

            const payload = {
                valor: total,
                data: String(formData.data).slice(0, 10),
                descricao: formData.descricao ?? '',
                modeloTransacao: formData.modeloTransacao,
                contraParte: formData.contraParte ?? '',
                compraBoiDTOS,
            }

            await api.createCompra(payload)
            return { success: true, message: 'Compra criada com sucesso.' }
            
        } catch (e) {
            console.error('createCompra error:', e?.response?.data || e)
            submitError.value = e?.response?.data?.message || 'Falha ao criar compra. Verifique os dados.'
            throw e
        } finally {
            uploading.value = false
        }
    }

    const reset = () => {
        previewRows.value = []
        fullData.value = []
        totalRows.value = 0
        parseError.value = null
        submitError.value = null
        uploading.value = false
    }

    return {
        // Estado
        expectedItemColumns,
        fullData,
        previewRows,
        totalRows,
        parseError,
        submitError,
        uploading,

        // Computeds
        previewReady,
        valorTotalItens,
        canSubmit,

        // Métodos
        onPreviewFile,
        onPreviewEdited,
        onDownloadTemplate,
        submitCompra,
        reset
    }
}