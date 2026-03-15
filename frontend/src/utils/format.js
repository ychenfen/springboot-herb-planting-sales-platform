/**
 * 通用格式化工具函数
 */

/**
 * 格式化日期
 * @param {string|Date} date 日期
 * @param {string} fmt 格式，默认 YYYY-MM-DD
 * @returns {string}
 */
export function formatDate(date, fmt = 'YYYY-MM-DD') {
  if (!date) return '--'
  const d = typeof date === 'string' ? new Date(date) : date
  if (isNaN(d.getTime())) return '--'

  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')

  return fmt
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 格式化金额
 * @param {number|string} value 金额
 * @param {number} precision 精度，默认 2
 * @returns {string}
 */
export function formatMoney(value, precision = 2) {
  if (value === null || value === undefined || value === '') return '--'
  const num = Number(value)
  if (isNaN(num)) return '--'
  return num.toFixed(precision)
}

/**
 * 格式化数量
 * @param {number|string} value 数量
 * @param {string} unit 单位
 * @returns {string}
 */
export function formatQuantity(value, unit = '') {
  if (value === null || value === undefined || value === '') return '--'
  const num = Number(value)
  if (isNaN(num)) return '--'
  return unit ? `${num.toFixed(2)} ${unit}` : num.toFixed(2)
}

/**
 * 格式化文件大小
 * @param {number} bytes 字节数
 * @returns {string}
 */
export function formatFileSize(bytes) {
  if (!bytes || bytes === 0) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return (bytes / Math.pow(1024, i)).toFixed(2) + ' ' + units[i]
}

/**
 * 格式化相对时间
 * @param {string|Date} date 日期
 * @returns {string}
 */
export function formatRelativeTime(date) {
  if (!date) return '--'
  const d = typeof date === 'string' ? new Date(date) : date
  if (isNaN(d.getTime())) return '--'

  const now = new Date()
  const diff = now.getTime() - d.getTime()
  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)

  if (seconds < 60) return '刚刚'
  if (minutes < 60) return `${minutes} 分钟前`
  if (hours < 24) return `${hours} 小时前`
  if (days < 7) return `${days} 天前`
  if (days < 30) return `${Math.floor(days / 7)} 周前`
  return formatDate(d)
}

/**
 * 截断文本
 * @param {string} text 文本
 * @param {number} maxLength 最大长度
 * @returns {string}
 */
export function truncateText(text, maxLength = 50) {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}
