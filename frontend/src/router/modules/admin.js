import { ROLES } from "@/constants/role-constants";
import { CONTROL_PANEL_PATH } from '@/constants/routes/base';
import {
  ADMIN,
  ADMIN_CONTENT,
  ADMIN_POST_MANAGE_PATH,
  ADMIN_POST_VERSION_MANAGE_PATH,
  ADMIN_CATEGORY_MANAGE_PATH,
  ADMIN_TAG_MANAGE_PATH,
  ADMIN_USER_ACTIVITY_MANAGE_PATH,
  ADMIN_COMMENT_MANAGE_PATH,
  ADMIN_FAVORITE_MANAGE_PATH,
  ADMIN_LIKE_MANAGE_PATH,
  ADMIN_REPORT_MANAGE_PATH,
  ADMIN_ROLE,
  ADMIN_USER_MANAGE_PATH,
  ADMIN_ROLE_MANAGE_PATH,
  ADMIN_ROLE_USER_MANAGE_PATH,
  ADMIN_ROLE_PERMISSION_MANAGE_PATH,
  ADMIN_PERMISSION_MANAGE_PATH,
  ADMIN_USER_SETTING_MANAGE_PATH,
  ADMIN_MESSAGE_MANAGE_PATH
} from '@/constants/routes/admin';

export default [
  {
    path: ADMIN,
    component: () => import('@/views/layout/admin/AdminLayout.vue'),
    redirect: CONTROL_PANEL_PATH,
    meta: {
      title: '系统管理',
      breadcrumb: true,
      roles: [
        ROLES.CATEGORY_MANAGER,
        ROLES.PERMISSION_MANAGER,
        ROLES.PERMISSION_MAPPING_MANAGER,
        ROLES.TAG_MANAGER,
        ROLES.POST_MANAGER,
        ROLES.USER_ACTIVITY_MANAGER,
        ROLES.USER_ROLE_MAPPING_MANAGER,
        ROLES.COMMENT_MANAGER,
        ROLES.MESSAGE_MANAGER,
        ROLES.REPORT_MANAGER,
        ROLES.ROLE_MANAGER,
        ROLES.SYSTEM_ADMINISTRATOR
      ],
    },
    children: [
      // 内容管理模块
      {
        path: ADMIN_CONTENT,
        component: () => import('@/views/layout/admin/ContentLayout.vue'),
        meta: {
          title: '内容管理',
          breadcrumb: true,
        },
        children: [
          {
            path: ADMIN_POST_MANAGE_PATH,
            component: () => import('@/views/post/admin/PostManage.vue'),
            meta: {
              title: '文章管理',
              breadcrumb: true,
              roles: [ROLES.POST_MANAGER, ROLES.SYSTEM_ADMINISTRATOR],
            }
          },
          {
            path: ADMIN_POST_VERSION_MANAGE_PATH,
            component: () => import('@/views/post/admin/PostVersionManage.vue'),
            meta: {
              title: '版本管理',
              breadcrumb: true,
              roles: [ROLES.SYSTEM_ADMINISTRATOR],
            }
          },
          {
            path: ADMIN_CATEGORY_MANAGE_PATH,
            component: () => import('@/views/category/CategoryManage.vue'),
            meta: {
              title: '分类管理',
              breadcrumb: true,
              roles: [ROLES.CATEGORY_MANAGER, ROLES.SYSTEM_ADMINISTRATOR],
            }
          },
          {
            path: ADMIN_TAG_MANAGE_PATH,
            component: () => import('@/views/tag/TagManage.vue'),
            meta: {
              title: '标签管理',
              breadcrumb: true,
              roles: [ROLES.TAG_MANAGER, ROLES.SYSTEM_ADMINISTRATOR],
            }
          }
        ]
      },

      // 用户行为管理模块
      {
        path: ADMIN_USER_ACTIVITY_MANAGE_PATH,
        component: () => import('@/views/layout/admin/UserActivityLayout.vue'),
        meta: {
          title: '用户行为管理',
          breadcrumb: true,
          roles: [ROLES.SYSTEM_ADMINISTRATOR, ROLES.USER_ACTIVITY_MANAGER, ROLES.COMMENT_MANAGER],
        },
        children: [
          {
            path: ADMIN_COMMENT_MANAGE_PATH,
            component: () => import('@/views/comment/admin/CommentManage.vue'),
            meta: {
              title: '评论管理',
              breadcrumb: true,
              roles: [ROLES.USER_ACTIVITY_MANAGER, ROLES.COMMENT_MANAGER, ROLES.SYSTEM_ADMINISTRATOR],
            }
          },
          {
            path: ADMIN_FAVORITE_MANAGE_PATH,
            component: () => import('@/views/favorite/admin/FavoriteManage.vue'),
            meta: {
              title: '收藏管理',
              breadcrumb: true,
              roles: [ROLES.USER_ACTIVITY_MANAGER, ROLES.SYSTEM_ADMINISTRATOR],
            }
          },
          {
            path: ADMIN_LIKE_MANAGE_PATH,
            component: () => import('@/views/like/admin/LikeManage.vue'),
            meta: {
              title: '点赞管理',
              breadcrumb: true,
              roles: [ROLES.USER_ACTIVITY_MANAGER, ROLES.SYSTEM_ADMINISTRATOR],
            }
          },
          {
            path: ADMIN_REPORT_MANAGE_PATH,
            component: () => import('@/views/report/admin/ReportManage.vue'),
            meta: {
              title: '举报管理',
              breadcrumb: true,
              roles: [ROLES.REPORT_MANAGER, ROLES.SYSTEM_ADMINISTRATOR],
            }
          }
        ]
      },

      // 用户权限管理模块
      {
        path: ADMIN_ROLE,
        component: () => import('@/views/layout/RoleLayout.vue'),
        meta: {
          title: '用户权限管理',
          breadcrumb: true,
          roles: [
            ROLES.USER_USER_MANAGER,
            ROLES.ROLE_MANAGER,
            ROLES.SYSTEM_ADMINISTRATOR,
            ROLES.PERMISSION_MAPPING_MANAGER,
            ROLES.PERMISSION_MANAGER
          ],
        },
        children: [
          {
            path: ADMIN_USER_MANAGE_PATH,
            component: () => import('@/views/user/admin/UserManage.vue'),
            meta: {
              title: '用户管理',
              breadcrumb: true,
              roles: [ROLES.SYSTEM_ADMINISTRATOR, ROLES.USER_USER_MANAGER],
            }
          },
          {
            path: ADMIN_ROLE_MANAGE_PATH,
            component: () => import('@/views/role/RoleManage.vue'),
            meta: {
              title: '角色管理',
              breadcrumb: true,
              roles: [ROLES.ROLE_MANAGER, ROLES.SYSTEM_ADMINISTRATOR],
            }
          },
          {
            path: ADMIN_ROLE_USER_MANAGE_PATH,
            component: () => import('@/views/user/UserRoleManage.vue'),
            meta: {
              title: '用户角色管理',
              breadcrumb: true,
              roles: [ROLES.USER_ROLE_MAPPING_MANAGER, ROLES.SYSTEM_ADMINISTRATOR],
            }
          },
          {
            path: ADMIN_ROLE_PERMISSION_MANAGE_PATH,
            component: () => import('@/views/role/RolePermissionManage.vue'),
            meta: {
              title: '角色权限配置',
              breadcrumb: true,
              roles: [ROLES.PERMISSION_MAPPING_MANAGER, ROLES.SYSTEM_ADMINISTRATOR],
            }
          },
          {
            path: ADMIN_PERMISSION_MANAGE_PATH,
            component: () => import('@/views/permission/PermissionManage.vue'),
            meta: {
              title: '权限管理',
              breadcrumb: true,
              roles: [ROLES.PERMISSION_MANAGER, ROLES.SYSTEM_ADMINISTRATOR],
            }
          },
          {
            path: ADMIN_USER_SETTING_MANAGE_PATH,
            component: () => import('@/views/user/admin/UserSettingManage.vue'),
            meta: {
              title: '用户设置管理',
              breadcrumb: true,
              roles: [ROLES.SYSTEM_ADMINISTRATOR],
            }
          }
        ]
      },

      // 消息管理
      {
        path: ADMIN_MESSAGE_MANAGE_PATH,
        component: () => import('@/views/message/admin/MessageManage.vue'),
        meta: {
          title: '消息管理',
          breadcrumb: true,
          roles: [ROLES.SYSTEM_ADMINISTRATOR, ROLES.MESSAGE_MANAGER],
        }
      }
    ]
  }
]