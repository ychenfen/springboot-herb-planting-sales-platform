<template>
  <div class="calendar-page">
    <section class="hero card-shadow">
      <div>
        <span class="hero-kicker">业务深度展示</span>
        <h2>种植日历 / 生长周期提醒</h2>
        <p>围绕播种、施肥、除草、病害巡检、采收等关键节点构建时间线，强化平台的生产指导能力。</p>
      </div>
      <el-alert
        title="老师常关注这里：不是简单 CRUD，而是结合药材生长周期输出农事提醒。"
        type="warning"
        :closable="false"
        show-icon
      />
    </section>

    <section class="control-grid">
      <div class="control-card card-shadow">
        <div class="section-title">
          <h3>生成种植日历</h3>
          <span>支持作物联动和手工推演</span>
        </div>

        <el-form :model="calendarForm" label-position="top">
          <el-form-item v-if="isFarmer" label="我的在田作物">
            <el-select v-model="calendarForm.cropId" clearable placeholder="优先选择已有作物" style="width: 100%">
              <el-option
                v-for="item in cropOptions"
                :key="item.id"
                :label="`${item.cropName}｜播种 ${item.plantDate || '待补充'}`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="药材名称">
            <el-select v-model="calendarForm.herbName" clearable placeholder="选择药材模板" style="width: 100%">
              <el-option v-for="item in herbOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="播种日期">
            <el-date-picker
              v-model="calendarForm.plantDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="选择播种日期"
              style="width: 100%"
            />
          </el-form-item>
          <div class="control-actions">
            <el-button @click="resetCalendarForm">重置</el-button>
            <el-button type="primary" :loading="calendarLoading" @click="loadCalendar">生成日历</el-button>
          </div>
        </el-form>
      </div>

      <div class="reminder-card card-shadow">
        <div class="section-title">
          <h3>近期农事提醒</h3>
          <span>{{ reminderDays }} 天内</span>
        </div>
        <div class="reminder-toolbar">
          <el-slider v-model="reminderDays" :min="7" :max="45" :step="1" show-input @change="loadReminders" />
        </div>
        <div class="reminder-list" v-loading="reminderLoading">
          <article v-for="item in reminders" :key="`${item.cropId}-${item.stageName}-${item.reminderDate}`" class="reminder-item">
            <div class="date-chip">{{ formatDate(item.reminderDate, 'MM-DD') }}</div>
            <div class="reminder-body">
              <strong>{{ item.herbName }} · {{ item.stageName }}</strong>
              <p>{{ item.stageTips || '请按农事节点执行相关作业。' }}</p>
              <small>作业类型：{{ item.actionType }} ｜ 距提醒 {{ formatDays(item.daysUntil) }}</small>
            </div>
          </article>
          <el-empty v-if="!reminderLoading && !reminders.length" description="最近暂无提醒" :image-size="70" />
        </div>
      </div>
    </section>

    <section class="timeline-card card-shadow" v-loading="calendarLoading">
      <div class="section-title">
        <div>
          <h3>生长周期时间线</h3>
          <span v-if="calendar.herbName">
            {{ calendar.herbName }} ｜ 播种 {{ formatDate(calendar.plantDate) }} ｜ 预计采收 {{ formatDate(calendar.expectedHarvestDate) }}
          </span>
        </div>
      </div>

      <el-empty
        v-if="!calendar.stages?.length"
        description="选择药材或作物后生成时间线"
        :image-size="90"
      />

      <div v-else class="timeline-grid">
        <el-timeline>
          <el-timeline-item
            v-for="item in calendar.stages"
            :key="`${item.stageName}-${item.startDate}`"
            :timestamp="`${formatDate(item.startDate)} 至 ${formatDate(item.endDate)}`"
            placement="top"
            type="success"
          >
            <div class="stage-card">
              <div class="stage-head">
                <strong>{{ item.stageName }}</strong>
                <el-tag size="small">{{ item.actionType }}</el-tag>
              </div>
              <p>{{ item.stageTips || '暂无阶段提示' }}</p>
              <div class="stage-meta">
                <span>提醒时间：{{ formatDate(item.reminderDate) }}</span>
                <span>作业窗口：{{ item.operationWindow || '按田间实际情况执行' }}</span>
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>

        <div class="calendar-summary">
          <div class="summary-block">
            <span>播种日期</span>
            <strong>{{ formatDate(calendar.plantDate) }}</strong>
          </div>
          <div class="summary-block">
            <span>预计采收</span>
            <strong>{{ formatDate(calendar.expectedHarvestDate) }}</strong>
          </div>
          <div class="summary-block">
            <span>日历节点</span>
            <strong>{{ calendar.stages?.length || 0 }} 个</strong>
          </div>
          <div class="summary-block">
            <span>近期提醒</span>
            <strong>{{ calendar.reminders?.length || 0 }} 条</strong>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getCropPage } from '@/api/planting'
import { getCalendarReminders, getPlantingCalendar } from '@/api/knowledge'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isFarmer = computed(() => userStore.userType === 1)

const herbOptions = ['黄芪', '当归', '白芍']
const cropOptions = ref([])
const reminders = ref([])
const reminderDays = ref(15)
const calendar = ref({})
const reminderLoading = ref(false)
const calendarLoading = ref(false)

const calendarForm = reactive({
  cropId: null,
  herbName: '',
  plantDate: ''
})

const formatDate = (value, pattern = 'YYYY-MM-DD') => {
  if (!value) return '--'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  if (pattern === 'MM-DD') {
    return `${month}-${day}`
  }
  return `${year}-${month}-${day}`
}

const formatDays = (days) => {
  if (days === null || days === undefined) return '--'
  if (Number(days) === 0) return '今天'
  if (Number(days) > 0) return `${days} 天`
  return `已过 ${Math.abs(Number(days))} 天`
}

const loadCropOptions = async () => {
  if (!isFarmer.value) return
  const res = await getCropPage({ pageNum: 1, pageSize: 100 })
  cropOptions.value = res.data.records || []
}

const loadReminders = async () => {
  reminderLoading.value = true
  try {
    const res = await getCalendarReminders({ days: reminderDays.value })
    reminders.value = res.data || []
  } finally {
    reminderLoading.value = false
  }
}

const loadCalendar = async () => {
  if (!calendarForm.cropId && !calendarForm.herbName) {
    ElMessage.warning('请先选择药材或已有作物')
    return
  }
  calendarLoading.value = true
  try {
    const res = await getPlantingCalendar({ ...calendarForm })
    calendar.value = res.data || {}
  } finally {
    calendarLoading.value = false
  }
}

const resetCalendarForm = () => {
  calendarForm.cropId = null
  calendarForm.herbName = ''
  calendarForm.plantDate = ''
  calendar.value = {}
}

onMounted(async () => {
  await Promise.all([loadCropOptions(), loadReminders()])
})
</script>

<style scoped>
.calendar-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hero {
  padding: 24px 28px;
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 18px;
  background:
    radial-gradient(circle at right top, rgba(201, 182, 108, 0.18), transparent 36%),
    linear-gradient(135deg, #faf8ef 0%, #f5f8ef 100%);
}

.hero-kicker {
  color: #8a6e1f;
  font-size: 12px;
  letter-spacing: 1px;
}

.hero h2 {
  margin: 10px 0 8px;
  color: #29412d;
  font-size: 26px;
}

.hero p {
  margin: 0;
  color: #627062;
  line-height: 1.8;
}

.control-grid {
  display: grid;
  grid-template-columns: 0.92fr 1.08fr;
  gap: 16px;
}

.control-card,
.reminder-card,
.timeline-card {
  padding: 20px;
}

.section-title {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 16px;
}

.section-title h3 {
  margin: 0 0 4px;
  color: #1f3d2a;
}

.section-title span {
  color: #7a8977;
  font-size: 13px;
}

.control-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.reminder-toolbar {
  margin-bottom: 18px;
}

.reminder-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 280px;
}

.reminder-item {
  display: grid;
  grid-template-columns: 64px 1fr;
  gap: 12px;
  padding: 14px;
  border-radius: 14px;
  background: #f8faf5;
}

.date-chip {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: linear-gradient(135deg, #4b8b42 0%, #97bb55 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}

.reminder-body {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.reminder-body strong {
  color: #29432f;
}

.reminder-body p,
.reminder-body small {
  margin: 0;
  line-height: 1.7;
  color: #647264;
}

.timeline-grid {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 20px;
}

.stage-card {
  padding: 14px 16px;
  border-radius: 14px;
  background: #f8faf5;
}

.stage-head {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
  margin-bottom: 8px;
}

.stage-card p {
  margin: 0;
  line-height: 1.8;
  color: #5b6a5b;
}

.stage-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-top: 10px;
  color: #7b8b78;
  font-size: 12px;
}

.calendar-summary {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  align-content: start;
}

.summary-block {
  padding: 16px;
  border-radius: 16px;
  background: linear-gradient(180deg, #f7fbf6 0%, #eef5ed 100%);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.summary-block span {
  color: #748774;
  font-size: 13px;
}

.summary-block strong {
  color: #26442d;
  font-size: 20px;
}

@media (max-width: 1100px) {
  .hero,
  .control-grid,
  .timeline-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .calendar-summary {
    grid-template-columns: 1fr;
  }

  .reminder-item {
    grid-template-columns: 1fr;
  }
}
</style>
