<template>
  <div class="page-container">
    <div class="search-bar card-shadow">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="操作">
          <el-input v-model="searchForm.operation" placeholder="请输入操作关键字" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.timeRange"
            type="datetimerange"
            value-format="YYYY-MM-DD HH:mm:ss"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 320px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <div class="action-buttons">
        <el-button type="danger" plain :icon="Delete" @click="handleClear">清空日志</el-button>
      </div>
    </div>

    <div class="table-container card-shadow">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="operation" label="操作" min-width="180" show-overflow-tooltip />
        <el-table-column prop="method" label="方法" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="executeTime" label="耗时(ms)" width="110" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="detailVisible" title="日志详情" width="720px" destroy-on-close>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ detail.username || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.statusName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ detail.ip || '-' }}</el-descriptions-item>
        <el-descriptions-item label="耗时(ms)">{{ detail.executeTime ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作" :span="2">{{ detail.operation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="方法" :span="2">{{ detail.method || '-' }}</el-descriptions-item>
        <el-descriptions-item label="时间" :span="2">{{ detail.createTime || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div class="detail-block">
        <div class="detail-label">请求参数</div>
        <el-input v-model="detail.params" type="textarea" :rows="4" readonly />
      </div>

      <div class="detail-block">
        <div class="detail-label">返回结果</div>
        <el-input v-model="detail.result" type="textarea" :rows="4" readonly />
      </div>

      <div v-if="detail.errorMsg" class="detail-block">
        <div class="detail-label">错误信息</div>
        <el-input v-model="detail.errorMsg" type="textarea" :rows="3" readonly />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete } from '@element-plus/icons-vue'
import { getLogPage, getLogById, deleteLog, clearLog } from '@/api/system'

const loading = ref(false)
const detailVisible = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detail = ref({})

const searchForm = reactive({
  username: '',
  operation: '',
  status: '',
  timeRange: []
})

const buildParams = () => {
  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value
  }
  if (searchForm.username) params.username = searchForm.username
  if (searchForm.operation) params.operation = searchForm.operation
  if (searchForm.status !== '' && searchForm.status !== null && searchForm.status !== undefined) {
    params.status = searchForm.status
  }
  if (searchForm.timeRange && searchForm.timeRange.length === 2) {
    params.startTime = searchForm.timeRange[0]
    params.endTime = searchForm.timeRange[1]
  }
  return params
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getLogPage(buildParams())
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
  searchForm.username = ''
  searchForm.operation = ''
  searchForm.status = ''
  searchForm.timeRange = []
  handleSearch()
}

const handleView = async (row) => {
  try {
    const res = await getLogById(row.id)
    detail.value = res.data || {}
    detailVisible.value = true
  } catch (e) {
    console.error(e)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该日志吗？', '提示', { type: 'warning' })
    await deleteLog(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确定要清空所有日志吗？', '提示', { type: 'warning' })
    await clearLog()
    ElMessage.success('日志已清空')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 16px; }
.search-bar { padding: 20px; display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 16px; }
.search-bar :deep(.el-form) { flex: 1; }
.search-bar :deep(.el-form-item) { margin-bottom: 0; }
.action-buttons { display: flex; align-items: center; gap: 12px; }
.table-container { padding: 20px; }
.table-container :deep(.el-pagination) { margin-top: 16px; justify-content: flex-end; }
.card-shadow { background: white; border-radius: 8px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08); }
.detail-block { margin-top: 16px; }
.detail-label { font-size: 14px; color: #606266; margin-bottom: 8px; }
</style>
