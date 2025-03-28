// 登录
export const LOGIN_PATH = '/login';

// 控制台
export const CONTROL_PANEL_PATH = '/control';

// 控制台首页
export const CONTROL_PANEL_INDEX_PATH = '/control/home';


export const MESSAGE_MANAGE_PATH = '/control/message/manage';
export const USER_BEHAVIOR_MANAGE_PATH = `/control/behavior/manage`;
export const REPORT_MANAGE_PATH = '/control/report/manage';

// 文章设置
export const POST_MANAGE_PATH = '/control/post';
export const POST_LIST_PATH = `${POST_MANAGE_PATH}/list`;
export const POST_VERSION_MANAGE_PATH = `${POST_MANAGE_PATH}/version/manage`;
export const POST_EDIT_PATH = `${POST_MANAGE_PATH}/edit/:id`;
export const POST_ATTACHMENT_LIST_PATH = `${POST_MANAGE_PATH}/attachment/list`;
export const COMMENT_MANAGE_PATH = `${POST_MANAGE_PATH}/comment/manage`;

// 个人信息设置
export const USER_MANAGE = '/control/user';
export const USER_PROFILE_PATH = `${USER_MANAGE}/profile`;
export const USER_AVATAR_PATH = `${USER_MANAGE}/avatar`;
export const USER_PASSWORD_PATH = `${USER_MANAGE}/password`;
export const USER_SETTING_PATH = `${USER_MANAGE}/setting`;
export const FAVORITE_MANAGE_PATH = `${USER_MANAGE}/favorite/manage`;

// 系统设置
export const SYSTEM_MANAGE_PATH = '/control/system'
export const CATEGORY_MANAGE_PATH = `${SYSTEM_MANAGE_PATH}/category/manage`;
export const TAG_MANAGE_PATH = `${SYSTEM_MANAGE_PATH}/tag/manage`;

// 用户设置
export const ROLE_MANAGE = '/control/role'
export const ROLE_MANAGE_PATH = `${ROLE_MANAGE}/manage`;
export const ROLE_PERMISSION_MANAGE_PATH = `${ROLE_MANAGE}/permission/manage`;
export const ROLE_USER_MANAGE_PATH = `${ROLE_MANAGE}/user/manage`;
export const PERMISSION_MANAGE_PATH = `${ROLE_MANAGE}/permission/list`;
