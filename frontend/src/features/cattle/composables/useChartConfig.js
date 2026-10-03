import { computed } from "vue";
export function useChartConfig(chartCategoriesRef){
    const chartOptions = computed(() => ({
        chart: {
            type: 'area',
            height: 300,
            toolbar: { show: false },
            tooltip: {
                x: { format: 'dd/MM' }
            }
        },
        fill: {
            type: 'gradient',
            gradient: {
                shadeIntensity: 1,
                opacityFrom: 0.7,
                opacityTo: 0.1,
                stops: [0, 90, 100]
            }
        },
        stroke: {
            curve: 'smooth',
            width: 2
        },
        markers: {
            size: 4,
            strokeWidth: 2,
            strokeColors: '#fff'
        },
        xaxis: {
            categories: chartCategoriesRef, 
            labels: {
                rotate: -45,
                style: { colors: '#666', fontSize: '12px' }
            }
        },
        yaxis: {
            title: {
                text: 'Peso (kg)',
                style: { color: '#666' }
            },
            labels: {
                formatter: (value) => `${value.toFixed(0)} kg`,
                style: { colors: '#666' }
            }
        },
        grid: {
            borderColor: '#e0e0e0',
            strokeDashArray: 4
        },
        legend: {
            show: false
        }
    }));
    return {
        chartOptions
    }
}