<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div v-if="isAdmin" class="stat-card" style="--card-color: #2e7d32">
        <div class="stat-icon">
          <el-icon :size="32"><User /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ dashboard.userCount || 0 }}</div>
          <div class="stat-label">用户总数</div>
        </div>
        <div class="stat-footer">
          <span>种植户 {{ dashboard.farmerCount || 0 }}</span>
          <span>采购商 {{ dashboard.buyerCount || 0 }}</span>
        </div>
      </div>

      <div class="stat-card" style="--card-color: #1976d2">
        <div class="stat-icon">
          <el-icon :size="32"><Cherry /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ dashboard.cropCount || 0 }}</div>
          <div class="stat-label">作物总数</div>
        </div>
        <div class="stat-footer">
          <span>生长中 {{ dashboard.growingCropCount || 0 }}</span>
          <span>地块 {{ dashboard.fieldCount || 0 }}</span>
        </div>
      </div>

      <div class="stat-card" style="--card-color: #ed6c02">
        <div class="stat-icon">
          <el-icon :size="32"><List /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ dashboard.orderCount || 0 }}</div>
          <div class="stat-label">订单总数</div>
        </div>
        <div class="stat-footer">
          <span>今日 {{ dashboard.todayOrderCount || 0 }}</span>
          <span>金额 ¥{{ formatMoney(dashboard.totalOrderAmount) }}</span>
        </div>
      </div>

      <div class="stat-card" style="--card-color: #9c27b0">
        <div class="stat-icon">
          <el-icon :size="32"><Connection /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ dashboard.traceCount || 0 }}</div>
          <div class="stat-label">溯源信息</div>
        </div>
        <div class="stat-footer">
          <span>供应 {{ dashboard.supplyCount || 0 }}</span>
          <span>需求 {{ dashboard.demandCount || 0 }}</span>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="chart-row">
      <div class="chart-card">
        <div class="card-header">
          <h3>订单趋势</h3>
          <el-radio-group v-model="orderDays" size="small" @change="loadOrderTrend">
            <el-radio-button :value="7">近7天</el-radio-button>
            <el-radio-button :value="30">近30天</el-radio-button>
          </el-radio-group>
        </div>
        <div ref="orderChartRef" class="chart-container"></div>
      </div>

      <div class="chart-card">
        <div class="card-header">
          <h3>供需趋势</h3>
          <el-radio-group v-model="supplyDays" size="small" @change="loadSupplyTrend">
            <el-radio-button :value="7">近7天</el-radio-button>
            <el-radio-button :value="30">近30天</el-radio-button>
          </el-radio-group>
        </div>
        <div ref="supplyChartRef" class="chart-container"></div>
      </div>
    </div>

    <div class="chart-row">
      <div v-if="isAdmin" class="chart-card small">
        <div class="card-header">
          <h3>用户类型分布</h3>
        </div>
        <div ref="userChartRef" class="chart-container"></div>
      </div>

      <div class="chart-card small">
        <div class="card-header">
          <h3>作物品种分布</h3>
        </div>
        <div ref="cropChartRef" class="chart-container"></div>
      </div>

      <div class="chart-card">
        <div class="card-header">
          <h3>药材销量排行</h3>
        </div>
        <div class="ranking-list">
          <div
            v-for="item in herbRanking"
            :key="item.rank"
            class="ranking-item"
          >
            <span class="rank" :class="{ top: item.rank <= 3 }">{{ item.rank }}</span>
            <span class="name">{{ item.name }}</span>
            <span class="value">¥{{ formatMoney(item.value) }}</span>
          </div>
          <el-empty v-if="!herbRanking.length" description="暂无数据" :image-size="60" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import * as echarts from 'echarts'
import { User, Cherry, List, Connection } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import {
  getDashboard,
  getOrderTrend,
  getSupplyDemandTrend,
  getHerbSalesRanking,
  getCropDistribution,
  getUserTypeDistribution
} from '@/api/statistics'

const dashboard = ref({})
const herbRanking = ref([])
const orderDays = ref(7)
const supplyDays = ref(7)

const userStore = useUserStore()
const isAdmin = computed(() => userStore.userType === 3)

const orderChartRef = ref()
const supplyChartRef = ref()
const userChartRef = ref()
const cropChartRef = ref()

let orderChart, supplyChart, userChart, cropChart

const formatMoney = (value) => {
  if (!value) return '0.00'
  return Number(value).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const loadDashboard = async () => {
  try {
    const res = await getDashboard()
    dashboard.value = res.data
  } catch (e) {
    console.error(e)
  }
}

const loadOrderTrend = async () => {
  try {
    const res = await getOrderTrend(orderDays.value)
    const { dates, dataList } = res.data
    orderChart?.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: dataList.map(d => d.name), bottom: 0 },
      grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
      xAxis: { type: 'category', data: dates, boundaryGap: false },
      yAxis: { type: 'value' },
      series: dataList.map((d, i) => ({
        name: d.name,
        type: 'line',
        smooth: true,
        areaStyle: { opacity: 0.3 },
        data: d.values,
        itemStyle: { color: i === 0 ? '#2e7d32' : '#1976d2' }
      }))
    })
  } catch (e) {
    console.error(e)
  }
}

const loadSupplyTrend = async () => {
  try {
    const res = await getSupplyDemandTrend(supplyDays.value)
    const { dates, dataList } = res.data
    supplyChart?.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: dataList.map(d => d.name), bottom: 0 },
      grid: { left: '3%', right: '4%', bottom: '15%', containLabel: true },
      xAxis: { type: 'category', data: dates },
      yAxis: { type: 'value' },
      series: dataList.map((d, i) => ({
        name: d.name,
        type: 'bar',
        data: d.values,
        itemStyle: { color: i === 0 ? '#ed6c02' : '#9c27b0', borderRadius: [4, 4, 0, 0] }
      }))
    })
  } catch (e) {
    console.error(e)
  }
}

const loadUserDistribution = async () => {
  if (!isAdmin.value) return
  try {
    const res = await getUserTypeDistribution()
    const { dates, dataList } = res.data
    const data = dates.map((name, i) => ({ name, value: dataList[0].values[i] }))
    userChart?.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false },
        emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
        data,
        color: ['#2e7d32', '#1976d2', '#ed6c02']
      }]
    })
  } catch (e) {
    console.error(e)
  }
}

const loadCropDistribution = async () => {
  try {
    const res = await getCropDistribution()
    const { dates, dataList } = res.data
    const data = dates.map((name, i) => ({ name, value: dataList[0].values[i] }))
    cropChart?.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false },
        emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
        data,
        color: ['#4caf50', '#8bc34a', '#cddc39', '#ffeb3b', '#ffc107', '#ff9800']
      }]
    })
  } catch (e) {
    console.error(e)
  }
}

const loadHerbRanking = async () => {
  try {
    const res = await getHerbSalesRanking(5)
    herbRanking.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const initCharts = () => {
  orderChart = echarts.init(orderChartRef.value)
  supplyChart = echarts.init(supplyChartRef.value)
  if (isAdmin.value && userChartRef.value) {
    userChart = echarts.init(userChartRef.value)
  }
  cropChart = echarts.init(cropChartRef.value)

  window.addEventListener('resize', () => {
    orderChart?.resize()
    supplyChart?.resize()
    userChart?.resize()
    cropChart?.resize()
  })
}

onMounted(() => {
  initCharts()
  loadDashboard()
  loadOrderTrend()
  loadSupplyTrend()
  loadUserDistribution()
  loadCropDistribution()
  loadHerbRanking()
})

onUnmounted(() => {
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
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: var(--card-color);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: color-mix(in srgb, var(--card-color) 15%, transparent);
  color: var(--card-color);
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.stat-footer {
  display: flex;
  justify-content: space-between;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  font-size: 12px;
  color: #909399;
}

.chart-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.chart-row:last-child {
  grid-template-columns: 1fr 1fr 1.5fr;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.chart-card.small {
  min-height: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin: 0;
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
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 8px;
  transition: all 0.3s;
}

.ranking-item:hover {
  background: #e8f5e9;
}

.rank {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #dcdfe6;
  color: #606266;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  margin-right: 12px;
}

.rank.top {
  background: linear-gradient(135deg, #2e7d32, #4caf50);
  color: white;
}

.name {
  flex: 1;
  font-size: 14px;
  color: #303133;
}

.value {
  font-size: 14px;
  font-weight: 600;
  color: #2e7d32;
}

@media (max-width: 1200px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .chart-row {
    grid-template-columns: 1fr;
  }

  .chart-row:last-child {
    grid-template-columns: 1fr;
  }
}
</style>
