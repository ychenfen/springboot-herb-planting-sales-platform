import request from '@/utils/request'

export function getNoticePage(params) {
  return request.get('/notice/page', { params })
}

export function getNoticeById(id) {
  return request.get(`/notice/${id}`)
}

export function getUnreadNoticeCount() {
  return request.get('/notice/unread-count')
}

export function markNoticeRead(id) {
  return request.put(`/notice/${id}/read`)
}

export function markAllNoticeRead() {
  return request.put('/notice/read-all')
}
