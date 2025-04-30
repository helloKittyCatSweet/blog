import { useUserStore } from '@/stores/modules/user'
import axios from 'axios'
import router from '@/router'


const baseURL = import.meta.env.MODE === 'development'
  ? 'http://localhost:8080'
  : '/api'

const instance = axios.create({
  baseURL,
  timeout: 100000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
instance.interceptors.request.use(async config => {
  const userStore = useUserStore()
  // 直接从user对象中取token
  const token = userStore.user?.token
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => {
  return Promise.reject(error)
})

// 响应
instance.interceptors.response.use(response => response, error => {
  if (error.response?.status === 401) {
    const userStore = useUserStore()
    userStore.clear()
    // 保存当前路径用于登录后跳转
    const currentPath = router.currentRoute.value.fullPath
    router.push({
      path: '/login',
      query: {
        redirect: currentPath
      }
    })
  } else if (error.response?.status === 403) {
    // 处理权限不足的情况
    ElMessage({
      message: '权限不足，请联系管理员',
      type: 'error',
      offset: 80
    })
    router.push('/403')
  }
  return Promise.reject(error)
})


export default instance
export { baseURL }
