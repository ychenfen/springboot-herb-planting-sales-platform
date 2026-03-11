<template>
  <div class="supply-page">
    <section class="hero card-shadow">
      <div>
        <span class="hero-kicker">规范 / 溯源 / 安全</span>
        <h2>中药材供应大厅</h2>
        <p>支持按克、按斤、按批发阈值智能计价，配合产地与质检信息展示，体现电商与规范化流通逻辑。</p>
      </div>
      <div class="hero-actions">
        <el-button v-if="isFarmer" type="primary" @click="handleAdd" v-permission="'trading:supply:add'">
          发布供应
        </el-button>
        <el-button v-if="canPlaceOrder" plain @click="cartVisible = true">
          购物车（{{ cartItems.length }}）
        </el-button>
      </div>
    </section>

    <section class="toolbar card-shadow">
      <el-form :inline="true" :model="queryForm" class="toolbar-form">
        <el-form-item label="药材名称">
          <el-input v-model="queryForm.herbName" clearable placeholder="输入药材名检索" />
        </el-form-item>
        <el-form-item label="质量等级">
          <el-select v-model="queryForm.qualityGrade" clearable placeholder="全部等级" style="width: 140px">
            <el-option v-for="item in gradeOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" clearable placeholder="全部状态" style="width: 140px">
            <el-option label="供应中" :value="1" />
            <el-option label="已售罄" :value="2" />
            <el-option label="已下架" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">检索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="table-card card-shadow">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="herbName" label="药材名称" min-width="120" />
        <el-table-column prop="herbVariety" label="品种" min-width="120" />
        <el-table-column prop="qualityGrade" label="等级" width="90" align="center" />
        <el-table-column label="库存" width="120" align="center">
          <template #default="{ row }">
            <strong>{{ formatQuantity(row.remainingQuantity) }}</strong>
            <div class="sub-text">总量 {{ formatQuantity(row.supplyQuantity) }}</div>
          </template>
        </el-table-column>
        <el-table-column label="智能计价" min-width="220">
          <template #default="{ row }">
            <div class="price-stack">
              <strong>标准价：¥{{ formatMoney(row.price) }}/kg</strong>
              <span>按克：¥{{ formatMoney(row.pricePerGram, 4) }}/g</span>
              <span>按斤：¥{{ formatMoney(row.pricePerJin) }}/斤</span>
              <span v-if="row.wholesalePrice && row.wholesaleMinQuantity">
                批发价：¥{{ formatMoney(row.wholesalePrice) }}/kg（{{ formatQuantity(row.wholesaleMinQuantity) }} 起）
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="溯源信息" min-width="220">
          <template #default="{ row }">
            <div class="trace-stack">
              <span>产地：{{ row.productionArea || '--' }}</span>
              <span>种植：{{ formatDate(row.plantDate) }}</span>
              <span>采收：{{ formatDate(row.harvestDate) }}</span>
              <el-tag size="small" :type="row.traceId ? 'success' : 'info'">
                {{ row.qualityCheckStatus || '待补充' }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">{{ row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="互动" width="110" align="center">
          <template #default="{ row }">
            <div class="interactive-cell">
              <span>收藏 {{ row.favoriteCount || 0 }}</span>
              <span>浏览 {{ row.viewCount || 0 }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="!isOwner(row) && row.isFavorite !== 1"
              type="warning"
              link
              @click="handleFavorite(row)"
            >
              收藏
            </el-button>
            <el-button
              v-if="!isOwner(row) && row.isFavorite === 1"
              type="info"
              link
              @click="handleUnfavorite(row)"
            >
              取消收藏
            </el-button>
            <el-button type="primary" link @click="openTrace(row)">溯源</el-button>
            <el-button type="success" link @click="openPricing(row)">计价</el-button>
            <el-button
              v-if="canPlaceOrder && !isOwner(row) && row.status === 1"
              type="primary"
              link
              @click="addToCart(row)"
            >
              加入购物车
            </el-button>
            <el-button
              v-if="canPlaceOrder && !isOwner(row) && row.status === 1"
              type="danger"
              link
              @click="openOrderDialog(row)"
            >
              立即下单
            </el-button>
            <el-button
              v-if="isOwner(row)"
              type="primary"
              link
              @click="handleEdit(row)"
              v-permission="'trading:supply:edit'"
            >
              编辑
            </el-button>
            <el-button
              v-if="isOwner(row) && row.status === 1"
              type="warning"
              link
              @click="handleOffline(row)"
              v-permission="'trading:supply:offline'"
            >
              下架
            </el-button>
            <el-button
              v-if="isOwner(row)"
              type="danger"
              link
              @click="handleDelete(row)"
              v-permission="'trading:supply:offline'"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </section>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑供应信息' : '发布供应信息'" width="760px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="关联作物" prop="cropId">
              <el-select v-model="form.cropId" clearable placeholder="可选，用于自动关联溯源" style="width: 100%">
                <el-option
                  v-for="item in cropOptions"
                  :key="item.id"
                  :label="`${item.cropName}｜${item.cropVariety || '标准品种'}`"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="药材名称" prop="herbName">
              <el-input v-model="form.herbName" placeholder="请输入药材名称" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="药材品种" prop="herbVariety">
              <el-input v-model="form.herbVariety" placeholder="请输入药材品种" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="质量等级" prop="qualityGrade">
              <el-select v-model="form.qualityGrade" placeholder="请选择等级" style="width: 100%">
                <el-option v-for="item in gradeOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="供应数量(kg)" prop="supplyQuantity">
              <el-input-number v-model="form.supplyQuantity" :min="0.01" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标准价(元/kg)" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="批发价(元/kg)" prop="wholesalePrice">
              <el-input-number v-model="form.wholesalePrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="起批量(kg)" prop="wholesaleMinQuantity">
              <el-input-number v-model="form.wholesaleMinQuantity" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="产地" prop="productionArea">
              <el-input v-model="form.productionArea" placeholder="请输入产地" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采收日期" prop="harvestDate">
              <el-date-picker
                v-model="form.harvestDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择采收日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="18">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="form.contactName" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="储存条件" prop="storageCondition">
          <el-input v-model="form.storageCondition" placeholder="例如：阴凉干燥、避光通风" />
        </el-form-item>

        <el-form-item label="产品描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入供应说明、加工方式、等级描述等" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="pricingVisible" title="智能计价预览" width="520px">
      <div class="pricing-panel">
        <el-form label-width="100px">
          <el-form-item label="药材名称">
            <span>{{ selectedSupply.herbName || '--' }}</span>
          </el-form-item>
          <el-form-item label="预估数量(kg)">
            <el-input-number v-model="pricingQuantity" :min="0.1" :precision="2" style="width: 100%" />
          </el-form-item>
        </el-form>
        <div class="pricing-actions">
          <el-button type="primary" :loading="pricingLoading" @click="calculatePricing">重新计算</el-button>
        </div>
        <div v-if="pricingResult" class="pricing-result">
          <div class="pricing-item">
            <span>标准价</span>
            <strong>¥{{ formatMoney(pricingResult.basePrice) }}/kg</strong>
          </div>
          <div class="pricing-item">
            <span>实际单价</span>
            <strong>¥{{ formatMoney(pricingResult.appliedUnitPrice) }}/kg</strong>
          </div>
          <div class="pricing-item">
            <span>价格模式</span>
            <strong>{{ pricingResult.wholesaleApplied ? '已触发批发价' : '标准零售价' }}</strong>
          </div>
          <div class="pricing-item">
            <span>应付总额</span>
            <strong class="money">¥{{ formatMoney(pricingResult.totalAmount) }}</strong>
          </div>
        </div>
      </div>
    </el-dialog>

    <el-drawer v-model="traceVisible" title="溯源详情" size="420px">
      <el-descriptions :column="1" border v-if="selectedSupply.id">
        <el-descriptions-item label="药材名称">{{ selectedSupply.herbName || '--' }}</el-descriptions-item>
        <el-descriptions-item label="产地">{{ selectedSupply.productionArea || '--' }}</el-descriptions-item>
        <el-descriptions-item label="种植时间">{{ formatDate(selectedSupply.plantDate) }}</el-descriptions-item>
        <el-descriptions-item label="采收时间">{{ formatDate(selectedSupply.harvestDate) }}</el-descriptions-item>
        <el-descriptions-item label="溯源码">{{ selectedSupply.traceCode || '未绑定溯源批次' }}</el-descriptions-item>
        <el-descriptions-item label="批次号">{{ selectedSupply.batchNo || '--' }}</el-descriptions-item>
        <el-descriptions-item label="质检状态">
          <el-tag :type="selectedSupply.traceId ? 'success' : 'info'">
            {{ selectedSupply.qualityCheckStatus || '待补充' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="质检标准">{{ selectedSupply.qualityStandard || '--' }}</el-descriptions-item>
        <el-descriptions-item label="质检报告">{{ selectedSupply.qualityReport || '--' }}</el-descriptions-item>
        <el-descriptions-item label="说明">{{ selectedSupply.description || '--' }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>

    <el-drawer v-model="cartVisible" title="购物车实时结算" size="520px">
      <div class="cart-panel">
        <el-empty v-if="!cartItems.length" description="购物车为空，先从供应大厅选择药材" />

        <div v-else class="cart-list">
          <article v-for="item in cartItems" :key="item.supplyId" class="cart-item">
            <div class="cart-head">
              <div>
                <strong>{{ item.herbName }}</strong>
                <p>{{ item.herbVariety || '标准品种' }} ｜ {{ item.qualityGrade || '等级待补充' }}</p>
              </div>
              <el-button type="danger" link @click="removeCartItem(item.supplyId)">移除</el-button>
            </div>

            <div class="cart-price">
              <span>标准价 ¥{{ formatMoney(item.price) }}/kg</span>
              <span v-if="item.wholesalePrice && item.wholesaleMinQuantity">
                批发价 ¥{{ formatMoney(item.wholesalePrice) }}/kg（{{ formatQuantity(item.wholesaleMinQuantity) }} 起）
              </span>
            </div>

            <div class="cart-actions">
              <el-input-number
                v-model="item.quantity"
                :min="0.1"
                :precision="2"
                @change="updateCartItem(item)"
              />
              <div class="cart-total">
                <span>{{ item.pricing?.wholesaleApplied ? '批发价' : '标准价' }}</span>
                <strong>¥{{ formatMoney(item.pricing?.totalAmount) }}</strong>
              </div>
              <el-button type="primary" @click="openOrderDialog(item, true)">结算</el-button>
            </div>
          </article>

          <div class="cart-summary">
            <span>购物车合计</span>
            <strong>¥{{ formatMoney(cartTotalAmount) }}</strong>
          </div>
        </div>
      </div>
    </el-drawer>

    <el-dialog v-model="orderVisible" title="提交订单" width="560px">
      <el-form ref="orderFormRef" :model="orderForm" :rules="orderRules" label-width="92px">
        <el-form-item label="药材名称">
          <span>{{ selectedSupply.herbName || '--' }}</span>
        </el-form-item>
        <el-form-item label="采购数量" prop="quantity">
          <el-input-number v-model="orderForm.quantity" :min="0.1" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="收货人" prop="deliveryName">
          <el-input v-model="orderForm.deliveryName" placeholder="请输入收货人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="deliveryPhone">
          <el-input v-model="orderForm.deliveryPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="收货地址" prop="deliveryAddress">
          <el-input v-model="orderForm.deliveryAddress" type="textarea" :rows="2" placeholder="请输入收货地址" />
        </el-form-item>
        <el-form-item label="订单备注" prop="remark">
          <el-input v-model="orderForm.remark" type="textarea" :rows="2" placeholder="选填，例如发票、包装要求" />
        </el-form-item>
      </el-form>

      <div v-if="orderPricing" class="order-preview">
        <span>预计计价方式：{{ orderPricing.wholesaleApplied ? '批发价' : '标准价' }}</span>
        <strong>预计金额 ¥{{ formatMoney(orderPricing.totalAmount) }}</strong>
      </div>

      <template #footer>
        <el-button @click="orderVisible = false">取消</el-button>
        <el-button type="primary" :loading="orderLoading" @click="submitOrder">确认下单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCropPage } from '@/api/planting'
import { addFavorite, removeFavorite } from '@/api/favorite'
import {
  createOrder,
  createSupply,
  deleteSupply,
  getSupplyPage,
  getSupplyPricing,
  offlineSupply,
  updateSupply
} from '@/api/sales'
import { useUserStore } from '@/stores/user'

const CART_KEY = 'herb-platform-cart'

const userStore = useUserStore()
const isFarmer = computed(() => userStore.userType === 1)
const canPlaceOrder = computed(() => [2, 4].includes(userStore.userType))

const loading = ref(false)
const submitLoading = ref(false)
const pricingLoading = ref(false)
const orderLoading = ref(false)
const dialogVisible = ref(false)
const pricingVisible = ref(false)
const traceVisible = ref(false)
const cartVisible = ref(false)
const orderVisible = ref(false)

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])
const cropOptions = ref([])
const selectedSupply = ref({})
const pricingQuantity = ref(1)
const pricingResult = ref(null)
const orderPricing = ref(null)
const cartItems = ref(readCart())

const gradeOptions = ['特级', '一级', '二级', '三级']

const queryForm = reactive({
  herbName: '',
  qualityGrade: '',
  status: null
})

const formRef = ref()
const form = ref(createEmptySupplyForm())
const rules = {
  herbName: [{ required: true, message: '请输入药材名称', trigger: 'blur' }],
  supplyQuantity: [{ required: true, message: '请输入供应数量', trigger: 'blur' }],
  price: [{ required: true, message: '请输入标准价格', trigger: 'blur' }]
}

const orderFormRef = ref()
const orderForm = reactive({
  supplyId: null,
  quantity: 1,
  deliveryName: '',
  deliveryPhone: '',
  deliveryAddress: '',
  remark: ''
})
const orderRules = {
  quantity: [{ required: true, message: '请输入采购数量', trigger: 'blur' }],
  deliveryName: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
  deliveryPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  deliveryAddress: [{ required: true, message: '请输入收货地址', trigger: 'blur' }]
}

function createEmptySupplyForm() {
  return {
    id: null,
    cropId: null,
    herbName: '',
    herbVariety: '',
    qualityGrade: '一级',
    supplyQuantity: null,
    price: null,
    wholesalePrice: null,
    wholesaleMinQuantity: null,
    productionArea: '',
    harvestDate: '',
    storageCondition: '',
    description: '',
    contactName: userStore.realName || '',
    contactPhone: userStore.userInfo.phone || '',
    contactWechat: ''
  }
}

function readCart() {
  try {
    return JSON.parse(localStorage.getItem(CART_KEY) || '[]')
  } catch {
    return []
  }
}

function persistCart() {
  localStorage.setItem(CART_KEY, JSON.stringify(cartItems.value))
}

const cartTotalAmount = computed(() =>
  cartItems.value.reduce((sum, item) => sum + Number(item.pricing?.totalAmount || 0), 0)
)

const formatMoney = (value, scale = 2) => {
  if (value === null || value === undefined || value === '') return '--'
  return Number(value).toFixed(scale)
}

const formatQuantity = (value) => {
  if (value === null || value === undefined || value === '') return '--'
  return `${Number(value).toFixed(2)} kg`
}

const formatDate = (value) => {
  if (!value) return '--'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  const year = date.getFullYear()
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  return `${year}-${month}-${day}`
}

const getStatusTagType = (status) => {
  if (status === 1) return 'success'
  if (status === 2) return 'danger'
  return 'info'
}

const isOwner = (row) => row?.userId === userStore.userInfo.id

const loadCropOptions = async () => {
  if (!isFarmer.value) return
  const res = await getCropPage({ pageNum: 1, pageSize: 100 })
  cropOptions.value = res.data.records || []
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getSupplyPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...queryForm
    })
    tableData.value = res.data.records || []
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
  queryForm.herbName = ''
  queryForm.qualityGrade = ''
  queryForm.status = null
  handleSearch()
}

const handleAdd = () => {
  form.value = createEmptySupplyForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  form.value = {
    ...createEmptySupplyForm(),
    ...row,
    harvestDate: row.harvestDate || ''
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (form.value.id) {
      await updateSupply(form.value)
      ElMessage.success('供应信息已更新')
    } else {
      await createSupply(form.value)
      ElMessage.success('供应信息已发布')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

const handleFavorite = async (row) => {
  await addFavorite({ targetType: 1, targetId: row.id })
  row.isFavorite = 1
  row.favoriteCount = Number(row.favoriteCount || 0) + 1
  ElMessage.success('已加入收藏')
}

const handleUnfavorite = async (row) => {
  await removeFavorite({ targetType: 1, targetId: row.id })
  row.isFavorite = 0
  row.favoriteCount = Math.max(0, Number(row.favoriteCount || 0) - 1)
  ElMessage.success('已取消收藏')
}

const handleOffline = async (row) => {
  await ElMessageBox.confirm('确定下架当前供应信息吗？', '提示', { type: 'warning' })
  await offlineSupply(row.id)
  ElMessage.success('已下架')
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定删除当前供应信息吗？删除后不可恢复。', '提示', { type: 'warning' })
  await deleteSupply(row.id)
  ElMessage.success('已删除')
  loadData()
}

const openTrace = (row) => {
  selectedSupply.value = row
  traceVisible.value = true
}

const openPricing = async (row) => {
  selectedSupply.value = row
  pricingQuantity.value = Number(row.wholesaleMinQuantity || 1)
  pricingVisible.value = true
  await calculatePricing()
}

const calculatePricing = async () => {
  if (!selectedSupply.value.id || !pricingQuantity.value) return
  pricingLoading.value = true
  try {
    const res = await getSupplyPricing(selectedSupply.value.id, pricingQuantity.value)
    pricingResult.value = res.data
  } finally {
    pricingLoading.value = false
  }
}

const upsertCartPricing = async (item) => {
  const res = await getSupplyPricing(item.supplyId, item.quantity)
  item.pricing = res.data
}

const refreshCartPricing = async () => {
  await Promise.all(
    cartItems.value.map(async (item) => {
      try {
        await upsertCartPricing(item)
      } catch {
        item.pricing = null
      }
    })
  )
  persistCart()
}

const addToCart = async (row) => {
  const existing = cartItems.value.find((item) => item.supplyId === row.id)
  if (existing) {
    existing.quantity = Number(existing.quantity || 0) + 1
    await updateCartItem(existing)
    ElMessage.success('已更新购物车数量')
    return
  }

  const item = {
    supplyId: row.id,
    herbName: row.herbName,
    herbVariety: row.herbVariety,
    qualityGrade: row.qualityGrade,
    price: row.price,
    wholesalePrice: row.wholesalePrice,
    wholesaleMinQuantity: row.wholesaleMinQuantity,
    quantity: 1
  }
  cartItems.value.push(item)
  await updateCartItem(item)
  ElMessage.success('已加入购物车')
}

const updateCartItem = async (item) => {
  if (!item.quantity || Number(item.quantity) <= 0) {
    item.quantity = 0.1
  }
  await upsertCartPricing(item)
  persistCart()
}

const removeCartItem = (supplyId) => {
  cartItems.value = cartItems.value.filter((item) => item.supplyId !== supplyId)
  persistCart()
}

const openOrderDialog = async (source, fromCart = false) => {
  selectedSupply.value = fromCart
    ? {
        id: source.supplyId,
        herbName: source.herbName,
        herbVariety: source.herbVariety
      }
    : source

  orderForm.supplyId = fromCart ? source.supplyId : source.id
  orderForm.quantity = Number(fromCart ? source.quantity : 1)
  orderForm.deliveryName = ''
  orderForm.deliveryPhone = ''
  orderForm.deliveryAddress = ''
  orderForm.remark = ''
  orderPricing.value = null
  orderVisible.value = true
}

const submitOrder = async () => {
  const valid = await orderFormRef.value?.validate().catch(() => false)
  if (!valid) return

  orderLoading.value = true
  try {
    await createOrder({ ...orderForm })
    ElMessage.success('订单已创建')
    orderVisible.value = false
    cartItems.value = cartItems.value.filter((item) => item.supplyId !== orderForm.supplyId)
    persistCart()
    loadData()
  } finally {
    orderLoading.value = false
  }
}

watch(
  () => [orderVisible.value, orderForm.supplyId, orderForm.quantity],
  async ([visible, supplyId, quantity]) => {
    if (!visible || !supplyId || !quantity || Number(quantity) <= 0) {
      orderPricing.value = null
      return
    }
    try {
      const res = await getSupplyPricing(supplyId, quantity)
      orderPricing.value = res.data
    } catch {
      orderPricing.value = null
    }
  }
)

onMounted(async () => {
  await Promise.all([loadData(), loadCropOptions(), refreshCartPricing()])
})
</script>

<style scoped>
.supply-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hero {
  padding: 24px 28px;
  display: flex;
  justify-content: space-between;
  gap: 18px;
  align-items: center;
  background:
    radial-gradient(circle at right top, rgba(219, 190, 119, 0.2), transparent 28%),
    linear-gradient(135deg, #fafaf5 0%, #eef6ee 100%);
}

.hero-kicker {
  color: #6e7c34;
  font-size: 12px;
  letter-spacing: 1px;
}

.hero h2 {
  margin: 10px 0 8px;
  font-size: 26px;
  color: #233d2d;
}

.hero p {
  margin: 0;
  max-width: 760px;
  color: #627265;
  line-height: 1.8;
}

.hero-actions {
  display: flex;
  gap: 10px;
}

.toolbar,
.table-card {
  padding: 18px 20px;
}

.toolbar-form {
  display: flex;
  flex-wrap: wrap;
}

.table-card :deep(.el-pagination) {
  margin-top: 16px;
  justify-content: flex-end;
}

.sub-text {
  color: #859286;
  font-size: 12px;
  margin-top: 4px;
}

.price-stack,
.trace-stack,
.interactive-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.price-stack strong,
.trace-stack span,
.interactive-cell span {
  color: #34533b;
}

.pricing-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.pricing-actions {
  display: flex;
  justify-content: flex-end;
}

.pricing-result {
  display: grid;
  gap: 12px;
}

.pricing-item {
  display: flex;
  justify-content: space-between;
  padding: 14px 16px;
  border-radius: 14px;
  background: #f7faf6;
}

.pricing-item span {
  color: #6f806f;
}

.pricing-item strong {
  color: #29442f;
}

.money {
  color: #c25715 !important;
}

.cart-panel {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.cart-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.cart-item {
  padding: 16px;
  border-radius: 16px;
  background: #f7faf6;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cart-head,
.cart-actions,
.cart-summary {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: center;
}

.cart-head p,
.cart-price span {
  margin: 0;
  color: #6b7b6a;
}

.cart-price {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.cart-total {
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 120px;
}

.cart-total span {
  color: #7a8979;
  font-size: 12px;
}

.cart-total strong,
.cart-summary strong {
  color: #1f4a2f;
}

.cart-summary {
  padding: 16px;
  border-radius: 16px;
  background: linear-gradient(135deg, #edf5ea 0%, #f7faf6 100%);
}

.order-preview {
  margin-top: 8px;
  padding: 14px 16px;
  border-radius: 12px;
  background: #f7faf6;
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.order-preview span {
  color: #6a7c6e;
}

.order-preview strong {
  color: #b85a1d;
}

@media (max-width: 768px) {
  .hero,
  .cart-head,
  .cart-actions,
  .cart-summary,
  .order-preview {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
