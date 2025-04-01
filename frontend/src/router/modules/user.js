import { ROLES } from '@/constants/role-constants'
import {
  USER,
  USER_POST_MANAGE_PATH,
  USER_POST_LIST_PATH,
  USER_POST_VERSION_MANAGE_PATH,
  USER_POST_EDIT_PATH,
  USER_POST_ATTACHMENT_LIST_PATH,
  USER_COMMENT_MANAGE_PATH,
  USER_REPORT_MANAGE_PATH,
  USER_FAVORITE_MANAGE_PATH,
  USER_MESSAGE_MANAGE_PATH,
  USER_PROFILE_PATH,
  USER_AVATAR_PATH,
  USER_PASSWORD_PATH,
  USER_SETTING_PATH,
  USER_POST_COMMENT_MANAGE_PATH,
  USER_MESSAGE,
  USER_PROFILE,
  USER_MESSAGE_DETAIL_PATH
} from '@/constants/routes-constants.js'

export default [
  {
    path: USER,
    component: () => import('@/views/layout/UserLayout.vue'),
    redirect: USER_POST_MANAGE_PATH,
    meta: {
      title: '我的',
      breadcrumb: true,
      roles: [ROLES.USER],
    },
    children: [
      // 内容管理
      {
        path: USER_POST_MANAGE_PATH,
        component: () => import('@/views/layout/user/PostLayout.vue'),
        redirect: USER_POST_LIST_PATH,
        meta: {
          title: '文章管理',
          breadcrumb: true,
          roles: [ROLES.USER],
        },
        children: [
          {
            path: USER_POST_LIST_PATH,
            component: () => import('@/views/post/user/PostList.vue'),
            meta: {
              title: '文章列表',
              breadcrumb: true,
              roles: [ROLES.USER],
            }
          },
          {
            path: USER_POST_EDIT_PATH,
            component: () => import('@/views/post/user/PostEdit.vue'),
            meta: {
              title: '修改文章',
              breadcrumb: true,
              roles: [ROLES.USER],
            }
          },
          {
            path: USER_POST_VERSION_MANAGE_PATH,
            component: () => import('@/views/post/user/PostVersionManage.vue'),
            meta: {
              title: '文章版本管理',
              breadcrumb: true,
              roles: [ROLES.USER],
            }
          },
          {
            path: USER_POST_ATTACHMENT_LIST_PATH,
            component: () => import('@/views/post/user/PostAttachmentManage.vue'),
            meta: {
              title: '文章附件管理',
              breadcrumb: true,
              roles: [ROLES.USER],
            }
          }
        ]
      },
      {
        path: USER_COMMENT_MANAGE_PATH,
        component: () => import('@/views/comment/user/CommentManage.vue'),
        meta: {
          title: '评论管理',
          breadcrumb: true,
          roles: [ROLES.USER],
        }
      },

      {
        path: USER_FAVORITE_MANAGE_PATH,
        component: () => import('@/views/favorite/user/FavoriteManage.vue'),
        meta: {
          title: '收藏夹',
          breadcrumb: true,
          roles: [ROLES.USER],
        }
      },
      // 消息中心
      {
        path: USER_MESSAGE,
        component: () => import('@/views/layout/user/MessageLayout.vue'),
        redirect: USER_MESSAGE_MANAGE_PATH,
        meta: {
          title: '消息中心',
          breadcrumb: true,
          roles: [ROLES.USER],
        },
        children: [
          {
            path: USER_MESSAGE_MANAGE_PATH,
            component: () => import('@/views/message/user/MessageManage.vue'),
            meta: {
              title: '消息管理',
              breadcrumb: true,
              roles: [ROLES.USER],
            }
          },
          {
            path: USER_MESSAGE_DETAIL_PATH,
            name: 'MessageChat',
            component: () => import('@/views/message/user/ChatWindow.vue'),
            meta: {
              title: '聊天',
              breadcrumb: true,
              roles: [ROLES.USER],
              requiresAuth: true
            }
          },
          {
            path: USER_REPORT_MANAGE_PATH,
            component: () => import('@/views/report/user/ReportManage.vue'),
            meta: {
              title: '举报管理',
              breadcrumb: true,
              roles: [ROLES.USER],
            }
          }
        ]
      },
      // 个人中心
      {
        path: USER_PROFILE,
        component: () => import('@/views/layout/user/ProfileLayout.vue'),
        redirect: USER_PROFILE_PATH,
        meta: {
          title: '个人中心',
          breadcrumb: true,
          roles: [ROLES.USER],
        },
        children: [
          {
            path: USER_PROFILE_PATH,
            component: () => import('@/views/user/UserProfile.vue'),
            meta: {
              title: '个人资料',
              breadcrumb: true,
              roles: [ROLES.USER],
            }
          },
          {
            path: USER_PASSWORD_PATH,
            component: () => import('@/views/user/UserPassword.vue'),
            meta: {
              title: '修改密码',
              breadcrumb: true,
              roles: [ROLES.USER],
            }
          },
          {
            path: USER_SETTING_PATH,
            component: () => import('@/views/user/UserSetting.vue'),
            meta: {
              title: '个人设置',
              breadcrumb: true,
              roles: [ROLES.USER],
            }
          }
        ]
      }
    ]
  }
]