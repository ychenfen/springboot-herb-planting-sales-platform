import request from '@/utils/request'

export function getKnowledgePage(params) {
  return request.get('/knowledge/page', { params })
}

export function getDiseaseList(params) {
  return request.get('/knowledge/diseases', { params })
}

export function identifyDisease(data) {
  return request.post('/knowledge/disease/identify', data, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function getPlantingCalendar(params) {
  return request.get('/knowledge/calendar', { params })
}

export function getCalendarReminders(params) {
  return request.get('/knowledge/calendar/reminders', { params })
}
