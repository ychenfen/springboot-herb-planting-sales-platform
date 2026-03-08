<template>
  <div class="page-container">
    <div class="search-bar card-shadow">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="药材名称">
          <el-input v-model="searchForm.herbName" placeholder="请输入药材名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" :icon="Plus" @click="handleAdd" v-permission="'trace:create'">新增溯源</el-button>
    </div>

    <div class="table-container card-shadow">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="traceCode" label="溯源码" width="200" />
        <el-table-column prop="herbName" label="药材名称" min-width="120" />
        <el-table-column prop="batchNo" label="批次号" width="120" />
        <el-table-column prop="productionArea" label="产地" width="120" />
        <el-table-column prop="plantDate" label="种植日期" width="120" />
        <el-table-column prop="harvestDate" label="采收日期" width="120" />
        <el-table-column prop="scanCount" label="扫码次数" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link v-if="row.status === 0" @click="handleEdit(row)" v-permission="'trace:edit'">编辑</el-button>
            <el-button type="success" link v-if="row.status === 0" @click="handlePublish(row)" v-permission="'trace:edit'">发布</el-button>
            <el-button type="warning" link v-if="row.qrCodeUrl" @click="showQRCode(row)" v-permission="'trace:qrcode'">二维码</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑溯源' : '新增溯源'" width="700px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="药材名称" prop="herbName">
              <el-input v-model="form.herbName" placeholder="请输入药材名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="批次号" prop="batchNo">
              <el-input v-model="form.batchNo" placeholder="请输入批次号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="产地" prop="productionArea">
              <el-input v-model="form.productionArea" placeholder="请输入产地" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="质量标准" prop="qualityStandard">
              <el-input v-model="form.qualityStandard" placeholder="请输入质量标准" />
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
            <el-form-item label="采收日期" prop="harvestDate">
              <el-date-picker v-model="form.harvestDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="质检报告" prop="qualityReport">
          <el-input v-model="form.qualityReport" placeholder="请输入质检报告URL" />
        </el-form-item>
        <el-form-item label="认证证书" prop="certification">
          <el-input v-model="form.certification" placeholder="请输入认证证书信息" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 二维码弹窗 -->
    <el-dialog v-model="qrVisible" title="溯源二维码" width="400px" align-center>
      <div class="qr-container">
        <img :src="currentQR" alt="溯源二维码" class="qr-image" />
        <p class="qr-code">溯源码：{{ currentTraceCode }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getTracePage, createTrace, updateTrace, deleteTrace, publishTrace } from '@/api/trace'

const router = useRouter()
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const qrVisible = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const currentQR = ref('')
const currentTraceCode = ref('')

const searchForm = reactive({ herbName: '', status: null })
const form = ref({})
const formRef = ref()

const rules = {
  herbName: [{ required: true, message: '请输入药材名称', trigger: 'blur' }],
  productionArea: [{ required: true, message: '请输入产地', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getTracePage({ pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

const handleSearch = () => { pageNum.value = 1; loadData() }
const handleReset = () => { searchForm.herbName = ''; searchForm.status = null; handleSearch() }
const handleAdd = () => { form.value = {}; dialogVisible.value = true }
const handleEdit = (row) => { form.value = { ...row }; dialogVisible.value = true }
const handleView = (row) => { router.push(`/trace/query?code=${row.traceCode}`) }

const showQRCode = (row) => {
  currentQR.value = row.qrCodeUrl
  currentTraceCode.value = row.traceCode
  qrVisible.value = true
}

const handlePublish = async (row) => {
  try {
    await ElMessageBox.confirm('发布后将生成二维码，确定发布吗？', '提示', { type: 'warning' })
    await publishTrace(row.id)
    ElMessage.success('发布成功')
    loadData()
  } catch (e) { if (e !== 'cancel') console.error(e) }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该溯源信息吗？', '提示', { type: 'warning' })
    await deleteTrace(row.id)
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
      await updateTrace(form.value)
      ElMessage.success('更新成功')
    } else {
      await createTrace(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } finally { submitLoading.value = false }
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
.qr-container { text-align: center; padding: 20px; }
.qr-image { width: 200px; height: 200px; border: 1px solid #eee; border-radius: 8px; }
.qr-code { margin-top: 16px; color: #606266; font-size: 14px; }
</style>
