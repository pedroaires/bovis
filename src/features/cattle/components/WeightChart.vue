<template>
    <div class="weight-chart-container">
        <h3 class="mb-3">Evolução do Peso</h3>
        <canvas ref="canvas" class="weight-chart"></canvas>
    </div>
</template>

<script>
export default {
    name: 'WeightChart',
    props: {
        weightHistory: {
            type: Array,
            required: true
        }
    },
    watch: {
        weightHistory: {
            handler() {
                this.$nextTick(() => {
                    this.drawChart()
                })
            },
            deep: true
        }
    },
    mounted() {
        this.drawChart()
        window.addEventListener('resize', this.drawChart)
    },
    beforeUnmount() {
        window.removeEventListener('resize', this.drawChart)
    },
    methods: {
        drawChart() {
            if (!this.$refs.canvas || !this.weightHistory.length) return

            const canvas = this.$refs.canvas
            const ctx = canvas.getContext('2d')
            
            // Configurar dimensões responsivas
            const container = canvas.parentElement
            const width = container.clientWidth
            const height = 300
            canvas.width = width
            canvas.height = height

            // Limpar canvas
            ctx.clearRect(0, 0, width, height)

            // Margens
            const padding = { top: 20, right: 30, bottom: 50, left: 60 }
            const chartWidth = width - padding.left - padding.right
            const chartHeight = height - padding.top - padding.bottom

            // Extrair dados
            const dates = this.weightHistory.map(p => {
                const d = new Date(p.data)
                return d.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit' })
            })
            const weights = this.weightHistory.map(p => Number(p.peso))

            // Calcular escalas
            const minWeight = Math.min(...weights)
            const maxWeight = Math.max(...weights)
            const weightRange = maxWeight - minWeight
            const yMin = minWeight - weightRange * 0.1
            const yMax = maxWeight + weightRange * 0.1

            // Função para mapear peso para coordenada Y
            const getY = (peso) => {
                const normalized = (peso - yMin) / (yMax - yMin)
                return padding.top + chartHeight * (1 - normalized)
            }

            // Função para mapear índice para coordenada X
            const getX = (index) => {
                return padding.left + (chartWidth / (dates.length - 1)) * index
            }

            // Desenhar grid horizontal
            ctx.strokeStyle = '#e0e0e0'
            ctx.lineWidth = 1
            const gridLines = 5
            for (let i = 0; i <= gridLines; i++) {
                const y = padding.top + (chartHeight / gridLines) * i
                ctx.beginPath()
                ctx.moveTo(padding.left, y)
                ctx.lineTo(padding.left + chartWidth, y)
                ctx.stroke()

                // Labels do eixo Y
                const value = yMax - ((yMax - yMin) / gridLines) * i
                ctx.fillStyle = '#666'
                ctx.font = '12px sans-serif'
                ctx.textAlign = 'right'
                ctx.fillText(`${value.toFixed(0)} kg`, padding.left - 10, y + 4)
            }

            // Desenhar área preenchida
            ctx.fillStyle = 'rgba(25, 118, 210, 0.1)'
            ctx.beginPath()
            ctx.moveTo(getX(0), padding.top + chartHeight)
            weights.forEach((peso, i) => {
                ctx.lineTo(getX(i), getY(peso))
            })
            ctx.lineTo(getX(weights.length - 1), padding.top + chartHeight)
            ctx.closePath()
            ctx.fill()

            // Desenhar linha
            ctx.strokeStyle = '#1976D2'
            ctx.lineWidth = 2
            ctx.beginPath()
            weights.forEach((peso, i) => {
                if (i === 0) {
                    ctx.moveTo(getX(i), getY(peso))
                } else {
                    ctx.lineTo(getX(i), getY(peso))
                }
            })
            ctx.stroke()

            // Desenhar pontos
            weights.forEach((peso, i) => {
                ctx.fillStyle = '#1976D2'
                ctx.beginPath()
                ctx.arc(getX(i), getY(peso), 4, 0, Math.PI * 2)
                ctx.fill()
                
                // Borda branca nos pontos
                ctx.strokeStyle = '#fff'
                ctx.lineWidth = 2
                ctx.stroke()
            })

            // Labels do eixo X
            ctx.fillStyle = '#666'
            ctx.font = '12px sans-serif'
            ctx.textAlign = 'center'
            dates.forEach((date, i) => {
                const x = getX(i)
                const y = padding.top + chartHeight + 20
                
                // Exibir apenas algumas labels se houver muitos pontos
                if (dates.length <= 10 || i % Math.ceil(dates.length / 10) === 0 || i === dates.length - 1) {
                    ctx.save()
                    ctx.translate(x, y)
                    ctx.rotate(-Math.PI / 4)
                    ctx.fillText(date, 0, 0)
                    ctx.restore()
                }
            })

            // Eixos
            ctx.strokeStyle = '#666'
            ctx.lineWidth = 2
            
            // Eixo Y
            ctx.beginPath()
            ctx.moveTo(padding.left, padding.top)
            ctx.lineTo(padding.left, padding.top + chartHeight)
            ctx.stroke()

            // Eixo X
            ctx.beginPath()
            ctx.moveTo(padding.left, padding.top + chartHeight)
            ctx.lineTo(padding.left + chartWidth, padding.top + chartHeight)
            ctx.stroke()
        }
    }
}
</script>

<style scoped>
.weight-chart-container {
    margin-bottom: 24px;
}

.weight-chart {
    width: 100%;
    height: 300px;
    display: block;
}

h3 {
    color: rgba(0, 0, 0, 0.87);
    font-weight: 500;
}
</style>