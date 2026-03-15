<template>
  <div class="register-page">
    <div class="ambient"></div>
    <div class="register-shell">
      <section class="brand-panel">
        <div class="brand-badge">
          <el-icon :size="36"><Grape /></el-icon>
        </div>
        <h1>加入本草云链</h1>
        <p>
          中药材种植与销售服务平台，为种植户、商家和消费者搭建高效协同桥梁。
          注册后即可体验种植管理、供需对接、质量溯源等全链路功能。
        </p>
        <div class="feature-list">
          <div class="feature-item">
            <el-icon :size="20"><CircleCheck /></el-icon>
            <span>种植户：管理地块、作物、农事记录，发布供应信息</span>
          </div>
          <div class="feature-item">
            <el-icon :size="20"><CircleCheck /></el-icon>
            <span>商家：发布采购需求，在线下单，追踪物流</span>
          </div>
          <div class="feature-item">
            <el-icon :size="20"><CircleCheck /></el-icon>
            <span>消费者：浏览供应大厅，查询溯源信息</span>
          </div>
        </div>
      </section>
      <section class="form-panel">
        <div class="form-head">
          <h2>注册账号</h2>
          <p>填写以下信息完成注册</p>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" :prefix-icon="User" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="form.realName" :prefix-icon="User" placeholder="请输入真实姓名" />
          </el-form-item>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="密码" prop="password">
                <el-input v-model="form.password" :prefix-icon="Lock" type="password" show-password placeholder="请输入密码" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="form.confirmPassword" :prefix-icon="Lock" type="password" show-password placeholder="请再次输入密码" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="用户类型" prop="userType">
            <el-radio-group v-model="form.userType">
              <el-radio-button :value="1">种植户</el-radio-button>
              <el-radio-button :value="2">商家</el-radio-button>
              <el-radio-button :value="4">普通用户</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="form.phone" placeholder="请输入手机号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="form.email" placeholder="请输入邮箱（选填）" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-button class="submit-btn" type="primary" :loading="loading" @click="handleRegister">
            注 册
          </el-button>
        </el-form>
        <div class="login-link">
          已有账号？<router-link to="/login">立即登录</router-link>
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
import { register } from '@/api/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  realName: '',
  password: '',
  confirmPassword: '',
  userType: 4,
  phone: '',
  email: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
    return
  }
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
    return
  }
  callback()
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度在 4 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  userType: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
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
.register-shell {
  position: relative;
  z-index: 1;
  width: min(1100px, 100%);
  min-height: 620px;
  display: grid;
  grid-template-columns: 1fr 1.1fr;
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
  padding: 40px 44px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow-y: auto;
}
.form-head {
  margin-bottom: 20px;
}
.form-head h2 {
  margin: 0 0 8px;
  color: #1d3f2b;
  font-size: 28px;
}
.form-head p {
  margin: 0;
  color: #738174;
}
.form-panel :deep(.el-input__wrapper) {
  min-height: 42px;
  border-radius: 12px;
}
.form-panel :deep(.el-form-item__label) {
  font-size: 13px;
  color: #506652;
}
.submit-btn {
  width: 100%;
  height: 48px;
  margin-top: 6px;
  border-radius: 14px;
  font-size: 16px;
  letter-spacing: 1px;
}
.login-link {
  margin-top: 20px;
  text-align: center;
  color: #738174;
  font-size: 14px;
}
.login-link a {
  color: #2e7d32;
  text-decoration: none;
  font-weight: 600;
}
.login-link a:hover {
  text-decoration: underline;
}
@media (max-width: 960px) {
  .register-shell {
    grid-template-columns: 1fr;
  }
  .brand-panel {
    padding: 40px 32px;
  }
}
@media (max-width: 640px) {
  .register-page {
    padding: 14px;
  }
  .form-panel,
  .brand-panel {
    padding: 28px 22px;
  }
}
</style>
