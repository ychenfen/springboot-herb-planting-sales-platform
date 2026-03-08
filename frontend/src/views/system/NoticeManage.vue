<template>
  <div class="page-container">
    <div class="search-bar card-shadow">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="用户ID">
          <el-input-number v-model="searchForm.userId" :min="1" style="width: 140px" />
        </el-form-item>
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
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增通知</el-button>
    </div>

    <div class="table-container card-shadow">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="userId" label="用户ID" width="90" />
        <el-table-column prop="username" label="用户名" width="140" />
        <el-table-column prop="realName" label="真实姓名" width="140" />
        <el-table-column prop="noticeTypeName" label="类型" width="120" />
        <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="isRead" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isRead === 1 ? 'success' : 'info'">
              {{ row.isRead === 1 ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑通知' : '新增通知'" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="用户ID" prop="userId">
          <el-input-number v-model="form.userId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="通知类型" prop="noticeType">
          <el-select v-model="form.noticeType" placeholder="请选择">
            <el-option v-for="item in noticeTypes" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="链接" prop="linkUrl">
          <el-input v-model="form.linkUrl" placeholder="可选：跳转链接" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getNoticePage, createNotice, updateNotice, deleteNotice } from '@/api/system'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const noticeTypes = [
  { value: 1, label: '系统通知' },
  { value: 2, label: '订单通知' },
  { value: 3, label: '评论通知' },
  { value: 4, label: '其他通知' }
]

const searchForm = reactive({ userId: null, noticeType: '', isRead: '', title: '' })
const form = ref({})
const formRef = ref()

const rules = {
  userId: [{ required: true, message: '请输入用户ID', trigger: 'blur' }],
  noticeType: [{ required: true, message: '请选择通知类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const buildParams = () => {
  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value
  }
  if (searchForm.userId !== null && searchForm.userId !== undefined) {
    params.userId = searchForm.userId
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
  searchForm.userId = null
  searchForm.noticeType = ''
  searchForm.isRead = ''
  searchForm.title = ''
  handleSearch()
}

const handleAdd = () => {
  form.value = { noticeType: 1 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = {
    id: row.id,
    userId: row.userId,
    noticeType: row.noticeType,
    title: row.title,
    content: row.content,
    linkUrl: row.linkUrl
  }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该通知吗？', '提示', { type: 'warning' })
    await deleteNotice(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    const payload = { ...form.value }
    if (payload.id) {
      await updateNotice(payload)
      ElMessage.success('更新成功')
    } else {
      await createNotice(payload)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
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
</style>
