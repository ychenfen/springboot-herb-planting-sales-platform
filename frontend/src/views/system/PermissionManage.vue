<template>
  <div class="page-container">
    <div class="search-bar card-shadow">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="名称/编码" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" :icon="Plus" @click="handleAddRoot">新增权限</el-button>
    </div>

    <div class="table-container card-shadow">
      <el-table
        :data="tableData"
        v-loading="loading"
        row-key="id"
        stripe
        :tree-props="{ children: 'children' }"
      >
        <el-table-column prop="permissionName" label="权限名称" min-width="160" />
        <el-table-column prop="permissionCode" label="权限编码" min-width="180" show-overflow-tooltip />
        <el-table-column prop="permissionTypeName" label="类型" width="90" />
        <el-table-column prop="path" label="路由" min-width="160" show-overflow-tooltip />
        <el-table-column prop="component" label="组件" min-width="160" show-overflow-tooltip />
        <el-table-column prop="icon" label="图标" width="110" />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="210" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleAddChild(row)">新增子级</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑权限' : '新增权限'" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="form.permissionName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="permissionCode">
          <el-input v-model="form.permissionCode" placeholder="请输入权限编码" />
        </el-form-item>
        <el-form-item label="权限类型" prop="permissionType">
          <el-select v-model="form.permissionType" placeholder="请选择">
            <el-option label="菜单" :value="1" />
            <el-option label="按钮" :value="2" />
            <el-option label="接口" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="父权限" prop="parentId">
          <el-cascader
            v-model="form.parentId"
            :options="parentOptions"
            :props="parentProps"
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="路由路径" prop="path">
          <el-input v-model="form.path" placeholder="菜单路由路径（可选）" />
        </el-form-item>
        <el-form-item label="组件路径" prop="component">
          <el-input v-model="form.component" placeholder="前端组件路径（可选）" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="form.icon" placeholder="菜单图标（可选）" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">正常</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
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
import { getPermissionTree, createPermission, updatePermission, deletePermission } from '@/api/system'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const tableData = ref([])
const rawTree = ref([])
const parentOptions = ref([])
const formRef = ref()

const searchForm = reactive({ keyword: '' })
const form = ref({})

const parentProps = {
  value: 'id',
  label: 'permissionName',
  children: 'children',
  emitPath: false,
  checkStrictly: true
}

const rules = {
  permissionName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  permissionCode: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  permissionType: [{ required: true, message: '请选择权限类型', trigger: 'change' }]
}

const normalizeTree = (nodes) => (nodes || []).map(item => ({
  ...item,
  children: normalizeTree(item.children)
}))

const buildParentOptions = (tree) => ([
  { id: 0, permissionName: '顶级', children: normalizeTree(tree) }
])

const filterTree = (nodes, keyword) => {
  if (!keyword) return normalizeTree(nodes)
  const lower = keyword.toLowerCase()
  const filtered = []
  nodes.forEach(node => {
    const name = String(node.permissionName || '')
    const code = String(node.permissionCode || '')
    const match = name.includes(keyword) || code.includes(keyword)
      || name.toLowerCase().includes(lower) || code.toLowerCase().includes(lower)
    const children = node.children ? filterTree(node.children, keyword) : []
    if (match || children.length) {
      filtered.push({ ...node, children })
    }
  })
  return filtered
}

const loadTree = async () => {
  loading.value = true
  try {
    const res = await getPermissionTree()
    rawTree.value = res.data || []
    tableData.value = normalizeTree(rawTree.value)
    parentOptions.value = buildParentOptions(rawTree.value)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  tableData.value = filterTree(rawTree.value, searchForm.keyword.trim())
}

const handleReset = () => {
  searchForm.keyword = ''
  tableData.value = normalizeTree(rawTree.value)
}

const handleAddRoot = () => {
  form.value = { parentId: 0, status: 1, sortOrder: 0, permissionType: 1 }
  dialogVisible.value = true
}

const handleAddChild = (row) => {
  form.value = { parentId: row.id, status: 1, sortOrder: 0, permissionType: 1 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = {
    id: row.id,
    permissionName: row.permissionName,
    permissionCode: row.permissionCode,
    permissionType: row.permissionType,
    parentId: row.parentId ?? 0,
    path: row.path,
    component: row.component,
    icon: row.icon,
    sortOrder: row.sortOrder ?? 0,
    status: row.status ?? 1
  }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该权限吗？', '提示', { type: 'warning' })
    await deletePermission(row.id)
    ElMessage.success('删除成功')
    loadTree()
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
    if (payload.parentId === undefined || payload.parentId === null) {
      payload.parentId = 0
    }
    if (payload.id) {
      await updatePermission(payload)
      ElMessage.success('更新成功')
    } else {
      await createPermission(payload)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadTree()
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadTree()
})
</script>

<style scoped>
.page-container { display: flex; flex-direction: column; gap: 16px; }
.search-bar { padding: 20px; display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 16px; }
.search-bar :deep(.el-form) { flex: 1; }
.search-bar :deep(.el-form-item) { margin-bottom: 0; }
.table-container { padding: 20px; }
.card-shadow { background: white; border-radius: 8px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08); }
</style>
