import { LOGIN_PATH } from '@/constants/routes-constants'

export default [
  // 登录
  {
    path: LOGIN_PATH,
    component: () => import('@/views/login/LoginPage.vue'),
    meta: {
      public: true, // 公开路由
    }
  },
]