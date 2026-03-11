<template>
  <div class="dashboard">
    <section class="stat-cards">
      <article v-if="isAdmin" class="stat-card" style="--card-color: #2e7d32">
        <div class="stat-icon">
          <el-icon :size="30"><User /></el-icon>
        </div>
        <div class="stat-info">
          <strong>{{ dashboard.userCount || 0 }}</strong>
          <span>平台用户总数</span>
        </div>
        <div class="stat-footer">
          <span>种植户 {{ dashboard.farmerCount || 0 }}</span>
          <span>采购端 {{ dashboard.buyerCount || 0 }}</span>
        </div>
      </article>

      <article class="stat-card" style="--card-color: #1976d2">
        <div class="stat-icon">
          <el-icon :size="30"><Cherry /></el-icon>
        </div>
        <div class="stat-info">
          <strong>{{ dashboard.cropCount || 0 }}</strong>
          <span>作物总数</span>
        </div>
        <div class="stat-footer">
          <span>在田作物 {{ dashboard.growingCropCount || 0 }}</span>
          <span>地块 {{ dashboard.fieldCount || 0 }}</span>
        </div>
      </article>

      <article class="stat-card" style="--card-color: #ed6c02">
        <div class="stat-icon">
          <el-icon :size="30"><List /></el-icon>
        </div>
        <div class="stat-info">
          <strong>{{ dashboard.orderCount || 0 }}</strong>
          <span>订单总数</span>
        </div>
        <div class="stat-footer">
          <span>今日新增 {{ dashboard.todayOrderCount || 0 }}</span>
          <span>金额 ¥{{ formatMoney(dashboard.totalOrderAmount) }}</span>
        </div>
      </article>

      <article class="stat-card" style="--card-color: #7e57c2">
        <div class="stat-icon">
          <el-icon :size="30"><Connection /></el-icon>
        </div>
        <div class="stat-info">
          <strong>{{ dashboard.traceCount || 0 }}</strong>
          <span>溯源批次</span>
        </div>
        <div class="stat-footer">
          <span>供应 {{ dashboard.supplyCount || 0 }}</span>
          <span>需求 {{ dashboard.demandCount || 0 }}</span>
        </div>
      </article>
    </section>

    <section class="chart-row">
      <div class="chart-card">
        <div class="card-header">
          <h3>订单趋势</h3>
          <el-radio-group v-model="orderDays" size="small" @change="loadOrderTrend">
            <el-radio-button :value="7">近 7 天</el-radio-button>
            <el-radio-button :value="30">近 30 天</el-radio-button>
          </el-radio-group>
        </div>
        <div ref="orderChartRef" class="chart-container"></div>
      </div>

      <div class="chart-card">
        <div class="card-header">
          <h3>供需趋势</h3>
          <el-radio-group v-model="supplyDays" size="small" @change="loadSupplyTrend">
            <el-radio-button :value="7">近 7 天</el-radio-button>
            <el-radio-button :value="30">近 30 天</el-radio-button>
          </el-radio-group>
        </div>
        <div ref="supplyChartRef" class="chart-container"></div>
      </div>
    </section>

    <section class="chart-row bottom">
      <div v-if="isAdmin" class="chart-card small">
        <div class="card-header">
          <h3>用户类型分布</h3>
        </div>
        <div ref="userChartRef" class="chart-container"></div>
      </div>

      <div class="chart-card small">
        <div class="card-header">
          <h3>作物品类分布</h3>
        </div>
        <div ref="cropChartRef" class="chart-container"></div>
      </div>

      <div class="chart-card ranking">
        <div class="card-header">
          <h3>药材销售排行</h3>
        </div>
        <div class="ranking-list">
          <div v-for="item in herbRanking" :key="item.rank" class="ranking-item">
            <span class="rank" :class="{ top: item.rank <= 3 }">{{ item.rank }}</span>
            <span class="name">{{ item.name }}</span>
            <span class="value">¥{{ formatMoney(item.value) }}</span>
          </div>
          <el-empty v-if="!herbRanking.length" description="暂无排行数据" :image-size="70" />
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import * as echarts from 'echarts'
import { Cherry, Connection, List, User } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import {
  getCropDistribution,
  getDashboard,
  getHerbSalesRanking,
  getOrderTrend,
  getSupplyDemandTrend,
  getUserTypeDistribution
} from '@/api/statistics'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.userType === 3)

const dashboard = ref({})
const herbRanking = ref([])
const orderDays = ref(7)
const supplyDays = ref(7)

const orderChartRef = ref()
const supplyChartRef = ref()
const userChartRef = ref()
const cropChartRef = ref()

let orderChart
let supplyChart
let userChart
let cropChart
let resizeHandler

const formatMoney = (value) => {
  if (value === null || value === undefined || value === '') return '0.00'
  return Number(value).toFixed(2)
}

const loadDashboard = async () => {
  const res = await getDashboard()
  dashboard.value = res.data || {}
}

const loadOrderTrend = async () => {
  const res = await getOrderTrend(orderDays.value)
  const { dates = [], dataList = [] } = res.data || {}
  orderChart?.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: dataList.map((item) => item.name), bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '16%', containLabel: true },
    xAxis: { type: 'category', data: dates, boundaryGap: false },
    yAxis: { type: 'value' },
    series: dataList.map((item, index) => ({
      name: item.name,
      type: 'line',
      smooth: true,
      areaStyle: { opacity: 0.22 },
      data: item.values,
      itemStyle: { color: index === 0 ? '#2e7d32' : '#1976d2' }
    }))
  })
}

const loadSupplyTrend = async () => {
  const res = await getSupplyDemandTrend(supplyDays.value)
  const { dates = [], dataList = [] } = res.data || {}
  supplyChart?.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: dataList.map((item) => item.name), bottom: 0 },
    grid: { left: '3%', right: '4%', bottom: '16%', containLabel: true },
    xAxis: { type: 'category', data: dates },
    yAxis: { type: 'value' },
    series: dataList.map((item, index) => ({
      name: item.name,
      type: 'bar',
      data: item.values,
      itemStyle: {
        color: index === 0 ? '#ed6c02' : '#7e57c2',
        borderRadius: [6, 6, 0, 0]
      }
    }))
  })
}

const loadUserDistribution = async () => {
  if (!isAdmin.value) return
  const res = await getUserTypeDistribution()
  const { dates = [], dataList = [] } = res.data || {}
  const values = dataList[0]?.values || []
  userChart?.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie',
        radius: ['42%', '70%'],
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false },
        emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
        data: dates.map((name, index) => ({ name, value: values[index] || 0 })),
        color: ['#2e7d32', '#c98c1a', '#d95d39', '#4f8b9e']
      }
    ]
  })
}

const loadCropDistribution = async () => {
  const res = await getCropDistribution()
  const { dates = [], dataList = [] } = res.data || {}
  const values = dataList[0]?.values || []
  cropChart?.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie',
        radius: ['42%', '70%'],
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false },
        emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
        data: dates.map((name, index) => ({ name, value: values[index] || 0 })),
        color: ['#4caf50', '#8bc34a', '#cddc39', '#ffca28', '#ff8f00', '#5c6bc0']
      }
    ]
  })
}

const loadHerbRanking = async () => {
  const res = await getHerbSalesRanking(5)
  herbRanking.value = res.data || []
}

const initCharts = () => {
  orderChart = echarts.init(orderChartRef.value)
  supplyChart = echarts.init(supplyChartRef.value)
  if (isAdmin.value && userChartRef.value) {
    userChart = echarts.init(userChartRef.value)
  }
  if (cropChartRef.value) {
    cropChart = echarts.init(cropChartRef.value)
  }

  resizeHandler = () => {
    orderChart?.resize()
    supplyChart?.resize()
    userChart?.resize()
    cropChart?.resize()
  }
  window.addEventListener('resize', resizeHandler)
}

onMounted(async () => {
  initCharts()
  await Promise.all([
    loadDashboard(),
    loadOrderTrend(),
    loadSupplyTrend(),
    loadUserDistribution(),
    loadCropDistribution(),
    loadHerbRanking()
  ])
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeHandler)
  orderChart?.dispose()
  supplyChart?.dispose()
  userChart?.dispose()
  cropChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.stat-card {
  position: relative;
  overflow: hidden;
  padding: 22px;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 10px 26px rgba(31, 47, 35, 0.08);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-card::before {
  content: '';
  position: absolute;
  inset: 0 auto 0 0;
  width: 5px;
  background: var(--card-color);
}

.stat-icon {
  width: 54px;
  height: 54px;
  border-radius: 16px;
  background: color-mix(in srgb, var(--card-color) 14%, #ffffff);
  color: var(--card-color);
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.stat-info strong {
  font-size: 32px;
  color: #243229;
}

.stat-info span,
.stat-footer span {
  color: #778475;
}

.stat-footer {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding-top: 10px;
  border-top: 1px solid #eef1ed;
  font-size: 13px;
}

.chart-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.chart-row.bottom {
  grid-template-columns: 1fr 1fr 1.3fr;
}

.chart-card {
  padding: 20px;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 10px 26px rgba(31, 47, 35, 0.08);
}

.chart-card.small,
.chart-card.ranking {
  min-height: 320px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  margin-bottom: 14px;
}

.card-header h3 {
  margin: 0;
  color: #24322a;
  font-size: 16px;
}

.chart-container {
  height: 280px;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  height: 280px;
  overflow-y: auto;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 14px;
  background: #f6f9f4;
}

.rank {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: #dfe5dc;
  color: #61705e;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
}

.rank.top {
  background: linear-gradient(135deg, #2e7d32 0%, #8db24f 100%);
  color: #fff;
}

.name {
  flex: 1;
  color: #2b3d2f;
}

.value {
  color: #c5691f;
  font-weight: 700;
}

@media (max-width: 1200px) {
  .stat-cards,
  .chart-row,
  .chart-row.bottom {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 768px) {
  .stat-cards,
  .chart-row,
  .chart-row.bottom {
    grid-template-columns: 1fr;
  }
}
</style>
