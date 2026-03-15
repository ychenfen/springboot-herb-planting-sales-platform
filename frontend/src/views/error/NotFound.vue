<template>
  <div class="not-found">
    <div class="content">
      <div class="error-code">404</div>
      <h2>页面未找到</h2>
      <p>抱歉，您访问的页面不存在或已被移除。</p>
      <div class="actions">
        <el-button type="primary" size="large" @click="goHome">
          <el-icon><HomeFilled /></el-icon>
          返回首页
        </el-button>
        <el-button size="large" @click="goBack">
          <el-icon><Back /></el-icon>
          返回上页
        </el-button>
      </div>
    </div>
    <div class="decoration">
      <div class="leaf leaf-1"></div>
      <div class="leaf leaf-2"></div>
      <div class="leaf leaf-3"></div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { HomeFilled, Back } from '@element-plus/icons-vue'

const router = useRouter()

const goHome = () => {
  router.push('/dashboard')
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.not-found {
  min-height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.content {
  text-align: center;
  position: relative;
  z-index: 1;
}

.error-code {
  font-size: 140px;
  font-weight: 900;
  line-height: 1;
  background: linear-gradient(135deg, #2e7d32 0%, #66bb6a 50%, #a5d6a7 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 16px;
}

h2 {
  font-size: 28px;
  color: #1d3f2b;
  margin: 0 0 12px;
}

p {
  font-size: 16px;
  color: #738174;
  margin: 0 0 32px;
}

.actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.leaf {
  position: absolute;
  width: 60px;
  height: 60px;
  border-radius: 50% 0 50% 0;
  opacity: 0.08;
}

.leaf-1 {
  background: #2e7d32;
  top: 15%;
  left: 10%;
  transform: rotate(30deg);
  width: 80px;
  height: 80px;
}

.leaf-2 {
  background: #66bb6a;
  bottom: 20%;
  right: 15%;
  transform: rotate(-20deg);
  width: 100px;
  height: 100px;
}

.leaf-3 {
  background: #a5d6a7;
  top: 30%;
  right: 25%;
  transform: rotate(60deg);
  width: 50px;
  height: 50px;
}

@media (max-width: 768px) {
  .error-code {
    font-size: 100px;
  }

  h2 {
    font-size: 22px;
  }

  .actions {
    flex-direction: column;
    align-items: center;
  }
}
</style>
