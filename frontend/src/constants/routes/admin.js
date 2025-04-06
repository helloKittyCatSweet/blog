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
export const ADMIN_REPORT_MANAGE_PATH = `${ADMIN}/report/manage`

// 用户权限管理模块
export const ADMIN_ROLE = `${ADMIN}/role`
export const ADMIN_USER_MANAGE_PATH = `${ADMIN_ROLE}/user/manage`
export const ADMIN_ROLE_MANAGE_PATH = `${ADMIN_ROLE}/role/manage`
export const ADMIN_ROLE_PERMISSION_MANAGE_PATH = `${ADMIN_ROLE}/permission/mapping/manage`


// 消息管理
export const ADMIN_MESSAGE_MANAGE_PATH = `${ADMIN}/message/manage`