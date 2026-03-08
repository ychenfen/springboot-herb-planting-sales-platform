<template>
  <div class="page-container">
    <div class="search-bar card-shadow">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="活动类型">
          <el-select v-model="searchForm.activityType" placeholder="请选择" clearable style="width: 120px">
            <el-option label="播种" value="sow" />
            <el-option label="施肥" value="fertilize" />
            <el-option label="浇水" value="water" />
            <el-option label="除草" value="weed" />
            <el-option label="施药" value="spray" />
            <el-option label="收获" value="harvest" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" :icon="Plus" @click="handleAdd" v-permission="'planting:record:add'">新增记录</el-button>
    </div>

    <div class="table-container card-shadow">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="cropName" label="关联作物" width="120" />
        <el-table-column prop="activityTypeName" label="活动类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.activityTypeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="activityDate" label="活动日期" width="120" />
        <el-table-column prop="activityTime" label="活动时间" width="100" />
        <el-table-column prop="description" label="活动描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="weather" label="天气" width="80" />
        <el-table-column prop="temperature" label="温度" width="80" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)" v-permission="'planting:record:edit'">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)" v-permission="'planting:record:delete'">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑记录' : '新增记录'" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="关联作物" prop="cropId">
          <el-select v-model="form.cropId" placeholder="请选择作物" style="width: 100%">
            <el-option v-for="item in cropList" :key="item.id" :label="item.cropName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="活动类型" prop="activityType">
              <el-select v-model="form.activityType" placeholder="请选择" style="width: 100%">
                <el-option label="播种" value="sow" />
                <el-option label="施肥" value="fertilize" />
                <el-option label="浇水" value="water" />
                <el-option label="除草" value="weed" />
                <el-option label="施药" value="spray" />
                <el-option label="收获" value="harvest" />
                <el-option label="修剪" value="prune" />
                <el-option label="巡检" value="inspect" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动日期" prop="activityDate">
              <el-date-picker v-model="form.activityDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="天气" prop="weather">
              <el-input v-model="form.weather" placeholder="如：晴、多云" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="温度" prop="temperature">
              <el-input v-model="form.temperature" placeholder="如：25℃" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="活动描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入活动描述" />
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
import { getFarmRecordPage, createFarmRecord, updateFarmRecord, deleteFarmRecord, getCropPage } from '@/api/planting'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const tableData = ref([])
const cropList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({ activityType: '' })
const form = ref({})
const formRef = ref()

const rules = {
  cropId: [{ required: true, message: '请选择作物', trigger: 'change' }],
  activityType: [{ required: true, message: '请选择活动类型', trigger: 'change' }],
  activityDate: [{ required: true, message: '请选择活动日期', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getFarmRecordPage({ pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

const loadCropList = async () => {
  try {
    const res = await getCropPage({ pageNum: 1, pageSize: 1000 })
    cropList.value = res.data.records || []
  } catch (e) { console.error(e) }
}

const handleSearch = () => { pageNum.value = 1; loadData() }
const handleReset = () => { searchForm.activityType = ''; handleSearch() }
const handleAdd = () => { form.value = {}; dialogVisible.value = true }
const handleEdit = (row) => { form.value = { ...row }; dialogVisible.value = true }

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该记录吗？', '提示', { type: 'warning' })
    await deleteFarmRecord(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (form.value.id) {
      await updateFarmRecord(form.value)
      ElMessage.success('更新成功')
    } else {
      await createFarmRecord(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
}

onMounted(() => { loadData(); loadCropList() })
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
