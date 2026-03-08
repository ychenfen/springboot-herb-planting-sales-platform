import request from '@/utils/request'

// 用户管理
export function getUserPage(params) {
  return request.get('/system/user/page', { params })
}

export function getUserById(id) {
  return request.get(`/system/user/${id}`)
}

export function createUser(data) {
  return request.post('/system/user', data)
}

export function updateUser(data) {
  return request.put('/system/user', data)
}

export function deleteUser(id) {
  return request.delete(`/system/user/${id}`)
}

export function updateUserStatus(id, status) {
  return request.put(`/system/user/${id}/status`, null, { params: { status } })
}

export function resetPassword(id, newPassword) {
  return request.put(`/system/user/${id}/password`, null, { params: { newPassword } })
}

// 角色管理
export function getRolePage(params) {
  return request.get('/system/role/page', { params })
}

export function getRoleList() {
  return request.get('/system/role/list')
}

export function getRoleById(id) {
  return request.get(`/system/role/${id}`)
}

export function createRole(data) {
  return request.post('/system/role', data)
}

export function updateRole(data) {
  return request.put('/system/role', data)
}

export function deleteRole(id) {
  return request.delete(`/system/role/${id}`)
}

// 权限管理
export function getPermissionTree() {
  return request.get('/system/permission/tree')
}

export function getPermissionList() {
  return request.get('/system/permission/list')
}

export function getUserMenus() {
  return request.get('/system/permission/menus')
}

export function getUserPermissions() {
  return request.get('/system/permission/user')
}

export function createPermission(data) {
  return request.post('/system/permission', data)
}

export function updatePermission(data) {
  return request.put('/system/permission', data)
}

export function deletePermission(id) {
  return request.delete(`/system/permission/${id}`)
}

// 系统通知
export function getNoticePage(params) {
  return request.get('/system/notice/page', { params })
}

export function getNoticeById(id) {
  return request.get(`/system/notice/${id}`)
}

export function createNotice(data) {
  return request.post('/system/notice', data)
}

export function updateNotice(data) {
  return request.put('/system/notice', data)
}

export function deleteNotice(id) {
  return request.delete(`/system/notice/${id}`)
}

// 数据字典
export function getDictPage(params) {
  return request.get('/system/dict/page', { params })
}

export function getDictByType(dictType) {
  return request.get(`/system/dict/type/${dictType}`)
}

export function getDictTypes() {
  return request.get('/system/dict/types')
}

export function createDict(data) {
  return request.post('/system/dict', data)
}

export function updateDict(data) {
  return request.put('/system/dict', data)
}

export function deleteDict(id) {
  return request.delete(`/system/dict/${id}`)
}

// 系统配置
export function getConfigPage(params) {
  return request.get('/system/config/page', { params })
}

export function getConfigTypes() {
  return request.get('/system/config/types')
}

export function getConfigById(id) {
  return request.get(`/system/config/${id}`)
}

export function createConfig(data) {
  return request.post('/system/config', data)
}

export function updateConfig(data) {
  return request.put('/system/config', data)
}

export function deleteConfig(id) {
  return request.delete(`/system/config/${id}`)
}

// 系统日志
export function getLogPage(params) {
  return request.get('/system/log/page', { params })
}

export function getLogById(id) {
  return request.get(`/system/log/${id}`)
}

export function deleteLog(id) {
  return request.delete(`/system/log/${id}`)
}

export function clearLog() {
  return request.delete('/system/log/clear')
}
