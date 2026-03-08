<template>
  <div class="page-container">
    <div class="search-bar card-shadow">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入订单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.orderStatus" placeholder="请选择" clearable style="width: 120px">
            <el-option label="待确认" :value="1" />
            <el-option label="已确认" :value="2" />
            <el-option label="配送中" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已取消" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container card-shadow">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="herbName" label="药材名称" min-width="120" />
        <el-table-column prop="quantity" label="数量(kg)" width="100" align="center" />
        <el-table-column prop="unitPrice" label="单价" width="100" align="center">
          <template #default="{ row }">¥{{ row.unitPrice }}</template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="120" align="center">
          <template #default="{ row }">
            <span class="price">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderStatus" label="订单状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.orderStatus)">{{ row.orderStatusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentStatus" label="支付状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.paymentStatus === 1 ? 'success' : 'info'" size="small">
              {{ row.paymentStatusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button type="success" link v-if="row.orderStatus === 1" @click="handleConfirm(row)" v-permission="'trading:order:confirm'">确认</el-button>
            <el-button type="warning" link v-if="row.orderStatus === 2" @click="handleDeliver(row)">发货</el-button>
            <el-button type="primary" link v-if="row.orderStatus === 3" @click="handleComplete(row)">完成</el-button>
            <el-button type="danger" link v-if="row.orderStatus < 4" @click="handleCancel(row)" v-permission="'trading:order:cancel'">取消</el-button>
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
    </div>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="药材名称">{{ detail.herbName }}</el-descriptions-item>
        <el-descriptions-item label="品种">{{ detail.herbVariety }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ detail.quantity }} kg</el-descriptions-item>
        <el-descriptions-item label="单价">¥{{ detail.unitPrice }}/kg</el-descriptions-item>
        <el-descriptions-item label="总金额">
          <span class="price">¥{{ detail.totalAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(detail.orderStatus)">{{ detail.orderStatusName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="卖家">{{ detail.sellerName }}</el-descriptions-item>
        <el-descriptions-item label="买家">{{ detail.buyerName }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ detail.deliveryAddress }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getOrderPage, getOrderById, confirmOrder, deliverOrder, completeOrder, cancelOrder } from '@/api/sales'

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const detail = ref({})

const searchForm = reactive({ orderNo: '', orderStatus: null })

const getStatusType = (status) => {
  const types = { 1: 'warning', 2: 'primary', 3: 'primary', 4: 'success', 5: 'info', 6: 'danger' }
  return types[status] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getOrderPage({ pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

const handleSearch = () => { pageNum.value = 1; loadData() }
const handleReset = () => { searchForm.orderNo = ''; searchForm.orderStatus = null; handleSearch() }

const handleView = async (row) => {
  try {
    const res = await getOrderById(row.id)
    detail.value = res.data
    detailVisible.value = true
  } catch (e) { console.error(e) }
}

const handleConfirm = async (row) => {
  try {
    await ElMessageBox.confirm('确定要确认该订单吗？', '提示', { type: 'warning' })
    await confirmOrder(row.id)
    ElMessage.success('订单已确认')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const handleDeliver = async (row) => {
  try {
    await ElMessageBox.prompt('请输入物流单号', '发货确认', { inputPattern: /.+/, inputErrorMessage: '请输入物流单号' })
      .then(async ({ value }) => {
        await deliverOrder(row.id, '自发货', value)
        ElMessage.success('已发货')
        loadData()
      })
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const handleComplete = async (row) => {
  try {
    await ElMessageBox.confirm('确定订单已完成？', '提示', { type: 'warning' })
    await completeOrder(row.id)
    ElMessage.success('订单已完成')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.prompt('请输入取消原因', '取消订单', { inputPattern: /.+/, inputErrorMessage: '请输入取消原因' })
      .then(async ({ value }) => {
        await cancelOrder(row.id, value)
        ElMessage.success('订单已取消')
        loadData()
      })
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

loadData()
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 16px; }
.search-bar { padding: 20px; display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 16px; }
.search-bar :deep(.el-form) { flex: 1; }
.search-bar :deep(.el-form-item) { margin-bottom: 0; }
.table-container { padding: 20px; }
.table-container :deep(.el-pagination) { margin-top: 16px; justify-content: flex-end; }
.card-shadow { background: white; border-radius: 8px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08); }
.price { color: #f56c6c; font-weight: 600; font-size: 16px; }
</style>
