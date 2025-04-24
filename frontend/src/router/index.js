import { createRouter, createWebHistory } from 'vue-router';
import {
  LOGIN_PATH,
  CONTROL_PANEL_PATH,
  CONTROL_PANEL_INDEX_PATH
} from '@/constants/routes-constants.js';
import { useUserStore } from '@/stores/modules/user'
import { ROLES } from '@/constants/role-constants.js'

// 导入路由模块
import authRoutes from './modules/auth.js'
import userRoutes from './modules/user.js'
import adminRoutes from './modules/admin.js'
import blogRoutes from './modules/blog.js'
import externalRoutes from './modules/external.js'

import { verifyToken } from '@/api/user/user.js';

// createRouter 创建路由实例
// 配置 history 模式
// 1. history模式：createWebHistory     地址栏不带 #
// 2. hash模式：   createWebHashHistory 地址栏带 #
// console.log(import.meta.env.DEV)

// vite 中的环境变量 import.meta.env.BASE_URL  就是 vite.config.js 中的 base 配置项
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 博客前台路由
    blogRoutes,

    // 基础权限认证路由
    ...authRoutes,

    // 控制台布局路由
    {
      path: CONTROL_PANEL_PATH,
      component: () => import('@/views/layout/ControlLayout.vue'),
      redirect: CONTROL_PANEL_INDEX_PATH,
      meta: {
        title: '控制台',
        breadcrumb: [
          { title: '控制台', path: CONTROL_PANEL_PATH },
        ],
        roles: [ROLES.USER],
      },
      children: [
        // 控制台首页 => 作者数据分析
        {
          path: CONTROL_PANEL_INDEX_PATH,
          component: () => import('@/views/layout/ControlPanel.vue'),
          meta: {
            title: '控制台首页',
            breadcrumb: [
              { title: '控制台', path: CONTROL_PANEL_PATH },
              { title: '控制台首页', path: CONTROL_PANEL_INDEX_PATH },
            ],
            roles: [ROLES.USER],
          }
        },
        // 用户路由
        ...userRoutes,
        // 管理路由
        ...adminRoutes,
        // 外部链接路由
        ...externalRoutes
      ],
    },
  ],
});

// 登录访问拦截 => 默认是直接放行的
// 根据返回值决定，是放行还是拦截
// 返回值：
// 1. undefined / true  直接放行
// 2. false 拦回from的地址页面
// 3. 具体路径 或 路径对象  拦截到对应的地址
//    '/login'   { name: 'login' }
// router.beforeEach((to) => {
//   // 如果没有token, 且访问的是非登录页，拦截到登录，其他情况正常放行
//   const useStore = useUserStore()
//   if (!useStore.token && to.path !== '/login') return '/login'
// })
// 路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();

  // 调试信息
  console.group(`[路由守卫] ${from.path} → ${to.path}`);
  console.log('用户状态:', {
    token: userStore.user.token,
    roles: userStore.user.roles,
    id: userStore.user.id
  });
  console.log('目标路由meta:', to.meta);

  try {
    // 1. 公开路由直接放行
    if (to.meta.public) {
      console.log('放行原因: 公开路由');
      return next();
    }

    // 2. 检查认证状态（确保token有效）
    if (!userStore.user.token) {
      console.log('拦截原因: 未认证');
      return next({
        path: LOGIN_PATH,
        query: { redirect: to.fullPath }
      });
    }

    // token有效性检查
    try {
      const response = await verifyToken(userStore.user.token);
      if (response.data.status === 200) {
        console.log('放行原因: token有效');
      } else {
        console.log('拦截原因: token无效');
        await userStore.logout(); // 清除用户登录状态和用户相关的data
        return next({
          path: LOGIN_PATH,
          query: { redirect: to.fullPath }
        });
      }
    } catch (error) {
      console.log('拦截原因: token无效');
      await userStore.logout(); // 清除用户登录状态和用户相关的dat
      return next({
        path: LOGIN_PATH,
        query: { redirect: to.fullPath }
      });
    }

    // 3. 已登录用户访问登录页 → 重定向到首页
    if (to.path === LOGIN_PATH) {
      console.log('拦截原因: 已认证用户访问登录页');
      return next(CONTROL_PANEL_PATH);
    }

    // 4. 检查角色权限
    if (to.meta.roles && !userStore.hasAnyRoles(to.meta.roles)) {
      console.log('拦截原因: 无权限');
      console.log('用户角色:', userStore.user.roles);
      console.log('需要角色:', to.meta.roles);
      return next('/403');
    }

    // 5. 放行访问
    console.log('放行原因: 已认证且有权访问');
    next();
  } catch (error) {
    console.error('路由守卫异常:', error);
    next(false);
  } finally {
    console.groupEnd();
  }
});
export default router;