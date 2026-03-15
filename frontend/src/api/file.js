import request from '@/utils/request'

/**
 * 上传图片
 * @param {File} file 图片文件
 * @param {string} module 所属模块
 * @returns {Promise}
 */
export function uploadImage(file, module = 'common') {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('module', module)
  return request.post('/file/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传文件
 * @param {File} file 文件
 * @param {string} module 所属模块
 * @returns {Promise}
 */
export function uploadFile(file, module = 'common') {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('module', module)
  return request.post('/file/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 批量上传图片
 * @param {File[]} files 图片文件数组
 * @param {string} module 所属模块
 * @returns {Promise}
 */
export function uploadImages(files, module = 'common') {
  const formData = new FormData()
  files.forEach(file => formData.append('files', file))
  formData.append('module', module)
  return request.post('/file/upload/images', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
