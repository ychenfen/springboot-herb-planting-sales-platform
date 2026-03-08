<template>
  <div v-if="isPublicPage" class="public-layout">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>

  <div v-else class="main-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo">
        <el-icon :size="28"><Grape /></el-icon>
        <span v-show="!isCollapsed" class="logo-text">中药材平台</span>
      </div>

      <el-menu
        :default-active="currentRoute"
        :collapse="isCollapsed"
        :collapse-transition="false"
        background-color="#1a472a"
        text-color="#a8d5ba"
        active-text-color="#ffffff"
        router
      >
        <el-menu-item index="/dashboard" v-permission="'dashboard'">
          <el-icon><HomeFilled /></el-icon>
          <template #title>首页</template>
        </el-menu-item>

        <el-sub-menu
          v-if="userStore.userType === 1"
          index="planting"
          v-permission="['planting','planting:field','planting:crop','planting:record']"
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
          v-if="userStore.userType === 1 || userStore.userType === 2"
          index="sales"
          v-permission="['trading','trading:supply_market','trading:demand_market','trading:order','trading:favorite']"
        >
          <template #title>
            <el-icon><Goods /></el-icon>
            <span>销售对接</span>
          </template>
          <el-menu-item index="/sales/supply" v-permission="'trading:supply_market'">供应信息</el-menu-item>
          <el-menu-item index="/sales/demand" v-permission="'trading:demand_market'">需求信息</el-menu-item>
          <el-menu-item index="/sales/favorite" v-permission="'trading:favorite'">我的收藏</el-menu-item>
          <el-menu-item index="/sales/order" v-permission="'trading:order'">订单管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu
          v-if="userStore.userType !== 3"
          index="trace"
          v-permission="['trace','trace:manage','trace:query']"
        >
          <template #title>
            <el-icon><Connection /></el-icon>
            <span>质量溯源</span>
          </template>
          <el-menu-item index="/trace/manage" v-permission="'trace:manage'">溯源管理</el-menu-item>
          <el-menu-item index="/trace/query" v-permission="'trace:query'">溯源查询</el-menu-item>
        </el-sub-menu>

        <el-sub-menu
          v-if="userStore.userType !== 2"
          index="analysis"
          v-permission="['analysis','analysis:yield','analysis:sales']"
        >
          <template #title>
            <el-icon><TrendCharts /></el-icon>
            <span>数据分析</span>
          </template>
          <el-menu-item index="/analysis/sales" v-permission="'analysis:sales'">销售分析</el-menu-item>
          <el-menu-item index="/analysis/yield" v-permission="'analysis:yield'">产量分析</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="system" v-if="userStore.userType === 3" v-permission="['system','system:user','system:role','system:permission','system:notice','system:config','system:log']">
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

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部导航 -->
      <header class="header">
        <div class="header-left">
          <el-button
            class="collapse-btn"
            :icon="isCollapsed ? Expand : Fold"
            @click="toggleCollapse"
          />
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title !== '首页'">
              {{ $route.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-tooltip content="全屏" placement="bottom">
            <el-button class="header-btn" :icon="FullScreen" @click="toggleFullscreen" />
          </el-tooltip>

          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="36" :src="userStore.avatar">
                {{ userStore.realName?.charAt(0) }}
              </el-avatar>
              <div class="user-detail">
                <span class="username">{{ userStore.realName }}</span>
                <span class="user-type">{{ userStore.userTypeName }}</span>
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

      <!-- 内容区域 -->
      <main class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>

      <el-dialog v-model="profileVisible" title="个人中心" width="480px">
        <el-form
          ref="profileFormRef"
          :model="profileForm"
          :rules="profileRules"
          label-width="90px"
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
          <el-form-item label="头像" prop="avatar">
            <el-input v-model="profileForm.avatar" placeholder="头像地址（可选）" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="profileVisible = false">取消</el-button>
          <el-button type="primary" :loading="profileLoading" @click="submitProfile">
            保存
          </el-button>
        </template>
      </el-dialog>

      <el-dialog v-model="passwordVisible" title="修改密码" width="420px">
        <el-form
          ref="passwordFormRef"
          :model="passwordForm"
          :rules="passwordRules"
          label-width="90px"
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
          <el-button type="primary" :loading="passwordLoading" @click="submitPassword">
            确认修改
          </el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  Grape, HomeFilled, Cherry, Goods, Connection, TrendCharts, Setting, Bell,
  Fold, Expand, FullScreen, ArrowDown, User, Lock, SwitchButton
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { updatePassword, updateProfile, logout as logoutApi } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapsed = ref(false)
const isPublicPage = computed(() => route.meta.public && !userStore.isLoggedIn)

const currentRoute = computed(() => route.path)

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
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
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
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}
const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不少于6位', trigger: 'blur' }
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

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      openProfileDialog()
      break
    case 'password':
      openPasswordDialog()
      break
    case 'notice':
      router.push('/notice')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          type: 'warning',
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        })
        try {
          await logoutApi()
        } catch {
          // 忽略退出请求失败
        }
        userStore.logout()
        router.push('/login')
        ElMessage.success('已退出登录')
      } catch {
        // 取消
      }
      break
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

const submitProfile = async () => {
  const valid = await profileFormRef.value?.validate().catch(() => false)
  if (!valid) return
  profileLoading.value = true
  try {
    const payload = {
      realName: profileForm.realName.trim(),
      phone: profileForm.phone.trim(),
      email: profileForm.email.trim(),
      avatar: profileForm.avatar.trim()
    }
    Object.keys(payload).forEach((key) => {
      if (payload[key] === '') {
        payload[key] = null
      }
    })
    await updateProfile(payload)
    await userStore.fetchUserInfo()
    ElMessage.success('个人信息已更新')
    profileVisible.value = false
  } finally {
    profileLoading.value = false
  }
}

const openPasswordDialog = () => {
  passwordFormRef.value?.resetFields()
  passwordVisible.value = true
}

const submitPassword = async () => {
  const valid = await passwordFormRef.value?.validate().catch(() => false)
  if (!valid) return
  passwordLoading.value = true
  try {
    await updatePassword(passwordForm)
    ElMessage.success('密码修改成功，请重新登录')
    passwordVisible.value = false
    userStore.logout()
    router.push('/login')
  } finally {
    passwordLoading.value = false
  }
}

const ensureUserInfo = async () => {
  if (!userStore.isLoggedIn || isPublicPage.value) return
  try {
    await userStore.fetchUserInfo()
  } catch {
    // 忽略失败，交由拦截器处理
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
  background: #f0f2f5;
  padding: 24px;
}

.main-layout {
  display: flex;
  min-height: 100vh;
  background: #f0f2f5;
}

.sidebar {
  width: 220px;
  background: #1a472a;
  transition: width 0.3s;
  display: flex;
  flex-direction: column;
}

.sidebar.collapsed {
  width: 64px;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: white;
  font-size: 18px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-text {
  white-space: nowrap;
}

.sidebar :deep(.el-menu) {
  border-right: none;
  flex: 1;
}

.sidebar :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.15) !important;
}

.sidebar :deep(.el-sub-menu__title:hover),
.sidebar :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1) !important;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 60px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-btn {
  font-size: 18px;
  border: none;
  background: transparent;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-btn {
  border: none;
  background: transparent;
  font-size: 18px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 8px;
  transition: background 0.3s;
}

.user-info:hover {
  background: #f5f7fa;
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.username {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.user-type {
  font-size: 12px;
  color: #909399;
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
