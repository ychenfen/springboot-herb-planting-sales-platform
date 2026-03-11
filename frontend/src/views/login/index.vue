<template>
  <div class="login-page">
    <div class="ambient"></div>

    <div class="login-shell">
      <section class="brand-panel">
        <div class="brand-badge">
          <el-icon :size="34"><Grape /></el-icon>
        </div>
        <h1>中药材种植与销售服务平台</h1>
        <p>聚焦种植知识、规范销售、质量溯源和多角色协同管理。</p>

        <div class="feature-list">
          <div class="feature-item">
            <el-icon><CircleCheck /></el-icon>
            <span>中药材智能百科与病虫害识别演示</span>
          </div>
          <div class="feature-item">
            <el-icon><CircleCheck /></el-icon>
            <span>规格计价、购物车与订单追踪</span>
          </div>
          <div class="feature-item">
            <el-icon><CircleCheck /></el-icon>
            <span>产地、采收、质检状态可视化溯源</span>
          </div>
          <div class="feature-item">
            <el-icon><CircleCheck /></el-icon>
            <span>普通用户 / 种植户 / 商家 / 管理员四角色</span>
          </div>
        </div>
      </section>

      <section class="form-panel">
        <div class="form-head">
          <h2>欢迎登录</h2>
          <p>请输入账号和密码进入系统</p>
        </div>

        <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              :prefix-icon="User"
              placeholder="请输入用户名"
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              :prefix-icon="Lock"
              placeholder="请输入密码"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-button type="primary" class="submit-btn" :loading="loading" @click="handleLogin">
            {{ loading ? '登录中...' : '登录系统' }}
          </el-button>
        </el-form>

        <div class="account-panel">
          <span class="account-title">演示账号</span>
          <div class="account-list">
            <div class="account-item">
              <strong>管理员</strong>
              <span>admin / admin123</span>
            </div>
            <div class="account-item">
              <strong>种植户</strong>
              <span>farmer001 / admin123</span>
            </div>
            <div class="account-item">
              <strong>商家</strong>
              <span>buyer001 / admin123</span>
            </div>
            <div class="account-item">
              <strong>普通用户</strong>
              <span>user001 / admin123</span>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCheck, Grape, Lock, User } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref()
const loading = ref(false)
const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login(loginForm)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  overflow: hidden;
  background:
    radial-gradient(circle at 12% 18%, rgba(199, 211, 137, 0.3), transparent 24%),
    radial-gradient(circle at 88% 82%, rgba(61, 120, 73, 0.24), transparent 22%),
    linear-gradient(135deg, #123321 0%, #1f5d34 50%, #7b934a 100%);
}

.ambient {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(120deg, rgba(255, 255, 255, 0.08) 0%, transparent 34%),
    linear-gradient(300deg, rgba(255, 255, 255, 0.05) 0%, transparent 32%);
}

.login-shell {
  position: relative;
  z-index: 1;
  width: min(1100px, 100%);
  min-height: 620px;
  display: grid;
  grid-template-columns: 1.15fr 0.85fr;
  border-radius: 28px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 28px 80px rgba(7, 22, 12, 0.28);
}

.brand-panel {
  padding: 56px 48px;
  color: #fff;
  background:
    radial-gradient(circle at top right, rgba(214, 214, 143, 0.16), transparent 25%),
    linear-gradient(180deg, #143725 0%, #19492e 55%, #2f6841 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.brand-badge {
  width: 68px;
  height: 68px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #93c267 0%, #cad26a 100%);
  color: #133624;
  margin-bottom: 24px;
}

.brand-panel h1 {
  margin: 0 0 12px;
  font-size: 34px;
  line-height: 1.25;
}

.brand-panel p {
  margin: 0 0 28px;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.82);
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.08);
}

.form-panel {
  padding: 56px 44px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-head {
  margin-bottom: 26px;
}

.form-head h2 {
  margin: 0 0 8px;
  color: #1d3f2b;
  font-size: 30px;
}

.form-head p {
  margin: 0;
  color: #738174;
}

.form-panel :deep(.el-input__wrapper) {
  min-height: 46px;
  border-radius: 12px;
}

.submit-btn {
  width: 100%;
  height: 48px;
  margin-top: 6px;
  border-radius: 14px;
  font-size: 16px;
  letter-spacing: 1px;
}

.account-panel {
  margin-top: 28px;
  padding-top: 22px;
  border-top: 1px solid #edf0ea;
}

.account-title {
  display: inline-block;
  margin-bottom: 14px;
  color: #506652;
  font-size: 13px;
}

.account-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.account-item {
  padding: 14px;
  border-radius: 14px;
  background: #f5f9f3;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.account-item strong {
  color: #284a33;
}

.account-item span {
  color: #718071;
  font-size: 13px;
}

@media (max-width: 960px) {
  .login-shell {
    grid-template-columns: 1fr;
  }

  .brand-panel {
    padding: 40px 32px;
  }
}

@media (max-width: 640px) {
  .login-page {
    padding: 14px;
  }

  .form-panel,
  .brand-panel {
    padding: 28px 22px;
  }

  .account-list {
    grid-template-columns: 1fr;
  }
}
</style>
