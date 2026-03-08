import request from '@/utils/request'

// 供应信息 - 供应大厅
export function getSupplyPage(params) {
  return request.get('/supply/market', { params })
}

// 我的供应
export function getMySupplyPage(params) {
  return request.get('/supply/my', { params })
}

export function getSupplyById(id) {
  return request.get(`/supply/${id}`)
}

export function createSupply(data) {
  return request.post('/supply', data)
}

export function updateSupply(data) {
  return request.put('/supply', data)
}

export function offlineSupply(id) {
  return request.put(`/supply/${id}/offline`)
}

export function deleteSupply(id) {
  return request.delete(`/supply/${id}`)
}

// 需求信息 - 需求大厅
export function getDemandPage(params) {
  return request.get('/demand/market', { params })
}

// 我的需求
export function getMyDemandPage(params) {
  return request.get('/demand/my', { params })
}

export function getDemandById(id) {
  return request.get(`/demand/${id}`)
}

export function createDemand(data) {
  return request.post('/demand', data)
}

export function updateDemand(data) {
  return request.put('/demand', data)
}

export function cancelDemand(id) {
  return request.put(`/demand/${id}/cancel`)
}

export function deleteDemand(id) {
  return request.delete(`/demand/${id}`)
}

// 订单管理
export function getOrderPage(params) {
  return request.get('/order/page', { params })
}

export function getOrderById(id) {
  return request.get(`/order/${id}`)
}

export function createOrder(data) {
  return request.post('/order', data)
}

export function confirmOrder(id) {
  return request.put(`/order/${id}/confirm`)
}

export function deliverOrder(id, logisticsCompany, logisticsNo) {
  return request.put(`/order/${id}/deliver`, null, { params: { logisticsCompany, logisticsNo } })
}

export function completeOrder(id) {
  return request.put(`/order/${id}/complete`)
}

export function cancelOrder(id, cancelReason) {
  return request.put(`/order/${id}/cancel`, null, { params: { cancelReason } })
}
