import { createRouter, createWebHistory } from 'vue-router';
import {
  LOGIN_PATH,
  CONTROL_PANEL_PATH,
  COMMENT_MANAGE_PATH,
  FAVORITE_MANAGE_PATH,
  MESSAGE_MANAGE_PATH,
  PERMISSION_MANAGE_PATH,
  POST_MANAGE_PATH,
  POST_LIST_PATH,
  POST_VERSION_MANAGE_PATH,
  POST_EDIT_PATH,
  POST_ATTACHMENT_LIST_PATH,
  REPORT_MANAGE_PATH,
  ROLE_MANAGE_PATH,
  TAG_MANAGE_PATH,
  USER_MANAGE,
  USER_BEHAVIOR_MANAGE_PATH,
  USER_PROFILE_PATH,
  USER_AVATAR_PATH,
  USER_PASSWORD_PATH,
  USER_SETTING_PATH,
  SYSTEM_MANAGE_PATH,
  CATEGORY_MANAGE_PATH,
  ROLE_PERMISSION_MANAGE_PATH,
  ROLE_USER_MANAGE_PATH,
  CONTROL_PANEL_INDEX_PATH
} from '@/constants/routes-constants.js';
import { useUserStore } from '@/stores/modules/user'
import {ROLES} from '@/costants/role-constants'

// createRouter 创建路由实例
// 配置 history 模式
// 1. history模式：createWebHistory     地址栏不带 #
// 2. hash模式：   createWebHashHistory 地址栏带 #
// console.log(import.meta.env.DEV)

// vite 中的环境变量 import.meta.env.BASE_URL  就是 vite.config.js 中的 base 配置项
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 登录
    {
      path: LOGIN_PATH,
      component: () => import('@/views/login/LoginPage.vue'),
      meta: {
        public: true, // 公开路由
      }
    },

    // 控制台
    {
      path: CONTROL_PANEL_PATH,
      component: () => import('@/views/layout/LayoutContainer.vue'),
      redirect: CONTROL_PANEL_INDEX_PATH,
      meta: {
        title: '控制台',
        breadcrumb: [
          { title: '控制台', path: CONTROL_PANEL_PATH },
        ],
      },
      children: [
        // 控制台首页
        {
          path: CONTROL_PANEL_INDEX_PATH,
          component: () => import('@/views/layout/ControlPanel.vue'),
          meta: {
            title: '控制台首页',
            breadcrumb: [
              { title: '控制台', path: CONTROL_PANEL_PATH },
              { title: '控制台首页', path: CONTROL_PANEL_INDEX_PATH },
            ],
          }
        },

        // 用户行为管理
        {
          path: USER_BEHAVIOR_MANAGE_PATH,
          component: () => import('@/views/user/UserBehaviorManage.vue'),
          meta: {
            title: '用户行为管理',
            breadcrumb: [
              { title: '控制台', path: CONTROL_PANEL_PATH },
              { title: '用户行为管理', path: USER_BEHAVIOR_MANAGE_PATH },
            ],
          }
        },
        // 消息管理
        {
          path: MESSAGE_MANAGE_PATH,
          component: () => import('@/views/message/MessageManage.vue'),
          meta: {
            title: '消息管理',
            breadcrumb: [
              { title: '控制台', path: CONTROL_PANEL_PATH },
              { title: '消息管理', path: MESSAGE_MANAGE_PATH },
            ],
          }
        },
        // 举报管理
        {
          path: REPORT_MANAGE_PATH,
          component: () => import('@/views/report/ReportManage.vue'),
          meta: {
            title: '举报管理',
            breadcrumb: [
              { title: '控制台', path: CONTROL_PANEL_PATH },
              { title: '举报管理', path: REPORT_MANAGE_PATH },
            ],
          }
        },

        // 文章管理
        {
          path: POST_MANAGE_PATH,
          component: () => import('@/views/layout/PostLayout.vue'),
          redirect: POST_LIST_PATH,
          meta: {
            title: '文章管理',
            breadcrumb: [
              { title: '控制台', path: CONTROL_PANEL_PATH },
              { title: '文章管理', path: POST_MANAGE_PATH },
            ],
          },
          children: [
            // 文章列表
            {
              path: POST_LIST_PATH,
              component: () => import('@/views/post/PostList.vue'),
              meta: {
                title: '文章列表',
                breadcrumb: [
                  { title: '控制台', path: CONTROL_PANEL_PATH },
                  { title: '文章管理', path: POST_MANAGE_PATH },
                  { title: '文章列表', path: POST_LIST_PATH },
                ],
              }
            },
            // 文章版本管理
            {
              path: POST_VERSION_MANAGE_PATH,
              component: () => import('@/views/post/PostVersionManage.vue'),
              meta: {
                title: '文章版本管理',
                breadcrumb: [
                  { title: '控制台', path: CONTROL_PANEL_PATH },
                  { title: '文章管理', path: POST_MANAGE_PATH },
                  { title: '文章版本管理', path: POST_VERSION_MANAGE_PATH },
                ],
              }
            },
            // 修改文章
            {
              path: POST_EDIT_PATH,
              component: () => import('@/views/post/PostEdit.vue'),
              meta: {
                title: '修改文章',
                breadcrumb: [
                  { title: '控制台', path: CONTROL_PANEL_PATH },
                  { title: '文章管理', path: POST_MANAGE_PATH },
                  { title: '修改文章', path: POST_EDIT_PATH },
                ],
              }
            },
            // 文章附件管理
            {
              path: POST_ATTACHMENT_LIST_PATH,
              component: () => import('@/views/post/PostAttachmentManage.vue'),
              meta: {
                title: '文章附件管理',
                breadcrumb: [
                  { title: '控制台', path: CONTROL_PANEL_PATH },
                  { title: '文章管理', path: POST_MANAGE_PATH },
                  { title: '文章附件管理', path: POST_ATTACHMENT_LIST_PATH },
                ],
              }
            },
            // 评论管理
            {
              path: COMMENT_MANAGE_PATH,
              component: () => import('@/views/comment/CommentManage.vue'),
              meta: {
                title: '评论管理',
                breadcrumb: [
                  { title: '控制台', path: CONTROL_PANEL_PATH },
                  { title: '文章管理', path: POST_MANAGE_PATH },
                  { title: '评论管理', path: COMMENT_MANAGE_PATH },
                ],
              }
            },
          ],
        },

        // 个人信息设置
        {
          path: USER_MANAGE,
          component: () => import('@/views/layout/UserLayout.vue'),
          redirect: USER_PROFILE_PATH,
          meta: {
            title: '个人信息设置',
            breadcrumb: [
              { title: '控制台', path: CONTROL_PANEL_PATH },
              { title: '个人信息设置', path: USER_MANAGE },
            ],
          },
          children: [
            // 收藏夹管理
            {
              path: FAVORITE_MANAGE_PATH,
              component: () => import('@/views/favorite/FavoriteManage.vue'),
              meta: {
                title: '收藏夹管理',
                breadcrumb: [
                  { title: '控制台', path: CONTROL_PANEL_PATH },
                  { title: '个人信息设置', path: USER_MANAGE },
                  { title: '收藏夹管理', path: FAVORITE_MANAGE_PATH },
                ],
              }
            },
            // 用户信息管理
            {
              path: USER_PROFILE_PATH,
              component: () => import('@/views/user/UserProfile.vue'),
              meta: {
                title: '用户信息管理',
                breadcrumb: [
                  { title: '控制台', path: CONTROL_PANEL_PATH },
                  { title: '个人信息设置', path: USER_MANAGE },
                  { title: '用户信息管理', path: USER_PROFILE_PATH },
                ],
              }
            },
            // 用户头像管理
            {
              path: USER_AVATAR_PATH,
              component: () => import('@/views/user/UserAvatar.vue'),
              meta: {
                title: '用户头像管理',
                breadcrumb: [
                  { title: '控制台', path: CONTROL_PANEL_PATH },
                  { title: '个人信息设置', path: USER_MANAGE },
                  { title: '用户头像管理', path: USER_AVATAR_PATH },
                ],
              }
            },
            // 用户重置密码
            {
              path: USER_PASSWORD_PATH,
              component: () => import('@/views/user/UserPassword.vue'),
              meta: {
                title: '用户重置密码',
                breadcrumb: [
                  { title: '控制台', path: CONTROL_PANEL_PATH },
                  { title: '个人信息设置', path: USER_MANAGE },
                  { title: '用户重置密码', path: USER_PASSWORD_PATH },
                ],
              }
            },
            // 用户设置
            {
              path: USER_SETTING_PATH,
              component: () => import('@/views/user/UserSetting.vue'),
              meta: {
                title: '用户设置',
                breadcrumb: [
                  { title: '控制台', path: CONTROL_PANEL_PATH },
                  { title: '个人信息设置', path: USER_MANAGE },
                  { title: '用户设置', path: USER_SETTING_PATH },
                ],
              }
            },
          ],
        },

        // 系统设置
        {
          path: SYSTEM_MANAGE_PATH,
          component: () => import('@/views/layout/SystemLayout.vue'),
          redirect: CATEGORY_MANAGE_PATH,
          meta: {
            title: '系统设置',
            breadcrumb: [
              { title: '控制台', path: CONTROL_PANEL_PATH },
              { title: '系统设置', path: SYSTEM_MANAGE_PATH },
            ],
          },
          children: [
            // 分类管理
            {
              path: CATEGORY_MANAGE_PATH,
              component: () => import('@/views/category/CategoryManage.vue'),
              meta: {
                title: '分类管理',
                breadcrumb: [
                  { title: '控制台', path: CONTROL_PANEL_PATH },
                  { title: '系统设置', path: SYSTEM_MANAGE_PATH },
                  { title: '分类管理', path: CATEGORY_MANAGE_PATH },
                ],
              }
            },
            // 标签管理
            {
              path: TAG_MANAGE_PATH,
              component: () => import('@/views/tag/TagManage.vue'),
              meta: {
                title: '标签管理',
                breadcrumb: [
                  { title: '控制台', path: CONTROL_PANEL_PATH },
                  { title: '系统设置', path: SYSTEM_MANAGE_PATH },
                  { title: '标签管理', path: TAG_MANAGE_PATH },
                ],
              }
            },
          ],
        },

        // 用户设置
        {
          path: ROLE_MANAGE_PATH,
          component: () => import('@/views/layout/RoleLayout.vue'),
          redirect: ROLE_USER_MANAGE_PATH,
          meta: {
            title: '用户设置',
            breadcrumb: [
              { title: '控制台', path: CONTROL_PANEL_PATH },
              { title: '用户设置', path: ROLE_MANAGE_PATH },
            ],
          },
          children: [
            // 角色管理
            {
              path: ROLE_MANAGE_PATH,
              component: () => import('@/views/role/RoleManage.vue'),
              meta: {
                title: '角色管理',
                breadcrumb: [
                  { title: '控制台', path: CONTROL_PANEL_PATH },
                  { title: '用户设置', path: ROLE_MANAGE_PATH },
                  { title: '角色管理', path: ROLE_MANAGE_PATH },
                ],
              }
            },
            // 角色权限映射管理
            {
              path: ROLE_PERMISSION_MANAGE_PATH,
              component: () => import('@/views/role/RolePermissionManage.vue'),
              meta: {
                title: '角色权限映射管理',
                breadcrumb: [
                  { title: '控制台', path: CONTROL_PANEL_PATH },
                  { title: '用户设置', path: ROLE_MANAGE_PATH },
                  { title: '角色权限映射管理', path: ROLE_PERMISSION_MANAGE_PATH }]
              }
            },

            // 用户角色管理
            {
              path: ROLE_USER_MANAGE_PATH,
              component: () => import('@/views/user/UserRoleManage.vue'),
              meta: {
                title: '用户角色管理',
                breadcrumb: [
                  { title: '控制台', path: CONTROL_PANEL_PATH },
                  { title: '用户设置', path: ROLE_MANAGE_PATH },
                  { title: '用户角色管理', path: ROLE_USER_MANAGE_PATH },
                ],
              }
            },
            // 权限管理
            {
              path: PERMISSION_MANAGE_PATH,
              component: () => import('@/views/permission/PermissionManage.vue'),
              meta: {
                title: '权限管理',
                breadcrumb: [
                  { title: '控制台', path: CONTROL_PANEL_PATH },
                  { title: '用户设置', path: ROLE_MANAGE_PATH },
                  { title: '权限管理', path: PERMISSION_MANAGE_PATH },
                ],
              }
            }
          ]
        }
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
  console.log('当前token:', userStore.user.token);
  console.log('目标路由meta:', to.meta);

  try {
    // 1. 公开路由直接放行
    if (to.meta.public) {
      console.log('放行原因: 公开路由');
      return next();
    }

    // 2. 检查认证状态（确保token有效）
    if (!userStore.user?.token) {
      console.log('拦截原因: 未认证');
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

    // 4. 检查用户信息是否完整（防止刷新后丢失）
    if (!userStore.user.id) {
      console.log('尝试获取用户信息...');
      try {
        await userStore.getUser();
      } catch (error) {
        console.error('获取用户信息失败:', error);
        userStore.clear();
        return next({
          path: LOGIN_PATH,
          query: { redirect: to.fullPath }
        });
      }
    }

    // 5. 检查角色权限
    if (to.meta.roles && !userStore.hasAnyRoles(to.meta.roles)) {
      console.log('拦截原因: 无权限');
      return next('/403');
    }

    // 6. 放行访问
    console.log('放行原因: 已认证且有权访问');
    next();
  } catch (error) {
    console.error('路由守卫异常:', error);
    next(false); // 中断当前导航
  } finally {
    console.groupEnd();
  }
});

export default router;