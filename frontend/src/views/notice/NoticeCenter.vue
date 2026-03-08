<template>
  <div class="page-container">
    <div class="search-bar card-shadow">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="类型">
          <el-select v-model="searchForm.noticeType" placeholder="请选择" clearable style="width: 140px">
            <el-option v-for="item in noticeTypes" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.isRead" placeholder="请选择" clearable style="width: 120px">
            <el-option label="未读" :value="0" />
            <el-option label="已读" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" plain :icon="Check" @click="handleReadAll">全部已读</el-button>
    </div>

    <div class="table-container card-shadow">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="noticeTypeName" label="类型" width="120" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="isRead" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isRead === 1 ? 'success' : 'info'">
              {{ row.isRead === 1 ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button v-if="row.isRead !== 1" type="success" link @click="handleRead(row)">标记已读</el-button>
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

    <el-dialog v-model="detailVisible" title="通知详情" width="680px" destroy-on-close>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="类型">{{ detail.noticeTypeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.isRead === 1 ? '已读' : '未读' }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ detail.title || '-' }}</el-descriptions-item>
        <el-descriptions-item label="时间" :span="2">{{ detail.createTime || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div class="detail-block">
        <div class="detail-label">内容</div>
        <el-input v-model="detail.content" type="textarea" :rows="5" readonly />
      </div>
      <div v-if="detail.linkUrl" class="detail-block">
        <div class="detail-label">链接</div>
        <el-link :href="detail.linkUrl" target="_blank">{{ detail.linkUrl }}</el-link>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Check } from '@element-plus/icons-vue'
import {
  getNoticePage,
  getNoticeById,
  markNoticeRead,
  markAllNoticeRead
} from '@/api/notice'

const loading = ref(false)
const detailVisible = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detail = ref({})

const noticeTypes = [
  { value: 1, label: '系统通知' },
  { value: 2, label: '订单通知' },
  { value: 3, label: '评论通知' },
  { value: 4, label: '其他通知' }
]

const searchForm = reactive({ noticeType: '', isRead: '', title: '' })

const buildParams = () => {
  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value
  }
  if (searchForm.noticeType !== '' && searchForm.noticeType !== null && searchForm.noticeType !== undefined) {
    params.noticeType = searchForm.noticeType
  }
  if (searchForm.isRead !== '' && searchForm.isRead !== null && searchForm.isRead !== undefined) {
    params.isRead = searchForm.isRead
  }
  if (searchForm.title) {
    params.title = searchForm.title
  }
  return params
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getNoticePage(buildParams())
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
  searchForm.noticeType = ''
  searchForm.isRead = ''
  searchForm.title = ''
  handleSearch()
}

const handleView = async (row) => {
  try {
    const res = await getNoticeById(row.id)
    detail.value = res.data || {}
    detailVisible.value = true
    if (row.isRead !== 1) {
      await markNoticeRead(row.id)
      loadData()
    }
  } catch (e) {
    console.error(e)
  }
}

const handleRead = async (row) => {
  try {
    await markNoticeRead(row.id)
    ElMessage.success('已标记为已读')
    loadData()
  } catch (e) {
    console.error(e)
  }
}

const handleReadAll = async () => {
  try {
    await ElMessageBox.confirm('确定将所有通知标记为已读吗？', '提示', { type: 'warning' })
    await markAllNoticeRead()
    ElMessage.success('已全部标记为已读')
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
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
.table-container { padding: 20px; }
.table-container :deep(.el-pagination) { margin-top: 16px; justify-content: flex-end; }
.card-shadow { background: white; border-radius: 8px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08); }
.detail-block { margin-top: 16px; }
.detail-label { font-size: 14px; color: #606266; margin-bottom: 8px; }
</style>
