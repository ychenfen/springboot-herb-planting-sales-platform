<template>
  <div class="order-page">
    <section class="hero card-shadow">
      <div>
        <span class="hero-kicker">订单进度可视化</span>
        <h2>订单状态实时追踪</h2>
        <p>覆盖待确认、待发货、待收货、已完成、已取消等状态，并通过时间线展示每一步业务动作。</p>
      </div>
    </section>

    <el-alert
      :title="pageHint"
      type="info"
      :closable="false"
      show-icon
    />

    <section class="toolbar card-shadow">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="订单号">
          <el-input v-model="queryForm.orderNo" clearable placeholder="输入订单号检索" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="queryForm.orderStatus" clearable placeholder="全部状态" style="width: 150px">
            <el-option label="待确认" :value="1" />
            <el-option label="待发货" :value="2" />
            <el-option label="待收货" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已取消" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">检索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="table-card card-shadow">
      <div v-if="!loading && !tableData.length" class="empty-panel">
        <el-empty description="暂无订单记录">
          <el-button type="primary" @click="goOrderSource">{{ emptyActionText }}</el-button>
        </el-empty>
      </div>

      <template v-else>
        <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="herbName" label="药材名称" min-width="120" />
        <el-table-column prop="quantity" label="数量(kg)" width="110" align="center">
          <template #default="{ row }">{{ formatQuantity(row.quantity) }}</template>
        </el-table-column>
        <el-table-column label="价格模式" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.pricingMode === '批发价' ? 'warning' : 'success'">{{ row.pricingMode || '标准价' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="订单金额" width="130" align="center">
          <template #default="{ row }">
            <span class="money">¥{{ formatMoney(row.totalAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.orderStatus)">{{ row.orderStatusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="支付状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.paymentStatus === 1 ? 'success' : 'info'">
              {{ row.paymentStatusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="最新节点" min-width="180">
          <template #default="{ row }">
            <div class="track-brief">
              <strong>{{ getLatestTrack(row).title }}</strong>
              <span>{{ getLatestTrack(row).description }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="170" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button
              v-if="isFarmer && row.orderStatus === 1"
              type="success"
              link
              @click="handleConfirm(row)"
              v-permission="'trading:order:confirm'"
            >
              确认
            </el-button>
            <el-button
              v-if="isFarmer && row.orderStatus === 2"
              type="warning"
              link
              @click="handleDeliver(row)"
            >
              发货
            </el-button>
            <el-button
              v-if="canReceive && row.orderStatus === 3"
              type="success"
              link
              @click="handleComplete(row)"
            >
              确认收货
            </el-button>
            <el-button
              v-if="row.orderStatus < 3"
              type="danger"
              link
              @click="handleCancel(row)"
              v-permission="'trading:order:cancel'"
            >
              取消
            </el-button>
          </template>
        </el-table-column>
        </el-table>

        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </template>
    </section>

    <el-drawer v-model="detailVisible" title="订单详情" size="560px">
      <div v-if="detail.id" class="detail-panel">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="药材名称">{{ detail.herbName }}</el-descriptions-item>
          <el-descriptions-item label="规格模式">{{ detail.pricingMode || '标准价' }}</el-descriptions-item>
          <el-descriptions-item label="采购数量">{{ formatQuantity(detail.quantity) }}</el-descriptions-item>
          <el-descriptions-item label="实际单价">¥{{ formatMoney(detail.unitPrice) }}/kg</el-descriptions-item>
          <el-descriptions-item label="订单金额">¥{{ formatMoney(detail.totalAmount) }}</el-descriptions-item>
          <el-descriptions-item label="卖家">{{ detail.sellerName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="买家">{{ detail.buyerName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="收货人">{{ detail.deliveryName || '--' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detail.deliveryPhone || '--' }}</el-descriptions-item>
          <el-descriptions-item label="收货地址">{{ detail.deliveryAddress || '--' }}</el-descriptions-item>
          <el-descriptions-item label="物流信息">
            {{ detail.logisticsCompany || '--' }} {{ detail.logisticsNo || '' }}
          </el-descriptions-item>
          <el-descriptions-item label="备注">{{ detail.remark || '--' }}</el-descriptions-item>
        </el-descriptions>

        <div class="timeline-section">
          <h3>订单时间线</h3>
          <el-timeline>
            <el-timeline-item
              v-for="item in detail.trackingNodes || []"
              :key="`${item.status}-${item.time}`"
              :timestamp="item.time || '待更新'"
              placement="top"
              type="success"
            >
              <div class="track-card">
                <strong>{{ item.title }}</strong>
                <p>{{ item.description }}</p>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  cancelOrder,
  completeOrder,
  confirmOrder,
  deliverOrder,
  getOrderById,
  getOrderPage
} from '@/api/sales'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const router = useRouter()
const isFarmer = computed(() => userStore.userType === 1)
const canReceive = computed(() => [2, 4].includes(userStore.userType))
const pageHint = computed(() => (
  isFarmer.value
    ? '卖家确认、发货和查看订单详情都在这里完成。'
    : '请先到“供应大厅”选择药材并下单，提交后订单会自动出现在这里。'
))
const emptyActionText = computed(() => (
  isFarmer.value ? '去供应大厅查看' : '去供应大厅下单'
))

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const detail = ref({})

const queryForm = reactive({
  orderNo: '',
  orderStatus: null
})

const formatMoney = (value) => {
  if (value === null || value === undefined || value === '') return '--'
  return Number(value).toFixed(2)
}

const formatQuantity = (value) => {
  if (value === null || value === undefined || value === '') return '--'
  return Number(value).toFixed(2)
}

const getStatusType = (status) => {
  if (status === 1) return 'warning'
  if (status === 2 || status === 3) return 'primary'
  if (status === 4) return 'success'
  return 'info'
}

const getLatestTrack = (row) => {
  const nodes = row.trackingNodes || []
  return nodes[nodes.length - 1] || { title: '等待处理', description: '暂无流转节点' }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOrderPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...queryForm
    })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

const handleReset = () => {
  queryForm.orderNo = ''
  queryForm.orderStatus = null
  handleSearch()
}

const handleView = async (row) => {
  const res = await getOrderById(row.id)
  detail.value = res.data
  detailVisible.value = true
}

const handleConfirm = async (row) => {
  await ElMessageBox.confirm('确认接单后，订单状态将流转到待发货。', '确认订单', {
    type: 'warning'
  })
  await confirmOrder(row.id)
  ElMessage.success('订单已确认')
  loadData()
}

const handleDeliver = async (row) => {
  const { value } = await ElMessageBox.prompt('请输入物流公司和单号，格式示例：顺丰 SF12345678', '发货确认', {
    inputPattern: /.+\s+.+/,
    inputErrorMessage: '请输入“物流公司 空格 物流单号”'
  })
  const [logisticsCompany, logisticsNo] = value.trim().split(/\s+/, 2)
  await deliverOrder(row.id, logisticsCompany, logisticsNo)
  ElMessage.success('已发货')
  loadData()
}

const handleComplete = async (row) => {
  await ElMessageBox.confirm('确认收货后，订单将进入已完成状态。', '确认收货', {
    type: 'warning'
  })
  await completeOrder(row.id)
  ElMessage.success('订单已完成')
  loadData()
}

const handleCancel = async (row) => {
  const { value } = await ElMessageBox.prompt('请输入取消原因', '取消订单', {
    inputPattern: /.+/,
    inputErrorMessage: '请输入取消原因'
  })
  await cancelOrder(row.id, value)
  ElMessage.success('订单已取消')
  loadData()
}

const goOrderSource = () => router.push('/sales/supply')

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.order-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hero {
  padding: 24px 28px;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  background:
    radial-gradient(circle at right top, rgba(106, 151, 222, 0.2), transparent 30%),
    linear-gradient(135deg, #f7fafc 0%, #eef5f8 100%);
}

.hero-kicker {
  color: #496a91;
  font-size: 12px;
  letter-spacing: 1px;
}

.hero h2 {
  margin: 10px 0 8px;
  color: #22384b;
  font-size: 26px;
}

.hero p {
  margin: 0;
  color: #637384;
  line-height: 1.8;
}

.toolbar,
.table-card {
  padding: 18px 20px;
}

.table-card :deep(.el-pagination) {
  margin-top: 16px;
  justify-content: flex-end;
}

.empty-panel {
  min-height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.money {
  color: #c56a23;
  font-weight: 700;
}

.track-brief {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.track-brief strong {
  color: #264258;
}

.track-brief span {
  color: #708091;
  font-size: 12px;
}

.detail-panel {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.timeline-section h3 {
  margin: 0 0 12px;
  color: #244359;
}

.track-card {
  padding: 14px 16px;
  border-radius: 14px;
  background: #f6fafc;
}

.track-card strong {
  color: #24465c;
}

.track-card p {
  margin: 8px 0 0;
  color: #6d7f8e;
  line-height: 1.7;
}

@media (max-width: 768px) {
  .hero {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
