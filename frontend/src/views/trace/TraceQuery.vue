<template>
  <div class="trace-query">
    <!-- 查询区域 -->
    <div class="query-section card-shadow">
      <h2><el-icon><Search /></el-icon> 药材溯源查询</h2>
      <p>输入溯源码或扫描二维码，查询药材完整溯源信息</p>
      <div class="query-input">
        <el-input
          v-model="traceCode"
          placeholder="请输入溯源码"
          size="large"
          clearable
          @keyup.enter="handleQuery"
        >
          <template #prefix>
            <el-icon><Connection /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" size="large" :loading="loading" @click="handleQuery">
          查询溯源
        </el-button>
      </div>
    </div>

    <!-- 溯源结果 -->
    <div v-if="traceInfo.id" class="result-section">
      <!-- 基本信息卡片 -->
      <div class="info-card card-shadow">
        <div class="card-header">
          <h3><el-icon><Document /></el-icon> 基本信息</h3>
          <el-tag type="success" size="large">已认证</el-tag>
        </div>
        <el-descriptions :column="3" border>
          <el-descriptions-item label="溯源码">{{ traceInfo.traceCode }}</el-descriptions-item>
          <el-descriptions-item label="药材名称">{{ traceInfo.herbName }}</el-descriptions-item>
          <el-descriptions-item label="批次号">{{ traceInfo.batchNo }}</el-descriptions-item>
          <el-descriptions-item label="产地">{{ traceInfo.productionArea }}</el-descriptions-item>
          <el-descriptions-item label="种植日期">{{ traceInfo.plantDate }}</el-descriptions-item>
          <el-descriptions-item label="采收日期">{{ traceInfo.harvestDate }}</el-descriptions-item>
          <el-descriptions-item label="质量标准">{{ traceInfo.qualityStandard }}</el-descriptions-item>
          <el-descriptions-item label="扫码次数">{{ traceInfo.scanCount }} 次</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ traceInfo.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 溯源节点时间线 -->
      <div class="timeline-card card-shadow">
        <div class="card-header">
          <h3><el-icon><Clock /></el-icon> 溯源轨迹</h3>
        </div>
        <el-timeline v-if="traceInfo.nodes?.length">
          <el-timeline-item
            v-for="node in traceInfo.nodes"
            :key="node.id"
            :timestamp="node.nodeTime"
            placement="top"
            :type="getNodeType(node.nodeType)"
            :hollow="false"
            size="large"
          >
            <div class="timeline-content">
              <div class="node-header">
                <el-tag>{{ node.nodeTypeName }}</el-tag>
                <span class="node-name">{{ node.nodeName }}</span>
              </div>
              <p class="node-desc">{{ node.description }}</p>
              <div class="node-info">
                <span><el-icon><User /></el-icon> {{ node.operator }}</span>
                <span><el-icon><Location /></el-icon> {{ node.location }}</span>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无溯源节点" />
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="searched" class="empty-section card-shadow">
      <el-empty description="未找到溯源信息，请检查溯源码是否正确" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Connection, Document, Clock, User, Location } from '@element-plus/icons-vue'
import { getTraceByCode } from '@/api/trace'

const route = useRoute()
const traceCode = ref('')
const loading = ref(false)
const searched = ref(false)
const traceInfo = ref({})

const getNodeType = (type) => {
  const types = {
    plant: 'success', grow: 'success', fertilize: 'primary', spray: 'warning',
    harvest: 'success', process: 'primary', quality: 'success', package: 'primary',
    storage: 'info', transport: 'warning'
  }
  return types[type] || 'primary'
}

const handleQuery = async () => {
  if (!traceCode.value.trim()) {
    ElMessage.warning('请输入溯源码')
    return
  }
  loading.value = true
  searched.value = true
  try {
    const res = await getTraceByCode(traceCode.value.trim())
    traceInfo.value = res.data || {}
  } catch (e) {
    traceInfo.value = {}
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  if (route.query.code) {
    traceCode.value = route.query.code
    handleQuery()
  }
})
</script>

<style scoped>
.trace-query { display: flex; flex-direction: column; gap: 20px; }

.query-section {
  padding: 40px;
  text-align: center;
  background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
}

.query-section h2 {
  font-size: 24px;
  color: #1a472a;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.query-section p {
  color: #606266;
  margin-bottom: 24px;
}

.query-input {
  display: flex;
  gap: 12px;
  max-width: 500px;
  margin: 0 auto;
}

.query-input :deep(.el-input) { flex: 1; }

.result-section { display: flex; flex-direction: column; gap: 20px; }

.info-card, .timeline-card { padding: 24px; }

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header h3 {
  font-size: 18px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
}

.timeline-content {
  background: #f5f7fa;
  padding: 16px;
  border-radius: 8px;
}

.node-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.node-name { font-weight: 600; color: #303133; }
.node-desc { color: #606266; margin: 8px 0; font-size: 14px; }

.node-info {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #909399;
}

.node-info span { display: flex; align-items: center; gap: 4px; }

.empty-section { padding: 60px; }

.card-shadow { background: white; border-radius: 12px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08); }
</style>
