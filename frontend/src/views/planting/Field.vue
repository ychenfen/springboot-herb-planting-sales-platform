<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <div class="search-bar card-shadow">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="地块名称">
          <el-input v-model="searchForm.fieldName" placeholder="请输入地块名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="休耕" :value="0" />
            <el-option label="在用" :value="1" />
            <el-option label="整改中" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" :icon="Plus" @click="handleAdd" v-permission="'planting:field:add'">新增地块</el-button>
    </div>

    <!-- 数据表格 -->
    <div class="table-container card-shadow">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="fieldName" label="地块名称" min-width="120" />
        <el-table-column prop="fieldCode" label="地块编号" width="120" />
        <el-table-column prop="location" label="位置" min-width="150" show-overflow-tooltip />
        <el-table-column prop="area" label="面积(亩)" width="100" align="center" />
        <el-table-column prop="soilType" label="土壤类型" width="100" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 0 ? 'info' : 'warning'">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)" v-permission="'planting:field:edit'">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)" v-permission="'planting:field:delete'">删除</el-button>
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

    <!-- 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="地块名称" prop="fieldName">
          <el-input v-model="form.fieldName" placeholder="请输入地块名称" />
        </el-form-item>
        <el-form-item label="地块编号" prop="fieldCode">
          <el-input v-model="form.fieldCode" placeholder="请输入地块编号" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="省份" prop="province">
              <el-input v-model="form.province" placeholder="省份" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="城市" prop="city">
              <el-input v-model="form.city" placeholder="城市" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="区县" prop="district">
              <el-input v-model="form.district" placeholder="区县" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址" prop="location">
          <el-input v-model="form.location" placeholder="请输入详细地址" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="面积(亩)" prop="area">
              <el-input-number v-model="form.area" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="土壤类型" prop="soilType">
              <el-input v-model="form.soilType" placeholder="如：黑土、红土" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">在用</el-radio>
            <el-radio :value="0">休耕</el-radio>
            <el-radio :value="2">整改中</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入地块描述" />
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
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getFieldPage, createField, updateField, deleteField } from '@/api/planting'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  fieldName: '',
  status: null
})

const form = ref({})
const formRef = ref()

const dialogTitle = computed(() => form.value.id ? '编辑地块' : '新增地块')

const rules = {
  fieldName: [{ required: true, message: '请输入地块名称', trigger: 'blur' }],
  fieldCode: [{ required: true, message: '请输入地块编号', trigger: 'blur' }],
  area: [{ required: true, message: '请输入面积', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getFieldPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

const handleReset = () => {
  searchForm.fieldName = ''
  searchForm.status = null
  handleSearch()
}

const handleAdd = () => {
  form.value = { status: 1 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该地块吗？', '提示', { type: 'warning' })
    await deleteField(row.id)
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
    if (form.value.id) {
      await updateField(form.value)
      ElMessage.success('更新成功')
    } else {
      await createField(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {
    console.error(e)
  } finally {
    submitLoading.value = false
  }
}

loadData()
</script>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-bar {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 16px;
}

.search-bar :deep(.el-form) {
  flex: 1;
}

.search-bar :deep(.el-form-item) {
  margin-bottom: 0;
}

.table-container {
  padding: 20px;
}

.table-container :deep(.el-pagination) {
  margin-top: 16px;
  justify-content: flex-end;
}

.card-shadow {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}
</style>
