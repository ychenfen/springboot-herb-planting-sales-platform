<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <div class="login-left">
        <div class="brand">
          <div class="logo-icon">
            <el-icon :size="48"><Grape /></el-icon>
          </div>
          <h1>中药材种植与销售服务平台</h1>
          <p>Traditional Chinese Medicine Planting & Sales Platform</p>
        </div>
        <div class="features">
          <div class="feature-item">
            <el-icon><CircleCheck /></el-icon>
            <span>智能种植管理</span>
          </div>
          <div class="feature-item">
            <el-icon><CircleCheck /></el-icon>
            <span>产销精准对接</span>
          </div>
          <div class="feature-item">
            <el-icon><CircleCheck /></el-icon>
            <span>全程质量溯源</span>
          </div>
          <div class="feature-item">
            <el-icon><CircleCheck /></el-icon>
            <span>数据智能分析</span>
          </div>
        </div>
      </div>

      <div class="login-right">
        <div class="login-form-wrapper">
          <h2>欢迎登录</h2>
          <p class="subtitle">请输入您的账户信息</p>

          <el-form
            ref="loginFormRef"
            :model="loginForm"
            :rules="loginRules"
            class="login-form"
            size="large"
          >
            <el-form-item prop="username">
              <el-input
                v-model="loginForm.username"
                placeholder="请输入用户名"
                :prefix-icon="User"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <el-form-item>
              <div class="form-options">
                <el-checkbox v-model="rememberMe">记住我</el-checkbox>
                <a href="javascript:void(0)" class="forgot-link">忘记密码?</a>
              </div>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                class="login-button"
                @click="handleLogin"
              >
                {{ loading ? '登录中...' : '登 录' }}
              </el-button>
            </el-form-item>
          </el-form>

          <div class="login-footer">
            <span>测试账号：admin / admin123</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Grape, CircleCheck } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref()
const loading = ref(false)
const rememberMe = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login(loginForm)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a472a 0%, #2e7d32 50%, #4caf50 100%);
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  right: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -50px;
  left: -50px;
  animation-delay: 2s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  left: 10%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-20px) scale(1.05);
  }
}

.login-card {
  display: flex;
  width: 900px;
  min-height: 520px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  z-index: 1;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #2e7d32 0%, #1b5e20 100%);
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: white;
}

.brand {
  text-align: center;
  margin-bottom: 40px;
}

.logo-icon {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.brand h1 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
}

.brand p {
  font-size: 12px;
  opacity: 0.8;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 10px;
  transition: all 0.3s;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(5px);
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.login-form-wrapper {
  width: 100%;
  max-width: 320px;
}

.login-form-wrapper h2 {
  font-size: 28px;
  color: #1a472a;
  margin-bottom: 8px;
}

.subtitle {
  color: #909399;
  margin-bottom: 32px;
}

.login-form {
  margin-bottom: 20px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 12px;
}

.form-options {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.forgot-link {
  color: #2e7d32;
  text-decoration: none;
  font-size: 14px;
}

.forgot-link:hover {
  text-decoration: underline;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #2e7d32 0%, #1b5e20 100%);
  border: none;
  transition: all 0.3s;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(46, 125, 50, 0.4);
}

.login-footer {
  text-align: center;
  color: #909399;
  font-size: 12px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

@media (max-width: 768px) {
  .login-card {
    flex-direction: column;
    width: 90%;
    max-width: 400px;
  }

  .login-left {
    padding: 30px 20px;
  }

  .features {
    display: none;
  }
}
</style>
