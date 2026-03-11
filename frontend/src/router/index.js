import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'notice',
        name: 'NoticeCenter',
        component: () => import('@/views/notice/NoticeCenter.vue'),
        meta: { title: '通知中心', allowTypes: [1, 2, 3, 4] }
      },
      {
        path: 'knowledge/encyclopedia',
        name: 'KnowledgeEncyclopedia',
        component: () => import('@/views/knowledge/Encyclopedia.vue'),
        meta: { title: '中药材百科', allowTypes: [1, 2, 3, 4], permissions: ['knowledge:encyclopedia', 'knowledge'] }
      },
      {
        path: 'knowledge/disease',
        name: 'DiseaseDiagnosis',
        component: () => import('@/views/knowledge/DiseaseDiagnosis.vue'),
        meta: { title: '病虫害识别', allowTypes: [1, 2, 3, 4], permissions: ['knowledge:disease', 'knowledge'] }
      },
      {
        path: 'knowledge/calendar',
        name: 'PlantingCalendar',
        component: () => import('@/views/knowledge/PlantingCalendar.vue'),
        meta: { title: '种植日历', allowTypes: [1, 2, 3, 4], permissions: ['knowledge:calendar', 'knowledge'] }
      },
      {
        path: 'planting/field',
        name: 'Field',
        component: () => import('@/views/planting/Field.vue'),
        meta: { title: '地块管理', allowTypes: [1], permissions: ['planting:field', 'planting'] }
      },
      {
        path: 'planting/crop',
        name: 'Crop',
        component: () => import('@/views/planting/Crop.vue'),
        meta: { title: '作物管理', allowTypes: [1], permissions: ['planting:crop', 'planting'] }
      },
      {
        path: 'planting/record',
        name: 'FarmRecord',
        component: () => import('@/views/planting/FarmRecord.vue'),
        meta: { title: '农事记录', allowTypes: [1], permissions: ['planting:record', 'planting'] }
      },
      {
        path: 'sales/supply',
        name: 'Supply',
        component: () => import('@/views/sales/Supply.vue'),
        meta: { title: '供应大厅', allowTypes: [1, 2, 4], permissions: ['trading:supply_market', 'trading'] }
      },
      {
        path: 'sales/demand',
        name: 'Demand',
        component: () => import('@/views/sales/Demand.vue'),
        meta: { title: '采购需求', allowTypes: [2], permissions: ['trading:demand_market', 'trading'] }
      },
      {
        path: 'sales/favorite',
        name: 'Favorite',
        component: () => import('@/views/sales/Favorite.vue'),
        meta: { title: '我的收藏', allowTypes: [1, 2, 4], permissions: ['trading:favorite', 'trading'] }
      },
      {
        path: 'sales/order',
        name: 'Order',
        component: () => import('@/views/sales/Order.vue'),
        meta: { title: '订单追踪', allowTypes: [1, 2, 4], permissions: ['trading:order', 'trading'] }
      },
      {
        path: 'trace/manage',
        name: 'TraceManage',
        component: () => import('@/views/trace/TraceManage.vue'),
        meta: { title: '溯源管理', allowTypes: [1], permissions: ['trace:manage', 'trace'] }
      },
      {
        path: 'trace/query',
        name: 'TraceQuery',
        component: () => import('@/views/trace/TraceQuery.vue'),
        meta: { title: '溯源查询', public: true }
      },
      {
        path: 'analysis/sales',
        name: 'SalesAnalysis',
        component: () => import('@/views/analysis/SalesAnalysis.vue'),
        meta: { title: '销售分析', allowTypes: [1, 3], permissions: ['analysis:sales', 'analysis'] }
      },
      {
        path: 'analysis/yield',
        name: 'YieldAnalysis',
        component: () => import('@/views/analysis/YieldAnalysis.vue'),
        meta: { title: '产量分析', allowTypes: [1, 3], permissions: ['analysis:yield', 'analysis'] }
      },
      {
        path: 'system/user',
        name: 'UserManage',
        component: () => import('@/views/system/UserManage.vue'),
        meta: { title: '用户管理', allowTypes: [3], permissions: ['system:user', 'system'] }
      },
      {
        path: 'system/role',
        name: 'RoleManage',
        component: () => import('@/views/system/RoleManage.vue'),
        meta: { title: '角色管理', allowTypes: [3], permissions: ['system:role', 'system'] }
      },
      {
        path: 'system/permission',
        name: 'PermissionManage',
        component: () => import('@/views/system/PermissionManage.vue'),
        meta: { title: '权限管理', allowTypes: [3], permissions: ['system:permission', 'system'] }
      },
      {
        path: 'system/notice',
        name: 'NoticeManage',
        component: () => import('@/views/system/NoticeManage.vue'),
        meta: { title: '通知管理', allowTypes: [3], permissions: ['system:notice', 'system'] }
      },
      {
        path: 'system/dict',
        name: 'DictManage',
        component: () => import('@/views/system/DictManage.vue'),
        meta: { title: '数据字典', allowTypes: [3], permissions: ['system', 'system:config'] }
      },
      {
        path: 'system/config',
        name: 'ConfigManage',
        component: () => import('@/views/system/ConfigManage.vue'),
        meta: { title: '系统配置', allowTypes: [3], permissions: ['system:config', 'system'] }
      },
      {
        path: 'system/log',
        name: 'LogManage',
        component: () => import('@/views/system/LogManage.vue'),
        meta: { title: '系统日志', allowTypes: [3], permissions: ['system:log', 'system'] }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title || '中药材种植与销售平台'

  const userStore = useUserStore()

  if (to.meta.public) {
    next()
    return
  }

  if (!userStore.isLoggedIn) {
    next('/login')
    return
  }

  const allowTypes = to.meta.allowTypes
  if (Array.isArray(allowTypes) && allowTypes.length > 0 && !allowTypes.includes(userStore.userType)) {
    next('/dashboard')
    return
  }

  const permissions = to.meta.permissions
  if (permissions && !userStore.hasPermission(permissions)) {
    next('/dashboard')
    return
  }

  next()
})

export default router
