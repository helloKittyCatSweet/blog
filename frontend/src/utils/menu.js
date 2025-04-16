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
  ChatDotRound,
  Message
} from "@element-plus/icons-vue";
import { ROLES } from "@/constants/role-constants";
import {
  CONTROL_PANEL_INDEX_PATH
} from '@/constants/routes/base';
import {
  USER,
  USER_POST_MANAGE_PATH,
  USER_POST_LIST_PATH,
  USER_POST_CREATE_PATH,
  USER_POST_ATTACHMENT_LIST_PATH,
  USER_POST_COMMENT_MANAGE_PATH,
  USER_COMMENT_MANAGE_PATH,
  USER_FAVORITE_MANAGE_PATH,
  USER_MESSAGE_MANAGE_PATH,
  USER_REPORT_MANAGE_PATH,
  USER_PROFILE_PATH,
  USER_PASSWORD_PATH,
  USER_SETTING_PATH,
  USER_MESSAGE_DETAIL_PATH,
  USER_LIKE_REPORT,
  USER_FOLLOW_MANAGE_PATH
} from '@/constants/routes/user';

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
  ADMIN_MESSAGE_MANAGE_PATH,
  ADMIN_SYSTEM_PATH,
  ADMIN_SYSTEM_MESSAGE_PATH
} from '@/constants/routes/admin';


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
            title: '写文章',
            index: USER_POST_CREATE_PATH,
            roles: [ROLES.USER]
          },
          {
            icon: Document,
            title: '文章附件管理',
            index: USER_POST_ATTACHMENT_LIST_PATH,
            roles: [ROLES.USER]
          },
          {
            icon: Comment,
            title: '文章评论管理',
            index: USER_POST_COMMENT_MANAGE_PATH,
            roles: [ROLES.USER]
          },
        ]
      },
      {
        icon: Comment,
        title: '我的评论',
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
    icon: Message,
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
        icon: InfoFilled,
        title: '点赞/收藏',
        index: USER_LIKE_REPORT,
      }
    ]
  },
  {
    icon: Bell,
    title: '举报管理',
    index: USER_REPORT_MANAGE_PATH,
    roles: [ROLES.USER]
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
      },
      {
        icon: UserFilled,
        title: '关注管理',
        index: USER_FOLLOW_MANAGE_PATH,
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
        roles: [ROLES.SYSTEM_ADMINISTRATOR, ROLES.POST_MANAGER, ROLES.CATEGORY_MANAGER, ROLES.TAG_MANAGER],
        children: [
          {
            icon: Files,
            title: '文章管理',
            index: ADMIN_POST_MANAGE_PATH,
            roles: [ROLES.POST_MANAGER, ROLES.SYSTEM_ADMINISTRATOR]
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
        icon: Bell,
        title: '举报管理',
        index: ADMIN_REPORT_MANAGE_PATH,
        roles: [ROLES.REPORT_MANAGER, ROLES.SYSTEM_ADMINISTRATOR]
      }

      ,
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
          /*
          {
            icon: SetUp,
            title: '角色权限配置',
            index: ADMIN_ROLE_PERMISSION_MANAGE_PATH,
            roles: [ROLES.PERMISSION_MAPPING_MANAGER, ROLES.SYSTEM_ADMINISTRATOR]
          },
          */
        ]
      },
      {
        icon: Comment,
        title: '消息管理',
        index: ADMIN_MESSAGE_MANAGE_PATH,
        roles: [ROLES.SYSTEM_ADMINISTRATOR, ROLES.MESSAGE_MANAGER]
      },
      {
        icon: Setting,
        title: '系统管理',
        index: ADMIN_SYSTEM_PATH,
        roles: [ROLES.SYSTEM_ADMINISTRATOR, ROLES.MESSAGE_MANAGER],
        children: [
          {
            icon: MessageBox,
            title: '系统消息管理',
            index: ADMIN_SYSTEM_MESSAGE_PATH,
            roles: [ROLES.SYSTEM_ADMINISTRATOR]
          }
        ]
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