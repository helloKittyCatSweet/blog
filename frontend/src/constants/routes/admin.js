import { CONTROL_PANEL_PATH } from './base'

// 管理员根路径
export const ADMIN = `${CONTROL_PANEL_PATH}/admin`

// 内容管理模块
export const ADMIN_CONTENT = `${ADMIN}/content`
export const ADMIN_POST_MANAGE_PATH = `${ADMIN_CONTENT}/post/manage`
export const ADMIN_POST_VERSION_MANAGE_PATH = `${ADMIN_CONTENT}/post/version/manage`
export const ADMIN_CATEGORY_MANAGE_PATH = `${ADMIN_CONTENT}/category/manage`
export const ADMIN_TAG_MANAGE_PATH = `${ADMIN_CONTENT}/tag/manage`

// 用户行为管理模块
export const ADMIN_USER_ACTIVITY_MANAGE_PATH = `${ADMIN}/user/activity`
export const ADMIN_COMMENT_MANAGE_PATH = `${ADMIN_USER_ACTIVITY_MANAGE_PATH}/comment/manage`
export const ADMIN_FAVORITE_MANAGE_PATH = `${ADMIN_USER_ACTIVITY_MANAGE_PATH}/favorite/manage`
export const ADMIN_LIKE_MANAGE_PATH = `${ADMIN_USER_ACTIVITY_MANAGE_PATH}/like/manage`
export const ADMIN_REPORT_MANAGE_PATH = `${ADMIN_USER_ACTIVITY_MANAGE_PATH}/report/manage`

// 用户权限管理模块
export const ADMIN_ROLE = `${ADMIN}/role`
export const ADMIN_USER_MANAGE_PATH = `${ADMIN_ROLE}/user/manage`
export const ADMIN_ROLE_MANAGE_PATH = `${ADMIN_ROLE}/role/manage`
export const ADMIN_ROLE_USER_MANAGE_PATH = `${ADMIN_ROLE}/user/role/manage`
export const ADMIN_ROLE_PERMISSION_MANAGE_PATH = `${ADMIN_ROLE}/permission/mapping/manage`
export const ADMIN_PERMISSION_MANAGE_PATH = `${ADMIN_ROLE}/permission/manage`
export const ADMIN_USER_SETTING_MANAGE_PATH = `${ADMIN_ROLE}/user/setting/manage`

// 消息管理
export const ADMIN_MESSAGE_MANAGE_PATH = `${ADMIN}/message/manage`