import { useUserStore } from '@/stores/user'

const normalize = (value) => {
  if (!value) return []
  return Array.isArray(value) ? value : [value]
}

const update = (el, binding) => {
  const userStore = useUserStore()
  const codes = normalize(binding.value)
  const hasPermission = userStore.hasPermission(codes)
  el.style.display = hasPermission ? '' : 'none'
}

export default {
  mounted(el, binding) {
    update(el, binding)
  },
  updated(el, binding) {
    update(el, binding)
  }
}
