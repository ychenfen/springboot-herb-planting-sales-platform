<template>
  <div class="page-container">
    <div class="search-bar card-shadow">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="药材名称">
          <el-input v-model="searchForm.herbName" placeholder="请输入药材名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="供应中" :value="1" />
            <el-option label="已售罄" :value="2" />
            <el-option label="已下架" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" :icon="Plus" @click="handleAdd" v-permission="'trading:supply:add'">发布供应</el-button>
    </div>

    <div class="table-container card-shadow">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="herbName" label="药材名称" min-width="120" />
        <el-table-column prop="herbVariety" label="品种" width="100" />
        <el-table-column prop="qualityGrade" label="质量等级" width="100" />
        <el-table-column prop="supplyQuantity" label="供应量(kg)" width="110" align="center" />
        <el-table-column prop="remainingQuantity" label="剩余量(kg)" width="110" align="center" />
        <el-table-column prop="price" label="单价(元/kg)" width="110" align="center">
          <template #default="{ row }">
            <span class="price">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="productionArea" label="产地" width="120" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'info'">
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
              v-permission="'trading:supply:edit'"
            >编辑</el-button>
            <el-button
              type="danger"
              link
              v-if="isOwner(row)"
              @click="handleDelete(row)"
              v-permission="'trading:supply:offline'"
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑供应' : '发布供应'" width="650px" destroy-on-close>
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
            <el-form-item label="质量等级" prop="qualityGrade">
              <el-select v-model="form.qualityGrade" placeholder="请选择" style="width: 100%">
                <el-option label="特级" value="特级" />
                <el-option label="一级" value="一级" />
                <el-option label="二级" value="二级" />
                <el-option label="三级" value="三级" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="产地" prop="productionArea">
              <el-input v-model="form.productionArea" placeholder="请输入产地" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应量(kg)" prop="supplyQuantity">
              <el-input-number v-model="form.supplyQuantity" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单价(元)" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="采收日期" prop="harvestDate">
              <el-date-picker v-model="form.harvestDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入供应描述" />
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
import { getSupplyPage, createSupply, updateSupply, deleteSupply } from '@/api/sales'
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

const searchForm = reactive({ herbName: '', status: null })
const form = ref({})
const formRef = ref()

const rules = {
  herbName: [{ required: true, message: '请输入药材名称', trigger: 'blur' }],
  supplyQuantity: [{ required: true, message: '请输入供应量', trigger: 'blur' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getSupplyPage({ pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally { loading.value = false }
}

const handleSearch = () => { pageNum.value = 1; loadData() }
const handleReset = () => { searchForm.herbName = ''; searchForm.status = null; handleSearch() }
const handleAdd = () => { form.value = {}; dialogVisible.value = true }
const handleEdit = (row) => { form.value = { ...row }; dialogVisible.value = true }

const isOwner = (row) => row && row.userId === userStore.userInfo.id

const handleFavorite = async (row) => {
  try {
    await addFavorite({ targetType: 1, targetId: row.id })
    row.isFavorite = 1
    if (row.favoriteCount !== null && row.favoriteCount !== undefined) {
      row.favoriteCount += 1
    }
    ElMessage.success('已收藏')
  } catch (e) {
    console.error(e)
  }
}

const handleUnfavorite = async (row) => {
  try {
    await removeFavorite({ targetType: 1, targetId: row.id })
    row.isFavorite = 0
    if (row.favoriteCount !== null && row.favoriteCount !== undefined && row.favoriteCount > 0) {
      row.favoriteCount -= 1
    }
    ElMessage.success('已取消收藏')
  } catch (e) {
    console.error(e)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该供应信息吗？', '提示', { type: 'warning' })
    await deleteSupply(row.id)
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
      await updateSupply(form.value)
      ElMessage.success('更新成功')
    } else {
      await createSupply(form.value)
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
.price { color: #f56c6c; font-weight: 600; }
</style>
