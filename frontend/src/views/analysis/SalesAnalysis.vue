<template>
  <div class="analysis-page">
    <div class="chart-row">
      <div class="chart-card card-shadow">
        <div class="card-header">
          <h3>订单趋势分析</h3>
          <el-radio-group v-model="orderDays" size="small" @change="loadOrderTrend">
            <el-radio-button :value="7">近7天</el-radio-button>
            <el-radio-button :value="30">近30天</el-radio-button>
            <el-radio-button :value="90">近90天</el-radio-button>
          </el-radio-group>
        </div>
        <div ref="orderChartRef" class="chart-container"></div>
      </div>
    </div>

    <div class="chart-row two-cols">
      <div class="chart-card card-shadow">
        <div class="card-header">
          <h3>药材销量排行</h3>
        </div>
        <div ref="rankChartRef" class="chart-container"></div>
      </div>

      <div class="chart-card card-shadow">
        <div class="card-header">
          <h3>地区分布</h3>
        </div>
        <div ref="regionChartRef" class="chart-container"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getOrderTrend, getHerbSalesRanking, getRegionDistribution } from '@/api/statistics'

const orderDays = ref(7)
const orderChartRef = ref()
const rankChartRef = ref()
const regionChartRef = ref()
let orderChart, rankChart, regionChart

const loadOrderTrend = async () => {
  try {
    const res = await getOrderTrend(orderDays.value)
    const { dates, dataList } = res.data
    orderChart?.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },
      legend: { data: ['订单数', '订单金额'], bottom: 0 },
      grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
      xAxis: { type: 'category', data: dates, boundaryGap: false },
      yAxis: [
        { type: 'value', name: '订单数', position: 'left' },
        { type: 'value', name: '金额(元)', position: 'right' }
      ],
      series: [
        { name: '订单数', type: 'line', smooth: true, data: dataList[0]?.values, areaStyle: { opacity: 0.3 }, itemStyle: { color: '#2e7d32' } },
        { name: '订单金额', type: 'bar', yAxisIndex: 1, data: dataList[1]?.values, itemStyle: { color: '#1976d2', borderRadius: [4, 4, 0, 0] } }
      ]
    })
  } catch (e) { console.error(e) }
}

const loadRankChart = async () => {
  try {
    const res = await getHerbSalesRanking(10)
    const data = res.data || []
    rankChart?.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '3%', right: '10%', bottom: '3%', containLabel: true },
      xAxis: { type: 'value' },
      yAxis: { type: 'category', data: data.map(d => d.name).reverse(), axisLabel: { width: 80, overflow: 'truncate' } },
      series: [{
        type: 'bar',
        data: data.map(d => d.value).reverse(),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#2e7d32' },
            { offset: 1, color: '#66bb6a' }
          ]),
          borderRadius: [0, 4, 4, 0]
        },
        label: { show: true, position: 'right', formatter: '¥{c}' }
      }]
    })
  } catch (e) { console.error(e) }
}

const loadRegionChart = async () => {
  try {
    const res = await getRegionDistribution()
    const { dates, dataList } = res.data
    const data = dates.map((name, i) => ({ name, value: dataList[0].values[i] }))
    regionChart?.setOption({
      tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
      legend: { type: 'scroll', orient: 'vertical', right: 10, top: 20, bottom: 20 },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false },
        emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
        data
      }]
    })
  } catch (e) { console.error(e) }
}

const initCharts = () => {
  orderChart = echarts.init(orderChartRef.value)
  rankChart = echarts.init(rankChartRef.value)
  regionChart = echarts.init(regionChartRef.value)
  window.addEventListener('resize', () => {
    orderChart?.resize()
    rankChart?.resize()
    regionChart?.resize()
  })
}

onMounted(() => {
  initCharts()
  loadOrderTrend()
  loadRankChart()
  loadRegionChart()
})

onUnmounted(() => {
  orderChart?.dispose()
  rankChart?.dispose()
  regionChart?.dispose()
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
