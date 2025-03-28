// directives/permission.js
import { useUserStore } from '@/stores/modules/user'

export const permissionDirective = {
  mounted(el, binding) {
    const userStore = useUserStore()
    const { value } = binding

    if (value && value instanceof Array) {
      const hasPermission = userStore.hasAnyRole(value)
      if (!hasPermission) {
        el.parentNode?.removeChild(el)
      }
    } else {
      throw new Error('需要传入角色数组，例如: v-permission="[\'ROLE_ADMIN\']"')
    }
  }
}
