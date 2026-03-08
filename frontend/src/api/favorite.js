import request from '@/utils/request'

export function getFavoritePage(params) {
  return request.get('/favorite/page', { params })
}

export function addFavorite(data) {
  return request.post('/favorite', data)
}

export function removeFavorite(params) {
  return request.delete('/favorite', { params })
}
