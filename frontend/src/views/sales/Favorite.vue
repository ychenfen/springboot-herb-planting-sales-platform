<template>
  <div class="page-container">
    <el-alert
      title="收藏入口位于供应信息页和需求信息页的操作列；收藏后会自动汇总到这里。"
      type="info"
      :closable="false"
      show-icon
    />

    <div class="search-bar card-shadow">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="类型">
          <el-select v-model="searchForm.targetType" placeholder="请选择" clearable style="width: 140px">
            <el-option label="供应信息" :value="1" />
            <el-option label="需求信息" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="药材名称/品种" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container card-shadow">
      <div v-if="!loading && !tableData.length" class="empty-panel">
        <el-empty description="暂无收藏内容，先去供应信息或需求信息页收藏感兴趣的项目">
          <div class="empty-actions">
            <el-button type="primary" @click="goSupply">去看供应信息</el-button>
            <el-button @click="goDemand">去看需求信息</el-button>
          </div>
        </el-empty>
      </div>

      <template v-else>
        <el-table :data="tableData" v-loading="loading" stripe>
          <el-table-column prop="targetTypeName" label="类型" width="120" />
          <el-table-column label="药材名称" min-width="140">
            <template #default="{ row }">
              {{ getItem(row)?.herbName || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="品种" width="120">
            <template #default="{ row }">
              {{ getItem(row)?.herbVariety || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="价格/目标价" width="130" align="center">
            <template #default="{ row }">
              <span v-if="row.targetType === 1">¥{{ getItem(row)?.price ?? '-' }}</span>
              <span v-else>¥{{ getItem(row)?.targetPrice ?? '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row)">
                {{ getItem(row)?.statusName || '-' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="收藏时间" width="170" />
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleView(row)">查看</el-button>
              <el-button type="danger" link @click="handleRemove(row)">取消收藏</el-button>
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
    </div>

    <el-dialog v-model="detailVisible" title="收藏详情" width="680px" destroy-on-close>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="类型">{{ detailTypeName }}</el-descriptions-item>
        <el-descriptions-item label="药材名称">{{ detail.herbName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="品种">{{ detail.herbVariety || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.statusName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发布者" :span="2">{{ detail.username || '-' }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ detail.description || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getFavoritePage, removeFavorite } from '@/api/favorite'

const loading = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const detail = ref({})
const detailTypeName = ref('')
const router = useRouter()

const searchForm = reactive({ targetType: null, keyword: '' })

const getItem = (row) => {
  if (!row) return null
  return row.targetType === 1 ? row.supply : row.demand
}

const getStatusTag = (row) => {
  if (!row) return 'info'
  if (row.targetType === 1) {
    const status = row.supply?.status
    if (status === 1) return 'success'
    if (status === 2) return 'warning'
    return 'info'
  }
  const status = row.demand?.status
  if (status === 1) return 'success'
  if (status === 2) return 'info'
  return 'danger'
}

const applyKeywordFilter = (rows) => {
  if (!searchForm.keyword) return rows
  const keyword = searchForm.keyword.toLowerCase()
  return rows.filter((row) => {
    const item = getItem(row)
    const name = String(item?.herbName || '').toLowerCase()
    const variety = String(item?.herbVariety || '').toLowerCase()
    return name.includes(keyword) || variety.includes(keyword)
  })
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    if (searchForm.targetType) {
      params.targetType = searchForm.targetType
    }
    const res = await getFavoritePage(params)
    const records = res.data.records || []
    const filteredRecords = applyKeywordFilter(records)
    tableData.value = filteredRecords
    total.value = searchForm.keyword ? filteredRecords.length : (res.data.total || 0)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

const handleReset = () => {
  searchForm.targetType = null
  searchForm.keyword = ''
  handleSearch()
}

const handleView = (row) => {
  const item = getItem(row) || {}
  detail.value = item
  detailTypeName.value = row.targetTypeName || (row.targetType === 1 ? '供应信息' : '需求信息')
  detailVisible.value = true
}

const goSupply = () => router.push('/sales/supply')
const goDemand = () => router.push('/sales/demand')

const handleRemove = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', { type: 'warning' })
    await removeFavorite({ targetType: row.targetType, targetId: row.targetId })
    ElMessage.success('已取消收藏')
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
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
.empty-panel { min-height: 320px; display: flex; align-items: center; justify-content: center; }
.empty-actions { display: flex; gap: 12px; justify-content: center; }
</style>
