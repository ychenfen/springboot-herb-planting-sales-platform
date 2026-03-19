<template>
  <div v-if="isPublicPage" class="public-layout">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>

  <div v-else class="main-layout">
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo">
        <div class="logo-mark">
          <el-icon :size="24"><Grape /></el-icon>
        </div>
        <div v-show="!isCollapsed" class="logo-text">
          <strong>中药材平台</strong>
          <span>种植 · 销售 · 溯源</span>
        </div>
      </div>

      <el-menu
        :default-active="currentRoute"
        :collapse="isCollapsed"
        :collapse-transition="false"
        background-color="#173826"
        text-color="#cfe3d5"
        active-text-color="#ffffff"
        router
      >
        <el-menu-item index="/dashboard" v-permission="'dashboard'">
          <el-icon><HomeFilled /></el-icon>
          <template #title>首页</template>
        </el-menu-item>

        <el-sub-menu
          v-if="canAccessKnowledge"
          index="knowledge"
          v-permission="['knowledge', 'knowledge:encyclopedia', 'knowledge:disease', 'knowledge:calendar']"
        >
          <template #title>
            <el-icon><Reading /></el-icon>
            <span>种植知识</span>
          </template>
          <el-menu-item index="/knowledge/encyclopedia" v-permission="'knowledge:encyclopedia'">
            中药材百科
          </el-menu-item>
          <el-menu-item index="/knowledge/disease" v-permission="'knowledge:disease'">
            病虫害识别
          </el-menu-item>
          <el-menu-item index="/knowledge/calendar" v-permission="'knowledge:calendar'">
            种植日历
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu
          v-if="isFarmer"
          index="planting"
          v-permission="['planting', 'planting:field', 'planting:crop', 'planting:record']"
        >
          <template #title>
            <el-icon><Cherry /></el-icon>
            <span>种植管理</span>
          </template>
          <el-menu-item index="/planting/field" v-permission="'planting:field'">地块管理</el-menu-item>
          <el-menu-item index="/planting/crop" v-permission="'planting:crop'">作物管理</el-menu-item>
          <el-menu-item index="/planting/record" v-permission="'planting:record'">农事记录</el-menu-item>
        </el-sub-menu>

        <el-sub-menu
          v-if="canBrowseTrading"
          index="sales"
          v-permission="['trading', 'trading:supply_market', 'trading:demand_market', 'trading:favorite', 'trading:order']"
        >
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>销售协同</span>
          </template>
          <el-menu-item index="/sales/supply" v-permission="'trading:supply_market'">供应大厅</el-menu-item>
          <el-menu-item v-if="isMerchant" index="/sales/demand" v-permission="'trading:demand_market'">
            采购需求
          </el-menu-item>
          <el-menu-item index="/sales/favorite" v-permission="'trading:favorite'">我的收藏</el-menu-item>
          <el-menu-item index="/sales/order" v-permission="'trading:order'">订单追踪</el-menu-item>
        </el-sub-menu>

        <el-sub-menu
          v-if="canAccessTrace"
          index="trace"
          v-permission="['trace', 'trace:manage', 'trace:query']"
        >
          <template #title>
            <el-icon><Connection /></el-icon>
            <span>质量溯源</span>
          </template>
          <el-menu-item v-if="isFarmer" index="/trace/manage" v-permission="'trace:manage'">溯源管理</el-menu-item>
          <el-menu-item index="/trace/query" v-permission="'trace:query'">溯源查询</el-menu-item>
        </el-sub-menu>

        <el-sub-menu
          v-if="canAccessAnalysis"
          index="analysis"
          v-permission="['analysis', 'analysis:sales', 'analysis:yield']"
        >
          <template #title>
            <el-icon><TrendCharts /></el-icon>
            <span>数据分析</span>
          </template>
          <el-menu-item index="/analysis/sales" v-permission="'analysis:sales'">销售分析</el-menu-item>
          <el-menu-item index="/analysis/yield" v-permission="'analysis:yield'">产量分析</el-menu-item>
        </el-sub-menu>

        <el-sub-menu
          v-if="isAdmin"
          index="system"
          v-permission="['system', 'system:user', 'system:role', 'system:permission', 'system:notice', 'system:config', 'system:log']"
        >
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/user" v-permission="'system:user'">用户管理</el-menu-item>
          <el-menu-item index="/system/role" v-permission="'system:role'">角色管理</el-menu-item>
          <el-menu-item index="/system/permission" v-permission="'system:permission'">权限管理</el-menu-item>
          <el-menu-item index="/system/notice" v-permission="'system:notice'">通知管理</el-menu-item>
          <el-menu-item index="/system/dict">数据字典</el-menu-item>
          <el-menu-item index="/system/config" v-permission="'system:config'">系统配置</el-menu-item>
          <el-menu-item index="/system/log" v-permission="'system:log'">系统日志</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </aside>

    <div class="main-container">
      <header class="header">
        <div class="header-left">
          <el-button
            class="collapse-btn"
            :icon="isCollapsed ? Expand : Fold"
            @click="toggleCollapse"
          />
          <div class="page-meta">
            <strong>{{ route.meta.title || '首页' }}</strong>
            <span>{{ userStore.userTypeName || '平台用户' }}</span>
          </div>
        </div>

        <div class="header-right">
          <el-tooltip content="全屏" placement="bottom">
            <el-button class="header-btn" :icon="FullScreen" @click="toggleFullscreen" />
          </el-tooltip>

          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="38" :src="userStore.avatar">
                {{ userStore.realName?.charAt(0) || '草' }}
              </el-avatar>
              <div class="user-detail">
                <span class="username">{{ userStore.realName || userStore.username }}</span>
                <span class="user-type">{{ userStore.userTypeName || '未分配角色' }}</span>
              </div>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="notice">
                  <el-icon><Bell /></el-icon>通知中心
                </el-dropdown-item>
                <el-dropdown-item command="password">
                  <el-icon><Lock /></el-icon>修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>

    <el-dialog v-model="profileVisible" title="个人中心" width="480px">
      <el-form
        ref="profileFormRef"
        :model="profileForm"
        :rules="profileRules"
        label-width="88px"
      >
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="头像地址" prop="avatar">
          <el-input v-model="profileForm.avatar" placeholder="选填，用于演示展示" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="profileVisible = false">取消</el-button>
        <el-button type="primary" :loading="profileLoading" @click="submitProfile">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="passwordVisible" title="修改密码" width="420px">
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="88px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordVisible = false">取消</el-button>
        <el-button type="primary" :loading="passwordLoading" @click="submitPassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowDown,
  Bell,
  Cherry,
  Connection,
  Expand,
  Fold,
  FullScreen,
  Goods,
  Grape,
  HomeFilled,
  Lock,
  Reading,
  Setting,
  SwitchButton,
  TrendCharts,
  User
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { logout as logoutApi, updatePassword, updateProfile } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapsed = ref(false)
const isPublicPage = computed(() => route.meta.public && !userStore.isLoggedIn)
const currentRoute = computed(() => route.path)

const isFarmer = computed(() => userStore.userType === 1)
const isMerchant = computed(() => userStore.userType === 2)
const isAdmin = computed(() => userStore.userType === 3)
const canAccessKnowledge = computed(() => [1, 2, 3, 4].includes(userStore.userType))
const canBrowseTrading = computed(() => [1, 2, 4].includes(userStore.userType))
const canAccessTrace = computed(() => [1, 2, 3, 4].includes(userStore.userType))
const canAccessAnalysis = computed(() => [1, 3].includes(userStore.userType))

const profileVisible = ref(false)
const profileLoading = ref(false)
const profileFormRef = ref()
const profileForm = reactive({
  realName: '',
  phone: '',
  email: '',
  avatar: ''
})
const profileRules = {
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const passwordVisible = ref(false)
const passwordLoading = ref(false)
const passwordFormRef = ref()
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入新密码'))
    return
  }
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
    return
  }
  callback()
}
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

const openProfileDialog = () => {
  profileForm.realName = userStore.userInfo.realName || ''
  profileForm.phone = userStore.userInfo.phone || ''
  profileForm.email = userStore.userInfo.email || ''
  profileForm.avatar = userStore.userInfo.avatar || ''
  profileVisible.value = true
  profileFormRef.value?.clearValidate()
}

const openPasswordDialog = () => {
  passwordFormRef.value?.resetFields()
  passwordVisible.value = true
}

const submitProfile = async () => {
  const valid = await profileFormRef.value?.validate().catch(() => false)
  if (!valid) return

  profileLoading.value = true
  try {
    const payload = {
      realName: profileForm.realName.trim() || null,
      phone: profileForm.phone.trim() || null,
      email: profileForm.email.trim() || null,
      avatar: profileForm.avatar.trim() || null
    }
    await updateProfile(payload)
    await userStore.fetchUserInfo()
    ElMessage.success('个人信息已更新')
    profileVisible.value = false
  } finally {
    profileLoading.value = false
  }
}

const submitPassword = async () => {
  const valid = await passwordFormRef.value?.validate().catch(() => false)
  if (!valid) return

  passwordLoading.value = true
  try {
    await updatePassword(passwordForm)
    ElMessage.success('密码已修改，请重新登录')
    passwordVisible.value = false
    userStore.logout()
    router.push('/login')
  } finally {
    passwordLoading.value = false
  }
}

const handleLogout = async () => {
  await ElMessageBox.confirm('确定退出当前账号吗？', '提示', {
    type: 'warning',
    confirmButtonText: '退出',
    cancelButtonText: '取消'
  })
  try {
    await logoutApi()
  } catch {
    // ignore logout api error
  }
  userStore.logout()
  router.push('/login')
  ElMessage.success('已退出登录')
}

const handleCommand = async (command) => {
  if (command === 'profile') {
    openProfileDialog()
    return
  }
  if (command === 'notice') {
    router.push('/notice')
    return
  }
  if (command === 'password') {
    openPasswordDialog()
    return
  }
  if (command === 'logout') {
    try {
      await handleLogout()
    } catch {
      // cancelled
    }
  }
}

const ensureUserInfo = async () => {
  if (!userStore.isLoggedIn || isPublicPage.value) return
  try {
    await userStore.fetchUserInfo()
  } catch {
    // handled by axios interceptor
  }
}

onMounted(() => {
  ensureUserInfo()
})

watch(
  () => userStore.isLoggedIn,
  (loggedIn) => {
    if (loggedIn) {
      ensureUserInfo()
    }
  }
)
</script>

<style scoped>
.public-layout {
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(74, 125, 66, 0.18), transparent 32%),
    linear-gradient(180deg, #f7f7f2 0%, #eef4ee 100%);
}

.main-layout {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(180deg, #f5f7f2 0%, #edf3ee 100%);
}

.sidebar {
  width: 232px;
  background: linear-gradient(180deg, #173826 0%, #10291d 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.25s ease;
  box-shadow: 18px 0 32px rgba(19, 44, 29, 0.14);
  position: relative;
  z-index: 2;
}

.sidebar.collapsed {
  width: 72px;
}

.logo {
  height: 72px;
  padding: 0 18px;
  display: flex;
  align-items: center;
  gap: 14px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  color: #fff;
}

.logo-mark {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #95c46b 0%, #4b8b42 100%);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2);
}

.logo-text {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.logo-text strong {
  font-size: 16px;
  letter-spacing: 1px;
}

.logo-text span {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.72);
}

.sidebar :deep(.el-menu) {
  border-right: none;
  flex: 1;
}

.sidebar :deep(.el-menu-item),
.sidebar :deep(.el-sub-menu__title) {
  height: 46px;
  margin: 4px 10px;
  border-radius: 10px;
}

.sidebar :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.16) !important;
}

.sidebar :deep(.el-menu-item:hover),
.sidebar :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.08) !important;
}

.main-container {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.header {
  height: 72px;
  margin: 16px 16px 0;
  padding: 0 22px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(14px);
  box-shadow: 0 10px 30px rgba(31, 58, 39, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn,
.header-btn {
  border: none;
  background: #f0f4ef;
  color: #28543b;
  font-size: 18px;
}

.page-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-meta strong {
  color: #20382a;
  font-size: 17px;
}

.page-meta span {
  color: #6b7f72;
  font-size: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 10px 6px 8px;
  border-radius: 14px;
  cursor: pointer;
  transition: background 0.2s ease;
}

.user-info:hover {
  background: #f3f7f2;
}

.user-detail {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.username {
  font-size: 14px;
  font-weight: 600;
  color: #1f3528;
}

.user-type {
  font-size: 12px;
  color: #70826f;
}

.content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

@media (max-width: 992px) {
  .sidebar {
    position: fixed;
    inset: 0 auto 0 0;
    height: 100vh;
  }

  .main-container {
    margin-left: 72px;
  }

  .sidebar:not(.collapsed) + .main-container {
    margin-left: 232px;
  }
}

@media (max-width: 768px) {
  .header {
    height: auto;
    padding: 14px;
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .header-left,
  .header-right {
    justify-content: space-between;
  }

  .content {
    padding: 12px;
  }
}
</style>
