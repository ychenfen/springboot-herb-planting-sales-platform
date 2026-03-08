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
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'notice',
        name: 'NoticeCenter',
        component: () => import('@/views/notice/NoticeCenter.vue'),
        meta: { title: '通知中心', icon: 'Bell' }
      },
      // 种植管理
      {
        path: 'planting/field',
        name: 'Field',
        component: () => import('@/views/planting/Field.vue'),
        meta: { title: '地块管理', icon: 'Location' }
      },
      {
        path: 'planting/crop',
        name: 'Crop',
        component: () => import('@/views/planting/Crop.vue'),
        meta: { title: '作物管理', icon: 'Cherry' }
      },
      {
        path: 'planting/record',
        name: 'FarmRecord',
        component: () => import('@/views/planting/FarmRecord.vue'),
        meta: { title: '农事记录', icon: 'Notebook' }
      },
      // 销售对接
      {
        path: 'sales/supply',
        name: 'Supply',
        component: () => import('@/views/sales/Supply.vue'),
        meta: { title: '供应信息', icon: 'Goods' }
      },
      {
        path: 'sales/demand',
        name: 'Demand',
        component: () => import('@/views/sales/Demand.vue'),
        meta: { title: '需求信息', icon: 'ShoppingCart' }
      },
      {
        path: 'sales/favorite',
        name: 'Favorite',
        component: () => import('@/views/sales/Favorite.vue'),
        meta: { title: '我的收藏', icon: 'Star' }
      },
      {
        path: 'sales/order',
        name: 'Order',
        component: () => import('@/views/sales/Order.vue'),
        meta: { title: '订单管理', icon: 'List' }
      },
      // 质量溯源
      {
        path: 'trace/manage',
        name: 'TraceManage',
        component: () => import('@/views/trace/TraceManage.vue'),
        meta: { title: '溯源管理', icon: 'Connection' }
      },
      {
        path: 'trace/query',
        name: 'TraceQuery',
        component: () => import('@/views/trace/TraceQuery.vue'),
        meta: { title: '溯源查询', icon: 'Search', public: true }
      },
      // 数据分析
      {
        path: 'analysis/sales',
        name: 'SalesAnalysis',
        component: () => import('@/views/analysis/SalesAnalysis.vue'),
        meta: { title: '销售分析', icon: 'TrendCharts' }
      },
      {
        path: 'analysis/yield',
        name: 'YieldAnalysis',
        component: () => import('@/views/analysis/YieldAnalysis.vue'),
        meta: { title: '产量分析', icon: 'DataAnalysis' }
      },
      // 系统管理
      {
        path: 'system/user',
        name: 'UserManage',
        component: () => import('@/views/system/UserManage.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'system/role',
        name: 'RoleManage',
        component: () => import('@/views/system/RoleManage.vue'),
        meta: { title: '角色管理', icon: 'UserFilled' }
      },
      {
        path: 'system/permission',
        name: 'PermissionManage',
        component: () => import('@/views/system/PermissionManage.vue'),
        meta: { title: '权限管理', icon: 'Key' }
      },
      {
        path: 'system/notice',
        name: 'NoticeManage',
        component: () => import('@/views/system/NoticeManage.vue'),
        meta: { title: '通知管理', icon: 'Bell' }
      },
      {
        path: 'system/dict',
        name: 'DictManage',
        component: () => import('@/views/system/DictManage.vue'),
        meta: { title: '数据字典', icon: 'Collection' }
      },
      {
        path: 'system/config',
        name: 'ConfigManage',
        component: () => import('@/views/system/ConfigManage.vue'),
        meta: { title: '系统配置', icon: 'Setting' }
      },
      {
        path: 'system/log',
        name: 'LogManage',
        component: () => import('@/views/system/LogManage.vue'),
        meta: { title: '系统日志', icon: 'Document' }
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

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || '中药材种植与销售服务平台'}`

  const userStore = useUserStore()

  if (to.meta.public) {
    next()
  } else if (!userStore.isLoggedIn) {
    next('/login')
  } else if (userStore.userType === 2 && (
    to.path.startsWith('/planting') ||
    to.path.startsWith('/analysis') ||
    to.path.startsWith('/trace/manage')
  )) {
    next('/dashboard')
  } else if (userStore.userType === 3 && (
    to.path.startsWith('/planting') ||
    to.path.startsWith('/sales') ||
    to.path.startsWith('/trace/manage')
  )) {
    next('/dashboard')
  } else if (to.path.startsWith('/system') && userStore.userType !== 3) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
