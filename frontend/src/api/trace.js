import request from '@/utils/request'

export function getTracePage(params) {
  return request.get('/trace/page', { params })
}

export function getTraceById(id) {
  return request.get(`/trace/${id}`)
}

export function getTraceByCode(traceCode) {
  return request.get(`/trace/public/${traceCode}`)
}

export function createTrace(data) {
  return request.post('/trace', data)
}

export function updateTrace(data) {
  return request.put('/trace', data)
}

export function deleteTrace(id) {
  return request.delete(`/trace/${id}`)
}

export function publishTrace(id) {
  return request.post(`/trace/${id}/publish`)
}

export function generateQRCode(id) {
  return request.post(`/trace/${id}/qrcode`)
}

// 溯源节点
export function addTraceNode(data) {
  return request.post('/trace/node', data)
}

export function updateTraceNode(data) {
  return request.put('/trace/node', data)
}

export function deleteTraceNode(nodeId) {
  return request.delete(`/trace/node/${nodeId}`)
}
