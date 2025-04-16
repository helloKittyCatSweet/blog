import { LOGIN_PATH } from '@/constants/routes-constants'


export default [
  // 登录
  {
    path: LOGIN_PATH,
    name: 'Login',
    component: () => import('@/views/login/LoginPage.vue'),
    meta: {
      title: '登录',
      public: true, // 公开路由
    },
  },
]