<template>
  <div class="knowledge-page">
    <section class="hero card-shadow">
      <div>
        <span class="hero-kicker">专业 / 实用 / 乡村振兴</span>
        <h2>中药材种植百科</h2>
        <p>支持按药材类型、种植季节和病害类型快速检索常见种植要点，也支持组合关键词直接搜索。</p>
      </div>
    </section>

    <section class="toolbar card-shadow">
      <el-form :inline="true" :model="queryForm" class="toolbar-form">
        <el-form-item label="关键词">
          <el-input
            v-model="queryForm.keyword"
            clearable
            placeholder="例如：黄芪 病害"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="药材分类">
          <el-select v-model="queryForm.herbCategory" clearable placeholder="全部分类" style="width: 160px">
            <el-option v-for="item in herbCategoryOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="种植季节">
          <el-select v-model="queryForm.plantingSeason" clearable placeholder="全部季节" style="width: 140px">
            <el-option v-for="item in seasonOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="病害类型">
          <el-select v-model="queryForm.diseaseType" clearable placeholder="全部类型" style="width: 160px">
            <el-option v-for="item in diseaseTypeOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">检索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <div class="toolbar-tip">
        当前共找到 <strong>{{ total }}</strong> 条知识记录
      </div>
    </section>

    <section class="result-grid" v-loading="loading">
      <article
        v-for="item in records"
        :key="item.id"
        class="knowledge-card card-shadow"
        @click="openDetail(item)"
      >
        <div class="card-head">
          <div>
            <h3>{{ item.herbName }}</h3>
            <p>{{ item.herbAlias || '暂无别名信息' }}</p>
          </div>
          <el-tag effect="dark" type="success">{{ item.herbCategory || '未分类' }}</el-tag>
        </div>
        <div class="card-tags">
          <el-tag size="small">{{ item.plantingSeason || '季节待补充' }}</el-tag>
          <el-tag size="small" type="warning">{{ item.diseaseType || '病害类型待补充' }}</el-tag>
          <el-tag size="small" type="info">{{ item.suitableRegion || '适宜区域待补充' }}</el-tag>
        </div>
        <p class="summary">{{ item.summary || '暂无摘要' }}</p>
        <div class="points">
          <div>
            <span>种植要点</span>
            <p>{{ item.plantingPoints || '暂无' }}</p>
          </div>
          <div>
            <span>乡村振兴价值</span>
            <p>{{ item.ruralValue || '暂无' }}</p>
          </div>
        </div>
      </article>

      <el-empty
        v-if="!loading && records.length === 0"
        class="empty-block card-shadow"
        description="没有检索到匹配的药材知识"
      />
    </section>

    <section class="pager card-shadow" v-if="total > 0">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[6, 12, 18]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </section>

    <el-drawer v-model="detailVisible" :title="activeItem.herbName || '知识详情'" size="48%">
      <div v-if="activeItem.id" class="detail-panel">
        <div class="detail-tags">
          <el-tag>{{ activeItem.herbCategory || '未分类' }}</el-tag>
          <el-tag type="success">{{ activeItem.plantingSeason || '季节待补充' }}</el-tag>
          <el-tag type="warning">{{ activeItem.diseaseType || '病害类型待补充' }}</el-tag>
        </div>

        <div class="detail-section">
          <h4>摘要</h4>
          <p>{{ activeItem.summary || '暂无摘要' }}</p>
        </div>

        <div class="detail-section">
          <h4>种植要点</h4>
          <p>{{ activeItem.plantingPoints || '暂无内容' }}</p>
        </div>

        <div class="detail-section">
          <h4>病害防治</h4>
          <p>{{ activeItem.diseasePrevention || '暂无内容' }}</p>
        </div>

        <div class="detail-section">
          <h4>适宜区域</h4>
          <p>{{ activeItem.suitableRegion || '暂无内容' }}</p>
        </div>

        <div class="detail-section">
          <h4>乡村振兴价值</h4>
          <p>{{ activeItem.ruralValue || '暂无内容' }}</p>
        </div>

        <div class="detail-section">
          <h4>扩展内容</h4>
          <p>{{ activeItem.content || '暂无扩展内容' }}</p>
        </div>

        <div class="detail-section">
          <h4>检索标签</h4>
          <p>{{ activeItem.keywordTags || '暂无标签' }}</p>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { getKnowledgePage } from '@/api/knowledge'

const loading = ref(false)
const records = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(6)
const detailVisible = ref(false)
const activeItem = ref({})

const herbCategoryOptions = ['补气类药材', '根茎类药材', '根类药材']
const seasonOptions = ['春季', '夏季', '秋季', '冬季']
const diseaseTypeOptions = ['叶部病害', '真菌性病害', '土传病害', '茎基部病害']

const queryForm = reactive({
  keyword: '',
  herbCategory: '',
  plantingSeason: '',
  diseaseType: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getKnowledgePage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...queryForm
    })
    records.value = res.data.records || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  loadData()
}

const handleReset = () => {
  queryForm.keyword = ''
  queryForm.herbCategory = ''
  queryForm.plantingSeason = ''
  queryForm.diseaseType = ''
  handleSearch()
}

const openDetail = (item) => {
  activeItem.value = item
  detailVisible.value = true
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.knowledge-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hero {
  padding: 24px 28px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  background:
    radial-gradient(circle at right top, rgba(109, 155, 86, 0.22), transparent 32%),
    linear-gradient(135deg, #f9fbf6 0%, #eef6ee 100%);
}

.hero-kicker {
  display: inline-block;
  margin-bottom: 10px;
  color: #497442;
  font-size: 12px;
  letter-spacing: 1px;
}

.hero h2 {
  margin: 0 0 8px;
  color: #21402d;
  font-size: 26px;
}

.hero p {
  margin: 0;
  color: #5e7261;
  max-width: 760px;
  line-height: 1.7;
}

.toolbar {
  padding: 18px 20px;
}

.toolbar-form {
  display: flex;
  flex-wrap: wrap;
}

.toolbar-tip {
  margin-top: 8px;
  color: #627568;
  font-size: 13px;
}

.result-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.knowledge-card {
  padding: 20px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.knowledge-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 14px 28px rgba(34, 64, 45, 0.12);
}

.card-head {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.card-head h3 {
  margin: 0 0 6px;
  font-size: 20px;
  color: #233c2d;
}

.card-head p {
  margin: 0;
  color: #809182;
  font-size: 13px;
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 14px 0;
}

.summary {
  margin: 0 0 16px;
  color: #4f6154;
  line-height: 1.7;
  min-height: 68px;
}

.points {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.points span {
  display: block;
  margin-bottom: 6px;
  color: #2a5838;
  font-size: 13px;
  font-weight: 600;
}

.points p {
  margin: 0;
  color: #687b6b;
  line-height: 1.6;
  font-size: 13px;
}

.pager {
  padding: 14px 18px;
  display: flex;
  justify-content: flex-end;
}

.empty-block {
  grid-column: 1 / -1;
  padding: 40px 0;
}

.detail-panel {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.detail-section h4 {
  margin: 0 0 8px;
  color: #23452f;
}

.detail-section p {
  margin: 0;
  line-height: 1.8;
  color: #55675a;
}

@media (max-width: 768px) {
  .hero {
    flex-direction: column;
  }
}
</style>
