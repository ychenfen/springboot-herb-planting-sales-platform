/**
 * 前端常量定义
 */

// 用户类型
export const USER_TYPES = [
  { value: 1, label: '种植户', color: '#2e7d32', tag: 'success' },
  { value: 2, label: '商家', color: '#1976d2', tag: 'primary' },
  { value: 3, label: '管理员', color: '#d32f2f', tag: 'danger' },
  { value: 4, label: '普通用户', color: '#ed6c02', tag: 'warning' }
]

// 作物状态
export const CROP_STATUS = [
  { value: 1, label: '生长中', tag: 'success' },
  { value: 2, label: '已收获', tag: 'warning' },
  { value: 3, label: '已销售', tag: 'info' }
]

// 供应状态
export const SUPPLY_STATUS = [
  { value: 0, label: '草稿', tag: 'info' },
  { value: 1, label: '在售', tag: 'success' },
  { value: 2, label: '已下架', tag: 'warning' },
  { value: 3, label: '已售罄', tag: 'danger' }
]

// 需求状态
export const DEMAND_STATUS = [
  { value: 0, label: '草稿', tag: 'info' },
  { value: 1, label: '进行中', tag: 'success' },
  { value: 2, label: '已关闭', tag: 'info' },
  { value: 3, label: '已满足', tag: 'warning' }
]

// 订单状态
export const ORDER_STATUS = [
  { value: 1, label: '待确认', tag: 'warning' },
  { value: 2, label: '待发货', tag: 'primary' },
  { value: 3, label: '待收货', tag: 'primary' },
  { value: 4, label: '已完成', tag: 'success' },
  { value: 5, label: '已取消', tag: 'info' }
]

// 溯源节点类型
export const TRACE_NODE_TYPES = [
  { value: 'plant', label: '种植', color: '#2e7d32' },
  { value: 'grow', label: '生长', color: '#4caf50' },
  { value: 'fertilize', label: '施肥', color: '#8bc34a' },
  { value: 'spray', label: '施药', color: '#ff9800' },
  { value: 'harvest', label: '采收', color: '#f44336' },
  { value: 'process', label: '加工', color: '#9c27b0' },
  { value: 'quality', label: '质检', color: '#2196f3' },
  { value: 'package', label: '包装', color: '#00bcd4' },
  { value: 'storage', label: '仓储', color: '#607d8b' },
  { value: 'transport', label: '运输', color: '#795548' }
]

// 农事活动类型
export const FARM_ACTIVITY_TYPES = [
  { value: 'sow', label: '播种' },
  { value: 'fertilize', label: '施肥' },
  { value: 'water', label: '浇水' },
  { value: 'weed', label: '除草' },
  { value: 'spray', label: '施药' },
  { value: 'harvest', label: '收获' },
  { value: 'prune', label: '修剪' },
  { value: 'inspect', label: '巡检' }
]

// 通知类型
export const NOTICE_TYPES = [
  { value: 1, label: '系统通知' },
  { value: 2, label: '订单通知' },
  { value: 3, label: '评论通知' },
  { value: 4, label: '其他通知' }
]

// 质量等级
export const QUALITY_GRADES = [
  '特级', '一级', '二级', '三级', '统货'
]

// 地块状态
export const FIELD_STATUS = [
  { value: 0, label: '休耕', tag: 'info' },
  { value: 1, label: '在用', tag: 'success' },
  { value: 2, label: '整改中', tag: 'warning' }
]
