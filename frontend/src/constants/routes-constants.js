// // 登录
// export const LOGIN_PATH = '/login';

// // 控制台
// export const CONTROL_PANEL_PATH = '/control';

// // 控制台首页
// export const CONTROL_PANEL_INDEX_PATH = '/control/home';

// /**
//  * 用户相关
//  */
// // 用户管理
// export const USER = `${CONTROL_PANEL_PATH}/my`

// // 评论管理
// export const USER_COMMENT_MANAGE = `${CONTROL_PANEL_PATH}/comment/manage`;

// // 文章管理
// export const USER_POST_MANAGE_PATH = `${USER}/post/manage`;
// export const USER_POST_LIST_PATH = `${USER_POST_MANAGE_PATH}/list`;
// export const USER_POST_VERSION_MANAGE_PATH = `${USER_POST_MANAGE_PATH}/version/manage`;
// export const USER_POST_EDIT_PATH = `${USER_POST_MANAGE_PATH}/edit/:id`;
// export const USER_POST_ATTACHMENT_LIST_PATH = `${USER_POST_MANAGE_PATH}/attachment/list`;
// export const USER_COMMENT_MANAGE_PATH = `${USER_POST_MANAGE_PATH}/comment/manage`;

// // 举报管理
// export const USER_REPORT_MANAGE_PATH = `${USER}/report/manage`;

// // 收藏夹管理
// export const USER_FAVORITE_MANAGE_PATH = `${USER}/favorite/manage`;

// // 个人信息设置
// export const USER_PROFILE_PATH = `${USER}/profile`;
// export const USER_AVATAR_PATH = `${USER}/avatar`;
// export const USER_PASSWORD_PATH = `${USER}/password`;
// export const USER_SETTING_PATH = `${USER}/setting`;

// // 消息管理
// export const USER_MESSAGE_MANAGE_PATH = `${USER}/message/manage`;

// /**
//  * 管理员相关
//  */
// // 管理员控制台
// export const ADMIN = `${CONTROL_PANEL_PATH}/admin`;


// // 文章管理
// export const ADMIN_POST_MANAGE_PATH = `${ADMIN}/post/manage`;

// // 举报管理
// export const ADMIN_REPORT_MANAGE_PATH = `${ADMIN}/report/manage`;

// // 文章版本管理
// export const ADMIN_POST_VERSION_MANAGE_PATH = `${ADMIN_POST_MANAGE_PATH}/version/manage`;

// // 用户活动管理
// export const ADMIN_USER_ACTIVITY_MANAGE_PATH = `${ADMIN}/user/activity`;
// // 收藏夹管理
// export const ADMIN_FAVORITE_MANAGE_PATH = `${ADMIN_USER_ACTIVITY_MANAGE_PATH}/favorite/manage`;
// // 评论管理
// export const ADMIN_COMMENT_MANAGE_PATH = `${ADMIN_USER_ACTIVITY_MANAGE_PATH}/comment/manage`;
// // 点赞管理
// export const ADMIN_LIKE_MANAGE_PATH = `${ADMIN_USER_ACTIVITY_MANAGE_PATH}/like/manage`;

// // 用户设置
// export const ADMIN_ROLE = `${ADMIN}/role`;
// // 用户管理
// export const ADMIN_USER_MANAGE_PATH = `${ADMIN_ROLE}/user/manage`;
// // 角色管理
// export const ADMIN_ROLE_MANAGE_PATH = `${ADMIN_ROLE}/user/manage`;
// // 角色权限映射管理
// export const ADMIN_ROLE_PERMISSION_MANAGE_PATH = `${ADMIN_ROLE}/mapping/permission/manage`;
// // 用户角色管理
// export const ADMIN_ROLE_USER_MANAGE_PATH = `${ADMIN_ROLE}/user/manage`;
// // 权限管理
// export const ADMIN_PERMISSION_MANAGE_PATH = `${ADMIN_ROLE}/permission/manage`;
// // 用户设置管理
// export const ADMIN_USER_SETTING_MANAGE_PATH = `${ADMIN_ROLE}/user/setting/manage`;

// // 消息管理
// export const ADMIN_MESSAGE_MANAGE_PATH = `${ADMIN}/message/manage`;
// // 分类管理
// export const ADMIN_CATEGORY_MANAGE_PATH = `${ADMIN}/category/manage`;
// // 标签管理
// export const ADMIN_TAG_MANAGE_PATH = `${ADMIN}/tag/manage`;

export * from './routes/base'
export * from './routes/user'
export * from './routes/admin'