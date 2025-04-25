import { ROLES } from "@/constants/role-constants";
import { CONTROL_PANEL_PATH } from '@/constants/routes/base';
import {
  ADMIN,
  ADMIN_CONTENT,
  ADMIN_POST_MANAGE_PATH,
  ADMIN_CATEGORY_MANAGE_PATH,
  ADMIN_TAG_MANAGE_PATH,
  ADMIN_REPORT_MANAGE_PATH,
  ADMIN_ROLE,
  ADMIN_USER_MANAGE_PATH,
  ADMIN_ROLE_MANAGE_PATH,
  ADMIN_ROLE_PERMISSION_MANAGE_PATH,
  ADMIN_MESSAGE_MANAGE_PATH,
  ADMIN_SYSTEM_PATH,
  ADMIN_SYSTEM_MESSAGE_PATH,
  ADMIN_SYSTEM_STATISTICS_PATH
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
        path: ADMIN_REPORT_MANAGE_PATH,
        component: () => import('@/views/report/admin/ReportManage.vue'),
        meta: {
          title: '举报管理',
          breadcrumb: true,
          roles: [ROLES.REPORT_MANAGER, ROLES.SYSTEM_ADMINISTRATOR],
        }
      },

      // 用户权限管理模块
      {
        path: ADMIN_ROLE,
        component: () => import('@/views/layout/RoleLayout.vue'),
        meta: {
          title: '用户权限管理',
          redirect: ADMIN_USER_MANAGE_PATH,
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
            path: ADMIN_ROLE_PERMISSION_MANAGE_PATH,
            component: () => import('@/views/role/RolePermissionManage.vue'),
            meta: {
              title: '角色权限配置',
              breadcrumb: true,
              roles: [ROLES.PERMISSION_MAPPING_MANAGER, ROLES.SYSTEM_ADMINISTRATOR],
            }
          },
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
      },
      // 系统管理
      {
        path: ADMIN_SYSTEM_PATH,
        component: () => import('@/views/system/SystemManage.vue'),
        redirect: ADMIN_SYSTEM_MESSAGE_PATH,
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
          {
            path: ADMIN_SYSTEM_MESSAGE_PATH,
            component: () => import('@/views/system/SystemMessageManage.vue'),
            meta: {
              title: '系统消息管理',
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
            }
          },
          {
            path: ADMIN_SYSTEM_STATISTICS_PATH,
            component: () => import('@/views/system/SystemStatistics.vue'),
            meta: {
              title: '系统统计',
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
            }
          }
        ]
      },
    ]
  }
]