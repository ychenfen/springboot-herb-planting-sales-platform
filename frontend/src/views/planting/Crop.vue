<template>
  <div class="page-container">
    <div class="search-bar card-shadow">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="作物名称">
          <el-input v-model="searchForm.cropName" placeholder="请输入作物名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="生长中" :value="1" />
            <el-option label="已收获" :value="2" />
            <el-option label="已销售" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" :icon="Plus" @click="handleAdd" v-permission="'planting:crop:add'">新增作物</el-button>
    </div>

    <div class="table-container card-shadow">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="cropName" label="作物名称" min-width="120" />
        <el-table-column prop="cropVariety" label="品种" width="120" />
        <el-table-column prop="fieldName" label="所属地块" width="120" />
        <el-table-column prop="plantDate" label="种植日期" width="120" />
        <el-table-column prop="plantArea" label="种植面积(亩)" width="120" align="center" />
        <el-table-column prop="growthStage" label="生长阶段" width="100" />
        <el-table-column prop="growthStatus" label="生长状况" width="100" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'warning' : 'info'">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)" v-permission="'planting:crop:edit'">编辑</el-button>
            <el-button type="success" link v-if="row.status === 1" @click="handleHarvest(row)" v-permission="'planting:crop:edit'">收获</el-button>
            <el-button type="danger" link @click="handleDelete(row)" v-permission="'planting:crop:delete'">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑作物' : '新增作物'" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属地块" prop="fieldId">
          <el-select v-model="form.fieldId" placeholder="请选择地块" style="width: 100%">
            <el-option v-for="item in fieldList" :key="item.id" :label="item.fieldName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="作物名称" prop="cropName">
              <el-input v-model="form.cropName" placeholder="请输入作物名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品种" prop="cropVariety">
              <el-input v-model="form.cropVariety" placeholder="请输入品种" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="种植日期" prop="plantDate">
              <el-date-picker v-model="form.plantDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计收获" prop="expectedHarvestDate">
              <el-date-picker v-model="form.expectedHarvestDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="种植面积" prop="plantArea">
              <el-input-number v-model="form.plantArea" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计产量" prop="expectedYield">
              <el-input-number v-model="form.expectedYield" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="生长阶段" prop="growthStage">
              <el-input v-model="form.growthStage" placeholder="如：发芽期、生长期" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生长状况" prop="growthStatus">
              <el-select v-model="form.growthStatus" placeholder="请选择" style="width: 100%">
                <el-option label="良好" value="good" />
                <el-option label="一般" value="normal" />
                <el-option label="较差" value="poor" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
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
import { getCropPage, createCrop, updateCrop, deleteCrop, harvestCrop } from '@/api/planting'
import { getFieldList } from '@/api/planting'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const tableData = ref([])
const fieldList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({ cropName: '', status: null })
const form = ref({})
const formRef = ref()

const rules = {
  fieldId: [{ required: true, message: '请选择地块', trigger: 'change' }],
  cropName: [{ required: true, message: '请输入作物名称', trigger: 'blur' }],
  plantDate: [{ required: true, message: '请选择种植日期', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getCropPage({ pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const loadFieldList = async () => {
  try {
    const res = await getFieldList()
    fieldList.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const handleSearch = () => { pageNum.value = 1; loadData() }
const handleReset = () => { searchForm.cropName = ''; searchForm.status = null; handleSearch() }
const handleAdd = () => { form.value = {}; dialogVisible.value = true }
const handleEdit = (row) => { form.value = { ...row }; dialogVisible.value = true }

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该作物吗？', '提示', { type: 'warning' })
    await deleteCrop(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const handleHarvest = async (row) => {
  try {
    await ElMessageBox.prompt('请输入实际产量(kg)', '收获确认', { inputPattern: /^\d+(\.\d+)?$/, inputErrorMessage: '请输入有效数字' })
      .then(async ({ value }) => {
        await harvestCrop(row.id, { actualYield: parseFloat(value) })
        ElMessage.success('收获成功')
        loadData()
      })
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitLoading.value = true
  try {
    if (form.value.id) {
      await updateCrop(form.value)
      ElMessage.success('更新成功')
    } else {
      await createCrop(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => { loadData(); loadFieldList() })
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
