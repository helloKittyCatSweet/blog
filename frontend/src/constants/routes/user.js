import { CONTROL_PANEL_PATH } from './base'

// 用户根路径
export const USER = `${CONTROL_PANEL_PATH}/my`

// 内容管理模块
export const USER_CONTENT = `${USER}/content`
export const USER_POST_MANAGE_PATH = `${USER_CONTENT}/post`
export const USER_POST_LIST_PATH = `${USER_POST_MANAGE_PATH}/list`
export const USER_POST_CREATE_PATH = `${USER_POST_MANAGE_PATH}/create`;
export const USER_POST_EDIT_PATH = `${USER_POST_MANAGE_PATH}/edit`;
export const USER_POST_VERSION_MANAGE_PATH = `${USER_POST_MANAGE_PATH}/version/manage`
export const USER_POST_ATTACHMENT_LIST_PATH = `${USER_POST_MANAGE_PATH}/attachment/list`
export const USER_POST_COMMENT_MANAGE_PATH = `${USER_POST_MANAGE_PATH}/comment/manage`
export const USER_COMMENT_MANAGE_PATH = `${USER_CONTENT}/comment/manage`
export const USER_FAVORITE_MANAGE_PATH = `${USER_CONTENT}/favorite/manage`

// 消息中心
export const USER_MESSAGE = `${USER}/message`
export const USER_MESSAGE_MANAGE_PATH = `${USER_MESSAGE}/manage`
export const USER_LIKE_REPORT = `${USER_MESSAGE}/like`
// 修改为可选
export const USER_MESSAGE_DETAIL_PATH = `${USER_MESSAGE}/chat`

// 举报中心
export const USER_REPORT_MANAGE_PATH = `${USER}/report/manage`

// 个人中心
export const USER_PROFILE = `${USER}/profile`
export const USER_PROFILE_PATH = `${USER_PROFILE}/info`
export const USER_PASSWORD_PATH = `${USER_PROFILE}/password`
export const USER_SETTING_PATH = `${USER_PROFILE}/setting`