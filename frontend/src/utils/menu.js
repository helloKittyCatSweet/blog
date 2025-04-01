import {
  DataAnalysis,
  UserFilled,
  User,
  Files,
  Document,
  Comment,
  Bell,
  StarFilled,
  Avatar,
  Setting,
  Key,
  MagicStick,
  SetUp,
  Orange,
  CollectionTag,
  List,
  Notebook,
  Edit,
  MessageBox,
  Connection,
  HelpFilled,
  InfoFilled,
  ChatDotRound
} from "@element-plus/icons-vue";
import { ROLES } from "@/constants/role-constants";
import {
  CONTROL_PANEL_INDEX_PATH
} from '@/constants/routes/base';
import {
  USER,
  USER_POST_MANAGE_PATH,
  USER_POST_LIST_PATH,
  USER_POST_EDIT_PATH,
  USER_POST_VERSION_MANAGE_PATH,
  USER_POST_ATTACHMENT_LIST_PATH,
  USER_COMMENT_MANAGE_PATH,
  USER_FAVORITE_MANAGE_PATH,
  USER_MESSAGE_MANAGE_PATH,
  USER_REPORT_MANAGE_PATH,
  USER_PROFILE_PATH,
  USER_AVATAR_PATH,
  USER_PASSWORD_PATH,
  USER_SETTING_PATH,
  USER_MESSAGE_DETAIL_PATH
} from '@/constants/routes/user';

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
import { icon } from "@fortawesome/fontawesome-svg-core";

// 用户菜单配置
export const userMenus = [
  {
    icon: DataAnalysis,
    title: '首页',
    index: CONTROL_PANEL_INDEX_PATH
  },
  {
    icon: Files,
    title: '内容管理',
    index: USER,
    children: [
      {
        icon: Files,
        title: '文章管理',
        index: USER_POST_MANAGE_PATH,
        roles: [ROLES.USER],
        children: [
          {
            icon: List,
            title: '文章列表',
            index: USER_POST_LIST_PATH,
            roles: [ROLES.USER]
          },
          {
            icon: Edit,
            title: '修改文章',
            index: USER_POST_EDIT_PATH,
            roles: [ROLES.USER]
          },
          {
            icon: Notebook,
            title: '文章版本管理',
            index: USER_POST_VERSION_MANAGE_PATH,
            roles: [ROLES.USER]
          },
          {
            icon: Document,
            title: '文章附件管理',
            index: USER_POST_ATTACHMENT_LIST_PATH,
            roles: [ROLES.USER]
          }
        ]
      },
      {
        icon: Comment,
        title: '评论管理',
        index: USER_COMMENT_MANAGE_PATH,
        roles: [ROLES.USER]
      },
      {
        icon: StarFilled,
        title: '收藏夹',
        index: USER_FAVORITE_MANAGE_PATH,
        roles: [ROLES.USER]
      }
    ]
  },
  {
    icon: Bell,
    title: '消息中心',
    index: USER_MESSAGE_MANAGE_PATH,
    children: [
      {
        icon: Comment,
        title: '消息管理',
        index: USER_MESSAGE_MANAGE_PATH,
        roles: [ROLES.USER]
      },
      {
        icon: ChatDotRound,
        title: '聊天',
        index: USER_MESSAGE_DETAIL_PATH,
        roles: [ROLES.USER]
      },
      {
        icon: Bell,
        title: '举报管理',
        index: USER_REPORT_MANAGE_PATH,
        roles: [ROLES.USER]
      }
    ]
  },
  {
    icon: User,
    title: '个人中心',
    index: USER_PROFILE_PATH,
    children: [
      {
        icon: Edit,
        title: '个人资料',
        index: USER_PROFILE_PATH,
        roles: [ROLES.USER]
      },
      {
        icon: Key,
        title: '修改密码',
        index: USER_PASSWORD_PATH,
        roles: [ROLES.USER]
      },
      {
        icon: Connection,
        title: '个人设置',
        index: USER_SETTING_PATH,
        roles: [ROLES.USER]
      }
    ]
  }
];

// 管理员菜单配置
export const adminMenus = [
  {
    icon: Setting,
    title: '系统管理',
    index: ADMIN,
    children: [
      {
        icon: Files,
        title: '内容管理',
        index: ADMIN_CONTENT,
        children: [
          {
            icon: Files,
            title: '文章管理',
            index: ADMIN_POST_MANAGE_PATH,
            roles: [ROLES.POST_MANAGER, ROLES.SYSTEM_ADMINISTRATOR]
          },
          {
            icon: Notebook,
            title: '版本管理',
            index: ADMIN_POST_VERSION_MANAGE_PATH,
            roles: [ROLES.SYSTEM_ADMINISTRATOR]
          },
          {
            icon: Orange,
            title: '分类管理',
            index: ADMIN_CATEGORY_MANAGE_PATH,
            roles: [ROLES.CATEGORY_MANAGER, ROLES.SYSTEM_ADMINISTRATOR]
          },
          {
            icon: CollectionTag,
            title: '标签管理',
            index: ADMIN_TAG_MANAGE_PATH,
            roles: [ROLES.TAG_MANAGER, ROLES.SYSTEM_ADMINISTRATOR]
          }
        ]
      },
      {
        icon: MagicStick,
        title: '用户行为管理',
        index: ADMIN_USER_ACTIVITY_MANAGE_PATH,
        roles: [ROLES.SYSTEM_ADMINISTRATOR, ROLES.USER_ACTIVITY_MANAGER, ROLES.COMMENT_MANAGER],
        children: [
          {
            icon: MessageBox,
            title: '评论管理',
            index: ADMIN_COMMENT_MANAGE_PATH,
            roles: [ROLES.USER_ACTIVITY_MANAGER, ROLES.COMMENT_MANAGER, ROLES.SYSTEM_ADMINISTRATOR]
          },
          {
            icon: StarFilled,
            title: '收藏管理',
            index: ADMIN_FAVORITE_MANAGE_PATH,
            roles: [ROLES.USER_ACTIVITY_MANAGER, ROLES.SYSTEM_ADMINISTRATOR]
          },
          {
            icon: StarFilled,
            title: '点赞管理',
            index: ADMIN_LIKE_MANAGE_PATH,
            roles: [ROLES.USER_ACTIVITY_MANAGER, ROLES.SYSTEM_ADMINISTRATOR]
          },
          {
            icon: Bell,
            title: '举报管理',
            index: ADMIN_REPORT_MANAGE_PATH,
            roles: [ROLES.REPORT_MANAGER, ROLES.SYSTEM_ADMINISTRATOR]
          }
        ]
      },
      {
        icon: UserFilled,
        title: '用户权限管理',
        index: ADMIN_ROLE,
        roles: [
          ROLES.USER_USER_MANAGER,
          ROLES.ROLE_MANAGER,
          ROLES.SYSTEM_ADMINISTRATOR,
          ROLES.PERMISSION_MAPPING_MANAGER,
          ROLES.PERMISSION_MANAGER
        ],
        children: [
          {
            icon: User,
            title: '用户管理',
            index: ADMIN_USER_MANAGE_PATH,
            roles: [ROLES.SYSTEM_ADMINISTRATOR, ROLES.USER_USER_MANAGER]
          },
          {
            icon: User,
            title: '角色管理',
            index: ADMIN_ROLE_MANAGE_PATH,
            roles: [ROLES.ROLE_MANAGER, ROLES.SYSTEM_ADMINISTRATOR]
          },
          {
            icon: HelpFilled,
            title: '用户角色管理',
            index: ADMIN_ROLE_USER_MANAGE_PATH,
            roles: [ROLES.USER_ROLE_MAPPING_MANAGER, ROLES.SYSTEM_ADMINISTRATOR]
          },
          {
            icon: SetUp,
            title: '角色权限配置',
            index: ADMIN_ROLE_PERMISSION_MANAGE_PATH,
            roles: [ROLES.PERMISSION_MAPPING_MANAGER, ROLES.SYSTEM_ADMINISTRATOR]
          },
          {
            icon: InfoFilled,
            title: '权限管理',
            index: ADMIN_PERMISSION_MANAGE_PATH,
            roles: [ROLES.PERMISSION_MANAGER, ROLES.SYSTEM_ADMINISTRATOR]
          },
          {
            icon: Setting,
            title: '用户设置管理',
            index: ADMIN_USER_SETTING_MANAGE_PATH,
            roles: [ROLES.SYSTEM_ADMINISTRATOR]
          }
        ]
      },
      {
        icon: Comment,
        title: '消息管理',
        index: ADMIN_MESSAGE_MANAGE_PATH,
        roles: [ROLES.SYSTEM_ADMINISTRATOR, ROLES.MESSAGE_MANAGER]
      }
    ]
  }
];

// 生成完整的菜单配置
export function generateMenus(userStore) {
  const menus = [...userMenus];

  if (userStore && userStore.hasRole) {
    const adminMenu = adminMenus[0];
    const accessibleChildren = adminMenu.children.filter(item => {
      const hasAccess = item.roles && item.roles.some(role => userStore.hasRole(role));

      if (hasAccess && item.children) {
        item.children = item.children.filter(child =>
          child.roles && child.roles.some(role => userStore.hasRole(role))
        );
      }

      return hasAccess;
    });

    if (accessibleChildren.length > 0) {
      menus.push({
        ...adminMenu,
        children: accessibleChildren
      });
    }
  }

  return menus;
}