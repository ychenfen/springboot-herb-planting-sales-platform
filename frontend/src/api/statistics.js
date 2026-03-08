import request from '@/utils/request'

export function getDashboard() {
  return request.get('/statistics/dashboard')
}

export function getOrderTrend(days = 7) {
  return request.get('/statistics/order-trend', { params: { days } })
}

export function getSupplyDemandTrend(days = 7) {
  return request.get('/statistics/supply-demand-trend', { params: { days } })
}

export function getHerbSalesRanking(limit = 10) {
  return request.get('/statistics/herb-sales-ranking', { params: { limit } })
}

export function getFarmerYieldRanking(limit = 10) {
  return request.get('/statistics/farmer-yield-ranking', { params: { limit } })
}

export function getCropDistribution() {
  return request.get('/statistics/crop-distribution')
}

export function getUserTypeDistribution() {
  return request.get('/statistics/user-type-distribution')
}

export function getRegionDistribution() {
  return request.get('/statistics/region-distribution')
}
