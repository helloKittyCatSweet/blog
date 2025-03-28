/*
 Navicat Premium Dump SQL

 Source Server         : localmysql
 Source Server Type    : MySQL
 Source Server Version : 80033 (8.0.33)
 Source Host           : localhost:3306
 Source Schema         : blog

 Target Server Type    : MySQL
 Target Server Version : 80033 (8.0.33)
 File Encoding         : 65001

 Date: 23/03/2025 15:30:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `parent_category_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`category_id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES (1, 'C++', 'C++模块', 0);
INSERT INTO `categories` VALUES (2, 'Java', 'Java模块', 0);
INSERT INTO `categories` VALUES (3, 'C++测试', NULL, 6);
INSERT INTO `categories` VALUES (4, 'C++开发', NULL, 6);
INSERT INTO `categories` VALUES (5, '使用vscode', NULL, 9);

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `user_id` int NOT NULL,
  `content` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `likes` int NULL DEFAULT 0,
  `parent_comment_id` int NULL DEFAULT NULL,
  `parent_comment` varbinary(255) NULL DEFAULT NULL,
  `replies` varbinary(255) NULL DEFAULT NULL,
  PRIMARY KEY (`comment_id`) USING BTREE,
  INDEX `post_id`(`post_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `parent_comment_id`(`parent_comment_id` ASC) USING BTREE,
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comments
-- ----------------------------
INSERT INTO `comments` VALUES (1, 1, 4, '这篇文章针不戳', '2025-03-21 12:38:45', 1, 0, NULL, NULL);
INSERT INTO `comments` VALUES (2, 1, 5, '这是对楼上的回复', '2025-03-21 15:14:01', 0, 1, NULL, NULL);
INSERT INTO `comments` VALUES (3, 1, 4, '你像个人机', '2025-03-21 15:14:19', 0, 2, NULL, NULL);
INSERT INTO `comments` VALUES (4, 2, 4, '这是另外一篇文章的评论', '2025-03-21 15:14:41', 0, 0, NULL, NULL);
INSERT INTO `comments` VALUES (5, 1, 4, '这是第一篇博客的独立的评论', '2025-03-21 15:17:26', 0, 0, NULL, NULL);

-- ----------------------------
-- Table structure for favorites
-- ----------------------------
DROP TABLE IF EXISTS `favorites`;
CREATE TABLE `favorites`  (
  `favorite_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `post_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`favorite_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `post_id`(`post_id` ASC) USING BTREE,
  CONSTRAINT `favorites_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `favorites_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorites
-- ----------------------------
INSERT INTO `favorites` VALUES (1, 4, 2, '2025-03-21 10:36:27');

-- ----------------------------
-- Table structure for messages
-- ----------------------------
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages`  (
  `message_id` int NOT NULL AUTO_INCREMENT,
  `sender_id` int NOT NULL,
  `receiver_id` int NOT NULL,
  `content` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `is_read` tinyint(1) NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `parent_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `sender_id`(`sender_id` ASC) USING BTREE,
  INDEX `receiver_id`(`receiver_id` ASC) USING BTREE,
  CONSTRAINT `messages_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `messages_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of messages
-- ----------------------------
INSERT INTO `messages` VALUES (1, 4, 5, '你好呀，这是一条消息', 0, '2025-03-21 10:51:22', 0);

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions`  (
  `permission_id` int NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  PRIMARY KEY (`permission_id`) USING BTREE,
  UNIQUE INDEX `UKpnvtwliis6p05pn6i3ndjrqt2`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 70 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permissions
-- ----------------------------
--INSERT INTO `permissions` VALUES (1, '2025-03-23 11:02:04', '用户可以创建新的分类', '创建分类');
--INSERT INTO `permissions` VALUES (2, '2025-03-23 11:02:04', '用户可以编辑自己的分类', '编辑分类');
--INSERT INTO `permissions` VALUES (3, '2025-03-23 11:02:04', '用户可以查看所有人创造的分类', '查看分类');
--INSERT INTO `permissions` VALUES (4, '2025-03-23 11:02:04', '用户可以删除自己的分类', '删除分类');
--INSERT INTO `permissions` VALUES (5, '2025-03-23 11:02:04', '用户可以创建新的标签', '创建标签');
--INSERT INTO `permissions` VALUES (6, '2025-03-23 11:02:04', '用户可以编辑自己的标签', '编辑标签');
--INSERT INTO `permissions` VALUES (7, '2025-03-23 11:02:04', '用户可以查看所有人的标签', '查看标签');
--INSERT INTO `permissions` VALUES (8, '2025-03-23 11:02:04', '用户可以删除自己的标签', '删除标签');
--INSERT INTO `permissions` VALUES (9, '2025-03-23 11:02:04', '用户可以创建自己的活动', '创建用户活动');
--INSERT INTO `permissions` VALUES (10, '2025-03-23 11:02:04', '用户可以查看用户所属的角色', '查看用户所属的角色');
--INSERT INTO `permissions` VALUES (11, '2025-03-23 11:02:04', '用户可以查看自己的评论', '查看评论');
--INSERT INTO `permissions` VALUES (12, '2025-03-23 11:02:04', '用户可以编辑自己的评论', '编辑评论');
--INSERT INTO `permissions` VALUES (13, '2025-03-23 11:02:04', '用户可以查看所有人的评论', '查看所有人的评论');
--INSERT INTO `permissions` VALUES (14, '2025-03-23 11:02:04', '用户可以删除自己发表的评论', '删除发表的评论');
--INSERT INTO `permissions` VALUES (15, '2025-03-23 11:02:04', '用户可以删除自己文章下的评论', '删除文章评论');
--INSERT INTO `permissions` VALUES (16, '2025-03-23 11:02:04', '用户可以创建自己的收藏', '创建收藏');
--INSERT INTO `permissions` VALUES (17, '2025-03-23 11:02:04', '用户可以编辑自己的收藏', '编辑收藏');
--INSERT INTO `permissions` VALUES (18, '2025-03-23 11:02:04', '用户可以查看自己的收藏', '查看收藏');
--INSERT INTO `permissions` VALUES (19, '2025-03-23 11:02:04', '用户可以删除自己的收藏', '删除收藏');
--INSERT INTO `permissions` VALUES (20, '2025-03-23 11:02:04', '用户可以创建自己的消息', '创建消息');
--INSERT INTO `permissions` VALUES (21, '2025-03-23 11:02:04', '用户可以编辑自己的消息', '编辑消息');
--INSERT INTO `permissions` VALUES (22, '2025-03-23 11:02:04', '用户可以查看自己的消息', '查看消息');
--INSERT INTO `permissions` VALUES (23, '2025-03-23 11:02:04', '用户可以删除自己的消息', '删除消息');
--INSERT INTO `permissions` VALUES (24, '2025-03-23 11:02:04', '用户可以创建自己的附件', '创建附件');
--INSERT INTO `permissions` VALUES (25, '2025-03-23 11:02:04', '用户可以编辑自己的附件', '编辑附件');
--INSERT INTO `permissions` VALUES (26, '2025-03-23 11:02:04', '用户可以删除查看所有人的附件', '查看附件');
--INSERT INTO `permissions` VALUES (27, '2025-03-23 11:02:04', '用户可以删除自己的附件', '删除附件');
--INSERT INTO `permissions` VALUES (28, '2025-03-23 11:02:04', '用户可以创建自己的版本', '创建版本');
--INSERT INTO `permissions` VALUES (29, '2025-03-23 11:02:04', '用户可以编辑自己的版本', '编辑版本');
--INSERT INTO `permissions` VALUES (30, '2025-03-23 11:02:04', '用户可以查看自己的版本', '查看版本');
--INSERT INTO `permissions` VALUES (31, '2025-03-23 11:02:04', '用户可以删除自己的版本', '删除版本');
--INSERT INTO `permissions` VALUES (32, '2025-03-23 11:02:04', '用户可以创建自己的举报', '创建举报');
--INSERT INTO `permissions` VALUES (33, '2025-03-23 11:02:04', '用户可以编辑自己的举报', '编辑举报');
--INSERT INTO `permissions` VALUES (34, '2025-03-23 11:02:04', '用户可以查看自己的举报', '查看举报');
--INSERT INTO `permissions` VALUES (35, '2025-03-23 11:02:04', '用户可以删除自己的举报', '删除举报');
--INSERT INTO `permissions` VALUES (36, '2025-03-23 11:02:04', '用户可以创建自己的信息', '创建用户');
--INSERT INTO `permissions` VALUES (37, '2025-03-23 11:02:04', '用户可以编辑自己的信息', '编辑用户');
--INSERT INTO `permissions` VALUES (38, '2025-03-23 11:02:04', '用户可以查看所有人的信息', '查看所有用户');
--INSERT INTO `permissions` VALUES (39, '2025-03-23 11:02:04', '用户可以删除自己的信息', '删除用户');
--INSERT INTO `permissions` VALUES (40, '2025-03-23 11:02:04', '用户可以编辑所有人的分类', '编辑所有分类');
--INSERT INTO `permissions` VALUES (41, '2025-03-23 11:02:04', '用户可以删除所有人的分类', '删除所有分类');
--INSERT INTO `permissions` VALUES (42, '2025-03-23 11:02:04', '用户可以创建角色所包含的权限', '创建角色所包含的权限');
--INSERT INTO `permissions` VALUES (43, '2025-03-23 11:02:04', '用户可以编辑角色所包含的权限', '编辑角色所包含的权限');
--INSERT INTO `permissions` VALUES (44, '2025-03-23 11:02:04', '用户可以查看角色所包含的权限', '查看角色所包含的权限');
--INSERT INTO `permissions` VALUES (45, '2025-03-23 11:02:04', '用户可以删除角色所包含的权限', '删除角色所包含的权限');
--INSERT INTO `permissions` VALUES (46, '2025-03-23 11:02:04', '用户可以编辑所有人的标签', '编辑所有标签');
--INSERT INTO `permissions` VALUES (47, '2025-03-23 11:02:04', '用户可以删除所有人的标签', '删除所有标签');
--INSERT INTO `permissions` VALUES (48, '2025-03-23 11:02:04', '用户可以编辑自己的活动', '编辑用户活动');
--INSERT INTO `permissions` VALUES (49, '2025-03-23 11:02:04', '用户可以查看自己的活动', '查看用户活动');
--INSERT INTO `permissions` VALUES (50, '2025-03-23 11:02:04', '用户可以删除自己的活动', '删除用户活动');
--INSERT INTO `permissions` VALUES (51, '2025-03-23 11:02:04', '用户可以创建用户所属的角色', '创建用户所属的角色');
--INSERT INTO `permissions` VALUES (52, '2025-03-23 11:02:04', '用户可以编辑用户所属的角色', '编辑用户所属的角色');
--INSERT INTO `permissions` VALUES (53, '2025-03-23 11:02:04', '用户可以删除用户所属的角色', '删除用户所属的角色');
--INSERT INTO `permissions` VALUES (54, '2025-03-23 11:02:04', '用户可以编辑所有人的评论', '编辑所有评论');
--INSERT INTO `permissions` VALUES (55, '2025-03-23 11:02:04', '用户可以删除所有人的评论', '删除所有评论');
--INSERT INTO `permissions` VALUES (56, '2025-03-23 11:02:04', '用户可以删除所有人的消息', '删除所有消息');
--INSERT INTO `permissions` VALUES (57, '2025-03-23 11:02:04', '用户可以创建新的权限', '创建权限');
--INSERT INTO `permissions` VALUES (58, '2025-03-23 11:02:04', '用户可以编辑自己的权限', '编辑权限');
--INSERT INTO `permissions` VALUES (59, '2025-03-23 11:02:04', '用户可以查看自己的权限', '查看权限');
--INSERT INTO `permissions` VALUES (60, '2025-03-23 11:02:04', '用户可以删除自己的权限', '删除权限');
--INSERT INTO `permissions` VALUES (61, '2025-03-23 11:02:04', '审核用户的举报', '审核举报');
--INSERT INTO `permissions` VALUES (64, '2025-03-23 11:03:42', '用户可以创建新的角色', '创建角色');
--INSERT INTO `permissions` VALUES (65, '2025-03-23 11:03:42', '用户可以编辑自己的角色', '编辑角色');
--INSERT INTO `permissions` VALUES (66, '2025-03-23 11:03:42', '用户可以查看自己的角色', '查看角色');
--INSERT INTO `permissions` VALUES (67, '2025-03-23 11:03:42', '用户可以删除自己的角色', '删除角色');
--INSERT INTO `permissions` VALUES (68, '2025-03-23 11:03:42', '用户可以删除自己的信息', '删除所有用户');
--INSERT INTO `permissions` VALUES (69, '2025-03-23 11:03:42', '用户可以激活的账户', '是否激活账户');

-- ----------------------------
-- Table structure for post_attachments
-- ----------------------------
DROP TABLE IF EXISTS `post_attachments`;
CREATE TABLE `post_attachments`  (
  `attachment_id` int NOT NULL AUTO_INCREMENT,
  `attachment_name` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `attachment_type` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `post_id` int NULL DEFAULT NULL,
  `url` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`attachment_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_attachments
-- ----------------------------
INSERT INTO `post_attachments` VALUES (1, 'tmp6654119341685452818-计算机科学与技术学院加分补录.zip', 'application/zip', '2025-03-22 17:04:59', 1, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/archives/1/1742634298956-tmp6654119341685452818-计算机科学与技术学院加分补录.zip');

-- ----------------------------
-- Table structure for post_categories
-- ----------------------------
DROP TABLE IF EXISTS `post_categories`;
CREATE TABLE `post_categories`  (
  `post_id` int NOT NULL,
  `category_id` int NOT NULL,
  PRIMARY KEY (`post_id`, `category_id`) USING BTREE,
  INDEX `category_id`(`category_id` ASC) USING BTREE,
  CONSTRAINT `post_categories_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `post_categories_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_categories
-- ----------------------------
INSERT INTO `post_categories` VALUES (1, 7);

-- ----------------------------
-- Table structure for post_tags
-- ----------------------------
DROP TABLE IF EXISTS `post_tags`;
CREATE TABLE `post_tags`  (
  `post_id` int NOT NULL,
  `tag_id` int NOT NULL,
  PRIMARY KEY (`post_id`, `tag_id`) USING BTREE,
  INDEX `tag_id`(`tag_id` ASC) USING BTREE,
  CONSTRAINT `post_tags_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `post_tags_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`tag_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_tags
-- ----------------------------
INSERT INTO `post_tags` VALUES (1, 2);

-- ----------------------------
-- Table structure for post_versions
-- ----------------------------
DROP TABLE IF EXISTS `post_versions`;
CREATE TABLE `post_versions`  (
  `version_id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `content` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `version_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `user_id` int NOT NULL,
  `version` int NOT NULL,
  PRIMARY KEY (`version_id`) USING BTREE,
  INDEX `post_id`(`post_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `post_versions_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `post_versions_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_versions
-- ----------------------------
INSERT INTO `post_versions` VALUES (1, 1, '恶搞开始！！', '2025-03-20 23:01:26', 5, 2);
INSERT INTO `post_versions` VALUES (2, 2, '你好，这是一个互动博客', '2025-03-20 23:04:41', 4, 1);

-- ----------------------------
-- Table structure for posts
-- ----------------------------
DROP TABLE IF EXISTS `posts`;
CREATE TABLE `posts`  (
  `post_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `title` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `content` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_published` tinyint(1) NULL DEFAULT 1,
  `is_draft` tinyint(1) NULL DEFAULT 0,
  `views` int NULL DEFAULT 0,
  `likes` int NULL DEFAULT 0,
  `favorites` int NULL DEFAULT 0,
  `cover_image` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `version` int NULL DEFAULT NULL,
  PRIMARY KEY (`post_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of posts
-- ----------------------------
INSERT INTO `posts` VALUES (1, 5, 'string', 'string', '2025-03-20 20:51:06', '2025-03-21 10:39:18', 1, 1, 0, -1, 0, 'string', 0);
INSERT INTO `posts` VALUES (2, 5, '第二篇博客', 'Kitty的第二篇博客，好激动！！！！', '2025-03-20 20:51:30', '2025-03-20 00:00:00', 1, 0, 0, 0, 0, 'string', NULL);

-- ----------------------------
-- Table structure for reports
-- ----------------------------
DROP TABLE IF EXISTS `reports`;
CREATE TABLE `reports`  (
  `report_id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `user_id` int NOT NULL,
  `reason` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`report_id`) USING BTREE,
  INDEX `post_id`(`post_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `reports_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `reports_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of reports
-- ----------------------------
INSERT INTO `reports` VALUES (1, 1, 4, '难绷', '2025-03-21 10:48:32');

-- ----------------------------
-- Table structure for role_permissions
-- ----------------------------
DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions`  (
  `permission_id` int NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`permission_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_permissions
-- ----------------------------
--INSERT INTO `role_permissions` VALUES (1, 1);
--INSERT INTO `role_permissions` VALUES (2, 1);
--INSERT INTO `role_permissions` VALUES (3, 1);
--INSERT INTO `role_permissions` VALUES (4, 1);
--INSERT INTO `role_permissions` VALUES (5, 1);
--INSERT INTO `role_permissions` VALUES (6, 1);
--INSERT INTO `role_permissions` VALUES (7, 1);
--INSERT INTO `role_permissions` VALUES (8, 1);
--INSERT INTO `role_permissions` VALUES (9, 1);
--INSERT INTO `role_permissions` VALUES (10, 1);
--INSERT INTO `role_permissions` VALUES (11, 1);
--INSERT INTO `role_permissions` VALUES (12, 1);
--INSERT INTO `role_permissions` VALUES (13, 1);
--INSERT INTO `role_permissions` VALUES (14, 1);
--INSERT INTO `role_permissions` VALUES (15, 1);
--INSERT INTO `role_permissions` VALUES (16, 1);
--INSERT INTO `role_permissions` VALUES (17, 1);
--INSERT INTO `role_permissions` VALUES (18, 1);
--INSERT INTO `role_permissions` VALUES (19, 1);
--INSERT INTO `role_permissions` VALUES (20, 1);
--INSERT INTO `role_permissions` VALUES (21, 1);
--INSERT INTO `role_permissions` VALUES (22, 1);
--INSERT INTO `role_permissions` VALUES (23, 1);
--INSERT INTO `role_permissions` VALUES (24, 1);
--INSERT INTO `role_permissions` VALUES (25, 1);
--INSERT INTO `role_permissions` VALUES (26, 1);
--INSERT INTO `role_permissions` VALUES (27, 1);
--INSERT INTO `role_permissions` VALUES (28, 1);
--INSERT INTO `role_permissions` VALUES (29, 1);
--INSERT INTO `role_permissions` VALUES (30, 1);
--INSERT INTO `role_permissions` VALUES (31, 1);
--INSERT INTO `role_permissions` VALUES (32, 1);
--INSERT INTO `role_permissions` VALUES (33, 1);
--INSERT INTO `role_permissions` VALUES (34, 1);
--INSERT INTO `role_permissions` VALUES (35, 1);
--INSERT INTO `role_permissions` VALUES (36, 1);
--INSERT INTO `role_permissions` VALUES (37, 1);
--INSERT INTO `role_permissions` VALUES (38, 1);
--INSERT INTO `role_permissions` VALUES (39, 1);
--INSERT INTO `role_permissions` VALUES (40, 2);
--INSERT INTO `role_permissions` VALUES (41, 2);
--INSERT INTO `role_permissions` VALUES (42, 3);
--INSERT INTO `role_permissions` VALUES (43, 3);
--INSERT INTO `role_permissions` VALUES (44, 3);
--INSERT INTO `role_permissions` VALUES (45, 3);
--INSERT INTO `role_permissions` VALUES (46, 4);
--INSERT INTO `role_permissions` VALUES (47, 4);
--INSERT INTO `role_permissions` VALUES (48, 5);
--INSERT INTO `role_permissions` VALUES (49, 5);
--INSERT INTO `role_permissions` VALUES (50, 5);
--INSERT INTO `role_permissions` VALUES (51, 6);
--INSERT INTO `role_permissions` VALUES (52, 6);
--INSERT INTO `role_permissions` VALUES (53, 6);
--INSERT INTO `role_permissions` VALUES (54, 7);
--INSERT INTO `role_permissions` VALUES (55, 7);
--INSERT INTO `role_permissions` VALUES (56, 8);
--INSERT INTO `role_permissions` VALUES (57, 9);
--INSERT INTO `role_permissions` VALUES (58, 9);
--INSERT INTO `role_permissions` VALUES (59, 9);
--INSERT INTO `role_permissions` VALUES (60, 9);
--INSERT INTO `role_permissions` VALUES (61, 10);
--INSERT INTO `role_permissions` VALUES (62, 10);
--INSERT INTO `role_permissions` VALUES (63, 11);
--INSERT INTO `role_permissions` VALUES (64, 11);
--INSERT INTO `role_permissions` VALUES (65, 11);
--INSERT INTO `role_permissions` VALUES (66, 11);
--INSERT INTO `role_permissions` VALUES (67, 12);
--INSERT INTO `role_permissions` VALUES (68, 12);

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE INDEX `role_name`(`role_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roles
-- ----------------------------

-- ----------------------------
-- Table structure for tags
-- ----------------------------
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags`  (
  `tag_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `weight` int NULL DEFAULT 0,
  PRIMARY KEY (`tag_id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tags
-- ----------------------------
INSERT INTO `tags` VALUES (1, '开发', 5);
INSERT INTO `tags` VALUES (2, '维护', 4);
INSERT INTO `tags` VALUES (3, '跳舞', 3);

-- ----------------------------
-- Table structure for user_activities
-- ----------------------------
DROP TABLE IF EXISTS `user_activities`;
CREATE TABLE `user_activities`  (
  `activity_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `activity_type` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `post_id` int NULL DEFAULT NULL,
  `activity_detail` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`activity_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `post_id`(`post_id` ASC) USING BTREE,
  CONSTRAINT `user_activities_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_activities_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `posts` (`post_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_activities
-- ----------------------------
INSERT INTO `user_activities` VALUES (1, 4, 'favorite', 2, 'string', '2025-03-21 09:33:26');

-- ----------------------------
-- Table structure for user_roles
-- ----------------------------
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles`  (
  `user_id` int NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
  INDEX `role_id`(`role_id` ASC) USING BTREE,
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_roles
-- ----------------------------

-- ----------------------------
-- Table structure for user_settings
-- ----------------------------
DROP TABLE IF EXISTS `user_settings`;
CREATE TABLE `user_settings`  (
  `setting_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `theme` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `notifications` tinyint(1) NULL DEFAULT 1,
  `bili_bili_account` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `csdnaccount` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `github_account` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `bilibili_account` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `csdn_account` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`setting_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `user_settings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_settings
-- ----------------------------
INSERT INTO `user_settings` VALUES (1, 4, 'blue', 1, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `email` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `avatar` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `token` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NULL DEFAULT 1,
  `address` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `introduction` text CHARACTER SET utf16 COLLATE utf16_general_ci NULL,
  `is_male` bit(1) NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `token`(`token` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'jonny', '$2a$10$m/UDxOQ0ZRq0wcVLPtZTNOIGiDaJSrt.clReeHOyPTJs4I91LVXpu', 'jonny.jiang@sap.com', 'string', 'string', '2025-03-20 10:35:13', '2025-03-21 20:31:58', 0, NULL, NULL, NULL, NULL);
INSERT INTO `users` VALUES (2, 'kitty', '$2a$10$QnHb/DthhNK8t8JgQ84EI.sMuznU6zTd/Xa7ztTgCcL85e5Kc77vu', 'kitty.wang@sap.com', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/user/5/1742632120504-avatar-2297458171461323628-fig1.png', NULL, '2025-03-20 10:56:55', '2025-03-22 16:28:40', 1, NULL, NULL, NULL, NULL);
INSERT INTO `users` VALUES (3, 'admin', '$2a$10$wp3M6pohRS8wSHZXWhWx3.g6tuAqntkqsB2GkCVgRsSQB2renrxBq', 'wzk@sap.com', NULL, NULL, '2025-03-21 19:48:11', '2025-03-21 20:08:20', 1, NULL, NULL, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
