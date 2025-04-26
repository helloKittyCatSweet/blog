import { LOGIN_PATH, GITHUB_CALLBACK_PATH } from '@/constants/routes/base'


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
  {
    path: GITHUB_CALLBACK_PATH,
    name: 'GitHubCallback',
    component: () => import('@/views/login/GitHubCallback.vue'),
    meta: {
      title: 'GitHub授权回调',
      public: true, // 添加public属性
    }
  }
]