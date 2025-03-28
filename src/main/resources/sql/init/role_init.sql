-- Common User
INSERT INTO roles (role_name, description, administrator_name) VALUES ('普通用户', '普通用户', 'ROLE_USER');
-- Category Manager
INSERT INTO roles (role_name, description, administrator_name) VALUES ('分类管理员', '管理分类', 'ROLE_CATEGORY_MANAGER');
-- roles Permission Mapping Manager
INSERT INTO roles (role_name, description, administrator_name) VALUES ('角色权限映射管理', '管理角色权限映射' , 'ROLE_PERMISSION_MAPPING_MANAGER');
-- Tag Manager
INSERT INTO roles (role_name, description, administrator_name) VALUES ('标签管理', '管理标签', 'ROLE_TAG_MANAGER');
-- UserActivity Manager
INSERT INTO roles (role_name, description, administrator_name) VALUES ('用户活动管理', '管理用户活动', 'ROLE_USER_ACTIVITY_MANAGER');
-- User roles Mapping Manager
INSERT INTO roles (role_name, description, administrator_name) VALUES ('用户角色映射管理', '管理用户角色映射', 'ROLE_USER_ROLE_MAPPING_MANAGER');
-- Comment Manager
INSERT INTO roles (role_name, description, administrator_name) VALUES ('评论管理', '管理评论', 'ROLE_COMMENT_MANAGER');
-- Message Manager
INSERT INTO roles (role_name, description, administrator_name) VALUES ('消息管理', '管理消息', 'ROLE_MESSAGE_MANAGER');
-- Report Manager
INSERT INTO roles (role_name, description, administrator_name) VALUES ('举报管理', '管理举报', 'ROLE_REPORT_MANAGER');
-- roles Manager
INSERT INTO roles (role_name, description, administrator_name) VALUES ('角色管理', '管理角色', 'ROLE_THE_ROLE_MANAGER');
-- System Administrator
INSERT INTO roles (role_name, description, administrator_name) VALUES ('系统管理员', '系统管理员', 'ROLE_SYSTEM_ADMINISTRATOR');