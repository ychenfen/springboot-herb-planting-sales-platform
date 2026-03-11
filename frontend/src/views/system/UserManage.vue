<template>
  <div class="user-page">
    <section class="toolbar card-shadow">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="用户名">
          <el-input v-model="queryForm.username" clearable placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="用户类型">
          <el-select v-model="queryForm.userType" clearable placeholder="全部类型" style="width: 150px">
            <el-option v-for="item in userTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" clearable placeholder="全部状态" style="width: 120px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">检索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="handleAdd" v-permission="'system:user:add'">新增用户</el-button>
    </section>

    <section class="table-card card-shadow">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column label="用户类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getUserTypeTag(row.userType)">{{ row.userTypeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="roleNames" label="角色" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
              v-permission="'system:user:edit'"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="210" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)" v-permission="'system:user:edit'">编辑</el-button>
            <el-button type="warning" link @click="handleResetPwd(row)" v-permission="'system:user:edit'">重置密码</el-button>
            <el-button type="danger" link @click="handleDelete(row)" v-permission="'system:user:delete'">删除</el-button>
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
    </section>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="660px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" :disabled="!!form.id" placeholder="请输入用户名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="18" v-if="!form.id">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="用户类型" prop="userType">
              <el-select v-model="form.userType" placeholder="请选择用户类型" style="width: 100%">
                <el-option v-for="item in userTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
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

        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="form.roleIds" multiple placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="item in roleList"
              :key="item.id"
              :label="`${item.roleName}（${item.roleCode}）`"
              :value="item.id"
            />
          </el-select>
          <div class="form-tip">切换用户类型时，系统会自动推荐对应角色，管理员可再手工调整。</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  createUser,
  deleteUser,
  getRoleList,
  getUserPage,
  resetPassword,
  updateUser,
  updateUserStatus
} from '@/api/system'

const userTypeOptions = [
  { label: '种植户', value: 1 },
  { label: '商家', value: 2 },
  { label: '管理员', value: 3 },
  { label: '普通用户', value: 4 }
]

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const tableData = ref([])
const roleList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const queryForm = reactive({
  username: '',
  userType: null,
  status: null
})

const formRef = ref()
const form = ref(createEmptyForm())
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
  ],
  userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }]
}

function createEmptyForm() {
  return {
    id: null,
    username: '',
    password: '',
    realName: '',
    phone: '',
    email: '',
    userType: 4,
    status: 1,
    roleIds: []
  }
}

const getUserTypeTag = (userType) => {
  if (userType === 1) return 'success'
  if (userType === 2) return 'warning'
  if (userType === 3) return 'danger'
  return 'info'
}

const findDefaultRoleIds = (userType) => {
  const map = {
    1: ['ROLE_FARMER'],
    2: ['ROLE_MERCHANT', 'ROLE_BUYER'],
    3: ['ROLE_ADMIN'],
    4: ['ROLE_USER']
  }
  const roleCodes = map[userType] || []
  return roleList.value
    .filter((item) => roleCodes.includes(item.roleCode))
    .map((item) => item.id)
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...queryForm
    })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const loadRoles = async () => {
  const res = await getRoleList()
  roleList.value = res.data || []
}

const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

const handleReset = () => {
  queryForm.username = ''
  queryForm.userType = null
  queryForm.status = null
  handleSearch()
}

const handleAdd = () => {
  form.value = createEmptyForm()
  form.value.roleIds = findDefaultRoleIds(form.value.userType)
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = {
    ...createEmptyForm(),
    ...row,
    roleIds: row.roleIds || []
  }
  dialogVisible.value = true
}

const handleStatusChange = async (row) => {
  try {
    await updateUserStatus(row.id, row.status)
    ElMessage.success('状态已更新')
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1
    console.error(error)
  }
}

const handleResetPwd = async (row) => {
  await ElMessageBox.confirm('确认将该用户密码重置为 123456 吗？', '重置密码', {
    type: 'warning'
  })
  await resetPassword(row.id, '123456')
  ElMessage.success('密码已重置为 123456')
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除该用户吗？', '删除用户', { type: 'warning' })
  await deleteUser(row.id)
  ElMessage.success('用户已删除')
  loadData()
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (form.value.id) {
      await updateUser(form.value)
      ElMessage.success('用户信息已更新')
    } else {
      await createUser(form.value)
      ElMessage.success('用户已创建')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

watch(
  () => form.value.userType,
  (userType) => {
    if (!dialogVisible.value || !userType) return
    if (!form.value.roleIds || form.value.roleIds.length === 0) {
      form.value.roleIds = findDefaultRoleIds(userType)
    }
  }
)

onMounted(async () => {
  await Promise.all([loadData(), loadRoles()])
})
</script>

<style scoped>
.user-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.toolbar,
.table-card {
  padding: 18px 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.table-card :deep(.el-pagination) {
  margin-top: 16px;
  justify-content: flex-end;
}

.form-tip {
  margin-top: 6px;
  color: #839181;
  font-size: 12px;
}

@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
  }
}
</style>
