<template>
  <div class="diagnosis-page">
    <section class="intro card-shadow">
      <div>
        <span class="intro-kicker">科技助农演示能力</span>
        <h2>病虫害智能识别</h2>
        <p>上传病害图片后，系统会基于本地样例库进行相似度匹配，并给出图文对照与防治建议。</p>
      </div>
      <el-alert
        title="演示模型说明：当前版本采用本地样例图库 + 图像相似度匹配，适合课程答辩和功能演示。"
        type="success"
        :closable="false"
        show-icon
      />
    </section>

    <section class="workspace">
      <div class="left-panel card-shadow">
        <div class="panel-head">
          <h3>样例病症库</h3>
          <span>{{ diseaseList.length }} 条样例</span>
        </div>
        <el-form :model="queryForm" class="filter-form" label-position="top">
          <el-form-item label="药材名称">
            <el-select v-model="queryForm.herbName" clearable placeholder="全部药材">
              <el-option v-for="item in herbOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="病害类型">
            <el-select v-model="queryForm.diseaseType" clearable placeholder="全部病害">
              <el-option v-for="item in diseaseTypeOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="关键词">
            <el-input v-model="queryForm.keyword" clearable placeholder="病斑、枯黄、腐烂等" />
          </el-form-item>
          <el-button type="primary" @click="loadDiseaseList">更新样例</el-button>
        </el-form>

        <div class="disease-list" v-loading="listLoading">
          <article v-for="item in diseaseList" :key="item.id" class="disease-card">
            <img :src="item.imageUrl" :alt="item.diseaseName" class="disease-image" @click="openPreview(item.imageUrl)" />
            <div class="disease-info">
              <div class="title-row">
                <strong>{{ item.diseaseName }}</strong>
                <el-tag size="small">{{ item.herbName }}</el-tag>
              </div>
              <div class="meta-row">
                <el-tag size="small" type="warning">{{ item.diseaseType || '病害待补充' }}</el-tag>
                <el-tag size="small" type="info">{{ item.season || '季节待补充' }}</el-tag>
              </div>
              <p>{{ item.symptomDescription }}</p>
              <small>防治方案：{{ item.preventionPlan }}</small>
            </div>
          </article>

          <el-empty v-if="!listLoading && !diseaseList.length" description="暂无病害样例" />
        </div>
      </div>

      <div class="right-panel">
        <section class="upload-card card-shadow">
          <div class="panel-head">
            <h3>上传识别</h3>
            <span>支持 PNG / JPG</span>
          </div>
          <el-form :model="identifyForm" label-position="top">
            <el-form-item label="识别药材">
              <el-select v-model="identifyForm.herbName" clearable placeholder="可选，帮助缩小匹配范围">
                <el-option v-for="item in herbOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-form>

          <el-upload
            class="uploader"
            drag
            :auto-upload="false"
            :show-file-list="false"
            accept=".png,.jpg,.jpeg"
            :on-change="handleFileChange"
          >
            <div v-if="previewUrl" class="preview-wrap">
              <img :src="previewUrl" alt="uploaded preview" class="preview-image" />
            </div>
            <template v-else>
              <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
              <div class="el-upload__text">
                将病害图片拖到此处，或 <em>点击上传</em>
              </div>
            </template>
          </el-upload>

          <div class="upload-actions">
            <el-button @click="resetUpload">清空</el-button>
            <el-button type="primary" :loading="identifyLoading" @click="handleIdentify">开始识别</el-button>
          </div>
        </section>

        <section class="result-card card-shadow" v-loading="identifyLoading">
          <div class="panel-head">
            <h3>识别结果</h3>
            <span>{{ result.matches?.length || 0 }} 条候选</span>
          </div>

          <div v-if="result.matches?.length" class="match-list">
            <article v-for="item in result.matches" :key="item.id" class="match-card">
              <img :src="item.imageUrl" :alt="item.diseaseName" class="match-image" />
              <div class="match-body">
                <div class="title-row">
                  <strong>{{ item.diseaseName }}</strong>
                  <el-tag type="success">{{ item.matchScore }}%</el-tag>
                </div>
                <div class="meta-row">
                  <el-tag size="small">{{ item.herbName }}</el-tag>
                  <el-tag size="small" type="warning">{{ item.severityLevel || '等级待补充' }}</el-tag>
                  <el-tag size="small" type="info">{{ item.featureTag || '特征待补充' }}</el-tag>
                </div>
                <el-progress :percentage="Number(item.matchScore || 0)" :stroke-width="10" />
                <p class="reason">{{ item.matchReason }}</p>
                <p>症状描述：{{ item.symptomDescription }}</p>
                <p>防治方案：{{ item.preventionPlan }}</p>
              </div>
            </article>
          </div>

          <el-empty
            v-else
            description="上传图片后查看相似病症和防治建议"
            :image-size="80"
          />
        </section>
      </div>
    </section>

    <el-dialog v-model="previewVisible" title="病症样例预览" width="560px">
      <img v-if="previewImage" :src="previewImage" alt="disease preview" class="dialog-preview" />
    </el-dialog>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { getDiseaseList, identifyDisease } from '@/api/knowledge'

const herbOptions = ['黄芪', '当归', '白芍']
const diseaseTypeOptions = ['叶部病害', '真菌性病害', '土传病害', '茎基部病害']

const listLoading = ref(false)
const identifyLoading = ref(false)
const diseaseList = ref([])
const previewVisible = ref(false)
const previewImage = ref('')
const selectedFile = ref(null)
const previewUrl = ref('')
const result = ref({})

const queryForm = reactive({
  herbName: '',
  diseaseType: '',
  keyword: ''
})

const identifyForm = reactive({
  herbName: ''
})

const revokePreview = () => {
  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value)
    previewUrl.value = ''
  }
}

const loadDiseaseList = async () => {
  listLoading.value = true
  try {
    const res = await getDiseaseList({ ...queryForm })
    diseaseList.value = res.data || []
  } finally {
    listLoading.value = false
  }
}

const handleFileChange = (file) => {
  selectedFile.value = file.raw
  revokePreview()
  previewUrl.value = URL.createObjectURL(file.raw)
}

const handleIdentify = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先上传病害图片')
    return
  }

  const formData = new FormData()
  formData.append('file', selectedFile.value)
  if (identifyForm.herbName) {
    formData.append('herbName', identifyForm.herbName)
  }

  identifyLoading.value = true
  try {
    const res = await identifyDisease(formData)
    result.value = res.data || {}
  } finally {
    identifyLoading.value = false
  }
}

const resetUpload = () => {
  selectedFile.value = null
  result.value = {}
  revokePreview()
}

const openPreview = (url) => {
  previewImage.value = url
  previewVisible.value = true
}

onMounted(() => {
  loadDiseaseList()
})

onBeforeUnmount(() => {
  revokePreview()
})
</script>

<style scoped>
.diagnosis-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.intro {
  padding: 24px 28px;
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 18px;
  background:
    radial-gradient(circle at right center, rgba(145, 194, 120, 0.2), transparent 34%),
    linear-gradient(135deg, #f9fbf7 0%, #edf6ee 100%);
}

.intro-kicker {
  color: #577f47;
  font-size: 12px;
  letter-spacing: 1px;
}

.intro h2 {
  margin: 10px 0 8px;
  color: #213f2e;
  font-size: 26px;
}

.intro p {
  margin: 0;
  line-height: 1.8;
  color: #5f7164;
}

.workspace {
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 16px;
  align-items: start;
}

.left-panel,
.upload-card,
.result-card {
  padding: 20px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.panel-head h3 {
  margin: 0;
  color: #22402e;
}

.panel-head span {
  color: #7a8a7b;
  font-size: 13px;
}

.filter-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px 14px;
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.filter-form :deep(.el-form-item:last-child) {
  align-self: end;
}

.disease-list {
  margin-top: 18px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  max-height: 820px;
  overflow-y: auto;
}

.disease-card,
.match-card {
  display: grid;
  grid-template-columns: 112px 1fr;
  gap: 14px;
  padding: 12px;
  border-radius: 14px;
  background: #f7faf7;
}

.disease-image,
.match-image {
  width: 112px;
  height: 112px;
  object-fit: cover;
  border-radius: 12px;
  background: #edf4ed;
  cursor: zoom-in;
}

.disease-info,
.match-body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.title-row,
.meta-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
}

.disease-info p,
.match-body p {
  margin: 0;
  color: #5c6e60;
  line-height: 1.7;
  font-size: 13px;
}

.disease-info small {
  color: #789079;
  line-height: 1.7;
}

.right-panel {
  display: grid;
  gap: 16px;
}

.uploader {
  margin-top: 8px;
}

.uploader :deep(.el-upload-dragger) {
  width: 100%;
  border-radius: 18px;
  background: linear-gradient(180deg, #fbfcfa 0%, #f0f6ef 100%);
}

.preview-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px 0;
}

.preview-image {
  width: 100%;
  max-height: 240px;
  object-fit: contain;
  border-radius: 12px;
}

.upload-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 14px;
}

.match-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.reason {
  color: #2b6b43 !important;
  font-weight: 600;
}

.dialog-preview {
  width: 100%;
  border-radius: 16px;
}

@media (max-width: 1100px) {
  .workspace,
  .intro {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .filter-form {
    grid-template-columns: 1fr;
  }

  .disease-card,
  .match-card {
    grid-template-columns: 1fr;
  }

  .disease-image,
  .match-image {
    width: 100%;
    height: 180px;
  }
}
</style>
