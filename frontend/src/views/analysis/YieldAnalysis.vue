<template>
  <div class="analysis-page">
    <div class="chart-row two-cols">
      <div class="chart-card card-shadow">
        <div class="card-header">
          <h3>作物品种分布</h3>
        </div>
        <div ref="cropChartRef" class="chart-container"></div>
      </div>

      <div class="chart-card card-shadow">
        <div class="card-header">
          <h3>种植户产量排行</h3>
        </div>
        <div ref="yieldChartRef" class="chart-container"></div>
      </div>
    </div>

    <div class="chart-row">
      <div class="chart-card card-shadow">
        <div class="card-header">
          <h3>供需趋势对比</h3>
          <el-radio-group v-model="trendDays" size="small" @change="loadTrend">
            <el-radio-button :value="7">近7天</el-radio-button>
            <el-radio-button :value="30">近30天</el-radio-button>
          </el-radio-group>
        </div>
        <div ref="trendChartRef" class="chart-container"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getCropDistribution, getFarmerYieldRanking, getSupplyDemandTrend } from '@/api/statistics'

const trendDays = ref(7)
const cropChartRef = ref()
const yieldChartRef = ref()
const trendChartRef = ref()
let cropChart, yieldChart, trendChart

const loadCropChart = async () => {
  try {
    const res = await getCropDistribution()
    const { dates, dataList } = res.data
    const data = dates.map((name, i) => ({ name, value: dataList[0].values[i] }))
    cropChart?.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { orient: 'vertical', left: 'left' },
      series: [{
        type: 'pie',
        radius: '70%',
        center: ['60%', '50%'],
        data,
        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } },
        itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 }
      }]
    })
  } catch (e) { console.error(e) }
}

const loadYieldChart = async () => {
  try {
    const res = await getFarmerYieldRanking(10)
    const data = res.data || []
    yieldChart?.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '3%', right: '10%', bottom: '3%', containLabel: true },
      xAxis: { type: 'value', name: '产量(kg)' },
      yAxis: { type: 'category', data: data.map(d => d.name).reverse() },
      series: [{
        type: 'bar',
        data: data.map(d => d.value).reverse(),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#ed6c02' },
            { offset: 1, color: '#ffb74d' }
          ]),
          borderRadius: [0, 4, 4, 0]
        },
        label: { show: true, position: 'right', formatter: '{c} kg' }
      }]
    })
  } catch (e) { console.error(e) }
}

const loadTrend = async () => {
  try {
    const res = await getSupplyDemandTrend(trendDays.value)
    const { dates, dataList } = res.data
    trendChart?.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: dataList.map(d => d.name), bottom: 0 },
      grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
      xAxis: { type: 'category', data: dates, boundaryGap: false },
      yAxis: { type: 'value' },
      series: dataList.map((d, i) => ({
        name: d.name,
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        data: d.values,
        areaStyle: { opacity: 0.2 },
        itemStyle: { color: i === 0 ? '#2e7d32' : '#1976d2' }
      }))
    })
  } catch (e) { console.error(e) }
}

const initCharts = () => {
  cropChart = echarts.init(cropChartRef.value)
  yieldChart = echarts.init(yieldChartRef.value)
  trendChart = echarts.init(trendChartRef.value)
  window.addEventListener('resize', () => {
    cropChart?.resize()
    yieldChart?.resize()
    trendChart?.resize()
  })
}

onMounted(() => {
  initCharts()
  loadCropChart()
  loadYieldChart()
  loadTrend()
})

onUnmounted(() => {
  cropChart?.dispose()
  yieldChart?.dispose()
  trendChart?.dispose()
})
</script>

<style scoped>
.analysis-page { display: flex; flex-direction: column; gap: 20px; }
.chart-row { display: grid; grid-template-columns: 1fr; gap: 20px; }
.chart-row.two-cols { grid-template-columns: repeat(2, 1fr); }
.chart-card { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.card-header h3 { font-size: 16px; font-weight: 600; color: #303133; margin: 0; }
.chart-container { height: 350px; }
.card-shadow { background: white; border-radius: 12px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08); }
</style>
