import request from '@/utils/request'

// 地块管理
export function getFieldPage(params) {
  return request.get('/field/page', { params })
}

export function getFieldList() {
  return request.get('/field/list')
}

export function getFieldById(id) {
  return request.get(`/field/${id}`)
}

export function createField(data) {
  return request.post('/field', data)
}

export function updateField(data) {
  return request.put('/field', data)
}

export function deleteField(id) {
  return request.delete(`/field/${id}`)
}

// 作物管理
export function getCropPage(params) {
  return request.get('/crop/page', { params })
}

export function getCropById(id) {
  return request.get(`/crop/${id}`)
}

export function createCrop(data) {
  return request.post('/crop', data)
}

export function updateCrop(data) {
  return request.put('/crop', data)
}

export function deleteCrop(id) {
  return request.delete(`/crop/${id}`)
}

export function harvestCrop(id, data) {
  return request.post(`/crop/${id}/harvest`, data)
}

// 农事记录
export function getFarmRecordPage(params) {
  return request.get('/farm-record/page', { params })
}

export function getFarmRecordById(id) {
  return request.get(`/farm-record/${id}`)
}

export function createFarmRecord(data) {
  return request.post('/farm-record', data)
}

export function updateFarmRecord(data) {
  return request.put('/farm-record', data)
}

export function deleteFarmRecord(id) {
  return request.delete(`/farm-record/${id}`)
}
