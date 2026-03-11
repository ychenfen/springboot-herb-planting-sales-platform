import request from '@/utils/request'

export function getSupplyPage(params) {
  return request.get('/supply/market', { params })
}

export function getMySupplyPage(params) {
  return request.get('/supply/my', { params })
}

export function getSupplyById(id) {
  return request.get(`/supply/${id}`)
}

export function getSupplyPricing(id, quantity) {
  return request.get(`/supply/${id}/pricing`, { params: { quantity } })
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

export function getDemandPage(params) {
  return request.get('/demand/market', { params })
}

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
  return request.put(`/order/${id}/deliver`, null, {
    params: { logisticsCompany, logisticsNo }
  })
}

export function completeOrder(id) {
  return request.put(`/order/${id}/complete`)
}

export function cancelOrder(id, cancelReason) {
  return request.put(`/order/${id}/cancel`, null, {
    params: { cancelReason }
  })
}
