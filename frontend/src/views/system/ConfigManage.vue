<template>
  <div class="page-container">
    <div class="search-bar card-shadow">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="配置类型">
          <el-select
            v-model="searchForm.configType"
            placeholder="请选择"
            clearable
            filterable
            style="width: 180px"
          >
            <el-option v-for="t in configTypes" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="配置键">
          <el-input v-model="searchForm.configKey" placeholder="请输入配置键" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" :icon="Plus" @click="handleAdd">新增配置</el-button>
    </div>

    <div class="table-container card-shadow">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="configKey" label="配置键" min-width="200" />
        <el-table-column prop="configValue" label="配置值" min-width="220" show-overflow-tooltip />
        <el-table-column prop="configType" label="配置类型" width="140" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="updateTime" label="更新时间" width="170" />
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑配置' : '新增配置'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="配置键" prop="configKey">
          <el-input v-model="form.configKey" placeholder="请输入配置键" />
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <el-input v-model="form.configValue" type="textarea" :rows="3" placeholder="请输入配置值" />
        </el-form-item>
        <el-form-item label="配置类型" prop="configType">
          <el-select
            v-model="form.configType"
            placeholder="请选择或输入"
            clearable
            filterable
            allow-create
            default-first-option
            style="width: 100%"
          >
            <el-option v-for="t in configTypes" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入描述" />
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
import {
  getConfigPage,
  getConfigTypes,
  createConfig,
  updateConfig,
  deleteConfig
} from '@/api/system'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const tableData = ref([])
const configTypes = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({ configKey: '', configType: '' })
const form = ref({})
const formRef = ref()

const rules = {
  configKey: [{ required: true, message: '请输入配置键', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getConfigPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const loadTypes = async () => {
  try {
    const res = await getConfigTypes()
    configTypes.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

const handleReset = () => {
  searchForm.configKey = ''
  searchForm.configType = ''
  handleSearch()
}

const handleAdd = () => {
  form.value = {}
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该配置吗？', '提示', { type: 'warning' })
    await deleteConfig(row.id)
    ElMessage.success('删除成功')
    loadData()
    loadTypes()
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (form.value.id) {
      await updateConfig(form.value)
      ElMessage.success('更新成功')
    } else {
      await createConfig(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
    loadTypes()
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadData()
  loadTypes()
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
