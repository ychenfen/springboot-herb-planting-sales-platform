<template>
  <div class="page-container">
    <el-alert
      title="可在需求信息页直接收藏感兴趣的采购需求，收藏后会同步到“我的收藏”。"
      type="info"
      :closable="false"
      show-icon
    />

    <div class="search-bar card-shadow">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="药材名称">
          <el-input v-model="searchForm.herbName" placeholder="请输入药材名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" :icon="Plus" @click="handleAdd" v-permission="'trading:demand:add'">发布需求</el-button>
    </div>

    <div class="table-container card-shadow">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="herbName" label="药材名称" min-width="120" />
        <el-table-column prop="herbVariety" label="品种" width="100" />
        <el-table-column prop="qualityRequirement" label="质量要求" width="120" />
        <el-table-column prop="demandQuantity" label="需求量(kg)" width="110" align="center" />
        <el-table-column prop="targetPrice" label="目标价(元/kg)" width="120" align="center">
          <template #default="{ row }">
            <span class="price">¥{{ row.targetPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="demandDate" label="需求日期" width="120" />
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'info' : 'danger'">
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button
              type="warning"
              link
              @click="handleFavorite(row)"
              v-if="row.isFavorite !== 1"
            >收藏</el-button>
            <el-button
              type="info"
              link
              @click="handleUnfavorite(row)"
              v-else
            >取消收藏</el-button>
            <el-button
              type="primary"
              link
              v-if="isOwner(row)"
              @click="handleEdit(row)"
              v-permission="'trading:demand:edit'"
            >编辑</el-button>
            <el-button
              type="danger"
              link
              v-if="isOwner(row)"
              @click="handleDelete(row)"
              v-permission="'trading:demand:cancel'"
            >删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑需求' : '发布需求'" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="药材名称" prop="herbName">
              <el-input v-model="form.herbName" placeholder="请输入药材名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品种" prop="herbVariety">
              <el-input v-model="form.herbVariety" placeholder="请输入品种" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="需求量(kg)" prop="demandQuantity">
              <el-input-number v-model="form.demandQuantity" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标价格" prop="targetPrice">
              <el-input-number v-model="form.targetPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="需求日期" prop="demandDate">
              <el-date-picker v-model="form.demandDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="质量要求" prop="qualityRequirement">
              <el-input v-model="form.qualityRequirement" placeholder="如：一级以上" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="form.contactName" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="需求描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入需求描述" />
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
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { getDemandPage, createDemand, updateDemand, deleteDemand } from '@/api/sales'
import { addFavorite, removeFavorite } from '@/api/favorite'
import { useUserStore } from '@/stores/user'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const userStore = useUserStore()

const searchForm = reactive({ herbName: '' })
const form = ref({})
const formRef = ref()

const rules = {
  herbName: [{ required: true, message: '请输入药材名称', trigger: 'blur' }],
  demandQuantity: [{ required: true, message: '请输入需求量', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getDemandPage({ pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

const handleSearch = () => { pageNum.value = 1; loadData() }
const handleReset = () => { searchForm.herbName = ''; handleSearch() }
const handleAdd = () => { form.value = {}; dialogVisible.value = true }
const handleEdit = (row) => { form.value = { ...row }; dialogVisible.value = true }

const isOwner = (row) => row && row.userId === userStore.userInfo.id

const handleFavorite = async (row) => {
  try {
    await addFavorite({ targetType: 2, targetId: row.id })
    row.isFavorite = 1
    ElMessage.success('已收藏')
  } catch (e) {
    console.error(e)
  }
}

const handleUnfavorite = async (row) => {
  try {
    await removeFavorite({ targetType: 2, targetId: row.id })
    row.isFavorite = 0
    ElMessage.success('已取消收藏')
  } catch (e) {
    console.error(e)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该需求信息吗？', '提示', { type: 'warning' })
    await deleteDemand(row.id)
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
      await updateDemand(form.value)
      ElMessage.success('更新成功')
    } else {
      await createDemand(form.value)
      ElMessage.success('发布成功')
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
.price { color: #2e7d32; font-weight: 600; }
</style>
