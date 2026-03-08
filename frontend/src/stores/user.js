import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getUserInfo as getUserInfoApi } from '@/api/auth'
import { getUserPermissions } from '@/api/system'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const permissionCodes = ref(JSON.parse(localStorage.getItem('permissionCodes') || '[]'))

  const isLoggedIn = computed(() => !!token.value)
  const username = computed(() => userInfo.value.username || '')
  const realName = computed(() => userInfo.value.realName || userInfo.value.username || '')
  const userType = computed(() => userInfo.value.userType || 0)
  const userTypeName = computed(() => userInfo.value.userTypeName || '')
  const avatar = computed(() => userInfo.value.avatar || '')

  const hasPermission = (codes) => {
    if (userType.value === 3) return true
    if (!codes || codes.length === 0) return true
    const checkList = Array.isArray(codes) ? codes : [codes]
    return checkList.some(code => permissionCodes.value.includes(code))
  }

  async function login(credentials) {
    const res = await loginApi(credentials)
    token.value = res.data.token
    userInfo.value = res.data
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userInfo', JSON.stringify(res.data))
    try {
      await fetchPermissions()
    } catch {
      // 权限获取失败不影响登录
    }
    return res
  }

  async function fetchUserInfo() {
    const res = await getUserInfoApi()
    userInfo.value = { ...userInfo.value, ...res.data }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    try {
      await fetchPermissions()
    } catch {
      // 权限获取失败不影响用户信息更新
    }
    return res
  }

  async function fetchPermissions() {
    const res = await getUserPermissions()
    const codes = (res.data || [])
      .map(item => item.permissionCode)
      .filter(code => !!code)
    permissionCodes.value = codes
    localStorage.setItem('permissionCodes', JSON.stringify(codes))
    return res
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    permissionCodes.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('permissionCodes')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    username,
    realName,
    userType,
    userTypeName,
    avatar,
    permissionCodes,
    hasPermission,
    login,
    fetchUserInfo,
    fetchPermissions,
    logout
  }
})
