import request from '@/utils/request'

export function login(data) {
  return request.post('/auth/login', data)
}

export function register(data) {
  return request.post('/auth/register', data)
}

export function getUserInfo() {
  return request.get('/auth/info')
}

export function logout() {
  return request.post('/auth/logout')
}

export function updatePassword(data) {
  return request.put('/auth/password', data)
}

export function updateProfile(data) {
  return request.put('/auth/profile', data)
}
