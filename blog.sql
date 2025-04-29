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

 Date: 29/04/2025 15:14:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for act_app_appdef
-- ----------------------------
DROP TABLE IF EXISTS `act_app_appdef`;
CREATE TABLE `act_app_appdef`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `REV_` int NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `KEY_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `VERSION_` int NOT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `RESOURCE_NAME_` varchar(4000) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DESCRIPTION_` varchar(4000) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE,
  UNIQUE INDEX `ACT_IDX_APP_DEF_UNIQ`(`KEY_` ASC, `VERSION_` ASC, `TENANT_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_APP_DEF_DPLY`(`DEPLOYMENT_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_APP_DEF_DPLY` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_app_deployment` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_app_appdef
-- ----------------------------

-- ----------------------------
-- Table structure for act_app_databasechangelog
-- ----------------------------
DROP TABLE IF EXISTS `act_app_databasechangelog`;
CREATE TABLE `act_app_databasechangelog`  (
  `ID` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `AUTHOR` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `FILENAME` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int NOT NULL,
  `EXECTYPE` varchar(10) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `MD5SUM` varchar(35) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DESCRIPTION` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `COMMENTS` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `TAG` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `LIQUIBASE` varchar(20) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CONTEXTS` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `LABELS` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DEPLOYMENT_ID` varchar(10) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_app_databasechangelog
-- ----------------------------
INSERT INTO `act_app_databasechangelog` VALUES ('1', 'flowable', 'org/flowable/app/db/liquibase/flowable-app-db-changelog.xml', '2025-04-03 20:49:43', 1, 'EXECUTED', '9:959783069c0c7ce80320a0617aa48969', 'createTable tableName=ACT_APP_DEPLOYMENT; createTable tableName=ACT_APP_DEPLOYMENT_RESOURCE; addForeignKeyConstraint baseTableName=ACT_APP_DEPLOYMENT_RESOURCE, constraintName=ACT_FK_APP_RSRC_DPL, referencedTableName=ACT_APP_DEPLOYMENT; createIndex...', '', NULL, '4.29.2', NULL, NULL, '3684582945');
INSERT INTO `act_app_databasechangelog` VALUES ('2', 'flowable', 'org/flowable/app/db/liquibase/flowable-app-db-changelog.xml', '2025-04-03 20:49:43', 2, 'EXECUTED', '9:c75407b1c0e16adf2a6db585c05a94c7', 'modifyDataType columnName=DEPLOY_TIME_, tableName=ACT_APP_DEPLOYMENT', '', NULL, '4.29.2', NULL, NULL, '3684582945');
INSERT INTO `act_app_databasechangelog` VALUES ('3', 'flowable', 'org/flowable/app/db/liquibase/flowable-app-db-changelog.xml', '2025-04-03 20:49:43', 3, 'EXECUTED', '9:c05b79a3b00e95136533085718361208', 'createIndex indexName=ACT_IDX_APP_DEF_UNIQ, tableName=ACT_APP_APPDEF', '', NULL, '4.29.2', NULL, NULL, '3684582945');

-- ----------------------------
-- Table structure for act_app_databasechangeloglock
-- ----------------------------
DROP TABLE IF EXISTS `act_app_databasechangeloglock`;
CREATE TABLE `act_app_databasechangeloglock`  (
  `ID` int NOT NULL,
  `LOCKED` tinyint NOT NULL,
  `LOCKGRANTED` datetime NULL DEFAULT NULL,
  `LOCKEDBY` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_app_databasechangeloglock
-- ----------------------------
INSERT INTO `act_app_databasechangeloglock` VALUES (1, 0, NULL, NULL);

-- ----------------------------
-- Table structure for act_app_deployment
-- ----------------------------
DROP TABLE IF EXISTS `act_app_deployment`;
CREATE TABLE `act_app_deployment`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `KEY_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DEPLOY_TIME_` datetime(3) NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_app_deployment
-- ----------------------------

-- ----------------------------
-- Table structure for act_app_deployment_resource
-- ----------------------------
DROP TABLE IF EXISTS `act_app_deployment_resource`;
CREATE TABLE `act_app_deployment_resource`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `RESOURCE_BYTES_` longblob NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_APP_RSRC_DPL`(`DEPLOYMENT_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_APP_RSRC_DPL` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_app_deployment` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_app_deployment_resource
-- ----------------------------

-- ----------------------------
-- Table structure for act_cmmn_casedef
-- ----------------------------
DROP TABLE IF EXISTS `act_cmmn_casedef`;
CREATE TABLE `act_cmmn_casedef`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `REV_` int NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `KEY_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `VERSION_` int NOT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `RESOURCE_NAME_` varchar(4000) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DESCRIPTION_` varchar(4000) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `HAS_GRAPHICAL_NOTATION_` tinyint NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT '',
  `DGRM_RESOURCE_NAME_` varchar(4000) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `HAS_START_FORM_KEY_` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  UNIQUE INDEX `ACT_IDX_CASE_DEF_UNIQ`(`KEY_` ASC, `VERSION_` ASC, `TENANT_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_CASE_DEF_DPLY`(`DEPLOYMENT_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_CASE_DEF_DPLY` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_cmmn_deployment` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_cmmn_casedef
-- ----------------------------

-- ----------------------------
-- Table structure for act_cmmn_databasechangelog
-- ----------------------------
DROP TABLE IF EXISTS `act_cmmn_databasechangelog`;
CREATE TABLE `act_cmmn_databasechangelog`  (
  `ID` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `AUTHOR` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `FILENAME` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int NOT NULL,
  `EXECTYPE` varchar(10) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `MD5SUM` varchar(35) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DESCRIPTION` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `COMMENTS` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `TAG` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `LIQUIBASE` varchar(20) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CONTEXTS` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `LABELS` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DEPLOYMENT_ID` varchar(10) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_cmmn_databasechangelog
-- ----------------------------
INSERT INTO `act_cmmn_databasechangelog` VALUES ('1', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', '2025-04-03 20:49:41', 1, 'EXECUTED', '9:d0cc0aaadf0e4ef70c5b412cd05fadc4', 'createTable tableName=ACT_CMMN_DEPLOYMENT; createTable tableName=ACT_CMMN_DEPLOYMENT_RESOURCE; addForeignKeyConstraint baseTableName=ACT_CMMN_DEPLOYMENT_RESOURCE, constraintName=ACT_FK_CMMN_RSRC_DPL, referencedTableName=ACT_CMMN_DEPLOYMENT; create...', '', NULL, '4.29.2', NULL, NULL, '3684580569');
INSERT INTO `act_cmmn_databasechangelog` VALUES ('2', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', '2025-04-03 20:49:41', 2, 'EXECUTED', '9:8095a5a8a222a100c2d0310cacbda5e7', 'addColumn tableName=ACT_CMMN_CASEDEF; addColumn tableName=ACT_CMMN_DEPLOYMENT_RESOURCE; addColumn tableName=ACT_CMMN_RU_CASE_INST; addColumn tableName=ACT_CMMN_RU_PLAN_ITEM_INST', '', NULL, '4.29.2', NULL, NULL, '3684580569');
INSERT INTO `act_cmmn_databasechangelog` VALUES ('3', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', '2025-04-03 20:49:41', 3, 'EXECUTED', '9:f031b4f0ae67bc5a640736b379049b12', 'addColumn tableName=ACT_CMMN_RU_PLAN_ITEM_INST; addColumn tableName=ACT_CMMN_RU_CASE_INST; createIndex indexName=ACT_IDX_PLAN_ITEM_STAGE_INST, tableName=ACT_CMMN_RU_PLAN_ITEM_INST; addColumn tableName=ACT_CMMN_RU_PLAN_ITEM_INST; addColumn tableNam...', '', NULL, '4.29.2', NULL, NULL, '3684580569');
INSERT INTO `act_cmmn_databasechangelog` VALUES ('4', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', '2025-04-03 20:49:41', 4, 'EXECUTED', '9:c484ecfb08719feccac2f80fc962dda9', 'createTable tableName=ACT_CMMN_HI_PLAN_ITEM_INST; addColumn tableName=ACT_CMMN_RU_MIL_INST; addColumn tableName=ACT_CMMN_HI_MIL_INST', '', NULL, '4.29.2', NULL, NULL, '3684580569');
INSERT INTO `act_cmmn_databasechangelog` VALUES ('5', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', '2025-04-03 20:49:42', 5, 'EXECUTED', '9:e6a67f8f0d16cd72117900442acfe6e0', 'modifyDataType columnName=DEPLOY_TIME_, tableName=ACT_CMMN_DEPLOYMENT; modifyDataType columnName=START_TIME_, tableName=ACT_CMMN_RU_CASE_INST; modifyDataType columnName=START_TIME_, tableName=ACT_CMMN_RU_PLAN_ITEM_INST; modifyDataType columnName=T...', '', NULL, '4.29.2', NULL, NULL, '3684580569');
INSERT INTO `act_cmmn_databasechangelog` VALUES ('6', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', '2025-04-03 20:49:42', 6, 'EXECUTED', '9:7343ab247d959e5add9278b5386de833', 'createIndex indexName=ACT_IDX_CASE_DEF_UNIQ, tableName=ACT_CMMN_CASEDEF', '', NULL, '4.29.2', NULL, NULL, '3684580569');
INSERT INTO `act_cmmn_databasechangelog` VALUES ('7', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', '2025-04-03 20:49:42', 7, 'EXECUTED', '9:d73200db684b6cdb748cc03570d5d2e9', 'renameColumn newColumnName=CREATE_TIME_, oldColumnName=START_TIME_, tableName=ACT_CMMN_RU_PLAN_ITEM_INST; renameColumn newColumnName=CREATE_TIME_, oldColumnName=CREATED_TIME_, tableName=ACT_CMMN_HI_PLAN_ITEM_INST; addColumn tableName=ACT_CMMN_RU_P...', '', NULL, '4.29.2', NULL, NULL, '3684580569');
INSERT INTO `act_cmmn_databasechangelog` VALUES ('8', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', '2025-04-03 20:49:42', 8, 'EXECUTED', '9:eda5e43816221f2d8554bfcc90f1c37e', 'addColumn tableName=ACT_CMMN_HI_PLAN_ITEM_INST', '', NULL, '4.29.2', NULL, NULL, '3684580569');
INSERT INTO `act_cmmn_databasechangelog` VALUES ('9', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', '2025-04-03 20:49:42', 9, 'EXECUTED', '9:c34685611779075a73caf8c380f078ea', 'addColumn tableName=ACT_CMMN_RU_PLAN_ITEM_INST; addColumn tableName=ACT_CMMN_HI_PLAN_ITEM_INST', '', NULL, '4.29.2', NULL, NULL, '3684580569');
INSERT INTO `act_cmmn_databasechangelog` VALUES ('10', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', '2025-04-03 20:49:42', 10, 'EXECUTED', '9:368e9472ad2348206205170d6c52d58e', 'addColumn tableName=ACT_CMMN_RU_CASE_INST; addColumn tableName=ACT_CMMN_RU_CASE_INST; createIndex indexName=ACT_IDX_CASE_INST_REF_ID_, tableName=ACT_CMMN_RU_CASE_INST; addColumn tableName=ACT_CMMN_HI_CASE_INST; addColumn tableName=ACT_CMMN_HI_CASE...', '', NULL, '4.29.2', NULL, NULL, '3684580569');
INSERT INTO `act_cmmn_databasechangelog` VALUES ('11', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', '2025-04-03 20:49:42', 11, 'EXECUTED', '9:e54b50ceb2bcd5355ae4dfb56d9ff3ad', 'addColumn tableName=ACT_CMMN_RU_PLAN_ITEM_INST; addColumn tableName=ACT_CMMN_HI_PLAN_ITEM_INST', '', NULL, '4.29.2', NULL, NULL, '3684580569');
INSERT INTO `act_cmmn_databasechangelog` VALUES ('12', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', '2025-04-03 20:49:42', 12, 'EXECUTED', '9:f53f262768d04e74529f43fcd93429b0', 'addColumn tableName=ACT_CMMN_RU_CASE_INST', '', NULL, '4.29.2', NULL, NULL, '3684580569');
INSERT INTO `act_cmmn_databasechangelog` VALUES ('13', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', '2025-04-03 20:49:42', 13, 'EXECUTED', '9:64e7eafbe97997094654e83caea99895', 'addColumn tableName=ACT_CMMN_RU_PLAN_ITEM_INST; addColumn tableName=ACT_CMMN_HI_PLAN_ITEM_INST', '', NULL, '4.29.2', NULL, NULL, '3684580569');
INSERT INTO `act_cmmn_databasechangelog` VALUES ('14', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', '2025-04-03 20:49:42', 14, 'EXECUTED', '9:ab7d934abde497eac034701542e0a281', 'addColumn tableName=ACT_CMMN_RU_CASE_INST; addColumn tableName=ACT_CMMN_HI_CASE_INST', '', NULL, '4.29.2', NULL, NULL, '3684580569');
INSERT INTO `act_cmmn_databasechangelog` VALUES ('16', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', '2025-04-03 20:49:42', 15, 'EXECUTED', '9:03928d422e510959770e7a9daa5a993f', 'addColumn tableName=ACT_CMMN_RU_CASE_INST; addColumn tableName=ACT_CMMN_HI_CASE_INST', '', NULL, '4.29.2', NULL, NULL, '3684580569');
INSERT INTO `act_cmmn_databasechangelog` VALUES ('17', 'flowable', 'org/flowable/cmmn/db/liquibase/flowable-cmmn-db-changelog.xml', '2025-04-03 20:49:42', 16, 'EXECUTED', '9:f30304cf001d6eac78c793ea88cd5781', 'createIndex indexName=ACT_IDX_HI_CASE_INST_END, tableName=ACT_CMMN_HI_CASE_INST', '', NULL, '4.29.2', NULL, NULL, '3684580569');

-- ----------------------------
-- Table structure for act_cmmn_databasechangeloglock
-- ----------------------------
DROP TABLE IF EXISTS `act_cmmn_databasechangeloglock`;
CREATE TABLE `act_cmmn_databasechangeloglock`  (
  `ID` int NOT NULL,
  `LOCKED` tinyint NOT NULL,
  `LOCKGRANTED` datetime NULL DEFAULT NULL,
  `LOCKEDBY` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_cmmn_databasechangeloglock
-- ----------------------------
INSERT INTO `act_cmmn_databasechangeloglock` VALUES (1, 0, NULL, NULL);

-- ----------------------------
-- Table structure for act_cmmn_deployment
-- ----------------------------
DROP TABLE IF EXISTS `act_cmmn_deployment`;
CREATE TABLE `act_cmmn_deployment`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `KEY_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DEPLOY_TIME_` datetime(3) NULL DEFAULT NULL,
  `PARENT_DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_cmmn_deployment
-- ----------------------------

-- ----------------------------
-- Table structure for act_cmmn_deployment_resource
-- ----------------------------
DROP TABLE IF EXISTS `act_cmmn_deployment_resource`;
CREATE TABLE `act_cmmn_deployment_resource`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `RESOURCE_BYTES_` longblob NULL,
  `GENERATED_` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_CMMN_RSRC_DPL`(`DEPLOYMENT_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_CMMN_RSRC_DPL` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_cmmn_deployment` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_cmmn_deployment_resource
-- ----------------------------

-- ----------------------------
-- Table structure for act_cmmn_hi_case_inst
-- ----------------------------
DROP TABLE IF EXISTS `act_cmmn_hi_case_inst`;
CREATE TABLE `act_cmmn_hi_case_inst`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `REV_` int NOT NULL,
  `BUSINESS_KEY_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `PARENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CASE_DEF_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `STATE_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `START_TIME_` datetime(3) NULL DEFAULT NULL,
  `END_TIME_` datetime(3) NULL DEFAULT NULL,
  `START_USER_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CALLBACK_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CALLBACK_TYPE_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT '',
  `REFERENCE_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `REFERENCE_TYPE_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `LAST_REACTIVATION_TIME_` datetime(3) NULL DEFAULT NULL,
  `LAST_REACTIVATION_USER_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `BUSINESS_STATUS_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_HI_CASE_INST_END`(`END_TIME_` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_cmmn_hi_case_inst
-- ----------------------------

-- ----------------------------
-- Table structure for act_cmmn_hi_mil_inst
-- ----------------------------
DROP TABLE IF EXISTS `act_cmmn_hi_mil_inst`;
CREATE TABLE `act_cmmn_hi_mil_inst`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `REV_` int NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `TIME_STAMP_` datetime(3) NULL DEFAULT NULL,
  `CASE_INST_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `CASE_DEF_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `ELEMENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_cmmn_hi_mil_inst
-- ----------------------------

-- ----------------------------
-- Table structure for act_cmmn_hi_plan_item_inst
-- ----------------------------
DROP TABLE IF EXISTS `act_cmmn_hi_plan_item_inst`;
CREATE TABLE `act_cmmn_hi_plan_item_inst`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `REV_` int NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `STATE_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CASE_DEF_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CASE_INST_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `STAGE_INST_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `IS_STAGE_` tinyint NULL DEFAULT NULL,
  `ELEMENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `ITEM_DEFINITION_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `ITEM_DEFINITION_TYPE_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CREATE_TIME_` datetime(3) NULL DEFAULT NULL,
  `LAST_AVAILABLE_TIME_` datetime(3) NULL DEFAULT NULL,
  `LAST_ENABLED_TIME_` datetime(3) NULL DEFAULT NULL,
  `LAST_DISABLED_TIME_` datetime(3) NULL DEFAULT NULL,
  `LAST_STARTED_TIME_` datetime(3) NULL DEFAULT NULL,
  `LAST_SUSPENDED_TIME_` datetime(3) NULL DEFAULT NULL,
  `COMPLETED_TIME_` datetime(3) NULL DEFAULT NULL,
  `OCCURRED_TIME_` datetime(3) NULL DEFAULT NULL,
  `TERMINATED_TIME_` datetime(3) NULL DEFAULT NULL,
  `EXIT_TIME_` datetime(3) NULL DEFAULT NULL,
  `ENDED_TIME_` datetime(3) NULL DEFAULT NULL,
  `LAST_UPDATED_TIME_` datetime(3) NULL DEFAULT NULL,
  `START_USER_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `REFERENCE_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `REFERENCE_TYPE_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT '',
  `ENTRY_CRITERION_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `EXIT_CRITERION_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `SHOW_IN_OVERVIEW_` tinyint NULL DEFAULT NULL,
  `EXTRA_VALUE_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DERIVED_CASE_DEF_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `LAST_UNAVAILABLE_TIME_` datetime(3) NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_cmmn_hi_plan_item_inst
-- ----------------------------

-- ----------------------------
-- Table structure for act_cmmn_ru_case_inst
-- ----------------------------
DROP TABLE IF EXISTS `act_cmmn_ru_case_inst`;
CREATE TABLE `act_cmmn_ru_case_inst`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `REV_` int NOT NULL,
  `BUSINESS_KEY_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `PARENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CASE_DEF_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `STATE_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `START_TIME_` datetime(3) NULL DEFAULT NULL,
  `START_USER_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CALLBACK_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CALLBACK_TYPE_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT '',
  `LOCK_TIME_` datetime(3) NULL DEFAULT NULL,
  `IS_COMPLETEABLE_` tinyint NULL DEFAULT NULL,
  `REFERENCE_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `REFERENCE_TYPE_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `LOCK_OWNER_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `LAST_REACTIVATION_TIME_` datetime(3) NULL DEFAULT NULL,
  `LAST_REACTIVATION_USER_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `BUSINESS_STATUS_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_CASE_INST_CASE_DEF`(`CASE_DEF_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_CASE_INST_PARENT`(`PARENT_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_CASE_INST_REF_ID_`(`REFERENCE_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_CASE_INST_CASE_DEF` FOREIGN KEY (`CASE_DEF_ID_`) REFERENCES `act_cmmn_casedef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_cmmn_ru_case_inst
-- ----------------------------

-- ----------------------------
-- Table structure for act_cmmn_ru_mil_inst
-- ----------------------------
DROP TABLE IF EXISTS `act_cmmn_ru_mil_inst`;
CREATE TABLE `act_cmmn_ru_mil_inst`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `TIME_STAMP_` datetime(3) NULL DEFAULT NULL,
  `CASE_INST_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `CASE_DEF_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `ELEMENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_MIL_CASE_DEF`(`CASE_DEF_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_MIL_CASE_INST`(`CASE_INST_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_MIL_CASE_DEF` FOREIGN KEY (`CASE_DEF_ID_`) REFERENCES `act_cmmn_casedef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_MIL_CASE_INST` FOREIGN KEY (`CASE_INST_ID_`) REFERENCES `act_cmmn_ru_case_inst` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_cmmn_ru_mil_inst
-- ----------------------------

-- ----------------------------
-- Table structure for act_cmmn_ru_plan_item_inst
-- ----------------------------
DROP TABLE IF EXISTS `act_cmmn_ru_plan_item_inst`;
CREATE TABLE `act_cmmn_ru_plan_item_inst`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `REV_` int NOT NULL,
  `CASE_DEF_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CASE_INST_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `STAGE_INST_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `IS_STAGE_` tinyint NULL DEFAULT NULL,
  `ELEMENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `STATE_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CREATE_TIME_` datetime(3) NULL DEFAULT NULL,
  `START_USER_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `REFERENCE_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `REFERENCE_TYPE_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT '',
  `ITEM_DEFINITION_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `ITEM_DEFINITION_TYPE_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `IS_COMPLETEABLE_` tinyint NULL DEFAULT NULL,
  `IS_COUNT_ENABLED_` tinyint NULL DEFAULT NULL,
  `VAR_COUNT_` int NULL DEFAULT NULL,
  `SENTRY_PART_INST_COUNT_` int NULL DEFAULT NULL,
  `LAST_AVAILABLE_TIME_` datetime(3) NULL DEFAULT NULL,
  `LAST_ENABLED_TIME_` datetime(3) NULL DEFAULT NULL,
  `LAST_DISABLED_TIME_` datetime(3) NULL DEFAULT NULL,
  `LAST_STARTED_TIME_` datetime(3) NULL DEFAULT NULL,
  `LAST_SUSPENDED_TIME_` datetime(3) NULL DEFAULT NULL,
  `COMPLETED_TIME_` datetime(3) NULL DEFAULT NULL,
  `OCCURRED_TIME_` datetime(3) NULL DEFAULT NULL,
  `TERMINATED_TIME_` datetime(3) NULL DEFAULT NULL,
  `EXIT_TIME_` datetime(3) NULL DEFAULT NULL,
  `ENDED_TIME_` datetime(3) NULL DEFAULT NULL,
  `ENTRY_CRITERION_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `EXIT_CRITERION_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `EXTRA_VALUE_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DERIVED_CASE_DEF_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `LAST_UNAVAILABLE_TIME_` datetime(3) NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_PLAN_ITEM_CASE_DEF`(`CASE_DEF_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_PLAN_ITEM_CASE_INST`(`CASE_INST_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_PLAN_ITEM_STAGE_INST`(`STAGE_INST_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_PLAN_ITEM_CASE_DEF` FOREIGN KEY (`CASE_DEF_ID_`) REFERENCES `act_cmmn_casedef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_PLAN_ITEM_CASE_INST` FOREIGN KEY (`CASE_INST_ID_`) REFERENCES `act_cmmn_ru_case_inst` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_cmmn_ru_plan_item_inst
-- ----------------------------

-- ----------------------------
-- Table structure for act_cmmn_ru_sentry_part_inst
-- ----------------------------
DROP TABLE IF EXISTS `act_cmmn_ru_sentry_part_inst`;
CREATE TABLE `act_cmmn_ru_sentry_part_inst`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `REV_` int NOT NULL,
  `CASE_DEF_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CASE_INST_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `PLAN_ITEM_INST_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `ON_PART_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `IF_PART_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `TIME_STAMP_` datetime(3) NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_SENTRY_CASE_DEF`(`CASE_DEF_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_SENTRY_CASE_INST`(`CASE_INST_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_SENTRY_PLAN_ITEM`(`PLAN_ITEM_INST_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_SENTRY_CASE_DEF` FOREIGN KEY (`CASE_DEF_ID_`) REFERENCES `act_cmmn_casedef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_SENTRY_CASE_INST` FOREIGN KEY (`CASE_INST_ID_`) REFERENCES `act_cmmn_ru_case_inst` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_SENTRY_PLAN_ITEM` FOREIGN KEY (`PLAN_ITEM_INST_ID_`) REFERENCES `act_cmmn_ru_plan_item_inst` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_cmmn_ru_sentry_part_inst
-- ----------------------------

-- ----------------------------
-- Table structure for act_dmn_databasechangelog
-- ----------------------------
DROP TABLE IF EXISTS `act_dmn_databasechangelog`;
CREATE TABLE `act_dmn_databasechangelog`  (
  `ID` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `AUTHOR` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `FILENAME` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int NOT NULL,
  `EXECTYPE` varchar(10) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `MD5SUM` varchar(35) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DESCRIPTION` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `COMMENTS` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `TAG` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `LIQUIBASE` varchar(20) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CONTEXTS` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `LABELS` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DEPLOYMENT_ID` varchar(10) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_dmn_databasechangelog
-- ----------------------------
INSERT INTO `act_dmn_databasechangelog` VALUES ('1', 'activiti', 'org/flowable/dmn/db/liquibase/flowable-dmn-db-changelog.xml', '2025-04-03 20:49:39', 1, 'EXECUTED', '9:5b36e70aee5a2e42f6e7a62ea5fa681b', 'createTable tableName=ACT_DMN_DEPLOYMENT; createTable tableName=ACT_DMN_DEPLOYMENT_RESOURCE; createTable tableName=ACT_DMN_DECISION_TABLE', '', NULL, '4.29.2', NULL, NULL, '3684579812');
INSERT INTO `act_dmn_databasechangelog` VALUES ('2', 'flowable', 'org/flowable/dmn/db/liquibase/flowable-dmn-db-changelog.xml', '2025-04-03 20:49:39', 2, 'EXECUTED', '9:fd13fa3f7af55d2b72f763fc261da30d', 'createTable tableName=ACT_DMN_HI_DECISION_EXECUTION', '', NULL, '4.29.2', NULL, NULL, '3684579812');
INSERT INTO `act_dmn_databasechangelog` VALUES ('3', 'flowable', 'org/flowable/dmn/db/liquibase/flowable-dmn-db-changelog.xml', '2025-04-03 20:49:39', 3, 'EXECUTED', '9:9f30e6a3557d4b4c713dbb2dcc141782', 'addColumn tableName=ACT_DMN_HI_DECISION_EXECUTION', '', NULL, '4.29.2', NULL, NULL, '3684579812');
INSERT INTO `act_dmn_databasechangelog` VALUES ('4', 'flowable', 'org/flowable/dmn/db/liquibase/flowable-dmn-db-changelog.xml', '2025-04-03 20:49:39', 4, 'EXECUTED', '9:41085fbde807dba96104ee75a2fcc4cc', 'dropColumn columnName=PARENT_DEPLOYMENT_ID_, tableName=ACT_DMN_DECISION_TABLE', '', NULL, '4.29.2', NULL, NULL, '3684579812');
INSERT INTO `act_dmn_databasechangelog` VALUES ('5', 'flowable', 'org/flowable/dmn/db/liquibase/flowable-dmn-db-changelog.xml', '2025-04-03 20:49:40', 5, 'EXECUTED', '9:169d906b6503ad6907b7e5cd0d70d004', 'modifyDataType columnName=DEPLOY_TIME_, tableName=ACT_DMN_DEPLOYMENT; modifyDataType columnName=START_TIME_, tableName=ACT_DMN_HI_DECISION_EXECUTION; modifyDataType columnName=END_TIME_, tableName=ACT_DMN_HI_DECISION_EXECUTION', '', NULL, '4.29.2', NULL, NULL, '3684579812');
INSERT INTO `act_dmn_databasechangelog` VALUES ('6', 'flowable', 'org/flowable/dmn/db/liquibase/flowable-dmn-db-changelog.xml', '2025-04-03 20:49:40', 6, 'EXECUTED', '9:f00f92f3ef1af3fc1604f0323630f9b1', 'createIndex indexName=ACT_IDX_DEC_TBL_UNIQ, tableName=ACT_DMN_DECISION_TABLE', '', NULL, '4.29.2', NULL, NULL, '3684579812');
INSERT INTO `act_dmn_databasechangelog` VALUES ('7', 'flowable', 'org/flowable/dmn/db/liquibase/flowable-dmn-db-changelog.xml', '2025-04-03 20:49:40', 7, 'EXECUTED', '9:d24d4c5f44083b4edf1231a7a682a2cd', 'dropIndex indexName=ACT_IDX_DEC_TBL_UNIQ, tableName=ACT_DMN_DECISION_TABLE; renameTable newTableName=ACT_DMN_DECISION, oldTableName=ACT_DMN_DECISION_TABLE; createIndex indexName=ACT_IDX_DMN_DEC_UNIQ, tableName=ACT_DMN_DECISION', '', NULL, '4.29.2', NULL, NULL, '3684579812');
INSERT INTO `act_dmn_databasechangelog` VALUES ('8', 'flowable', 'org/flowable/dmn/db/liquibase/flowable-dmn-db-changelog.xml', '2025-04-03 20:49:40', 8, 'EXECUTED', '9:3998ef0958b46fe9c19458183952d2a0', 'addColumn tableName=ACT_DMN_DECISION', '', NULL, '4.29.2', NULL, NULL, '3684579812');
INSERT INTO `act_dmn_databasechangelog` VALUES ('9', 'flowable', 'org/flowable/dmn/db/liquibase/flowable-dmn-db-changelog.xml', '2025-04-03 20:49:40', 9, 'EXECUTED', '9:5c9dc65601456faa1aa12f8d3afe0e9e', 'createIndex indexName=ACT_IDX_DMN_INSTANCE_ID, tableName=ACT_DMN_HI_DECISION_EXECUTION', '', NULL, '4.29.2', NULL, NULL, '3684579812');

-- ----------------------------
-- Table structure for act_dmn_databasechangeloglock
-- ----------------------------
DROP TABLE IF EXISTS `act_dmn_databasechangeloglock`;
CREATE TABLE `act_dmn_databasechangeloglock`  (
  `ID` int NOT NULL,
  `LOCKED` tinyint NOT NULL,
  `LOCKGRANTED` datetime NULL DEFAULT NULL,
  `LOCKEDBY` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_dmn_databasechangeloglock
-- ----------------------------
INSERT INTO `act_dmn_databasechangeloglock` VALUES (1, 0, NULL, NULL);

-- ----------------------------
-- Table structure for act_dmn_decision
-- ----------------------------
DROP TABLE IF EXISTS `act_dmn_decision`;
CREATE TABLE `act_dmn_decision`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `VERSION_` int NULL DEFAULT NULL,
  `KEY_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `RESOURCE_NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DESCRIPTION_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DECISION_TYPE_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  UNIQUE INDEX `ACT_IDX_DMN_DEC_UNIQ`(`KEY_` ASC, `VERSION_` ASC, `TENANT_ID_` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_dmn_decision
-- ----------------------------

-- ----------------------------
-- Table structure for act_dmn_deployment
-- ----------------------------
DROP TABLE IF EXISTS `act_dmn_deployment`;
CREATE TABLE `act_dmn_deployment`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DEPLOY_TIME_` datetime(3) NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `PARENT_DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_dmn_deployment
-- ----------------------------

-- ----------------------------
-- Table structure for act_dmn_deployment_resource
-- ----------------------------
DROP TABLE IF EXISTS `act_dmn_deployment_resource`;
CREATE TABLE `act_dmn_deployment_resource`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `RESOURCE_BYTES_` longblob NULL,
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_dmn_deployment_resource
-- ----------------------------

-- ----------------------------
-- Table structure for act_dmn_hi_decision_execution
-- ----------------------------
DROP TABLE IF EXISTS `act_dmn_hi_decision_execution`;
CREATE TABLE `act_dmn_hi_decision_execution`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `DECISION_DEFINITION_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `START_TIME_` datetime(3) NULL DEFAULT NULL,
  `END_TIME_` datetime(3) NULL DEFAULT NULL,
  `INSTANCE_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `EXECUTION_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `ACTIVITY_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `FAILED_` tinyint NULL DEFAULT 0,
  `TENANT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `EXECUTION_JSON_` longtext CHARACTER SET utf16 COLLATE utf16_general_ci NULL,
  `SCOPE_TYPE_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_DMN_INSTANCE_ID`(`INSTANCE_ID_` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_dmn_hi_decision_execution
-- ----------------------------

-- ----------------------------
-- Table structure for act_evt_log
-- ----------------------------
DROP TABLE IF EXISTS `act_evt_log`;
CREATE TABLE `act_evt_log`  (
  `LOG_NR_` bigint NOT NULL AUTO_INCREMENT,
  `TYPE_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TIME_STAMP_` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DATA_` longblob NULL,
  `LOCK_OWNER_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `LOCK_TIME_` timestamp(3) NULL DEFAULT NULL,
  `IS_PROCESSED_` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`LOG_NR_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_evt_log
-- ----------------------------

-- ----------------------------
-- Table structure for act_ge_bytearray
-- ----------------------------
DROP TABLE IF EXISTS `act_ge_bytearray`;
CREATE TABLE `act_ge_bytearray`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DEPLOYMENT_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `BYTES_` longblob NULL,
  `GENERATED_` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_FK_BYTEARR_DEPL`(`DEPLOYMENT_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_BYTEARR_DEPL` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_re_deployment` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_ge_bytearray
-- ----------------------------
INSERT INTO `act_ge_bytearray` VALUES ('15e7f599-210d-11f0-80d1-0a0027000009', 1, 'var-reason', NULL, 0xACED00057E72002B636F6D2E6B697474792E626C6F672E636F6D6D6F6E2E636F6E7374616E742E5265706F7274526561736F6E00000000000000001200007872000E6A6176612E6C616E672E456E756D000000000000000012000078707400045350414D, NULL);
INSERT INTO `act_ge_bytearray` VALUES ('15e92e1b-210d-11f0-80d1-0a0027000009', 1, 'hist.var-reason', NULL, 0xACED00057E72002B636F6D2E6B697474792E626C6F672E636F6D6D6F6E2E636F6E7374616E742E5265706F7274526561736F6E00000000000000001200007872000E6A6176612E6C616E672E456E756D000000000000000012000078707400045350414D, NULL);
INSERT INTO `act_ge_bytearray` VALUES ('6b034dc9-21d3-11f0-be21-0a002700000a', 1, 'hist.var-reason', NULL, 0xACED00057E72002B636F6D2E6B697474792E626C6F672E636F6D6D6F6E2E636F6E7374616E742E5265706F7274526561736F6E00000000000000001200007872000E6A6176612E6C616E672E456E756D000000000000000012000078707400045350414D, NULL);
INSERT INTO `act_ge_bytearray` VALUES ('76a0613c-2360-11f0-9dca-0a0027000009', 1, 'var-reason', NULL, 0xACED00057E72002B636F6D2E6B697474792E626C6F672E636F6D6D6F6E2E636F6E7374616E742E5265706F7274526561736F6E00000000000000001200007872000E6A6176612E6C616E672E456E756D0000000000000000120000787074000D494E415050524F505249415445, NULL);
INSERT INTO `act_ge_bytearray` VALUES ('76a1c0ce-2360-11f0-9dca-0a0027000009', 1, 'hist.var-reason', NULL, 0xACED00057E72002B636F6D2E6B697474792E626C6F672E636F6D6D6F6E2E636F6E7374616E742E5265706F7274526561736F6E00000000000000001200007872000E6A6176612E6C616E672E456E756D0000000000000000120000787074000D494E415050524F505249415445, NULL);
INSERT INTO `act_ge_bytearray` VALUES ('b12f519a-19d2-11f0-b05e-0a0027000009', 1, 'workflow/reportReviewProcess.bpmn20.xml', 'b12f5199-19d2-11f0-b05e-0a0027000009', 0x3C3F786D6C2076657273696F6E3D22312E302220656E636F64696E673D225554462D38223F3E0D0A3C646566696E6974696F6E7320786D6C6E733D22687474703A2F2F7777772E6F6D672E6F72672F737065632F42504D4E2F32303130303532342F4D4F44454C220D0A20202020202020202020202020786D6C6E733A666C6F7761626C653D22687474703A2F2F666C6F7761626C652E6F72672F62706D6E220D0A202020202020202020202020207461726765744E616D6573706163653D22687474703A2F2F666C6F7761626C652E6F72672F70726F636573736573223E0D0A0D0A202020203C70726F636573732069643D227265706F727452657669657750726F6365737322206E616D653D22E4B8BEE68AA5E5AEA1E6A0B8E6B581E7A88B223E0D0A20202020202020203C73746172744576656E742069643D22737461727422206E616D653D22E5BC80E5A78B222F3E0D0A20202020202020203C73657175656E6365466C6F772069643D22666C6F77312220736F757263655265663D22737461727422207461726765745265663D227265766965775461736B222F3E0D0A20202020202020200D0A20202020202020203C757365725461736B2069643D227265766965775461736B22206E616D653D22E5AEA1E6A0B8E4B8BEE68AA52220666C6F7761626C653A63616E64696461746547726F7570733D226D616E6167657273223E0D0A2020202020202020202020203C646F63756D656E746174696F6E3EE5AEA1E6A0B8E4B8BEE68AA5E58685E5AEB93C2F646F63756D656E746174696F6E3E0D0A20202020202020203C2F757365725461736B3E0D0A20202020202020200D0A20202020202020203C73657175656E6365466C6F772069643D22666C6F77322220736F757263655265663D227265766965775461736B22207461726765745265663D22617070726F76616C47617465776179222F3E0D0A20202020202020200D0A20202020202020203C6578636C7573697665476174657761792069643D22617070726F76616C4761746577617922206E616D653D22E5AEA1E6A0B8E586B3E5AE9A222F3E0D0A20202020202020200D0A20202020202020203C73657175656E6365466C6F772069643D22666C6F77332220736F757263655265663D22617070726F76616C4761746577617922207461726765745265663D22617070726F766564223E0D0A2020202020202020202020203C636F6E646974696F6E45787072657373696F6E3E3C215B43444154415B247B617070726F766564203D3D20747275657D5D5D3E3C2F636F6E646974696F6E45787072657373696F6E3E0D0A20202020202020203C2F73657175656E6365466C6F773E0D0A20202020202020200D0A20202020202020203C73657175656E6365466C6F772069643D22666C6F77342220736F757263655265663D22617070726F76616C4761746577617922207461726765745265663D2272656A6563746564223E0D0A2020202020202020202020203C636F6E646974696F6E45787072657373696F6E3E3C215B43444154415B247B617070726F766564203D3D2066616C73657D5D5D3E3C2F636F6E646974696F6E45787072657373696F6E3E0D0A20202020202020203C2F73657175656E6365466C6F773E0D0A20202020202020200D0A20202020202020203C656E644576656E742069643D22617070726F76656422206E616D653D22E4B8BEE68AA5E9809AE8BF87222F3E0D0A20202020202020203C656E644576656E742069643D2272656A656374656422206E616D653D22E4B8BEE68AA5E9A9B3E59B9E222F3E0D0A202020203C2F70726F636573733E0D0A3C2F646566696E6974696F6E733E, 0);
INSERT INTO `act_ge_bytearray` VALUES ('ea017e1c-21ec-11f0-a5f1-0a002700000a', 1, 'var-reason', NULL, 0xACED00057E72002B636F6D2E6B697474792E626C6F672E636F6D6D6F6E2E636F6E7374616E742E5265706F7274526561736F6E00000000000000001200007872000E6A6176612E6C616E672E456E756D00000000000000001200007870740007494C4C4547414C, NULL);
INSERT INTO `act_ge_bytearray` VALUES ('ea021a5e-21ec-11f0-a5f1-0a002700000a', 1, 'hist.var-reason', NULL, 0xACED00057E72002B636F6D2E6B697474792E626C6F672E636F6D6D6F6E2E636F6E7374616E742E5265706F7274526561736F6E00000000000000001200007872000E6A6176612E6C616E672E456E756D00000000000000001200007870740007494C4C4547414C, NULL);

-- ----------------------------
-- Table structure for act_ge_property
-- ----------------------------
DROP TABLE IF EXISTS `act_ge_property`;
CREATE TABLE `act_ge_property`  (
  `NAME_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `VALUE_` varchar(300) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `REV_` int NULL DEFAULT NULL,
  PRIMARY KEY (`NAME_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_ge_property
-- ----------------------------
INSERT INTO `act_ge_property` VALUES ('batch.schema.version', '7.0.0.0', 1);
INSERT INTO `act_ge_property` VALUES ('cfg.execution-related-entities-count', 'true', 1);
INSERT INTO `act_ge_property` VALUES ('cfg.task-related-entities-count', 'true', 1);
INSERT INTO `act_ge_property` VALUES ('common.schema.version', '7.0.0.0', 1);
INSERT INTO `act_ge_property` VALUES ('entitylink.schema.version', '7.0.0.0', 1);
INSERT INTO `act_ge_property` VALUES ('eventsubscription.schema.version', '7.0.0.0', 1);
INSERT INTO `act_ge_property` VALUES ('identitylink.schema.version', '7.0.0.0', 1);
INSERT INTO `act_ge_property` VALUES ('job.schema.version', '7.0.0.0', 1);
INSERT INTO `act_ge_property` VALUES ('next.dbid', '1', 1);
INSERT INTO `act_ge_property` VALUES ('schema.history', 'create(7.0.0.0)', 1);
INSERT INTO `act_ge_property` VALUES ('schema.version', '7.0.0.0', 1);
INSERT INTO `act_ge_property` VALUES ('task.schema.version', '7.0.0.0', 1);
INSERT INTO `act_ge_property` VALUES ('variable.schema.version', '7.0.0.0', 1);

-- ----------------------------
-- Table structure for act_hi_actinst
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_actinst`;
CREATE TABLE `act_hi_actinst`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT 1,
  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `ACT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CALL_PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ACT_NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ACT_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `ASSIGNEE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `START_TIME_` datetime(3) NOT NULL,
  `END_TIME_` datetime(3) NULL DEFAULT NULL,
  `TRANSACTION_ORDER_` int NULL DEFAULT NULL,
  `DURATION_` bigint NULL DEFAULT NULL,
  `DELETE_REASON_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_HI_ACT_INST_START`(`START_TIME_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_ACT_INST_END`(`END_TIME_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_ACT_INST_PROCINST`(`PROC_INST_ID_` ASC, `ACT_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_ACT_INST_EXEC`(`EXECUTION_ID_` ASC, `ACT_ID_` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_hi_actinst
-- ----------------------------
INSERT INTO `act_hi_actinst` VALUES ('15e95531-210d-11f0-80d1-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '15e4c147-210d-11f0-80d1-0a0027000009', '15e95530-210d-11f0-80d1-0a0027000009', 'start', NULL, NULL, '开始', 'startEvent', NULL, '2025-04-24 21:07:33.439', '2025-04-24 21:07:33.447', 1, 8, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('15eb7812-210d-11f0-80d1-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '15e4c147-210d-11f0-80d1-0a0027000009', '15e95530-210d-11f0-80d1-0a0027000009', 'flow1', NULL, NULL, NULL, 'sequenceFlow', NULL, '2025-04-24 21:07:33.453', '2025-04-24 21:07:33.453', 2, 0, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('15eb7813-210d-11f0-80d1-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '15e4c147-210d-11f0-80d1-0a0027000009', '15e95530-210d-11f0-80d1-0a0027000009', 'reviewTask', '15f1e0b4-210d-11f0-80d1-0a0027000009', NULL, '审核举报', 'userTask', NULL, '2025-04-24 21:07:33.453', NULL, 3, NULL, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('6b0374df-21d3-11f0-be21-0a002700000a', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '6b0103d5-21d3-11f0-be21-0a002700000a', '6b0374de-21d3-11f0-be21-0a002700000a', 'start', NULL, NULL, '开始', 'startEvent', NULL, '2025-04-25 20:47:16.569', '2025-04-25 20:47:16.574', 1, 5, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('6b048650-21d3-11f0-be21-0a002700000a', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '6b0103d5-21d3-11f0-be21-0a002700000a', '6b0374de-21d3-11f0-be21-0a002700000a', 'flow1', NULL, NULL, NULL, 'sequenceFlow', NULL, '2025-04-25 20:47:16.576', '2025-04-25 20:47:16.576', 2, 0, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('6b048651-21d3-11f0-be21-0a002700000a', 2, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '6b0103d5-21d3-11f0-be21-0a002700000a', '6b0374de-21d3-11f0-be21-0a002700000a', 'reviewTask', '6b096852-21d3-11f0-be21-0a002700000a', NULL, '审核举报', 'userTask', NULL, '2025-04-25 20:47:16.576', '2025-04-25 21:16:14.073', 3, 1737497, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('76a20ef4-2360-11f0-9dca-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '769c428a-2360-11f0-9dca-0a0027000009', '76a1e7e3-2360-11f0-9dca-0a0027000009', 'start', NULL, NULL, '开始', 'startEvent', NULL, '2025-04-27 20:09:26.265', '2025-04-27 20:09:26.277', 1, 12, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('76a51c35-2360-11f0-9dca-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '769c428a-2360-11f0-9dca-0a0027000009', '76a1e7e3-2360-11f0-9dca-0a0027000009', 'flow1', NULL, NULL, NULL, 'sequenceFlow', NULL, '2025-04-27 20:09:26.285', '2025-04-27 20:09:26.285', 2, 0, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('76a51c36-2360-11f0-9dca-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '769c428a-2360-11f0-9dca-0a0027000009', '76a1e7e3-2360-11f0-9dca-0a0027000009', 'reviewTask', '76ae9217-2360-11f0-9dca-0a0027000009', NULL, '审核举报', 'userTask', NULL, '2025-04-27 20:09:26.285', NULL, 3, NULL, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('76a69886-21d7-11f0-8d9c-0a002700000a', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '6b0103d5-21d3-11f0-be21-0a002700000a', '6b0374de-21d3-11f0-be21-0a002700000a', 'flow2', NULL, NULL, NULL, 'sequenceFlow', NULL, '2025-04-25 21:16:14.080', '2025-04-25 21:16:14.080', 1, 0, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('76a6bf97-21d7-11f0-8d9c-0a002700000a', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '6b0103d5-21d3-11f0-be21-0a002700000a', '6b0374de-21d3-11f0-be21-0a002700000a', 'approvalGateway', NULL, NULL, '审核决定', 'exclusiveGateway', NULL, '2025-04-25 21:16:14.081', '2025-04-25 21:16:14.114', 2, 33, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('76abc8a8-21d7-11f0-8d9c-0a002700000a', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '6b0103d5-21d3-11f0-be21-0a002700000a', '6b0374de-21d3-11f0-be21-0a002700000a', 'flow4', NULL, NULL, NULL, 'sequenceFlow', NULL, '2025-04-25 21:16:14.114', '2025-04-25 21:16:14.114', 3, 0, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('76abefb9-21d7-11f0-8d9c-0a002700000a', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '6b0103d5-21d3-11f0-be21-0a002700000a', '6b0374de-21d3-11f0-be21-0a002700000a', 'rejected', NULL, NULL, '举报驳回', 'endEvent', NULL, '2025-04-25 21:16:14.115', '2025-04-25 21:16:14.117', 4, 2, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('bb4bd0e4-19d2-11f0-b05e-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', 'bb4bd0e3-19d2-11f0-b05e-0a0027000009', 'start', NULL, NULL, '开始', 'startEvent', NULL, '2025-04-15 16:22:12.462', '2025-04-15 16:22:12.468', 1, 6, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('bb4d0965-19d2-11f0-b05e-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', 'bb4bd0e3-19d2-11f0-b05e-0a0027000009', 'flow1', NULL, NULL, NULL, 'sequenceFlow', NULL, '2025-04-15 16:22:12.470', '2025-04-15 16:22:12.470', 2, 0, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('bb4d0966-19d2-11f0-b05e-0a0027000009', 2, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', 'bb4bd0e3-19d2-11f0-b05e-0a0027000009', 'reviewTask', 'bb51c457-19d2-11f0-b05e-0a0027000009', NULL, '审核举报', 'userTask', NULL, '2025-04-15 16:22:12.470', '2025-04-15 16:23:46.374', 3, 93904, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('ea021a64-21ec-11f0-a5f1-0a002700000a', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'ea021a63-21ec-11f0-a5f1-0a002700000a', 'start', NULL, NULL, '开始', 'startEvent', NULL, '2025-04-25 23:49:47.049', '2025-04-25 23:49:47.052', 1, 3, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('ea02b6a5-21ec-11f0-a5f1-0a002700000a', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'ea021a63-21ec-11f0-a5f1-0a002700000a', 'flow1', NULL, NULL, NULL, 'sequenceFlow', NULL, '2025-04-25 23:49:47.053', '2025-04-25 23:49:47.053', 2, 0, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('ea02b6a6-21ec-11f0-a5f1-0a002700000a', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'ea021a63-21ec-11f0-a5f1-0a002700000a', 'reviewTask', 'ea05c3e7-21ec-11f0-a5f1-0a002700000a', NULL, '审核举报', 'userTask', NULL, '2025-04-25 23:49:47.053', NULL, 3, NULL, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('f345cd7d-19d2-11f0-b05e-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', 'bb4bd0e3-19d2-11f0-b05e-0a0027000009', 'flow2', NULL, NULL, NULL, 'sequenceFlow', NULL, '2025-04-15 16:23:46.375', '2025-04-15 16:23:46.375', 1, 0, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('f3461b9e-19d2-11f0-b05e-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', 'bb4bd0e3-19d2-11f0-b05e-0a0027000009', 'approvalGateway', NULL, NULL, '审核决定', 'exclusiveGateway', NULL, '2025-04-15 16:23:46.377', '2025-04-15 16:23:46.389', 2, 12, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('f347f05f-19d2-11f0-b05e-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', 'bb4bd0e3-19d2-11f0-b05e-0a0027000009', 'flow3', NULL, NULL, NULL, 'sequenceFlow', NULL, '2025-04-15 16:23:46.389', '2025-04-15 16:23:46.389', 3, 0, NULL, '');
INSERT INTO `act_hi_actinst` VALUES ('f3483e80-19d2-11f0-b05e-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', 'bb4bd0e3-19d2-11f0-b05e-0a0027000009', 'approved', NULL, NULL, '举报通过', 'endEvent', NULL, '2025-04-15 16:23:46.391', '2025-04-15 16:23:46.394', 4, 3, NULL, '');

-- ----------------------------
-- Table structure for act_hi_attachment
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_attachment`;
CREATE TABLE `act_hi_attachment`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DESCRIPTION_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `URL_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CONTENT_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TIME_` datetime(3) NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_hi_attachment
-- ----------------------------

-- ----------------------------
-- Table structure for act_hi_comment
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_comment`;
CREATE TABLE `act_hi_comment`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TIME_` datetime(3) NOT NULL,
  `USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ACTION_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `MESSAGE_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `FULL_MSG_` longblob NULL,
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_hi_comment
-- ----------------------------

-- ----------------------------
-- Table structure for act_hi_detail
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_detail`;
CREATE TABLE `act_hi_detail`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ACT_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `VAR_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `REV_` int NULL DEFAULT NULL,
  `TIME_` datetime(3) NOT NULL,
  `BYTEARRAY_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DOUBLE_` double NULL DEFAULT NULL,
  `LONG_` bigint NULL DEFAULT NULL,
  `TEXT_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TEXT2_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_HI_DETAIL_PROC_INST`(`PROC_INST_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_DETAIL_ACT_INST`(`ACT_INST_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_DETAIL_TIME`(`TIME_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_DETAIL_NAME`(`NAME_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_DETAIL_TASK_ID`(`TASK_ID_` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_hi_detail
-- ----------------------------

-- ----------------------------
-- Table structure for act_hi_entitylink
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_entitylink`;
CREATE TABLE `act_hi_entitylink`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `LINK_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CREATE_TIME_` datetime(3) NULL DEFAULT NULL,
  `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PARENT_ELEMENT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `REF_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `REF_SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `REF_SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ROOT_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ROOT_SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `HIERARCHY_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_HI_ENT_LNK_SCOPE`(`SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC, `LINK_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_ENT_LNK_REF_SCOPE`(`REF_SCOPE_ID_` ASC, `REF_SCOPE_TYPE_` ASC, `LINK_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_ENT_LNK_ROOT_SCOPE`(`ROOT_SCOPE_ID_` ASC, `ROOT_SCOPE_TYPE_` ASC, `LINK_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_ENT_LNK_SCOPE_DEF`(`SCOPE_DEFINITION_ID_` ASC, `SCOPE_TYPE_` ASC, `LINK_TYPE_` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_hi_entitylink
-- ----------------------------

-- ----------------------------
-- Table structure for act_hi_identitylink
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_identitylink`;
CREATE TABLE `act_hi_identitylink`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `GROUP_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CREATE_TIME_` datetime(3) NULL DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_HI_IDENT_LNK_USER`(`USER_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_IDENT_LNK_SCOPE`(`SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_IDENT_LNK_SUB_SCOPE`(`SUB_SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_IDENT_LNK_SCOPE_DEF`(`SCOPE_DEFINITION_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_IDENT_LNK_TASK`(`TASK_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_IDENT_LNK_PROCINST`(`PROC_INST_ID_` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_hi_identitylink
-- ----------------------------
INSERT INTO `act_hi_identitylink` VALUES ('15e50f68-210d-11f0-80d1-0a0027000009', NULL, 'starter', 'helloKittyCatSweet', NULL, '2025-04-24 21:07:33.412', '15e4c147-210d-11f0-80d1-0a0027000009', NULL, NULL, NULL, NULL);
INSERT INTO `act_hi_identitylink` VALUES ('15f22ed5-210d-11f0-80d1-0a0027000009', 'managers', 'candidate', NULL, '15f1e0b4-210d-11f0-80d1-0a0027000009', '2025-04-24 21:07:33.497', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_hi_identitylink` VALUES ('6b0151f6-21d3-11f0-be21-0a002700000a', NULL, 'starter', 'helloKittyCatSweet', NULL, '2025-04-25 20:47:16.555', '6b0103d5-21d3-11f0-be21-0a002700000a', NULL, NULL, NULL, NULL);
INSERT INTO `act_hi_identitylink` VALUES ('6b09b673-21d3-11f0-be21-0a002700000a', 'managers', 'candidate', NULL, '6b096852-21d3-11f0-be21-0a002700000a', '2025-04-25 20:47:16.610', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_hi_identitylink` VALUES ('769d05db-2360-11f0-9dca-0a0027000009', NULL, 'starter', 'kitty', NULL, '2025-04-27 20:09:26.234', '769c428a-2360-11f0-9dca-0a0027000009', NULL, NULL, NULL, NULL);
INSERT INTO `act_hi_identitylink` VALUES ('76a3d965-21d7-11f0-8d9c-0a002700000a', NULL, 'participant', 'kitty', NULL, '2025-04-25 21:16:14.062', '6b0103d5-21d3-11f0-be21-0a002700000a', NULL, NULL, NULL, NULL);
INSERT INTO `act_hi_identitylink` VALUES ('76af0748-2360-11f0-9dca-0a0027000009', 'managers', 'candidate', NULL, '76ae9217-2360-11f0-9dca-0a0027000009', '2025-04-27 20:09:26.350', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_hi_identitylink` VALUES ('bb4b359d-19d2-11f0-b05e-0a0027000009', NULL, 'starter', 'kitty', NULL, '2025-04-15 16:22:12.460', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', NULL, NULL, NULL, NULL);
INSERT INTO `act_hi_identitylink` VALUES ('bb521278-19d2-11f0-b05e-0a0027000009', 'managers', 'candidate', NULL, 'bb51c457-19d2-11f0-b05e-0a0027000009', '2025-04-15 16:22:12.503', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_hi_identitylink` VALUES ('e9fff77b-21ec-11f0-a5f1-0a002700000a', NULL, 'starter', 'kitty', NULL, '2025-04-25 23:49:47.035', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', NULL, NULL, NULL, NULL);
INSERT INTO `act_hi_identitylink` VALUES ('ea05eaf8-21ec-11f0-a5f1-0a002700000a', 'managers', 'candidate', NULL, 'ea05c3e7-21ec-11f0-a5f1-0a002700000a', '2025-04-25 23:49:47.074', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_hi_identitylink` VALUES ('f343d1ac-19d2-11f0-b05e-0a0027000009', NULL, 'participant', 'kitty', NULL, '2025-04-15 16:23:46.362', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for act_hi_procinst
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_procinst`;
CREATE TABLE `act_hi_procinst`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT 1,
  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `BUSINESS_KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `START_TIME_` datetime(3) NOT NULL,
  `END_TIME_` datetime(3) NULL DEFAULT NULL,
  `DURATION_` bigint NULL DEFAULT NULL,
  `START_USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `START_ACT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `END_ACT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUPER_PROCESS_INSTANCE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DELETE_REASON_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CALLBACK_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CALLBACK_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `REFERENCE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `REFERENCE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROPAGATED_STAGE_INST_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `BUSINESS_STATUS_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  UNIQUE INDEX `PROC_INST_ID_`(`PROC_INST_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_PRO_INST_END`(`END_TIME_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_PRO_I_BUSKEY`(`BUSINESS_KEY_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_PRO_SUPER_PROCINST`(`SUPER_PROCESS_INSTANCE_ID_` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_hi_procinst
-- ----------------------------
INSERT INTO `act_hi_procinst` VALUES ('15e4c147-210d-11f0-80d1-0a0027000009', 1, '15e4c147-210d-11f0-80d1-0a0027000009', '14', 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '2025-04-24 21:07:33.409', NULL, NULL, 'helloKittyCatSweet', 'start', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_hi_procinst` VALUES ('6b0103d5-21d3-11f0-be21-0a002700000a', 2, '6b0103d5-21d3-11f0-be21-0a002700000a', '15', 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '2025-04-25 20:47:16.553', '2025-04-25 21:16:14.157', 1737604, 'helloKittyCatSweet', 'start', 'rejected', NULL, NULL, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_hi_procinst` VALUES ('769c428a-2360-11f0-9dca-0a0027000009', 1, '769c428a-2360-11f0-9dca-0a0027000009', '17', 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '2025-04-27 20:09:26.226', NULL, NULL, 'kitty', 'start', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_hi_procinst` VALUES ('bb4ae77c-19d2-11f0-b05e-0a0027000009', 2, 'bb4ae77c-19d2-11f0-b05e-0a0027000009', '13', 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '2025-04-15 16:22:12.456', '2025-04-15 16:23:46.433', 93977, 'kitty', 'start', 'approved', NULL, NULL, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_hi_procinst` VALUES ('e9fff77a-21ec-11f0-a5f1-0a002700000a', 1, 'e9fff77a-21ec-11f0-a5f1-0a002700000a', '16', 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '2025-04-25 23:49:47.035', NULL, NULL, 'kitty', 'start', NULL, NULL, NULL, '', NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for act_hi_taskinst
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_taskinst`;
CREATE TABLE `act_hi_taskinst`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT 1,
  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TASK_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TASK_DEF_KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROPAGATED_STAGE_INST_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PARENT_TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DESCRIPTION_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `OWNER_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ASSIGNEE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `START_TIME_` datetime(3) NOT NULL,
  `CLAIM_TIME_` datetime(3) NULL DEFAULT NULL,
  `END_TIME_` datetime(3) NULL DEFAULT NULL,
  `DURATION_` bigint NULL DEFAULT NULL,
  `DELETE_REASON_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PRIORITY_` int NULL DEFAULT NULL,
  `DUE_DATE_` datetime(3) NULL DEFAULT NULL,
  `FORM_KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  `LAST_UPDATED_TIME_` datetime(3) NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_HI_TASK_SCOPE`(`SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_TASK_SUB_SCOPE`(`SUB_SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_TASK_SCOPE_DEF`(`SCOPE_DEFINITION_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_TASK_INST_PROCINST`(`PROC_INST_ID_` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_hi_taskinst
-- ----------------------------
INSERT INTO `act_hi_taskinst` VALUES ('15f1e0b4-210d-11f0-80d1-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', NULL, 'reviewTask', '15e4c147-210d-11f0-80d1-0a0027000009', '15e95530-210d-11f0-80d1-0a0027000009', NULL, NULL, NULL, NULL, NULL, '审核举报', NULL, '审核举报内容', NULL, NULL, '2025-04-24 21:07:33.453', NULL, NULL, NULL, NULL, 50, NULL, NULL, NULL, '', '2025-04-24 21:07:33.497');
INSERT INTO `act_hi_taskinst` VALUES ('6b096852-21d3-11f0-be21-0a002700000a', 2, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', NULL, 'reviewTask', '6b0103d5-21d3-11f0-be21-0a002700000a', '6b0374de-21d3-11f0-be21-0a002700000a', NULL, NULL, NULL, NULL, NULL, '审核举报', NULL, '审核举报内容', NULL, NULL, '2025-04-25 20:47:16.576', NULL, '2025-04-25 21:16:14.065', 1737489, NULL, 50, NULL, NULL, NULL, '', '2025-04-25 21:16:14.065');
INSERT INTO `act_hi_taskinst` VALUES ('76ae9217-2360-11f0-9dca-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', NULL, 'reviewTask', '769c428a-2360-11f0-9dca-0a0027000009', '76a1e7e3-2360-11f0-9dca-0a0027000009', NULL, NULL, NULL, NULL, NULL, '审核举报', NULL, '审核举报内容', NULL, NULL, '2025-04-27 20:09:26.285', NULL, NULL, NULL, NULL, 50, NULL, NULL, NULL, '', '2025-04-27 20:09:26.349');
INSERT INTO `act_hi_taskinst` VALUES ('bb51c457-19d2-11f0-b05e-0a0027000009', 2, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', NULL, 'reviewTask', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', 'bb4bd0e3-19d2-11f0-b05e-0a0027000009', NULL, NULL, NULL, NULL, NULL, '审核举报', NULL, '审核举报内容', NULL, NULL, '2025-04-15 16:22:12.470', NULL, '2025-04-15 16:23:46.366', 93896, NULL, 50, NULL, NULL, NULL, '', '2025-04-15 16:23:46.366');
INSERT INTO `act_hi_taskinst` VALUES ('ea05c3e7-21ec-11f0-a5f1-0a002700000a', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', NULL, 'reviewTask', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'ea021a63-21ec-11f0-a5f1-0a002700000a', NULL, NULL, NULL, NULL, NULL, '审核举报', NULL, '审核举报内容', NULL, NULL, '2025-04-25 23:49:47.053', NULL, NULL, NULL, NULL, 50, NULL, NULL, NULL, '', '2025-04-25 23:49:47.074');

-- ----------------------------
-- Table structure for act_hi_tsk_log
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_tsk_log`;
CREATE TABLE `act_hi_tsk_log`  (
  `ID_` bigint NOT NULL AUTO_INCREMENT,
  `TYPE_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `TIME_STAMP_` timestamp(3) NOT NULL,
  `USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DATA_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_hi_tsk_log
-- ----------------------------

-- ----------------------------
-- Table structure for act_hi_varinst
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_varinst`;
CREATE TABLE `act_hi_varinst`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT 1,
  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `VAR_TYPE_` varchar(100) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `BYTEARRAY_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DOUBLE_` double NULL DEFAULT NULL,
  `LONG_` bigint NULL DEFAULT NULL,
  `TEXT_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TEXT2_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `META_INFO_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CREATE_TIME_` datetime(3) NULL DEFAULT NULL,
  `LAST_UPDATED_TIME_` datetime(3) NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_HI_PROCVAR_NAME_TYPE`(`NAME_` ASC, `VAR_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_VAR_SCOPE_ID_TYPE`(`SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_VAR_SUB_ID_TYPE`(`SUB_SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_PROCVAR_PROC_INST`(`PROC_INST_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_PROCVAR_TASK_ID`(`TASK_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_HI_PROCVAR_EXE`(`EXECUTION_ID_` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_hi_varinst
-- ----------------------------
INSERT INTO `act_hi_varinst` VALUES ('15e8dffa-210d-11f0-80d1-0a0027000009', 0, '15e4c147-210d-11f0-80d1-0a0027000009', '15e4c147-210d-11f0-80d1-0a0027000009', NULL, 'reason', 'serializable', NULL, NULL, NULL, '15e92e1b-210d-11f0-80d1-0a0027000009', NULL, NULL, NULL, NULL, NULL, '2025-04-24 21:07:33.438', '2025-04-24 21:07:33.438');
INSERT INTO `act_hi_varinst` VALUES ('15e92e1c-210d-11f0-80d1-0a0027000009', 0, '15e4c147-210d-11f0-80d1-0a0027000009', '15e4c147-210d-11f0-80d1-0a0027000009', NULL, 'reportId', 'integer', NULL, NULL, NULL, NULL, NULL, 14, '14', NULL, NULL, '2025-04-24 21:07:33.438', '2025-04-24 21:07:33.438');
INSERT INTO `act_hi_varinst` VALUES ('15e92e1d-210d-11f0-80d1-0a0027000009', 0, '15e4c147-210d-11f0-80d1-0a0027000009', '15e4c147-210d-11f0-80d1-0a0027000009', NULL, 'description', 'string', NULL, NULL, NULL, NULL, NULL, NULL, '啥也没写', NULL, NULL, '2025-04-24 21:07:33.438', '2025-04-24 21:07:33.438');
INSERT INTO `act_hi_varinst` VALUES ('15e92e1e-210d-11f0-80d1-0a0027000009', 0, '15e4c147-210d-11f0-80d1-0a0027000009', '15e4c147-210d-11f0-80d1-0a0027000009', NULL, 'postId', 'integer', NULL, NULL, NULL, NULL, NULL, 3, '3', NULL, NULL, '2025-04-24 21:07:33.438', '2025-04-24 21:07:33.438');
INSERT INTO `act_hi_varinst` VALUES ('15e92e1f-210d-11f0-80d1-0a0027000009', 0, '15e4c147-210d-11f0-80d1-0a0027000009', '15e4c147-210d-11f0-80d1-0a0027000009', NULL, 'userId', 'integer', NULL, NULL, NULL, NULL, NULL, 15, '15', NULL, NULL, '2025-04-24 21:07:33.438', '2025-04-24 21:07:33.438');
INSERT INTO `act_hi_varinst` VALUES ('6b0326b8-21d3-11f0-be21-0a002700000a', 0, '6b0103d5-21d3-11f0-be21-0a002700000a', '6b0103d5-21d3-11f0-be21-0a002700000a', NULL, 'reason', 'serializable', NULL, NULL, NULL, '6b034dc9-21d3-11f0-be21-0a002700000a', NULL, NULL, NULL, NULL, NULL, '2025-04-25 20:47:16.568', '2025-04-25 20:47:16.568');
INSERT INTO `act_hi_varinst` VALUES ('6b034dca-21d3-11f0-be21-0a002700000a', 0, '6b0103d5-21d3-11f0-be21-0a002700000a', '6b0103d5-21d3-11f0-be21-0a002700000a', NULL, 'reportId', 'integer', NULL, NULL, NULL, NULL, NULL, 15, '15', NULL, NULL, '2025-04-25 20:47:16.568', '2025-04-25 20:47:16.568');
INSERT INTO `act_hi_varinst` VALUES ('6b0374db-21d3-11f0-be21-0a002700000a', 0, '6b0103d5-21d3-11f0-be21-0a002700000a', '6b0103d5-21d3-11f0-be21-0a002700000a', NULL, 'description', 'string', NULL, NULL, NULL, NULL, NULL, NULL, '啥也没写', NULL, NULL, '2025-04-25 20:47:16.569', '2025-04-25 20:47:16.569');
INSERT INTO `act_hi_varinst` VALUES ('6b0374dc-21d3-11f0-be21-0a002700000a', 0, '6b0103d5-21d3-11f0-be21-0a002700000a', '6b0103d5-21d3-11f0-be21-0a002700000a', NULL, 'postId', 'integer', NULL, NULL, NULL, NULL, NULL, 3, '3', NULL, NULL, '2025-04-25 20:47:16.569', '2025-04-25 20:47:16.569');
INSERT INTO `act_hi_varinst` VALUES ('6b0374dd-21d3-11f0-be21-0a002700000a', 0, '6b0103d5-21d3-11f0-be21-0a002700000a', '6b0103d5-21d3-11f0-be21-0a002700000a', NULL, 'userId', 'integer', NULL, NULL, NULL, NULL, NULL, 15, '15', NULL, NULL, '2025-04-25 20:47:16.569', '2025-04-25 20:47:16.569');
INSERT INTO `act_hi_varinst` VALUES ('76a1248d-2360-11f0-9dca-0a0027000009', 0, '769c428a-2360-11f0-9dca-0a0027000009', '769c428a-2360-11f0-9dca-0a0027000009', NULL, 'reason', 'serializable', NULL, NULL, NULL, '76a1c0ce-2360-11f0-9dca-0a0027000009', NULL, NULL, NULL, NULL, NULL, '2025-04-27 20:09:26.261', '2025-04-27 20:09:26.261');
INSERT INTO `act_hi_varinst` VALUES ('76a1e7df-2360-11f0-9dca-0a0027000009', 0, '769c428a-2360-11f0-9dca-0a0027000009', '769c428a-2360-11f0-9dca-0a0027000009', NULL, 'reportId', 'integer', NULL, NULL, NULL, NULL, NULL, 17, '17', NULL, NULL, '2025-04-27 20:09:26.264', '2025-04-27 20:09:26.264');
INSERT INTO `act_hi_varinst` VALUES ('76a1e7e0-2360-11f0-9dca-0a0027000009', 0, '769c428a-2360-11f0-9dca-0a0027000009', '769c428a-2360-11f0-9dca-0a0027000009', NULL, 'description', 'string', NULL, NULL, NULL, NULL, NULL, NULL, '文章内容过短', NULL, NULL, '2025-04-27 20:09:26.264', '2025-04-27 20:09:26.264');
INSERT INTO `act_hi_varinst` VALUES ('76a1e7e1-2360-11f0-9dca-0a0027000009', 0, '769c428a-2360-11f0-9dca-0a0027000009', '769c428a-2360-11f0-9dca-0a0027000009', NULL, 'postId', 'integer', NULL, NULL, NULL, NULL, NULL, 2, '2', NULL, NULL, '2025-04-27 20:09:26.264', '2025-04-27 20:09:26.264');
INSERT INTO `act_hi_varinst` VALUES ('76a1e7e2-2360-11f0-9dca-0a0027000009', 0, '769c428a-2360-11f0-9dca-0a0027000009', '769c428a-2360-11f0-9dca-0a0027000009', NULL, 'userId', 'integer', NULL, NULL, NULL, NULL, NULL, 2, '2', NULL, NULL, '2025-04-27 20:09:26.264', '2025-04-27 20:09:26.264');
INSERT INTO `act_hi_varinst` VALUES ('76a31612-21d7-11f0-8d9c-0a002700000a', 0, '6b0103d5-21d3-11f0-be21-0a002700000a', '6b0103d5-21d3-11f0-be21-0a002700000a', NULL, 'approved', 'boolean', NULL, NULL, NULL, NULL, NULL, 0, NULL, NULL, NULL, '2025-04-25 21:16:14.059', '2025-04-25 21:16:14.059');
INSERT INTO `act_hi_varinst` VALUES ('76a38b43-21d7-11f0-8d9c-0a002700000a', 0, '6b0103d5-21d3-11f0-be21-0a002700000a', '6b0103d5-21d3-11f0-be21-0a002700000a', NULL, 'comment', 'string', NULL, NULL, NULL, NULL, NULL, NULL, '经检验，用户举报不合理', NULL, NULL, '2025-04-25 21:16:14.060', '2025-04-25 21:16:14.060');
INSERT INTO `act_hi_varinst` VALUES ('76a38b44-21d7-11f0-8d9c-0a002700000a', 0, '6b0103d5-21d3-11f0-be21-0a002700000a', '6b0103d5-21d3-11f0-be21-0a002700000a', NULL, 'reviewTime', 'date', NULL, NULL, NULL, NULL, NULL, 1745586973938, NULL, NULL, NULL, '2025-04-25 21:16:14.060', '2025-04-25 21:16:14.060');
INSERT INTO `act_hi_varinst` VALUES ('bb4b83be-19d2-11f0-b05e-0a0027000009', 0, 'bb4ae77c-19d2-11f0-b05e-0a0027000009', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', NULL, 'reason', 'string', NULL, NULL, NULL, NULL, NULL, NULL, 'PLAGIARISM', NULL, NULL, '2025-04-15 16:22:12.461', '2025-04-15 16:22:12.461');
INSERT INTO `act_hi_varinst` VALUES ('bb4baacf-19d2-11f0-b05e-0a0027000009', 0, 'bb4ae77c-19d2-11f0-b05e-0a0027000009', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', NULL, 'reportId', 'integer', NULL, NULL, NULL, NULL, NULL, 13, '13', NULL, NULL, '2025-04-15 16:22:12.461', '2025-04-15 16:22:12.461');
INSERT INTO `act_hi_varinst` VALUES ('bb4baad0-19d2-11f0-b05e-0a0027000009', 0, 'bb4ae77c-19d2-11f0-b05e-0a0027000009', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', NULL, 'description', 'string', NULL, NULL, NULL, NULL, NULL, NULL, '抄袭小铃铛在CSDN的文章', NULL, NULL, '2025-04-15 16:22:12.461', '2025-04-15 16:22:12.461');
INSERT INTO `act_hi_varinst` VALUES ('bb4bd0e1-19d2-11f0-b05e-0a0027000009', 0, 'bb4ae77c-19d2-11f0-b05e-0a0027000009', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', NULL, 'postId', 'integer', NULL, NULL, NULL, NULL, NULL, 16, '16', NULL, NULL, '2025-04-15 16:22:12.462', '2025-04-15 16:22:12.462');
INSERT INTO `act_hi_varinst` VALUES ('bb4bd0e2-19d2-11f0-b05e-0a0027000009', 0, 'bb4ae77c-19d2-11f0-b05e-0a0027000009', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', NULL, 'userId', 'integer', NULL, NULL, NULL, NULL, NULL, 2, '2', NULL, NULL, '2025-04-15 16:22:12.462', '2025-04-15 16:22:12.462');
INSERT INTO `act_hi_varinst` VALUES ('ea01cc3d-21ec-11f0-a5f1-0a002700000a', 0, 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', NULL, 'reason', 'serializable', NULL, NULL, NULL, 'ea021a5e-21ec-11f0-a5f1-0a002700000a', NULL, NULL, NULL, NULL, NULL, '2025-04-25 23:49:47.048', '2025-04-25 23:49:47.048');
INSERT INTO `act_hi_varinst` VALUES ('ea021a5f-21ec-11f0-a5f1-0a002700000a', 0, 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', NULL, 'reportId', 'integer', NULL, NULL, NULL, NULL, NULL, 16, '16', NULL, NULL, '2025-04-25 23:49:47.049', '2025-04-25 23:49:47.049');
INSERT INTO `act_hi_varinst` VALUES ('ea021a60-21ec-11f0-a5f1-0a002700000a', 0, 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', NULL, 'description', 'string', NULL, NULL, NULL, NULL, NULL, NULL, '哈哈哈哈哈哈', NULL, NULL, '2025-04-25 23:49:47.049', '2025-04-25 23:49:47.049');
INSERT INTO `act_hi_varinst` VALUES ('ea021a61-21ec-11f0-a5f1-0a002700000a', 0, 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', NULL, 'postId', 'integer', NULL, NULL, NULL, NULL, NULL, 3, '3', NULL, NULL, '2025-04-25 23:49:47.049', '2025-04-25 23:49:47.049');
INSERT INTO `act_hi_varinst` VALUES ('ea021a62-21ec-11f0-a5f1-0a002700000a', 0, 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', NULL, 'userId', 'integer', NULL, NULL, NULL, NULL, NULL, 2, '2', NULL, NULL, '2025-04-25 23:49:47.049', '2025-04-25 23:49:47.049');
INSERT INTO `act_hi_varinst` VALUES ('f343aa99-19d2-11f0-b05e-0a0027000009', 0, 'bb4ae77c-19d2-11f0-b05e-0a0027000009', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', NULL, 'approved', 'boolean', NULL, NULL, NULL, NULL, NULL, 1, NULL, NULL, NULL, '2025-04-15 16:23:46.361', '2025-04-15 16:23:46.361');
INSERT INTO `act_hi_varinst` VALUES ('f343d1aa-19d2-11f0-b05e-0a0027000009', 0, 'bb4ae77c-19d2-11f0-b05e-0a0027000009', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', NULL, 'comment', 'string', NULL, NULL, NULL, NULL, NULL, NULL, '经检验，该用户举报合理', NULL, NULL, '2025-04-15 16:23:46.362', '2025-04-15 16:23:46.362');
INSERT INTO `act_hi_varinst` VALUES ('f343d1ab-19d2-11f0-b05e-0a0027000009', 0, 'bb4ae77c-19d2-11f0-b05e-0a0027000009', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', NULL, 'reviewTime', 'date', NULL, NULL, NULL, NULL, NULL, 1744705426342, NULL, NULL, NULL, '2025-04-15 16:23:46.362', '2025-04-15 16:23:46.362');

-- ----------------------------
-- Table structure for act_id_bytearray
-- ----------------------------
DROP TABLE IF EXISTS `act_id_bytearray`;
CREATE TABLE `act_id_bytearray`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `BYTES_` longblob NULL,
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_id_bytearray
-- ----------------------------

-- ----------------------------
-- Table structure for act_id_group
-- ----------------------------
DROP TABLE IF EXISTS `act_id_group`;
CREATE TABLE `act_id_group`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_id_group
-- ----------------------------

-- ----------------------------
-- Table structure for act_id_info
-- ----------------------------
DROP TABLE IF EXISTS `act_id_info`;
CREATE TABLE `act_id_info`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `USER_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TYPE_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `VALUE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PASSWORD_` longblob NULL,
  `PARENT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_id_info
-- ----------------------------

-- ----------------------------
-- Table structure for act_id_membership
-- ----------------------------
DROP TABLE IF EXISTS `act_id_membership`;
CREATE TABLE `act_id_membership`  (
  `USER_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `GROUP_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  PRIMARY KEY (`USER_ID_`, `GROUP_ID_`) USING BTREE,
  INDEX `ACT_FK_MEMB_GROUP`(`GROUP_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_MEMB_GROUP` FOREIGN KEY (`GROUP_ID_`) REFERENCES `act_id_group` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_MEMB_USER` FOREIGN KEY (`USER_ID_`) REFERENCES `act_id_user` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_id_membership
-- ----------------------------

-- ----------------------------
-- Table structure for act_id_priv
-- ----------------------------
DROP TABLE IF EXISTS `act_id_priv`;
CREATE TABLE `act_id_priv`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  UNIQUE INDEX `ACT_UNIQ_PRIV_NAME`(`NAME_` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_id_priv
-- ----------------------------

-- ----------------------------
-- Table structure for act_id_priv_mapping
-- ----------------------------
DROP TABLE IF EXISTS `act_id_priv_mapping`;
CREATE TABLE `act_id_priv_mapping`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `PRIV_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `GROUP_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_FK_PRIV_MAPPING`(`PRIV_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_PRIV_USER`(`USER_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_PRIV_GROUP`(`GROUP_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_PRIV_MAPPING` FOREIGN KEY (`PRIV_ID_`) REFERENCES `act_id_priv` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_id_priv_mapping
-- ----------------------------

-- ----------------------------
-- Table structure for act_id_property
-- ----------------------------
DROP TABLE IF EXISTS `act_id_property`;
CREATE TABLE `act_id_property`  (
  `NAME_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `VALUE_` varchar(300) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `REV_` int NULL DEFAULT NULL,
  PRIMARY KEY (`NAME_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_id_property
-- ----------------------------
INSERT INTO `act_id_property` VALUES ('schema.version', '7.0.0.0', 1);

-- ----------------------------
-- Table structure for act_id_token
-- ----------------------------
DROP TABLE IF EXISTS `act_id_token`;
CREATE TABLE `act_id_token`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `TOKEN_VALUE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TOKEN_DATE_` timestamp(3) NULL DEFAULT NULL,
  `IP_ADDRESS_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `USER_AGENT_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TOKEN_DATA_` varchar(2000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_id_token
-- ----------------------------

-- ----------------------------
-- Table structure for act_id_user
-- ----------------------------
DROP TABLE IF EXISTS `act_id_user`;
CREATE TABLE `act_id_user`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `FIRST_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `LAST_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DISPLAY_NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EMAIL_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PWD_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PICTURE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_id_user
-- ----------------------------

-- ----------------------------
-- Table structure for act_procdef_info
-- ----------------------------
DROP TABLE IF EXISTS `act_procdef_info`;
CREATE TABLE `act_procdef_info`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `INFO_JSON_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  UNIQUE INDEX `ACT_UNIQ_INFO_PROCDEF`(`PROC_DEF_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_INFO_PROCDEF`(`PROC_DEF_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_INFO_JSON_BA`(`INFO_JSON_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_INFO_JSON_BA` FOREIGN KEY (`INFO_JSON_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_INFO_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_procdef_info
-- ----------------------------

-- ----------------------------
-- Table structure for act_re_deployment
-- ----------------------------
DROP TABLE IF EXISTS `act_re_deployment`;
CREATE TABLE `act_re_deployment`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  `DEPLOY_TIME_` timestamp(3) NULL DEFAULT NULL,
  `DERIVED_FROM_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DERIVED_FROM_ROOT_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PARENT_DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ENGINE_VERSION_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_re_deployment
-- ----------------------------
INSERT INTO `act_re_deployment` VALUES ('b12f5199-19d2-11f0-b05e-0a0027000009', '举报审核流程', NULL, NULL, '', '2025-04-15 16:21:55.498', NULL, NULL, 'b12f5199-19d2-11f0-b05e-0a0027000009', NULL);

-- ----------------------------
-- Table structure for act_re_model
-- ----------------------------
DROP TABLE IF EXISTS `act_re_model`;
CREATE TABLE `act_re_model`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
  `LAST_UPDATE_TIME_` timestamp(3) NULL DEFAULT NULL,
  `VERSION_` int NULL DEFAULT NULL,
  `META_INFO_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DEPLOYMENT_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EDITOR_SOURCE_VALUE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EDITOR_SOURCE_EXTRA_VALUE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_FK_MODEL_SOURCE`(`EDITOR_SOURCE_VALUE_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_MODEL_SOURCE_EXTRA`(`EDITOR_SOURCE_EXTRA_VALUE_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_MODEL_DEPLOYMENT`(`DEPLOYMENT_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_MODEL_DEPLOYMENT` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_re_deployment` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_MODEL_SOURCE` FOREIGN KEY (`EDITOR_SOURCE_VALUE_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_MODEL_SOURCE_EXTRA` FOREIGN KEY (`EDITOR_SOURCE_EXTRA_VALUE_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_re_model
-- ----------------------------

-- ----------------------------
-- Table structure for act_re_procdef
-- ----------------------------
DROP TABLE IF EXISTS `act_re_procdef`;
CREATE TABLE `act_re_procdef`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `VERSION_` int NOT NULL,
  `DEPLOYMENT_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `RESOURCE_NAME_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DGRM_RESOURCE_NAME_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DESCRIPTION_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `HAS_START_FORM_KEY_` tinyint NULL DEFAULT NULL,
  `HAS_GRAPHICAL_NOTATION_` tinyint NULL DEFAULT NULL,
  `SUSPENSION_STATE_` int NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  `ENGINE_VERSION_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DERIVED_FROM_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DERIVED_FROM_ROOT_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DERIVED_VERSION_` int NOT NULL DEFAULT 0,
  PRIMARY KEY (`ID_`) USING BTREE,
  UNIQUE INDEX `ACT_UNIQ_PROCDEF`(`KEY_` ASC, `VERSION_` ASC, `DERIVED_VERSION_` ASC, `TENANT_ID_` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_re_procdef
-- ----------------------------
INSERT INTO `act_re_procdef` VALUES ('reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', 1, 'http://flowable.org/processes', '举报审核流程', 'reportReviewProcess', 1, 'b12f5199-19d2-11f0-b05e-0a0027000009', 'workflow/reportReviewProcess.bpmn20.xml', NULL, NULL, 0, 0, 1, '', NULL, NULL, NULL, 0);

-- ----------------------------
-- Table structure for act_ru_actinst
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_actinst`;
CREATE TABLE `act_ru_actinst`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT 1,
  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `ACT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CALL_PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ACT_NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ACT_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `ASSIGNEE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `START_TIME_` datetime(3) NOT NULL,
  `END_TIME_` datetime(3) NULL DEFAULT NULL,
  `DURATION_` bigint NULL DEFAULT NULL,
  `TRANSACTION_ORDER_` int NULL DEFAULT NULL,
  `DELETE_REASON_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_RU_ACTI_START`(`START_TIME_` ASC) USING BTREE,
  INDEX `ACT_IDX_RU_ACTI_END`(`END_TIME_` ASC) USING BTREE,
  INDEX `ACT_IDX_RU_ACTI_PROC`(`PROC_INST_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_RU_ACTI_PROC_ACT`(`PROC_INST_ID_` ASC, `ACT_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_RU_ACTI_EXEC`(`EXECUTION_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_RU_ACTI_EXEC_ACT`(`EXECUTION_ID_` ASC, `ACT_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_RU_ACTI_TASK`(`TASK_ID_` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_ru_actinst
-- ----------------------------
INSERT INTO `act_ru_actinst` VALUES ('15e95531-210d-11f0-80d1-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '15e4c147-210d-11f0-80d1-0a0027000009', '15e95530-210d-11f0-80d1-0a0027000009', 'start', NULL, NULL, '开始', 'startEvent', NULL, '2025-04-24 21:07:33.439', '2025-04-24 21:07:33.447', 8, 1, NULL, '');
INSERT INTO `act_ru_actinst` VALUES ('15eb7812-210d-11f0-80d1-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '15e4c147-210d-11f0-80d1-0a0027000009', '15e95530-210d-11f0-80d1-0a0027000009', 'flow1', NULL, NULL, NULL, 'sequenceFlow', NULL, '2025-04-24 21:07:33.453', '2025-04-24 21:07:33.453', 0, 2, NULL, '');
INSERT INTO `act_ru_actinst` VALUES ('15eb7813-210d-11f0-80d1-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '15e4c147-210d-11f0-80d1-0a0027000009', '15e95530-210d-11f0-80d1-0a0027000009', 'reviewTask', '15f1e0b4-210d-11f0-80d1-0a0027000009', NULL, '审核举报', 'userTask', NULL, '2025-04-24 21:07:33.453', NULL, NULL, 3, NULL, '');
INSERT INTO `act_ru_actinst` VALUES ('76a20ef4-2360-11f0-9dca-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '769c428a-2360-11f0-9dca-0a0027000009', '76a1e7e3-2360-11f0-9dca-0a0027000009', 'start', NULL, NULL, '开始', 'startEvent', NULL, '2025-04-27 20:09:26.265', '2025-04-27 20:09:26.277', 12, 1, NULL, '');
INSERT INTO `act_ru_actinst` VALUES ('76a51c35-2360-11f0-9dca-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '769c428a-2360-11f0-9dca-0a0027000009', '76a1e7e3-2360-11f0-9dca-0a0027000009', 'flow1', NULL, NULL, NULL, 'sequenceFlow', NULL, '2025-04-27 20:09:26.285', '2025-04-27 20:09:26.285', 0, 2, NULL, '');
INSERT INTO `act_ru_actinst` VALUES ('76a51c36-2360-11f0-9dca-0a0027000009', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', '769c428a-2360-11f0-9dca-0a0027000009', '76a1e7e3-2360-11f0-9dca-0a0027000009', 'reviewTask', '76ae9217-2360-11f0-9dca-0a0027000009', NULL, '审核举报', 'userTask', NULL, '2025-04-27 20:09:26.285', NULL, NULL, 3, NULL, '');
INSERT INTO `act_ru_actinst` VALUES ('ea021a64-21ec-11f0-a5f1-0a002700000a', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'ea021a63-21ec-11f0-a5f1-0a002700000a', 'start', NULL, NULL, '开始', 'startEvent', NULL, '2025-04-25 23:49:47.049', '2025-04-25 23:49:47.052', 3, 1, NULL, '');
INSERT INTO `act_ru_actinst` VALUES ('ea02b6a5-21ec-11f0-a5f1-0a002700000a', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'ea021a63-21ec-11f0-a5f1-0a002700000a', 'flow1', NULL, NULL, NULL, 'sequenceFlow', NULL, '2025-04-25 23:49:47.053', '2025-04-25 23:49:47.053', 0, 2, NULL, '');
INSERT INTO `act_ru_actinst` VALUES ('ea02b6a6-21ec-11f0-a5f1-0a002700000a', 1, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'ea021a63-21ec-11f0-a5f1-0a002700000a', 'reviewTask', 'ea05c3e7-21ec-11f0-a5f1-0a002700000a', NULL, '审核举报', 'userTask', NULL, '2025-04-25 23:49:47.053', NULL, NULL, 3, NULL, '');

-- ----------------------------
-- Table structure for act_ru_deadletter_job
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_deadletter_job`;
CREATE TABLE `act_ru_deadletter_job`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `EXCLUSIVE_` tinyint(1) NULL DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROCESS_INSTANCE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ELEMENT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ELEMENT_NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CORRELATION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EXCEPTION_STACK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EXCEPTION_MSG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
  `REPEAT_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `HANDLER_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `HANDLER_CFG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CUSTOM_VALUES_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_DEADLETTER_JOB_EXCEPTION_STACK_ID`(`EXCEPTION_STACK_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_DEADLETTER_JOB_CUSTOM_VALUES_ID`(`CUSTOM_VALUES_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_DEADLETTER_JOB_CORRELATION_ID`(`CORRELATION_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_DJOB_SCOPE`(`SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_DJOB_SUB_SCOPE`(`SUB_SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_DJOB_SCOPE_DEF`(`SCOPE_DEFINITION_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_FK_DEADLETTER_JOB_EXECUTION`(`EXECUTION_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE`(`PROCESS_INSTANCE_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_DEADLETTER_JOB_PROC_DEF`(`PROC_DEF_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_DEADLETTER_JOB_CUSTOM_VALUES` FOREIGN KEY (`CUSTOM_VALUES_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_DEADLETTER_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_DEADLETTER_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_DEADLETTER_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_ru_deadletter_job
-- ----------------------------

-- ----------------------------
-- Table structure for act_ru_entitylink
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_entitylink`;
CREATE TABLE `act_ru_entitylink`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `CREATE_TIME_` datetime(3) NULL DEFAULT NULL,
  `LINK_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PARENT_ELEMENT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `REF_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `REF_SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `REF_SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ROOT_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ROOT_SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `HIERARCHY_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_ENT_LNK_SCOPE`(`SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC, `LINK_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_ENT_LNK_REF_SCOPE`(`REF_SCOPE_ID_` ASC, `REF_SCOPE_TYPE_` ASC, `LINK_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_ENT_LNK_ROOT_SCOPE`(`ROOT_SCOPE_ID_` ASC, `ROOT_SCOPE_TYPE_` ASC, `LINK_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_ENT_LNK_SCOPE_DEF`(`SCOPE_DEFINITION_ID_` ASC, `SCOPE_TYPE_` ASC, `LINK_TYPE_` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_ru_entitylink
-- ----------------------------

-- ----------------------------
-- Table structure for act_ru_event_subscr
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_event_subscr`;
CREATE TABLE `act_ru_event_subscr`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `EVENT_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `EVENT_NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ACTIVITY_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CONFIGURATION_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CREATED_` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUB_SCOPE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_DEFINITION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_TYPE_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `LOCK_TIME_` timestamp(3) NULL DEFAULT NULL,
  `LOCK_OWNER_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_EVENT_SUBSCR_CONFIG_`(`CONFIGURATION_` ASC) USING BTREE,
  INDEX `ACT_IDX_EVENT_SUBSCR_SCOPEREF_`(`SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_FK_EVENT_EXEC`(`EXECUTION_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_EVENT_EXEC` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_ru_event_subscr
-- ----------------------------

-- ----------------------------
-- Table structure for act_ru_execution
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_execution`;
CREATE TABLE `act_ru_execution`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `BUSINESS_KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PARENT_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUPER_EXEC_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ACT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `IS_ACTIVE_` tinyint NULL DEFAULT NULL,
  `IS_CONCURRENT_` tinyint NULL DEFAULT NULL,
  `IS_SCOPE_` tinyint NULL DEFAULT NULL,
  `IS_EVENT_SCOPE_` tinyint NULL DEFAULT NULL,
  `IS_MI_ROOT_` tinyint NULL DEFAULT NULL,
  `SUSPENSION_STATE_` int NULL DEFAULT NULL,
  `CACHED_ENT_STATE_` int NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `START_ACT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `START_TIME_` datetime(3) NULL DEFAULT NULL,
  `START_USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `LOCK_TIME_` timestamp(3) NULL DEFAULT NULL,
  `LOCK_OWNER_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `IS_COUNT_ENABLED_` tinyint NULL DEFAULT NULL,
  `EVT_SUBSCR_COUNT_` int NULL DEFAULT NULL,
  `TASK_COUNT_` int NULL DEFAULT NULL,
  `JOB_COUNT_` int NULL DEFAULT NULL,
  `TIMER_JOB_COUNT_` int NULL DEFAULT NULL,
  `SUSP_JOB_COUNT_` int NULL DEFAULT NULL,
  `DEADLETTER_JOB_COUNT_` int NULL DEFAULT NULL,
  `EXTERNAL_WORKER_JOB_COUNT_` int NULL DEFAULT NULL,
  `VAR_COUNT_` int NULL DEFAULT NULL,
  `ID_LINK_COUNT_` int NULL DEFAULT NULL,
  `CALLBACK_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CALLBACK_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `REFERENCE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `REFERENCE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROPAGATED_STAGE_INST_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `BUSINESS_STATUS_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_EXEC_BUSKEY`(`BUSINESS_KEY_` ASC) USING BTREE,
  INDEX `ACT_IDC_EXEC_ROOT`(`ROOT_PROC_INST_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_EXEC_REF_ID_`(`REFERENCE_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_EXE_PROCINST`(`PROC_INST_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_EXE_PARENT`(`PARENT_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_EXE_SUPER`(`SUPER_EXEC_` ASC) USING BTREE,
  INDEX `ACT_FK_EXE_PROCDEF`(`PROC_DEF_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_EXE_PARENT` FOREIGN KEY (`PARENT_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_EXE_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_EXE_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ACT_FK_EXE_SUPER` FOREIGN KEY (`SUPER_EXEC_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_ru_execution
-- ----------------------------
INSERT INTO `act_ru_execution` VALUES ('15e4c147-210d-11f0-80d1-0a0027000009', 1, '15e4c147-210d-11f0-80d1-0a0027000009', '14', NULL, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', NULL, '15e4c147-210d-11f0-80d1-0a0027000009', NULL, 1, 0, 1, 0, 0, 1, NULL, '', NULL, 'start', '2025-04-24 21:07:33.409', 'helloKittyCatSweet', NULL, NULL, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_ru_execution` VALUES ('15e95530-210d-11f0-80d1-0a0027000009', 1, '15e4c147-210d-11f0-80d1-0a0027000009', NULL, '15e4c147-210d-11f0-80d1-0a0027000009', 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', NULL, '15e4c147-210d-11f0-80d1-0a0027000009', 'reviewTask', 1, 0, 0, 0, 0, 1, NULL, '', NULL, NULL, '2025-04-24 21:07:33.439', NULL, NULL, NULL, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_ru_execution` VALUES ('769c428a-2360-11f0-9dca-0a0027000009', 1, '769c428a-2360-11f0-9dca-0a0027000009', '17', NULL, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', NULL, '769c428a-2360-11f0-9dca-0a0027000009', NULL, 1, 0, 1, 0, 0, 1, NULL, '', NULL, 'start', '2025-04-27 20:09:26.226', 'kitty', NULL, NULL, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_ru_execution` VALUES ('76a1e7e3-2360-11f0-9dca-0a0027000009', 1, '769c428a-2360-11f0-9dca-0a0027000009', NULL, '769c428a-2360-11f0-9dca-0a0027000009', 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', NULL, '769c428a-2360-11f0-9dca-0a0027000009', 'reviewTask', 1, 0, 0, 0, 0, 1, NULL, '', NULL, NULL, '2025-04-27 20:09:26.264', NULL, NULL, NULL, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_ru_execution` VALUES ('e9fff77a-21ec-11f0-a5f1-0a002700000a', 1, 'e9fff77a-21ec-11f0-a5f1-0a002700000a', '16', NULL, 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', NULL, 'e9fff77a-21ec-11f0-a5f1-0a002700000a', NULL, 1, 0, 1, 0, 0, 1, NULL, '', NULL, 'start', '2025-04-25 23:49:47.035', 'kitty', NULL, NULL, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_ru_execution` VALUES ('ea021a63-21ec-11f0-a5f1-0a002700000a', 1, 'e9fff77a-21ec-11f0-a5f1-0a002700000a', NULL, 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', NULL, 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'reviewTask', 1, 0, 0, 0, 0, 1, NULL, '', NULL, NULL, '2025-04-25 23:49:47.049', NULL, NULL, NULL, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for act_ru_external_job
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_external_job`;
CREATE TABLE `act_ru_external_job`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `LOCK_EXP_TIME_` timestamp(3) NULL DEFAULT NULL,
  `LOCK_OWNER_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EXCLUSIVE_` tinyint(1) NULL DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROCESS_INSTANCE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ELEMENT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ELEMENT_NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CORRELATION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `RETRIES_` int NULL DEFAULT NULL,
  `EXCEPTION_STACK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EXCEPTION_MSG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
  `REPEAT_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `HANDLER_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `HANDLER_CFG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CUSTOM_VALUES_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_EXTERNAL_JOB_EXCEPTION_STACK_ID`(`EXCEPTION_STACK_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_EXTERNAL_JOB_CUSTOM_VALUES_ID`(`CUSTOM_VALUES_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_EXTERNAL_JOB_CORRELATION_ID`(`CORRELATION_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_EJOB_SCOPE`(`SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_EJOB_SUB_SCOPE`(`SUB_SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_EJOB_SCOPE_DEF`(`SCOPE_DEFINITION_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_EXTERNAL_JOB_CUSTOM_VALUES` FOREIGN KEY (`CUSTOM_VALUES_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_EXTERNAL_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_ru_external_job
-- ----------------------------

-- ----------------------------
-- Table structure for act_ru_history_job
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_history_job`;
CREATE TABLE `act_ru_history_job`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `LOCK_EXP_TIME_` timestamp(3) NULL DEFAULT NULL,
  `LOCK_OWNER_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `RETRIES_` int NULL DEFAULT NULL,
  `EXCEPTION_STACK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EXCEPTION_MSG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `HANDLER_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `HANDLER_CFG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CUSTOM_VALUES_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ADV_HANDLER_CFG_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
  `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_ru_history_job
-- ----------------------------

-- ----------------------------
-- Table structure for act_ru_identitylink
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_identitylink`;
CREATE TABLE `act_ru_identitylink`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `GROUP_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `USER_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_IDENT_LNK_USER`(`USER_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_IDENT_LNK_GROUP`(`GROUP_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_IDENT_LNK_SCOPE`(`SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_IDENT_LNK_SUB_SCOPE`(`SUB_SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_IDENT_LNK_SCOPE_DEF`(`SCOPE_DEFINITION_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_ATHRZ_PROCEDEF`(`PROC_DEF_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_TSKASS_TASK`(`TASK_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_IDL_PROCINST`(`PROC_INST_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_ATHRZ_PROCEDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_IDL_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_TSKASS_TASK` FOREIGN KEY (`TASK_ID_`) REFERENCES `act_ru_task` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_ru_identitylink
-- ----------------------------
INSERT INTO `act_ru_identitylink` VALUES ('15e50f68-210d-11f0-80d1-0a0027000009', 1, NULL, 'starter', 'helloKittyCatSweet', NULL, '15e4c147-210d-11f0-80d1-0a0027000009', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_ru_identitylink` VALUES ('15f22ed5-210d-11f0-80d1-0a0027000009', 1, 'managers', 'candidate', NULL, '15f1e0b4-210d-11f0-80d1-0a0027000009', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_ru_identitylink` VALUES ('769d05db-2360-11f0-9dca-0a0027000009', 1, NULL, 'starter', 'kitty', NULL, '769c428a-2360-11f0-9dca-0a0027000009', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_ru_identitylink` VALUES ('76af0748-2360-11f0-9dca-0a0027000009', 1, 'managers', 'candidate', NULL, '76ae9217-2360-11f0-9dca-0a0027000009', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_ru_identitylink` VALUES ('e9fff77b-21ec-11f0-a5f1-0a002700000a', 1, NULL, 'starter', 'kitty', NULL, 'e9fff77a-21ec-11f0-a5f1-0a002700000a', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_ru_identitylink` VALUES ('ea05eaf8-21ec-11f0-a5f1-0a002700000a', 1, 'managers', 'candidate', NULL, 'ea05c3e7-21ec-11f0-a5f1-0a002700000a', NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for act_ru_job
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_job`;
CREATE TABLE `act_ru_job`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `LOCK_EXP_TIME_` timestamp(3) NULL DEFAULT NULL,
  `LOCK_OWNER_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EXCLUSIVE_` tinyint(1) NULL DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROCESS_INSTANCE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ELEMENT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ELEMENT_NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CORRELATION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `RETRIES_` int NULL DEFAULT NULL,
  `EXCEPTION_STACK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EXCEPTION_MSG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
  `REPEAT_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `HANDLER_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `HANDLER_CFG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CUSTOM_VALUES_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_JOB_EXCEPTION_STACK_ID`(`EXCEPTION_STACK_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_JOB_CUSTOM_VALUES_ID`(`CUSTOM_VALUES_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_JOB_CORRELATION_ID`(`CORRELATION_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_JOB_SCOPE`(`SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_JOB_SUB_SCOPE`(`SUB_SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_JOB_SCOPE_DEF`(`SCOPE_DEFINITION_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_FK_JOB_EXECUTION`(`EXECUTION_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_JOB_PROCESS_INSTANCE`(`PROCESS_INSTANCE_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_JOB_PROC_DEF`(`PROC_DEF_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_JOB_CUSTOM_VALUES` FOREIGN KEY (`CUSTOM_VALUES_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_ru_job
-- ----------------------------

-- ----------------------------
-- Table structure for act_ru_suspended_job
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_suspended_job`;
CREATE TABLE `act_ru_suspended_job`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `EXCLUSIVE_` tinyint(1) NULL DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROCESS_INSTANCE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ELEMENT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ELEMENT_NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CORRELATION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `RETRIES_` int NULL DEFAULT NULL,
  `EXCEPTION_STACK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EXCEPTION_MSG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
  `REPEAT_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `HANDLER_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `HANDLER_CFG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CUSTOM_VALUES_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_SUSPENDED_JOB_EXCEPTION_STACK_ID`(`EXCEPTION_STACK_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_SUSPENDED_JOB_CUSTOM_VALUES_ID`(`CUSTOM_VALUES_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_SUSPENDED_JOB_CORRELATION_ID`(`CORRELATION_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_SJOB_SCOPE`(`SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_SJOB_SUB_SCOPE`(`SUB_SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_SJOB_SCOPE_DEF`(`SCOPE_DEFINITION_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_FK_SUSPENDED_JOB_EXECUTION`(`EXECUTION_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE`(`PROCESS_INSTANCE_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_SUSPENDED_JOB_PROC_DEF`(`PROC_DEF_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_SUSPENDED_JOB_CUSTOM_VALUES` FOREIGN KEY (`CUSTOM_VALUES_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_SUSPENDED_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_SUSPENDED_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_SUSPENDED_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_ru_suspended_job
-- ----------------------------

-- ----------------------------
-- Table structure for act_ru_task
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_task`;
CREATE TABLE `act_ru_task`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TASK_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROPAGATED_STAGE_INST_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PARENT_TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DESCRIPTION_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TASK_DEF_KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `OWNER_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ASSIGNEE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DELEGATION_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PRIORITY_` int NULL DEFAULT NULL,
  `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
  `DUE_DATE_` datetime(3) NULL DEFAULT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUSPENSION_STATE_` int NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  `FORM_KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CLAIM_TIME_` datetime(3) NULL DEFAULT NULL,
  `IS_COUNT_ENABLED_` tinyint NULL DEFAULT NULL,
  `VAR_COUNT_` int NULL DEFAULT NULL,
  `ID_LINK_COUNT_` int NULL DEFAULT NULL,
  `SUB_TASK_COUNT_` int NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_TASK_CREATE`(`CREATE_TIME_` ASC) USING BTREE,
  INDEX `ACT_IDX_TASK_SCOPE`(`SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_TASK_SUB_SCOPE`(`SUB_SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_TASK_SCOPE_DEF`(`SCOPE_DEFINITION_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_FK_TASK_EXE`(`EXECUTION_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_TASK_PROCINST`(`PROC_INST_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_TASK_PROCDEF`(`PROC_DEF_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_TASK_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_TASK_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_TASK_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_ru_task
-- ----------------------------
INSERT INTO `act_ru_task` VALUES ('15f1e0b4-210d-11f0-80d1-0a0027000009', 1, '15e95530-210d-11f0-80d1-0a0027000009', '15e4c147-210d-11f0-80d1-0a0027000009', 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', NULL, NULL, NULL, NULL, NULL, NULL, '审核举报', NULL, '审核举报内容', 'reviewTask', NULL, NULL, NULL, 50, '2025-04-24 21:07:33.453', NULL, NULL, 1, '', NULL, NULL, 1, 0, 1, 0);
INSERT INTO `act_ru_task` VALUES ('76ae9217-2360-11f0-9dca-0a0027000009', 1, '76a1e7e3-2360-11f0-9dca-0a0027000009', '769c428a-2360-11f0-9dca-0a0027000009', 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', NULL, NULL, NULL, NULL, NULL, NULL, '审核举报', NULL, '审核举报内容', 'reviewTask', NULL, NULL, NULL, 50, '2025-04-27 20:09:26.285', NULL, NULL, 1, '', NULL, NULL, 1, 0, 1, 0);
INSERT INTO `act_ru_task` VALUES ('ea05c3e7-21ec-11f0-a5f1-0a002700000a', 1, 'ea021a63-21ec-11f0-a5f1-0a002700000a', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'reportReviewProcess:1:b14d12cb-19d2-11f0-b05e-0a0027000009', NULL, NULL, NULL, NULL, NULL, NULL, '审核举报', NULL, '审核举报内容', 'reviewTask', NULL, NULL, NULL, 50, '2025-04-25 23:49:47.053', NULL, NULL, 1, '', NULL, NULL, 1, 0, 1, 0);

-- ----------------------------
-- Table structure for act_ru_timer_job
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_timer_job`;
CREATE TABLE `act_ru_timer_job`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `LOCK_EXP_TIME_` timestamp(3) NULL DEFAULT NULL,
  `LOCK_OWNER_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EXCLUSIVE_` tinyint(1) NULL DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROCESS_INSTANCE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ELEMENT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `ELEMENT_NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_DEFINITION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CORRELATION_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `RETRIES_` int NULL DEFAULT NULL,
  `EXCEPTION_STACK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `EXCEPTION_MSG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
  `REPEAT_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `HANDLER_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `HANDLER_CFG_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CUSTOM_VALUES_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_TIMER_JOB_EXCEPTION_STACK_ID`(`EXCEPTION_STACK_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_TIMER_JOB_CUSTOM_VALUES_ID`(`CUSTOM_VALUES_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_TIMER_JOB_CORRELATION_ID`(`CORRELATION_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_TIMER_JOB_DUEDATE`(`DUEDATE_` ASC) USING BTREE,
  INDEX `ACT_IDX_TJOB_SCOPE`(`SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_TJOB_SUB_SCOPE`(`SUB_SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_TJOB_SCOPE_DEF`(`SCOPE_DEFINITION_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_FK_TIMER_JOB_EXECUTION`(`EXECUTION_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_TIMER_JOB_PROCESS_INSTANCE`(`PROCESS_INSTANCE_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_TIMER_JOB_PROC_DEF`(`PROC_DEF_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_TIMER_JOB_CUSTOM_VALUES` FOREIGN KEY (`CUSTOM_VALUES_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_TIMER_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_TIMER_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_TIMER_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_TIMER_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_ru_timer_job
-- ----------------------------

-- ----------------------------
-- Table structure for act_ru_variable
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_variable`;
CREATE TABLE `act_ru_variable`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TASK_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUB_SCOPE_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_TYPE_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `BYTEARRAY_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `DOUBLE_` double NULL DEFAULT NULL,
  `LONG_` bigint NULL DEFAULT NULL,
  `TEXT_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TEXT2_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `META_INFO_` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `ACT_IDX_RU_VAR_SCOPE_ID_TYPE`(`SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_IDX_RU_VAR_SUB_ID_TYPE`(`SUB_SCOPE_ID_` ASC, `SCOPE_TYPE_` ASC) USING BTREE,
  INDEX `ACT_FK_VAR_BYTEARRAY`(`BYTEARRAY_ID_` ASC) USING BTREE,
  INDEX `ACT_IDX_VARIABLE_TASK_ID`(`TASK_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_VAR_EXE`(`EXECUTION_ID_` ASC) USING BTREE,
  INDEX `ACT_FK_VAR_PROCINST`(`PROC_INST_ID_` ASC) USING BTREE,
  CONSTRAINT `ACT_FK_VAR_BYTEARRAY` FOREIGN KEY (`BYTEARRAY_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_VAR_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `ACT_FK_VAR_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of act_ru_variable
-- ----------------------------
INSERT INTO `act_ru_variable` VALUES ('15e8dffa-210d-11f0-80d1-0a0027000009', 1, 'serializable', 'reason', '15e4c147-210d-11f0-80d1-0a0027000009', '15e4c147-210d-11f0-80d1-0a0027000009', NULL, NULL, NULL, NULL, '15e7f599-210d-11f0-80d1-0a0027000009', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_ru_variable` VALUES ('15e92e1c-210d-11f0-80d1-0a0027000009', 1, 'integer', 'reportId', '15e4c147-210d-11f0-80d1-0a0027000009', '15e4c147-210d-11f0-80d1-0a0027000009', NULL, NULL, NULL, NULL, NULL, NULL, 14, '14', NULL, NULL);
INSERT INTO `act_ru_variable` VALUES ('15e92e1d-210d-11f0-80d1-0a0027000009', 1, 'string', 'description', '15e4c147-210d-11f0-80d1-0a0027000009', '15e4c147-210d-11f0-80d1-0a0027000009', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '啥也没写', NULL, NULL);
INSERT INTO `act_ru_variable` VALUES ('15e92e1e-210d-11f0-80d1-0a0027000009', 1, 'integer', 'postId', '15e4c147-210d-11f0-80d1-0a0027000009', '15e4c147-210d-11f0-80d1-0a0027000009', NULL, NULL, NULL, NULL, NULL, NULL, 3, '3', NULL, NULL);
INSERT INTO `act_ru_variable` VALUES ('15e92e1f-210d-11f0-80d1-0a0027000009', 1, 'integer', 'userId', '15e4c147-210d-11f0-80d1-0a0027000009', '15e4c147-210d-11f0-80d1-0a0027000009', NULL, NULL, NULL, NULL, NULL, NULL, 15, '15', NULL, NULL);
INSERT INTO `act_ru_variable` VALUES ('76a1248d-2360-11f0-9dca-0a0027000009', 1, 'serializable', 'reason', '769c428a-2360-11f0-9dca-0a0027000009', '769c428a-2360-11f0-9dca-0a0027000009', NULL, NULL, NULL, NULL, '76a0613c-2360-11f0-9dca-0a0027000009', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_ru_variable` VALUES ('76a1e7df-2360-11f0-9dca-0a0027000009', 1, 'integer', 'reportId', '769c428a-2360-11f0-9dca-0a0027000009', '769c428a-2360-11f0-9dca-0a0027000009', NULL, NULL, NULL, NULL, NULL, NULL, 17, '17', NULL, NULL);
INSERT INTO `act_ru_variable` VALUES ('76a1e7e0-2360-11f0-9dca-0a0027000009', 1, 'string', 'description', '769c428a-2360-11f0-9dca-0a0027000009', '769c428a-2360-11f0-9dca-0a0027000009', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '文章内容过短', NULL, NULL);
INSERT INTO `act_ru_variable` VALUES ('76a1e7e1-2360-11f0-9dca-0a0027000009', 1, 'integer', 'postId', '769c428a-2360-11f0-9dca-0a0027000009', '769c428a-2360-11f0-9dca-0a0027000009', NULL, NULL, NULL, NULL, NULL, NULL, 2, '2', NULL, NULL);
INSERT INTO `act_ru_variable` VALUES ('76a1e7e2-2360-11f0-9dca-0a0027000009', 1, 'integer', 'userId', '769c428a-2360-11f0-9dca-0a0027000009', '769c428a-2360-11f0-9dca-0a0027000009', NULL, NULL, NULL, NULL, NULL, NULL, 2, '2', NULL, NULL);
INSERT INTO `act_ru_variable` VALUES ('ea01cc3d-21ec-11f0-a5f1-0a002700000a', 1, 'serializable', 'reason', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', NULL, NULL, NULL, NULL, 'ea017e1c-21ec-11f0-a5f1-0a002700000a', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `act_ru_variable` VALUES ('ea021a5f-21ec-11f0-a5f1-0a002700000a', 1, 'integer', 'reportId', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', NULL, NULL, NULL, NULL, NULL, NULL, 16, '16', NULL, NULL);
INSERT INTO `act_ru_variable` VALUES ('ea021a60-21ec-11f0-a5f1-0a002700000a', 1, 'string', 'description', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '哈哈哈哈哈哈', NULL, NULL);
INSERT INTO `act_ru_variable` VALUES ('ea021a61-21ec-11f0-a5f1-0a002700000a', 1, 'integer', 'postId', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', NULL, NULL, NULL, NULL, NULL, NULL, 3, '3', NULL, NULL);
INSERT INTO `act_ru_variable` VALUES ('ea021a62-21ec-11f0-a5f1-0a002700000a', 1, 'integer', 'userId', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'e9fff77a-21ec-11f0-a5f1-0a002700000a', NULL, NULL, NULL, NULL, NULL, NULL, 2, '2', NULL, NULL);

-- ----------------------------
-- Table structure for flw_channel_definition
-- ----------------------------
DROP TABLE IF EXISTS `flw_channel_definition`;
CREATE TABLE `flw_channel_definition`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `VERSION_` int NULL DEFAULT NULL,
  `KEY_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CREATE_TIME_` datetime(3) NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `RESOURCE_NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DESCRIPTION_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `TYPE_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `IMPLEMENTATION_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  UNIQUE INDEX `ACT_IDX_CHANNEL_DEF_UNIQ`(`KEY_` ASC, `VERSION_` ASC, `TENANT_ID_` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of flw_channel_definition
-- ----------------------------

-- ----------------------------
-- Table structure for flw_ev_databasechangelog
-- ----------------------------
DROP TABLE IF EXISTS `flw_ev_databasechangelog`;
CREATE TABLE `flw_ev_databasechangelog`  (
  `ID` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `AUTHOR` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `FILENAME` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int NOT NULL,
  `EXECTYPE` varchar(10) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `MD5SUM` varchar(35) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DESCRIPTION` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `COMMENTS` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `TAG` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `LIQUIBASE` varchar(20) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CONTEXTS` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `LABELS` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DEPLOYMENT_ID` varchar(10) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of flw_ev_databasechangelog
-- ----------------------------
INSERT INTO `flw_ev_databasechangelog` VALUES ('1', 'flowable', 'org/flowable/eventregistry/db/liquibase/flowable-eventregistry-db-changelog.xml', '2025-04-03 20:49:39', 1, 'EXECUTED', '9:63268f536c469325acef35970312551b', 'createTable tableName=FLW_EVENT_DEPLOYMENT; createTable tableName=FLW_EVENT_RESOURCE; createTable tableName=FLW_EVENT_DEFINITION; createIndex indexName=ACT_IDX_EVENT_DEF_UNIQ, tableName=FLW_EVENT_DEFINITION; createTable tableName=FLW_CHANNEL_DEFIN...', '', NULL, '4.29.2', NULL, NULL, '3684579082');
INSERT INTO `flw_ev_databasechangelog` VALUES ('2', 'flowable', 'org/flowable/eventregistry/db/liquibase/flowable-eventregistry-db-changelog.xml', '2025-04-03 20:49:39', 2, 'EXECUTED', '9:dcb58b7dfd6dbda66939123a96985536', 'addColumn tableName=FLW_CHANNEL_DEFINITION; addColumn tableName=FLW_CHANNEL_DEFINITION', '', NULL, '4.29.2', NULL, NULL, '3684579082');
INSERT INTO `flw_ev_databasechangelog` VALUES ('3', 'flowable', 'org/flowable/eventregistry/db/liquibase/flowable-eventregistry-db-changelog.xml', '2025-04-03 20:49:39', 3, 'EXECUTED', '9:d0c05678d57af23ad93699991e3bf4f6', 'customChange', '', NULL, '4.29.2', NULL, NULL, '3684579082');

-- ----------------------------
-- Table structure for flw_ev_databasechangeloglock
-- ----------------------------
DROP TABLE IF EXISTS `flw_ev_databasechangeloglock`;
CREATE TABLE `flw_ev_databasechangeloglock`  (
  `ID` int NOT NULL,
  `LOCKED` tinyint NOT NULL,
  `LOCKGRANTED` datetime NULL DEFAULT NULL,
  `LOCKEDBY` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of flw_ev_databasechangeloglock
-- ----------------------------
INSERT INTO `flw_ev_databasechangeloglock` VALUES (1, 0, NULL, NULL);

-- ----------------------------
-- Table structure for flw_event_definition
-- ----------------------------
DROP TABLE IF EXISTS `flw_event_definition`;
CREATE TABLE `flw_event_definition`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `VERSION_` int NULL DEFAULT NULL,
  `KEY_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `RESOURCE_NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DESCRIPTION_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE,
  UNIQUE INDEX `ACT_IDX_EVENT_DEF_UNIQ`(`KEY_` ASC, `VERSION_` ASC, `TENANT_ID_` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of flw_event_definition
-- ----------------------------

-- ----------------------------
-- Table structure for flw_event_deployment
-- ----------------------------
DROP TABLE IF EXISTS `flw_event_deployment`;
CREATE TABLE `flw_event_deployment`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `CATEGORY_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DEPLOY_TIME_` datetime(3) NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `PARENT_DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of flw_event_deployment
-- ----------------------------

-- ----------------------------
-- Table structure for flw_event_resource
-- ----------------------------
DROP TABLE IF EXISTS `flw_event_resource`;
CREATE TABLE `flw_event_resource`  (
  `ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `NAME_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `DEPLOYMENT_ID_` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `RESOURCE_BYTES_` longblob NULL,
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of flw_event_resource
-- ----------------------------

-- ----------------------------
-- Table structure for flw_ru_batch
-- ----------------------------
DROP TABLE IF EXISTS `flw_ru_batch`;
CREATE TABLE `flw_ru_batch`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `TYPE_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `SEARCH_KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SEARCH_KEY2_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CREATE_TIME_` datetime(3) NOT NULL,
  `COMPLETE_TIME_` datetime(3) NULL DEFAULT NULL,
  `STATUS_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `BATCH_DOC_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of flw_ru_batch
-- ----------------------------

-- ----------------------------
-- Table structure for flw_ru_batch_part
-- ----------------------------
DROP TABLE IF EXISTS `flw_ru_batch_part`;
CREATE TABLE `flw_ru_batch_part`  (
  `ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `REV_` int NULL DEFAULT NULL,
  `BATCH_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TYPE_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `SCOPE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SUB_SCOPE_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SCOPE_TYPE_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SEARCH_KEY_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `SEARCH_KEY2_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `CREATE_TIME_` datetime(3) NOT NULL,
  `COMPLETE_TIME_` datetime(3) NULL DEFAULT NULL,
  `STATUS_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `RESULT_DOC_ID_` varchar(64) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  `TENANT_ID_` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT '',
  PRIMARY KEY (`ID_`) USING BTREE,
  INDEX `FLW_IDX_BATCH_PART`(`BATCH_ID_` ASC) USING BTREE,
  CONSTRAINT `FLW_FK_BATCH_PART_PARENT` FOREIGN KEY (`BATCH_ID_`) REFERENCES `flw_ru_batch` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of flw_ru_batch_part
-- ----------------------------

-- ----------------------------
-- Table structure for fs_categories
-- ----------------------------
DROP TABLE IF EXISTS `fs_categories`;
CREATE TABLE `fs_categories`  (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `parent_category_id` int NULL DEFAULT NULL,
  `use_count` int NULL DEFAULT NULL,
  PRIMARY KEY (`category_id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of fs_categories
-- ----------------------------
INSERT INTO `fs_categories` VALUES (1, 'C++', 'C++模块', 0, 2);
INSERT INTO `fs_categories` VALUES (2, 'Java', 'Java模块', 0, 1);
INSERT INTO `fs_categories` VALUES (3, 'C++测试', 'C++测试', 1, 0);
INSERT INTO `fs_categories` VALUES (4, 'C++开发', 'C++开发', 1, 0);
INSERT INTO `fs_categories` VALUES (5, '使用vscode', '使用vscode', 2, 0);
INSERT INTO `fs_categories` VALUES (7, 'CUnit单元测试', 'CUnit单元测试', 3, 1);
INSERT INTO `fs_categories` VALUES (8, 'Vue', 'Vue模块\n', 0, 0);
INSERT INTO `fs_categories` VALUES (10, '生活', '日常', 0, 0);

-- ----------------------------
-- Table structure for fs_comments
-- ----------------------------
DROP TABLE IF EXISTS `fs_comments`;
CREATE TABLE `fs_comments`  (
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
  CONSTRAINT `fs_comments_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `fs_posts` (`post_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fs_comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `fs_users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of fs_comments
-- ----------------------------
INSERT INTO `fs_comments` VALUES (1, 1, 4, '这篇文章针不戳', '2025-03-21 12:38:45', 1, 0, NULL, NULL);
INSERT INTO `fs_comments` VALUES (3, 1, 4, '你像个人机', '2025-03-21 15:14:19', 0, 2, NULL, NULL);
INSERT INTO `fs_comments` VALUES (5, 1, 4, '这是第一篇博客的独立的评论', '2025-03-21 15:17:26', 0, 0, NULL, NULL);
INSERT INTO `fs_comments` VALUES (6, 2, 5, '这是对楼上的回复', '2025-03-21 15:14:01', 1, 7, NULL, NULL);
INSERT INTO `fs_comments` VALUES (7, 2, 4, '这是另外一篇文章的评论', '2025-03-21 15:14:41', 1, 0, NULL, NULL);
INSERT INTO `fs_comments` VALUES (9, 2, 2, '我是孙子', '2025-04-10 15:47:39', 1, 6, NULL, NULL);
INSERT INTO `fs_comments` VALUES (11, 16, 2, '楼主写的非常棒，解决了我的问题😀，支持楼主继续发博客', '2025-04-13 21:25:10', 7, 0, NULL, NULL);
INSERT INTO `fs_comments` VALUES (12, 16, 1, '支持楼上', '2025-04-13 21:59:45', 1, 11, NULL, NULL);
INSERT INTO `fs_comments` VALUES (14, 16, 2, '顶顶', '2025-04-14 15:08:32', 6, 11, NULL, NULL);
INSERT INTO `fs_comments` VALUES (15, 16, 2, '棒棒哒', '2025-04-14 16:06:36', 5, 12, NULL, NULL);
INSERT INTO `fs_comments` VALUES (16, 16, 2, '博主加油', '2025-04-14 17:25:49', 0, 0, NULL, NULL);
INSERT INTO `fs_comments` VALUES (18, 13, 15, '？这是只写了一半吗', '2025-04-25 19:22:18', 0, 0, NULL, NULL);
INSERT INTO `fs_comments` VALUES (19, 3, 2, '🤣', '2025-04-25 23:49:10', 0, 0, NULL, NULL);
INSERT INTO `fs_comments` VALUES (20, 16, 15, '向博主看齐🤩', '2025-04-26 09:53:08', 0, 0, NULL, NULL);
INSERT INTO `fs_comments` VALUES (21, 3, 2, 'hi', '2025-04-27 12:54:32', 1, 0, NULL, NULL);
INSERT INTO `fs_comments` VALUES (22, 3, 2, '你好', '2025-04-27 12:54:48', 0, 21, NULL, NULL);
INSERT INTO `fs_comments` VALUES (23, 2, 2, '你好，这是一条评论测试', '2025-04-27 20:09:56', 0, 7, NULL, NULL);
INSERT INTO `fs_comments` VALUES (24, 23, 15, '我认为作者写的很棒，请继续努力', '2025-04-27 20:40:20', 1, 0, NULL, NULL);
INSERT INTO `fs_comments` VALUES (25, 23, 15, '支持', '2025-04-27 20:40:31', 0, 24, NULL, NULL);

-- ----------------------------
-- Table structure for fs_favorites
-- ----------------------------
DROP TABLE IF EXISTS `fs_favorites`;
CREATE TABLE `fs_favorites`  (
  `favorite_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `post_id` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `folder_name` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`favorite_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `post_id`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fs_favorites_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `fs_users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fs_favorites_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `fs_posts` (`post_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of fs_favorites
-- ----------------------------
INSERT INTO `fs_favorites` VALUES (1, 2, 2, '2025-03-21 10:36:27', '默认收藏夹');
INSERT INTO `fs_favorites` VALUES (3, 2, 3, '2025-04-05 17:32:43', 'SpringBoot相关资料');
INSERT INTO `fs_favorites` VALUES (14, 2, 16, '2025-04-27 12:53:38', '报错指南');

-- ----------------------------
-- Table structure for fs_favorites_aud
-- ----------------------------
DROP TABLE IF EXISTS `fs_favorites_aud`;
CREATE TABLE `fs_favorites_aud`  (
  `favorite_id` int NOT NULL,
  `rev` int NOT NULL,
  `revtype` tinyint NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `post_id` int NULL DEFAULT NULL,
  `user_id` int NULL DEFAULT NULL,
  `folder_name` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`rev`, `favorite_id`) USING BTREE,
  CONSTRAINT `FKb34wsa6xxvocb0b46ucm8omcy` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fs_favorites_aud
-- ----------------------------
INSERT INTO `fs_favorites_aud` VALUES (1, 47, 1, '2025-04-05 17:30:34', 2, 2, 'SpringBoot');
INSERT INTO `fs_favorites_aud` VALUES (1, 48, 1, '2025-04-05 17:30:40', 2, 2, '默认文件夹');
INSERT INTO `fs_favorites_aud` VALUES (2, 49, 1, '2025-04-05 17:30:43', 3, 2, '');
INSERT INTO `fs_favorites_aud` VALUES (2, 50, 2, '2025-04-05 17:32:11', NULL, NULL, NULL);
INSERT INTO `fs_favorites_aud` VALUES (1, 51, 1, '2025-04-05 17:47:14', 2, 2, 'SpringBoot相关资料');
INSERT INTO `fs_favorites_aud` VALUES (1, 52, 1, '2025-04-05 17:48:41', 2, 2, '默认收藏夹');
INSERT INTO `fs_favorites_aud` VALUES (4, 242, 0, '2025-04-13 19:53:31', 16, 2, '报错指南');
INSERT INTO `fs_favorites_aud` VALUES (4, 249, 2, '2025-04-13 20:15:42', NULL, NULL, NULL);
INSERT INTO `fs_favorites_aud` VALUES (5, 251, 0, '2025-04-13 20:15:50', 16, 2, '报错指南');
INSERT INTO `fs_favorites_aud` VALUES (6, 450, 0, '2025-04-23 21:15:03', 11, 2, '默认收藏夹');
INSERT INTO `fs_favorites_aud` VALUES (7, 491, 0, '2025-04-24 21:03:20', 3, 15, '默认收藏夹');
INSERT INTO `fs_favorites_aud` VALUES (7, 517, 2, '2025-04-25 19:29:07', NULL, NULL, NULL);
INSERT INTO `fs_favorites_aud` VALUES (8, 518, 0, '2025-04-25 19:39:20', 3, 15, '默认收藏夹');
INSERT INTO `fs_favorites_aud` VALUES (8, 520, 2, '2025-04-25 19:39:32', NULL, NULL, NULL);
INSERT INTO `fs_favorites_aud` VALUES (9, 557, 0, '2025-04-26 09:41:14', 23, 15, '默认收藏夹');
INSERT INTO `fs_favorites_aud` VALUES (9, 559, 2, '2025-04-26 09:41:15', NULL, NULL, NULL);
INSERT INTO `fs_favorites_aud` VALUES (10, 560, 0, '2025-04-26 09:43:28', 23, 15, '默认收藏夹');
INSERT INTO `fs_favorites_aud` VALUES (10, 562, 2, '2025-04-26 09:43:29', NULL, NULL, NULL);
INSERT INTO `fs_favorites_aud` VALUES (11, 564, 0, '2025-04-26 09:45:48', 23, 15, '默认收藏夹');
INSERT INTO `fs_favorites_aud` VALUES (11, 566, 2, '2025-04-26 09:46:06', NULL, NULL, NULL);
INSERT INTO `fs_favorites_aud` VALUES (12, 567, 0, '2025-04-26 09:48:56', 23, 15, '默认收藏夹');
INSERT INTO `fs_favorites_aud` VALUES (12, 569, 2, '2025-04-26 09:48:57', NULL, NULL, NULL);
INSERT INTO `fs_favorites_aud` VALUES (13, 570, 0, '2025-04-26 09:49:00', 23, 15, '默认收藏夹');
INSERT INTO `fs_favorites_aud` VALUES (5, 603, 2, '2025-04-27 12:53:23', NULL, NULL, NULL);
INSERT INTO `fs_favorites_aud` VALUES (14, 604, 0, '2025-04-27 12:53:38', 16, 2, '200');
INSERT INTO `fs_favorites_aud` VALUES (6, 612, 2, '2025-04-27 14:02:13', NULL, NULL, NULL);
INSERT INTO `fs_favorites_aud` VALUES (13, 624, 2, '2025-04-27 20:39:48', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for fs_messages
-- ----------------------------
DROP TABLE IF EXISTS `fs_messages`;
CREATE TABLE `fs_messages`  (
  `message_id` int NOT NULL AUTO_INCREMENT,
  `sender_id` int NOT NULL,
  `receiver_id` int NOT NULL,
  `content` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `is_read` tinyint(1) NULL DEFAULT 0,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `parent_id` int NULL DEFAULT NULL,
  `message_type` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `reason` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `score` int NULL DEFAULT NULL,
  `suspicious` bit(1) NOT NULL,
  `operation` bit(1) NOT NULL,
  PRIMARY KEY (`message_id`) USING BTREE,
  INDEX `sender_id`(`sender_id` ASC) USING BTREE,
  INDEX `receiver_id`(`receiver_id` ASC) USING BTREE,
  CONSTRAINT `fs_messages_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `fs_users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fs_messages_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `fs_users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of fs_messages
-- ----------------------------
INSERT INTO `fs_messages` VALUES (1, 1, 2, '你好呀，这是一条消息', 1, '2025-03-21 10:51:22', 0, NULL, NULL, '2025-04-11 09:22:54', NULL, 0, b'0', b'0');
INSERT INTO `fs_messages` VALUES (2, 2, 1, '你好，这是一个消息测试', 1, '2025-03-26 14:21:28', 0, NULL, NULL, '2025-04-11 09:22:54', NULL, 0, b'0', b'0');
INSERT INTO `fs_messages` VALUES (3, 2, 1, 'Hello，Jonny', 1, '2025-03-30 23:17:45', 2, NULL, NULL, '2025-04-11 09:22:55', NULL, 0, b'0', b'0');
INSERT INTO `fs_messages` VALUES (6, 2, 1, '今天天气真好\n', 1, '2025-03-31 15:31:05', 5, NULL, 'SENT', '2025-04-11 09:22:55', NULL, 0, b'0', b'0');
INSERT INTO `fs_messages` VALUES (11, 2, 1, '你好， 我是Kitty\n', 1, '2025-04-01 09:59:23', 10, NULL, NULL, '2025-04-11 09:22:56', NULL, 0, b'0', b'0');
INSERT INTO `fs_messages` VALUES (12, 2, 1, '🤪', 0, '2025-04-01 12:32:49', 11, NULL, NULL, '2025-04-11 09:22:56', NULL, 0, b'0', b'0');
INSERT INTO `fs_messages` VALUES (14, 3, 2, '你好', 1, '2025-04-01 15:28:13', NULL, NULL, NULL, '2025-04-27 20:54:48', '敏感信息', 80, b'0', b'0');
INSERT INTO `fs_messages` VALUES (16, 15, 2, '大佬求带', 1, '2025-04-16 15:56:24', 0, NULL, 'SENT', '2025-04-25 19:44:47', '', 0, b'0', b'0');
INSERT INTO `fs_messages` VALUES (17, 2, 15, '您关注的作者发布了新文章：未命名文章', 1, '2025-04-16 19:50:04', 16, NULL, 'SENT', '2025-04-25 21:24:44', NULL, 0, b'0', b'0');
INSERT INTO `fs_messages` VALUES (18, 15, 2, 'hello kitty😃', 1, '2025-04-18 11:25:33', 17, NULL, 'SENT', '2025-04-25 19:44:47', '', 0, b'0', b'0');
INSERT INTO `fs_messages` VALUES (19, 15, 2, '🤪晚上好\n', 0, '2025-04-25 20:45:55', 18, NULL, 'SENT', '2025-04-25 20:45:55', '', 0, b'0', b'0');
INSERT INTO `fs_messages` VALUES (20, 15, 2, '您关注的作者发布了新文章：未命名文章', 0, '2025-04-26 11:34:49', 19, NULL, 'SENT', '2025-04-27 11:42:16', NULL, 0, b'0', b'0');
INSERT INTO `fs_messages` VALUES (21, 2, 3, '❤️', 0, '2025-04-27 20:46:52', 14, NULL, 'SENT', '2025-04-27 20:46:52', '', 0, b'0', b'0');

-- ----------------------------
-- Table structure for fs_permissions
-- ----------------------------
DROP TABLE IF EXISTS `fs_permissions`;
CREATE TABLE `fs_permissions`  (
  `permission_id` int NOT NULL AUTO_INCREMENT,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `name` varchar(50) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  PRIMARY KEY (`permission_id`) USING BTREE,
  UNIQUE INDEX `UKpnvtwliis6p05pn6i3ndjrqt2`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 134 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of fs_permissions
-- ----------------------------
INSERT INTO `fs_permissions` VALUES (70, '2025-03-24 19:22:35', '用户可以创建新的分类', '创建分类');
INSERT INTO `fs_permissions` VALUES (71, '2025-03-24 19:22:35', '用户可以编辑自己的分类', '编辑分类');
INSERT INTO `fs_permissions` VALUES (72, '2025-03-24 19:22:35', '用户可以查看所有人创造的分类', '查看分类');
INSERT INTO `fs_permissions` VALUES (73, '2025-03-24 19:22:35', '用户可以创建新的标签', '创建标签');
INSERT INTO `fs_permissions` VALUES (74, '2025-03-24 19:22:35', '用户可以编辑自己的标签', '编辑标签');
INSERT INTO `fs_permissions` VALUES (75, '2025-03-24 19:22:35', '用户可以查看所有人的标签', '查看标签');
INSERT INTO `fs_permissions` VALUES (76, '2025-03-24 19:22:35', '用户可以创建自己的活动', '创建用户活动');
INSERT INTO `fs_permissions` VALUES (77, '2025-03-24 19:22:35', '用户可以查看自己的活动', '查看用户活动');
INSERT INTO `fs_permissions` VALUES (78, '2025-03-24 19:22:35', '用户可以查看用户所属的角色', '查看用户所属的角色');
INSERT INTO `fs_permissions` VALUES (79, '2025-03-24 19:22:36', '用户可以查看自己的评论', '查看评论');
INSERT INTO `fs_permissions` VALUES (80, '2025-03-24 19:22:36', '用户可以编辑自己的评论', '编辑评论');
INSERT INTO `fs_permissions` VALUES (81, '2025-03-24 19:22:36', '用户可以查看所有人的评论', '查看所有人的评论');
INSERT INTO `fs_permissions` VALUES (82, '2025-03-24 19:22:36', '用户可以删除自己发表的评论', '删除发表的评论');
INSERT INTO `fs_permissions` VALUES (83, '2025-03-24 19:22:36', '用户可以删除自己文章下的评论', '删除文章评论');
INSERT INTO `fs_permissions` VALUES (84, '2025-03-24 19:22:36', '用户可以创建自己的收藏', '创建收藏');
INSERT INTO `fs_permissions` VALUES (85, '2025-03-24 19:22:36', '用户可以编辑自己的收藏', '编辑收藏');
INSERT INTO `fs_permissions` VALUES (86, '2025-03-24 19:22:36', '用户可以查看自己的收藏', '查看收藏');
INSERT INTO `fs_permissions` VALUES (87, '2025-03-24 19:22:36', '用户可以删除自己的收藏', '删除收藏');
INSERT INTO `fs_permissions` VALUES (88, '2025-03-24 19:22:36', '用户可以创建自己的消息', '创建消息');
INSERT INTO `fs_permissions` VALUES (89, '2025-03-24 19:22:36', '用户可以编辑自己的消息', '编辑消息');
INSERT INTO `fs_permissions` VALUES (90, '2025-03-24 19:22:36', '用户可以查看自己的消息', '查看消息');
INSERT INTO `fs_permissions` VALUES (91, '2025-03-24 19:22:36', '用户可以删除自己的消息', '删除消息');
INSERT INTO `fs_permissions` VALUES (92, '2025-03-24 19:22:36', '用户可以创建自己的附件', '创建附件');
INSERT INTO `fs_permissions` VALUES (93, '2025-03-24 19:22:36', '用户可以编辑自己的附件', '编辑附件');
INSERT INTO `fs_permissions` VALUES (94, '2025-03-24 19:22:36', '用户可以删除查看所有人的附件', '查看附件');
INSERT INTO `fs_permissions` VALUES (95, '2025-03-24 19:22:36', '用户可以删除自己的附件', '删除附件');
INSERT INTO `fs_permissions` VALUES (96, '2025-03-24 19:22:36', '用户可以创建自己的版本', '创建版本');
INSERT INTO `fs_permissions` VALUES (97, '2025-03-24 19:22:36', '用户可以编辑自己的版本', '编辑版本');
INSERT INTO `fs_permissions` VALUES (98, '2025-03-24 19:22:36', '用户可以查看自己的版本', '查看版本');
INSERT INTO `fs_permissions` VALUES (99, '2025-03-24 19:22:36', '用户可以删除自己的版本', '删除版本');
INSERT INTO `fs_permissions` VALUES (100, '2025-03-24 19:22:36', '用户可以创建自己的举报', '创建举报');
INSERT INTO `fs_permissions` VALUES (101, '2025-03-24 19:22:36', '用户可以编辑自己的举报', '编辑举报');
INSERT INTO `fs_permissions` VALUES (102, '2025-03-24 19:22:36', '用户可以查看自己的举报', '查看举报');
INSERT INTO `fs_permissions` VALUES (103, '2025-03-24 19:22:36', '用户可以删除自己的举报', '删除举报');
INSERT INTO `fs_permissions` VALUES (104, '2025-03-24 19:22:36', '用户可以创建自己的信息', '创建用户');
INSERT INTO `fs_permissions` VALUES (105, '2025-03-24 19:22:36', '用户可以编辑自己的信息', '编辑用户');
INSERT INTO `fs_permissions` VALUES (106, '2025-03-24 19:22:36', '用户可以查看所有人的信息', '查看所有用户');
INSERT INTO `fs_permissions` VALUES (107, '2025-03-24 19:22:36', '用户可以删除自己的信息', '删除用户');
INSERT INTO `fs_permissions` VALUES (108, '2025-03-24 19:22:36', '用户可以删除分类', '删除分类');
INSERT INTO `fs_permissions` VALUES (109, '2025-03-24 19:22:36', '用户可以创建角色所包含的权限', '创建角色所包含的权限');
INSERT INTO `fs_permissions` VALUES (110, '2025-03-24 19:22:36', '用户可以编辑角色所包含的权限', '编辑角色所包含的权限');
INSERT INTO `fs_permissions` VALUES (111, '2025-03-24 19:22:36', '用户可以查看角色所包含的权限', '查看角色所包含的权限');
INSERT INTO `fs_permissions` VALUES (112, '2025-03-24 19:22:36', '用户可以删除角色所包含的权限', '删除角色所包含的权限');
INSERT INTO `fs_permissions` VALUES (113, '2025-03-24 19:22:36', '用户可以删除所有标签', '删除所有标签');
INSERT INTO `fs_permissions` VALUES (114, '2025-03-24 19:22:36', '用户可以编辑自己的活动', '编辑用户活动');
INSERT INTO `fs_permissions` VALUES (115, '2025-03-24 19:22:36', '用户可以删除自己的活动', '删除用户活动');
INSERT INTO `fs_permissions` VALUES (116, '2025-03-24 19:22:36', '用户可以创建用户所属的角色', '创建用户所属的角色');
INSERT INTO `fs_permissions` VALUES (117, '2025-03-24 19:22:36', '用户可以编辑用户所属的角色', '编辑用户所属的角色');
INSERT INTO `fs_permissions` VALUES (118, '2025-03-24 19:22:36', '用户可以删除用户所属的角色', '删除用户所属的角色');
INSERT INTO `fs_permissions` VALUES (119, '2025-03-24 19:22:36', '用户可以编辑所有人的评论', '编辑所有评论');
INSERT INTO `fs_permissions` VALUES (120, '2025-03-24 19:22:36', '用户可以删除所有人的评论', '删除所有评论');
INSERT INTO `fs_permissions` VALUES (121, '2025-03-24 19:22:36', '用户可以删除所有人的消息', '删除所有消息');
INSERT INTO `fs_permissions` VALUES (122, '2025-03-24 19:22:36', '用户可以创建新的权限', '创建权限');
INSERT INTO `fs_permissions` VALUES (123, '2025-03-24 19:22:36', '用户可以编辑自己的权限', '编辑权限');
INSERT INTO `fs_permissions` VALUES (124, '2025-03-24 19:22:36', '用户可以查看自己的权限', '查看权限');
INSERT INTO `fs_permissions` VALUES (125, '2025-03-24 19:22:36', '用户可以删除自己的权限', '删除权限');
INSERT INTO `fs_permissions` VALUES (126, '2025-03-24 19:22:36', '审核用户的举报', '审核举报');
INSERT INTO `fs_permissions` VALUES (127, '2025-03-24 19:22:36', '用户可以删除所有人的举报', '删除所有人的举报');
INSERT INTO `fs_permissions` VALUES (128, '2025-03-24 19:22:36', '用户可以创建新的角色', '创建角色');
INSERT INTO `fs_permissions` VALUES (129, '2025-03-24 19:22:36', '用户可以编辑自己的角色', '编辑角色');
INSERT INTO `fs_permissions` VALUES (130, '2025-03-24 19:22:36', '用户可以查看自己的角色', '查看角色');
INSERT INTO `fs_permissions` VALUES (131, '2025-03-24 19:22:36', '用户可以删除自己的角色', '删除角色');
INSERT INTO `fs_permissions` VALUES (132, '2025-03-24 19:22:36', '用户可以删除自己的信息', '删除所有用户');
INSERT INTO `fs_permissions` VALUES (133, '2025-03-24 19:22:36', '用户可以激活的账户', '是否激活账户');

-- ----------------------------
-- Table structure for fs_post_attachments
-- ----------------------------
DROP TABLE IF EXISTS `fs_post_attachments`;
CREATE TABLE `fs_post_attachments`  (
  `attachment_id` int NOT NULL AUTO_INCREMENT,
  `attachment_name` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `attachment_type` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `created_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `post_id` int NULL DEFAULT NULL,
  `url` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `size` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`attachment_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of fs_post_attachments
-- ----------------------------
INSERT INTO `fs_post_attachments` VALUES (1, 'tmp6654119341685452818-计算机科学与技术学院加分补录.zip', 'application/zip', '2025-03-22 17:04:59', 1, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/archives/1/1742634298956-tmp6654119341685452818-计算机科学与技术学院加分补录.zip', NULL);
INSERT INTO `fs_post_attachments` VALUES (4, 'tmp4304496941237729176-2.png', 'image/png', '2025-04-07 20:40:08', 11, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/11/1744029608020-tmp4304496941237729176-2.png', NULL);
INSERT INTO `fs_post_attachments` VALUES (5, 'tmp4797937238183366197-2.png', 'image/png', '2025-04-07 20:42:06', 12, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/12/1744029726420-tmp4797937238183366197-2.png', NULL);
INSERT INTO `fs_post_attachments` VALUES (6, 'tmp6472320869495803875-2.png', 'image/png', '2025-04-07 20:44:17', 13, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/13/1744029857569-tmp6472320869495803875-2.png', NULL);
INSERT INTO `fs_post_attachments` VALUES (7, 'tmp2725684952046291601-2.png', 'image/png', '2025-04-07 20:47:44', 14, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/14/1744030064391-tmp2725684952046291601-2.png', NULL);
INSERT INTO `fs_post_attachments` VALUES (8, 'tmp12416319274954349252-01.png', 'image/png', '2025-04-07 20:48:21', 14, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/14/1744030100898-tmp12416319274954349252-01.png', NULL);
INSERT INTO `fs_post_attachments` VALUES (9, 'tmp1679700868521692453-2.png', 'image/png', '2025-04-07 20:48:32', 14, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/14/1744030112185-tmp1679700868521692453-2.png', NULL);
INSERT INTO `fs_post_attachments` VALUES (14, 'tmp4664492870873539452-IMG_6476.JPG', NULL, '2025-04-09 21:19:51', 2, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/posts/2/1744204791136-tmp4664492870873539452-IMG_6476.JPG', NULL);
INSERT INTO `fs_post_attachments` VALUES (15, 'tmp2592781799983063999-length_distribution.png', 'image/png', '2025-04-09 22:04:43', 2, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/2/1744207482945-tmp2592781799983063999-length_distribution.png', 114824);
INSERT INTO `fs_post_attachments` VALUES (16, 'tmp8790667147709736222-用户列表_2025_4_6 (1).xlsx', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', '2025-04-09 22:16:02', 2, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/posts/2/1744208162270-tmp8790667147709736222-用户列表_2025_4_6 (1).xlsx', 17498);
INSERT INTO `fs_post_attachments` VALUES (17, 'tmp964781067902368300-image.png', 'image/png', '2025-04-13 14:46:01', 11, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/11/1744526759956-tmp964781067902368300-image.png', 191157);
INSERT INTO `fs_post_attachments` VALUES (18, 'tmp3855278358342627585-image.png', 'image/png', '2025-04-25 11:41:16', 17, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/17/1745552474598-tmp3855278358342627585-image.png', 77254);
INSERT INTO `fs_post_attachments` VALUES (19, 'tmp8192432520308391675-image.png', 'image/png', '2025-04-25 14:37:58', 19, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/19/1745563076748-tmp8192432520308391675-image.png', 74486);
INSERT INTO `fs_post_attachments` VALUES (20, 'tmp8660708803752220951-image.png', 'image/png', '2025-04-25 14:48:14', 21, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/21/1745563692355-tmp8660708803752220951-image.png', 77289);
INSERT INTO `fs_post_attachments` VALUES (21, 'tmp1269905069239645232-image.png', 'image/png', '2025-04-25 15:09:35', 23, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/23/1745564972935-tmp1269905069239645232-image.png', 77441);
INSERT INTO `fs_post_attachments` VALUES (22, 'tmp14708438243895737199-image.png', 'image/png', '2025-04-25 17:03:18', 23, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/23/1745571796204-tmp14708438243895737199-image.png', 74536);
INSERT INTO `fs_post_attachments` VALUES (23, 'tmp6527162678552314545-image.png', 'image/png', '2025-04-25 17:06:58', 23, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/23/1745572012907-tmp6527162678552314545-image.png', 74112);
INSERT INTO `fs_post_attachments` VALUES (24, 'tmp14515325445148263863-image.png', 'image/png', '2025-04-26 11:33:42', 24, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/24/1745638420084-tmp14515325445148263863-image.png', 139414);
INSERT INTO `fs_post_attachments` VALUES (25, 'tmp3472771581423529379-image.png', 'image/png', '2025-04-26 11:34:14', 24, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/24/1745638453382-tmp3472771581423529379-image.png', 235018);

-- ----------------------------
-- Table structure for fs_post_categories
-- ----------------------------
DROP TABLE IF EXISTS `fs_post_categories`;
CREATE TABLE `fs_post_categories`  (
  `post_id` int NOT NULL,
  `category_id` int NOT NULL,
  PRIMARY KEY (`post_id`, `category_id`) USING BTREE,
  INDEX `category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  CONSTRAINT `fs_post_categories_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `fs_posts` (`post_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fs_post_categories_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `fs_categories` (`category_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of fs_post_categories
-- ----------------------------
INSERT INTO `fs_post_categories` VALUES (3, 1);
INSERT INTO `fs_post_categories` VALUES (11, 1);
INSERT INTO `fs_post_categories` VALUES (16, 2);
INSERT INTO `fs_post_categories` VALUES (1, 7);

-- ----------------------------
-- Table structure for fs_post_tags
-- ----------------------------
DROP TABLE IF EXISTS `fs_post_tags`;
CREATE TABLE `fs_post_tags`  (
  `post_id` int NOT NULL,
  `tag_id` int NOT NULL,
  PRIMARY KEY (`post_id`, `tag_id`) USING BTREE,
  INDEX `tag_id`(`tag_id` ASC) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_tag_id`(`tag_id` ASC) USING BTREE,
  INDEX `idx_post_tag`(`post_id` ASC, `tag_id` ASC) USING BTREE,
  CONSTRAINT `fs_post_tags_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `fs_posts` (`post_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fs_post_tags_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `fs_tags` (`tag_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of fs_post_tags
-- ----------------------------
INSERT INTO `fs_post_tags` VALUES (11, 1);
INSERT INTO `fs_post_tags` VALUES (16, 1);
INSERT INTO `fs_post_tags` VALUES (1, 2);
INSERT INTO `fs_post_tags` VALUES (16, 2);
INSERT INTO `fs_post_tags` VALUES (16, 4);
INSERT INTO `fs_post_tags` VALUES (16, 5);
INSERT INTO `fs_post_tags` VALUES (16, 6);
INSERT INTO `fs_post_tags` VALUES (23, 6);
INSERT INTO `fs_post_tags` VALUES (16, 11);

-- ----------------------------
-- Table structure for fs_post_versions
-- ----------------------------
DROP TABLE IF EXISTS `fs_post_versions`;
CREATE TABLE `fs_post_versions`  (
  `version_id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `content` longtext CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `version_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `user_id` int NOT NULL,
  `version` int NOT NULL,
  PRIMARY KEY (`version_id`) USING BTREE,
  INDEX `post_id`(`post_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fs_post_versions_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `fs_posts` (`post_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fs_post_versions_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `fs_users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of fs_post_versions
-- ----------------------------
INSERT INTO `fs_post_versions` VALUES (1, 1, '恶搞开始！！', '2025-03-20 23:01:26', 5, 2);
INSERT INTO `fs_post_versions` VALUES (2, 2, '你好，这是一个互动博客', '2025-03-20 23:04:41', 4, 1);
INSERT INTO `fs_post_versions` VALUES (5, 11, '正在编辑...\n$$\n\n\\sigma = \\sqrt{\\frac{1}{N} \\sum_{i=1}^N (x_i - \\mu)^2}\n\n$$\n\n问题出在 `selectedVersion` 和下拉选项值的比较上。Element Plus 的 `el-select` 组件在比较选项值时使用的是严格相等（`===`），而你的版本数据可能在不同时间获取时创建了新的对象引用，导致比较失败。\n\n以下是解决方案：\n\n1. **使用版本ID作为选项值**：修改 `el-select` 和 `el-option` 使用 `versionId` 作为值，而不是整个对象\n\n2. **添加自定义比较逻辑**：使用 `value-key` 属性指定比较的键\n\n修改后的代码：\n\n```vue\n<script setup>\n// ...其他导入和定义保持不变...\n\n// 版本变更处理\nconst handleVersionChange = (versionId) => {\n  const selected = versions.value.find(v => v.versionId === versionId);\n  if (selected) {\n    selectedVersion.value = selected;\n    emit(\"update:modelValue\", selected);\n  }\n};\n</script>\n\n<template>\n  <div class=\"version-select\">\n    <!-- 显示当前激活的版本 -->\n    <div class=\"active-version\">激活版本：{{ postVersion }}</div>\n\n    <!-- 版本选择下拉框 -->\n    <el-select\n      v-model=\"selectedVersion\"\n      placeholder=\"选择要编辑的版本\"\n      :loading=\"loading\"\n      @change=\"handleVersionChange\"\n      style=\"width: 200px\"\n      value-key=\"versionId\"\n    >\n      <el-option\n        v-for=\"version in versions\"\n        :key=\"version.versionId\"\n        :label=\"`版本 ${version.version}${\n          version.version === postVersion ? \' (已激活)\' : \'\'\n        }`\"\n        :value=\"version\"\n      >\n        <!-- 选项内容保持不变 -->\n      </el-option>\n    </el-select>\n\n    <!-- 其他部分保持不变 -->\n  </div>\n</template>\n```\n\n关键修改点：\n\n1. 添加了 `value-key=\"versionId\"` 属性，告诉 `el-select` 使用 `versionId` 来比较选项\n\n2. 确保 `selectedVersion` 始终是 `versions` 数组中的引用，而不是新创建的对象\n\n如果问题仍然存在，可以尝试以下调试方法：\n\n1. 在模板中添加调试信息：\n```html\n<div>当前选中: {{ selectedVersion?.version }} (ID: {{ selectedVersion?.versionId }})</div>\n<div>版本列表: {{ versions.map(v => v.version) }}</div>\n```\n\n2. 检查 `selectedVersion` 和 `versions` 中的对象是否具有相同的 `versionId` 但不同的引用\n\n3. 确保在 `loadVersions` 和 `handleCreateVersion` 等方法中，更新 `selectedVersion` 时总是使用 `versions` 数组中的引用，而不是新创建的对象\n\n如果还是有问题，可能需要检查后端返回的数据结构是否一致，或者提供更详细的调试信息。', '2025-04-07 20:40:07', 2, 1);
INSERT INTO `fs_post_versions` VALUES (6, 12, '正在编辑...', '2025-04-07 20:42:06', 2, 1);
INSERT INTO `fs_post_versions` VALUES (7, 13, '正在编辑...', '2025-04-07 20:44:17', 2, 1);
INSERT INTO `fs_post_versions` VALUES (8, 14, '正在编辑...', '2025-04-07 20:47:44', 2, 1);
INSERT INTO `fs_post_versions` VALUES (9, 16, 'The error occurs because the content column in the database is too small to store the provided content. Let\'s modify the content field in the Post entity to use @Lob annotation for large text content:\n\n```java\n    @Column(nullable = false)\n    @Lob\n    @Column(columnDefinition = \"LONGTEXT\")\n    private String content;\n ```\n\nThis change will:\n\n1. Use @Lob to indicate this is a Large Object\n2. Set the column type to LONGTEXT in MySQL which can store up to 4GB of text\n3. Prevent the data truncation error when saving large content\nYou\'ll need to update your database schema accordingly. You can use this SQL:\n\n```sql\nALTER TABLE fs_posts MODIFY COLUMN content LONGTEXT;\n ```\n\nThis will allow you to store large amounts of text content in your blog posts without truncation issues.', '2025-04-07 21:02:05', 2, 1);
INSERT INTO `fs_post_versions` VALUES (11, 11, '正在编辑...\n$$\n\n\\sigma = \\sqrt{\\frac{1}{N} \\sum_{i=1}^N (x_i - \\mu)^2}\n\n$$\n', '2025-04-08 21:25:58', 2, 2);
INSERT INTO `fs_post_versions` VALUES (15, 23, '## 一、为什么要学习Vue\n\n1.前端必备技能\n\n2.岗位多，绝大互联网公司都在使用Vue\n\n3.提高开发效率\n\n4.高薪必备技能（Vue2+Vue3）![图片](https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/23/1745564972935-tmp1269905069239645232-image.png)\n### vue亮点：\n\n**亮点1.Vue2+Vue3一套课程全覆盖**\n\n包含最新Vue3语法，紧跟最新生态\n\n如：2023年5月发布的Vue3.3新特性\n\n1.宏函数-defineOptions\n\n2.宏函数-defineModel\n\n**亮点2：不墨迹，主线清晰**\n![图片](https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/23/1745571796204-tmp14708438243895737199-image.png)\n不再讲官网中已经废弃的语法，讲Vue2+3通用的语法\n**亮点3：实战式教学，20+实战案例，2大企业化项目实战**\n\n**亮点4：丰富的配套资料（接口文档，PPT，MD笔记，项目素材，作业）**\n\n**亮点5：进入AI时代，为程序员赋能，利用Chatgpt企业化开发实战**', '2025-04-25 15:45:22', 15, 1);
INSERT INTO `fs_post_versions` VALUES (16, 23, '## 一、为什么要学习Vue\n\n1.前端必备技能\n\n2.岗位多，绝大互联网公司都在使用Vue\n\n3.提高开发效率\n\n4.高薪必备技能（Vue2+Vue3）![图片](https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/23/1745564972935-tmp1269905069239645232-image.png)\n### vue亮点：\n\n**亮点1.Vue2+Vue3一套课程全覆盖**\n\n包含最新Vue3语法，紧跟最新生态\n\n如：2023年5月发布的Vue3.3新特性\n\n1.宏函数-defineOptions\n\n2.宏函数-defineModel\n\n**亮点2：不墨迹，主线清晰**\n![图片](https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/23/1745571796204-tmp14708438243895737199-image.png)\n不再讲官网中已经废弃的语法，讲Vue2+3通用的语法\n**亮点3：实战式教学，20+实战案例，2大企业化项目实战**\n\n**亮点4：丰富的配套资料（接口文档，PPT，MD笔记，项目素材，作业）**\n\n**亮点5：进入AI时代，为程序员赋能，利用Chatgpt企业化开发实战**', '2025-04-25 17:27:03', 15, 2);
INSERT INTO `fs_post_versions` VALUES (17, 24, '![图片](https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/24/1745638420084-tmp14515325445148263863-image.png)\n今日进度\n急急急急急急\n![图片](https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/24/1745638453382-tmp3472771581423529379-image.png)\n', '2025-04-26 11:33:39', 15, 1);
INSERT INTO `fs_post_versions` VALUES (18, 11, '正在编辑...\n$$\n\n\\sigma = \\sqrt{\\frac{1}{N} \\sum_{i=1}^N (x_i - \\mu)^2}\n\n$$\n', '2025-04-27 14:00:20', 2, 3);
INSERT INTO `fs_post_versions` VALUES (19, 23, ' ', '2025-04-27 17:20:05', 15, 3);
INSERT INTO `fs_post_versions` VALUES (21, 13, '正在编辑...', '2025-04-27 20:13:34', 2, 2);
INSERT INTO `fs_post_versions` VALUES (22, 16, 'The error occurs because the content column in the database is too small to store the provided content. Let\'s modify the content field in the Post entity to use @Lob annotation for large text content:\n\n```java\n    @Column(nullable = false)\n    @Lob\n    @Column(columnDefinition = \"LONGTEXT\")\n    private String content;\n ```\n\nThis change will:\n\n1. Use @Lob to indicate this is a Large Object\n2. Set the column type to LONGTEXT in MySQL which can store up to 4GB of text\n3. Prevent the data truncation error when saving large content\nYou\'ll need to update your database schema accordingly. You can use this SQL:\n\n```sql\nALTER TABLE fs_posts MODIFY COLUMN content LONGTEXT;\n ```\n\nThis will allow you to store large amounts of text content in your blog posts without truncation issues.', '2025-04-27 20:21:59', 2, 2);
INSERT INTO `fs_post_versions` VALUES (23, 16, ' ', '2025-04-27 20:43:54', 2, 3);

-- ----------------------------
-- Table structure for fs_posts
-- ----------------------------
DROP TABLE IF EXISTS `fs_posts`;
CREATE TABLE `fs_posts`  (
  `post_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `title` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `content` longtext CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_published` tinyint(1) NULL DEFAULT 1,
  `is_draft` tinyint(1) NULL DEFAULT 0,
  `views` int NULL DEFAULT 0,
  `likes` int NULL DEFAULT 0,
  `favorites` int NULL DEFAULT 0,
  `cover_image` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `version` int NULL DEFAULT NULL,
  `visibility` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `abstract_content` text CHARACTER SET utf16 COLLATE utf16_general_ci NULL,
  PRIMARY KEY (`post_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fs_posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `fs_users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of fs_posts
-- ----------------------------
INSERT INTO `fs_posts` VALUES (1, 5, '777', 'string', '2025-03-20 20:51:06', '2025-04-28 10:43:01', 1, 1, 22, -1, 0, '', 1, 'PUBLIC', b'0', NULL);
INSERT INTO `fs_posts` VALUES (2, 2, '第二篇博客', '你好，这是一个互动博客', '2025-03-20 20:51:30', '2025-04-27 20:59:08', 1, 0, 11, 5, 1, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/posts/2/1744204791136-tmp4664492870873539452-IMG_6476.JPG', 1, 'PUBLIC', b'0', '<think>\n嗯，我最近在学习编程，发现有时候写代码的时候会遇到一些小问题，比如语法错误或者是逻辑上的疏忽。有时候我会觉得特别困惑，不知道该从哪里开始解决。这时候，我就会想到去网上找些资源或者教程来帮助自己。不过，我发现有些时候网络上提供的信息可能不太全面，或者解释得不够清楚，导致我仍然无法解决问题。于是，我想到了这个“编程互助”这个概念，希望能够找到一种更有效的方式来帮助我解决编程中的难题。\n\n有一次，我在写一个函数的时候遇到了一个问题，但总是出错，不知道为什么。我尝试在网上查找资料，但是没有找到合适的解决方案。于是，我就想到了在这个编程互助的社区里寻求帮助。我发了一篇帖子，描述了我的问题，并附上了我的代码。没想到，很快就有人回复了，他们不仅指出了我代码中的错误，还给出了修改后的代码。这让我感到非常高兴，因为我终于解决了这个问题。从那以后，我就经常在这个社区里提问和分享经验，感觉受益匪浅。\n\n不过，我也注意到有时候有些问题并没有得到及时的帮助，甚至有些回复并不准确。有时候，我也会担心自己的隐私会不会被泄露，毕竟我在网上和陌生人交流。所以，我觉得需要更多的安全措施来保护自己的个人信息。 ');
INSERT INTO `fs_posts` VALUES (3, 1, 'SpringBoot报错', 'SpringBoot框架非常繁琐', '2025-04-05 16:18:54', '2025-04-27 12:54:24', 1, 0, 12, 0, 2, NULL, 1, 'PUBLIC', b'0', NULL);
INSERT INTO `fs_posts` VALUES (11, 2, '方差公式', '正在编辑...\n$$\n\n\\sigma = \\sqrt{\\frac{1}{N} \\sum_{i=1}^N (x_i - \\mu)^2}\n\n$$\n\n问题出在 `selectedVersion` 和下拉选项值的比较上。Element Plus 的 `el-select` 组件在比较选项值时使用的是严格相等（`===`），而你的版本数据可能在不同时间获取时创建了新的对象引用，导致比较失败。\n\n以下是解决方案：\n\n1. **使用版本ID作为选项值**：修改 `el-select` 和 `el-option` 使用 `versionId` 作为值，而不是整个对象\n\n2. **添加自定义比较逻辑**：使用 `value-key` 属性指定比较的键\n\n修改后的代码：\n\n```vue\n<script setup>\n// ...其他导入和定义保持不变...\n\n// 版本变更处理\nconst handleVersionChange = (versionId) => {\n  const selected = versions.value.find(v => v.versionId === versionId);\n  if (selected) {\n    selectedVersion.value = selected;\n    emit(\"update:modelValue\", selected);\n  }\n};\n</script>\n\n<template>\n  <div class=\"version-select\">\n    <!-- 显示当前激活的版本 -->\n    <div class=\"active-version\">激活版本：{{ postVersion }}</div>\n\n    <!-- 版本选择下拉框 -->\n    <el-select\n      v-model=\"selectedVersion\"\n      placeholder=\"选择要编辑的版本\"\n      :loading=\"loading\"\n      @change=\"handleVersionChange\"\n      style=\"width: 200px\"\n      value-key=\"versionId\"\n    >\n      <el-option\n        v-for=\"version in versions\"\n        :key=\"version.versionId\"\n        :label=\"`版本 ${version.version}${\n          version.version === postVersion ? \' (已激活)\' : \'\'\n        }`\"\n        :value=\"version\"\n      >\n        <!-- 选项内容保持不变 -->\n      </el-option>\n    </el-select>\n\n    <!-- 其他部分保持不变 -->\n  </div>\n</template>\n```\n\n关键修改点：\n\n1. 添加了 `value-key=\"versionId\"` 属性，告诉 `el-select` 使用 `versionId` 来比较选项\n\n2. 确保 `selectedVersion` 始终是 `versions` 数组中的引用，而不是新创建的对象\n\n如果问题仍然存在，可以尝试以下调试方法：\n\n1. 在模板中添加调试信息：\n```html\n<div>当前选中: {{ selectedVersion?.version }} (ID: {{ selectedVersion?.versionId }})</div>\n<div>版本列表: {{ versions.map(v => v.version) }}</div>\n```\n\n2. 检查 `selectedVersion` 和 `versions` 中的对象是否具有相同的 `versionId` 但不同的引用\n\n3. 确保在 `loadVersions` 和 `handleCreateVersion` 等方法中，更新 `selectedVersion` 时总是使用 `versions` 数组中的引用，而不是新创建的对象\n\n如果还是有问题，可能需要检查后端返回的数据结构是否一致，或者提供更详细的调试信息。', '2025-04-07 20:40:07', '2025-04-27 14:18:11', 1, 0, 12, 0, 1, NULL, 2, 'PUBLIC', b'0', NULL);
INSERT INTO `fs_posts` VALUES (12, 2, '未命名文章', '正在编辑...', '2025-04-07 20:42:06', '2025-04-24 19:52:35', 0, 0, 1, 0, 0, NULL, 1, 'PRIVATE', b'0', NULL);
INSERT INTO `fs_posts` VALUES (13, 2, '未命名文章', '正在编辑...', '2025-04-07 20:44:17', '2025-04-27 20:13:41', 1, 0, 2, 0, 0, NULL, 2, 'PUBLIC', b'0', '正在编辑......');
INSERT INTO `fs_posts` VALUES (14, 2, '未命名文章', '正在编辑...', '2025-04-07 20:47:44', '2025-04-09 17:13:55', 0, 0, 0, 0, 0, NULL, 1, 'PUBLIC', b'1', NULL);
INSERT INTO `fs_posts` VALUES (16, 2, '解决content过长带来的问题', 'The error occurs because the content column in the database is too small to store the provided content. Let\'s modify the content field in the Post entity to use @Lob annotation for large text content:\n\n```java\n    @Column(nullable = false)\n    @Lob\n    @Column(columnDefinition = \"LONGTEXT\")\n    private String content;\n ```\n\nThis change will:\n\n1. Use @Lob to indicate this is a Large Object\n2. Set the column type to LONGTEXT in MySQL which can store up to 4GB of text\n3. Prevent the data truncation error when saving large content\nYou\'ll need to update your database schema accordingly. You can use this SQL:\n\n```sql\nALTER TABLE fs_posts MODIFY COLUMN content LONGTEXT;\n ```\n\nThis will allow you to store large amounts of text content in your blog posts without truncation issues.', '2025-04-07 21:02:05', '2025-04-28 09:54:53', 1, 0, 40, 0, 3, '', 3, 'PUBLIC', b'0', 'The error occurs because the content column in the database is too small to store the provided content. Let\'s modify the content field in the Post entity to use @Lob annotation for large text content:...');
INSERT INTO `fs_posts` VALUES (23, 2, '学习vue day1', '## 一、为什么要学习Vue\n\n1.前端必备技能\n\n2.岗位多，绝大互联网公司都在使用Vue\n\n3.提高开发效率\n\n4.高薪必备技能（Vue2+Vue3）![图片](https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/23/1745564972935-tmp1269905069239645232-image.png)\n### vue亮点：\n\n**亮点1.Vue2+Vue3一套课程全覆盖**\n\n包含最新Vue3语法，紧跟最新生态\n\n如：2023年5月发布的Vue3.3新特性\n\n1.宏函数-defineOptions\n\n2.宏函数-defineModel\n\n**亮点2：不墨迹，主线清晰**\n![图片](https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/23/1745571796204-tmp14708438243895737199-image.png)\n不再讲官网中已经废弃的语法，讲Vue2+3通用的语法\n**亮点3：实战式教学，20+实战案例，2大企业化项目实战**\n\n**亮点4：丰富的配套资料（接口文档，PPT，MD笔记，项目素材，作业）**\n\n**亮点5：进入AI时代，为程序员赋能，利用Chatgpt企业化开发实战**', '2025-04-25 15:09:32', '2025-04-28 10:18:48', 1, 0, 8, 0, 5, '', 2, 'PUBLIC', b'0', '<think>\n今天我决定开始学习Vue框架，因为我听说它在前端开发中非常重要，很多大公司都在用它，所以我想先从基础的学起。首先，我需要了解什么是Vue，为什么它这么火？听说Vue是一个前后端都可以使用的双栈框架，这听起来挺方便的，可以减少维护代码量。但是具体是怎么实现前后端分离的呢？是不是通过传状态的方式？我有点不太明白。\n\n接下来，我要学的基本语法有哪些？比如，new Vue()是创建组件，那如何传入数据呢？用什么方法？还有，Vue中的事件机制是怎样的？比如click, change这些常用的事件怎么处理？另外，如何绑定组件之间的数据流？这些都是我需要掌握的基础知识。\n\n关于组件生命周期管理，我还不太清楚。比如，mount和unmount是什么意思？什么时候用这两个方法？还有child和watch有什么区别？记得以前学过domContentLoaded和domDetach，但当时没怎么用，这次要弄清楚它们的区别和应用场景。\n\n组件引用方面，如何引用父级组件？@for循环是什么？如何使用watch来动态更新引用的组件？这些概念听起来有点抽象，可能需要自己动手写一些例子才能理解透彻。\n\n组件生命周期管理部分，除了mount和unmount，child和watch之外，还有谁是用来管理父级组件的状态变化的吗？比如，有时候需要判断父级是否有新的值出现，该怎么操作？\n\n关于数据绑定，如何使用@Input，如何将组件的数据传递到其他地方？比如，如何将组件的值赋值给一个变量或者数据库？还有，如何使用@Output来接收外部的数据，并将它反馈到组件里？\n\n组件生命周期管理与数据绑定结合起来的话，会是什么样子的场景？比如，在组件mount的时候初始化一些数据，或者在child组件变化时触发某个动作。\n\n组件生命周期管理与数据绑定结合在一起，如何避免重复绑定同一个对象的情况？比如，当父级组件变化的时候，子级组件也跟着变化了，这时候会不会导致子级多次绑定同一对象？\n\n关于组件生命周期管理与数据绑定结合在一起，如何避免重复绑定同一个对象的情况？比如，当父级组件变化的时候，子级组件也跟着变化了，这时候会不会导致子级多次绑定同一对象？ ');
INSERT INTO `fs_posts` VALUES (24, 15, '今日毕设进度汇报', '![图片](https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/24/1745638420084-tmp14515325445148263863-image.png)\n今日进度\n急急急急急急\n![图片](https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/post/24/1745638453382-tmp3472771581423529379-image.png)\n', '2025-04-26 11:33:39', '2025-04-26 11:34:51', 1, 0, 0, 0, 0, '', 1, 'PUBLIC', b'0', '毕设急急急急急');

-- ----------------------------
-- Table structure for fs_reports
-- ----------------------------
DROP TABLE IF EXISTS `fs_reports`;
CREATE TABLE `fs_reports`  (
  `report_id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `user_id` int NOT NULL,
  `reason` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `comment` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `process_instance_id` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`report_id`) USING BTREE,
  INDEX `post_id`(`post_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fs_reports_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `fs_posts` (`post_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fs_reports_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `fs_users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of fs_reports
-- ----------------------------
INSERT INTO `fs_reports` VALUES (6, 2, 2, 'OTHER', '2025-04-10 19:26:40', 'PENDING', NULL, NULL, '没有意思');
INSERT INTO `fs_reports` VALUES (13, 16, 2, 'PLAGIARISM', '2025-04-15 16:22:12', 'APPROVED', '经检验，该用户举报合理', 'bb4ae77c-19d2-11f0-b05e-0a0027000009', '抄袭小铃铛在CSDN的文章');
INSERT INTO `fs_reports` VALUES (15, 3, 15, 'SPAM', '2025-04-25 20:47:16', 'REJECTED', '经检验，用户举报不合理', '6b0103d5-21d3-11f0-be21-0a002700000a', '啥也没写');
INSERT INTO `fs_reports` VALUES (16, 3, 2, 'ILLEGAL', '2025-04-25 23:49:46', 'PENDING', NULL, 'e9fff77a-21ec-11f0-a5f1-0a002700000a', '哈哈哈哈哈哈');
INSERT INTO `fs_reports` VALUES (17, 2, 2, 'INAPPROPRIATE', '2025-04-27 20:09:26', 'PENDING', NULL, '769c428a-2360-11f0-9dca-0a0027000009', '文章内容过短');

-- ----------------------------
-- Table structure for fs_reports_aud
-- ----------------------------
DROP TABLE IF EXISTS `fs_reports_aud`;
CREATE TABLE `fs_reports_aud`  (
  `report_id` int NOT NULL,
  `rev` int NOT NULL,
  `revtype` tinyint NULL DEFAULT NULL,
  `comment` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `post_id` int NULL DEFAULT NULL,
  `process_instance_id` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `reason` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `status` enum('APPROVED','PENDING','REJECTED','REVIEWING') CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `user_id` int NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`rev`, `report_id`) USING BTREE,
  CONSTRAINT `FKlq51xiwrbad4hkbhnt2vgl7vl` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fs_reports_aud
-- ----------------------------
INSERT INTO `fs_reports_aud` VALUES (1, 8, 2, NULL, '2025-04-04 10:40:12', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_reports_aud` VALUES (2, 165, 2, NULL, '2025-04-10 19:24:02', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_reports_aud` VALUES (3, 166, 2, NULL, '2025-04-10 19:30:24', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_reports_aud` VALUES (4, 167, 2, NULL, '2025-04-10 19:33:09', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_reports_aud` VALUES (5, 168, 2, NULL, '2025-04-10 19:40:24', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_reports_aud` VALUES (7, 349, 0, NULL, '2025-04-15 15:19:30', 16, NULL, '垃圾广告 - Deepseek的广告', 'PENDING', 2, NULL);
INSERT INTO `fs_reports_aud` VALUES (7, 350, 2, NULL, '2025-04-15 15:53:55', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_reports_aud` VALUES (8, 351, 0, NULL, '2025-04-15 15:54:46', 16, NULL, 'PLAGIARISM', 'PENDING', 2, '抄袭了小铃铛在CSDN的博客');
INSERT INTO `fs_reports_aud` VALUES (9, 352, 0, NULL, '2025-04-15 16:00:00', 16, NULL, 'PLAGIARISM', 'PENDING', 2, '抄袭了小铃铛在CSDN的博客');
INSERT INTO `fs_reports_aud` VALUES (10, 354, 0, NULL, '2025-04-15 16:05:13', 16, NULL, 'PLAGIARISM', 'PENDING', 2, '抄袭小铃铛在CSDN的文章');
INSERT INTO `fs_reports_aud` VALUES (11, 355, 0, NULL, '2025-04-15 16:08:49', 16, NULL, 'PLAGIARISM', 'PENDING', 2, '抄袭小铃铛在CSDN的文章');
INSERT INTO `fs_reports_aud` VALUES (12, 356, 0, NULL, '2025-04-15 16:18:56', 16, NULL, 'PLAGIARISM', 'PENDING', 2, '抄袭小铃铛在CSDN的文章');
INSERT INTO `fs_reports_aud` VALUES (13, 357, 0, NULL, '2025-04-15 16:22:12', 16, 'bb4ae77c-19d2-11f0-b05e-0a0027000009', 'PLAGIARISM', 'PENDING', 2, '抄袭小铃铛在CSDN的文章');
INSERT INTO `fs_reports_aud` VALUES (13, 358, 1, '经检验，该用户举报合理', '2025-04-15 16:23:46', 16, 'bb4ae77c-19d2-11f0-b05e-0a0027000009', 'PLAGIARISM', 'APPROVED', 2, '抄袭小铃铛在CSDN的文章');
INSERT INTO `fs_reports_aud` VALUES (14, 493, 0, NULL, '2025-04-24 21:07:33', 3, '15e4c147-210d-11f0-80d1-0a0027000009', 'SPAM', 'PENDING', 15, '啥也没写');
INSERT INTO `fs_reports_aud` VALUES (14, 524, 2, NULL, '2025-04-25 20:46:56', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_reports_aud` VALUES (15, 525, 0, NULL, '2025-04-25 20:47:16', 3, '6b0103d5-21d3-11f0-be21-0a002700000a', 'SPAM', 'PENDING', 15, '啥也没写');
INSERT INTO `fs_reports_aud` VALUES (15, 529, 1, '经检验，用户举报不合理', '2025-04-25 21:16:14', 3, '6b0103d5-21d3-11f0-be21-0a002700000a', 'SPAM', 'REJECTED', 15, '啥也没写');
INSERT INTO `fs_reports_aud` VALUES (16, 542, 0, NULL, '2025-04-25 23:49:47', 3, 'e9fff77a-21ec-11f0-a5f1-0a002700000a', 'ILLEGAL', 'PENDING', 2, '哈哈哈哈哈哈');
INSERT INTO `fs_reports_aud` VALUES (17, 618, 0, NULL, '2025-04-27 20:09:26', 2, '769c428a-2360-11f0-9dca-0a0027000009', 'INAPPROPRIATE', 'PENDING', 2, '文章内容过短');

-- ----------------------------
-- Table structure for fs_role_permissions
-- ----------------------------
DROP TABLE IF EXISTS `fs_role_permissions`;
CREATE TABLE `fs_role_permissions`  (
  `permission_id` int NOT NULL,
  `role_id` int NOT NULL,
  PRIMARY KEY (`permission_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of fs_role_permissions
-- ----------------------------
INSERT INTO `fs_role_permissions` VALUES (1, 1);
INSERT INTO `fs_role_permissions` VALUES (2, 1);
INSERT INTO `fs_role_permissions` VALUES (3, 1);
INSERT INTO `fs_role_permissions` VALUES (4, 1);
INSERT INTO `fs_role_permissions` VALUES (5, 1);
INSERT INTO `fs_role_permissions` VALUES (6, 1);
INSERT INTO `fs_role_permissions` VALUES (7, 1);
INSERT INTO `fs_role_permissions` VALUES (8, 1);
INSERT INTO `fs_role_permissions` VALUES (9, 1);
INSERT INTO `fs_role_permissions` VALUES (10, 1);
INSERT INTO `fs_role_permissions` VALUES (11, 1);
INSERT INTO `fs_role_permissions` VALUES (12, 1);
INSERT INTO `fs_role_permissions` VALUES (13, 1);
INSERT INTO `fs_role_permissions` VALUES (14, 1);
INSERT INTO `fs_role_permissions` VALUES (15, 1);
INSERT INTO `fs_role_permissions` VALUES (16, 1);
INSERT INTO `fs_role_permissions` VALUES (17, 1);
INSERT INTO `fs_role_permissions` VALUES (18, 1);
INSERT INTO `fs_role_permissions` VALUES (19, 1);
INSERT INTO `fs_role_permissions` VALUES (20, 1);
INSERT INTO `fs_role_permissions` VALUES (21, 1);
INSERT INTO `fs_role_permissions` VALUES (22, 1);
INSERT INTO `fs_role_permissions` VALUES (23, 1);
INSERT INTO `fs_role_permissions` VALUES (24, 1);
INSERT INTO `fs_role_permissions` VALUES (25, 1);
INSERT INTO `fs_role_permissions` VALUES (26, 1);
INSERT INTO `fs_role_permissions` VALUES (27, 1);
INSERT INTO `fs_role_permissions` VALUES (28, 1);
INSERT INTO `fs_role_permissions` VALUES (29, 1);
INSERT INTO `fs_role_permissions` VALUES (30, 1);
INSERT INTO `fs_role_permissions` VALUES (31, 1);
INSERT INTO `fs_role_permissions` VALUES (32, 1);
INSERT INTO `fs_role_permissions` VALUES (33, 1);
INSERT INTO `fs_role_permissions` VALUES (34, 1);
INSERT INTO `fs_role_permissions` VALUES (35, 1);
INSERT INTO `fs_role_permissions` VALUES (36, 1);
INSERT INTO `fs_role_permissions` VALUES (37, 1);
INSERT INTO `fs_role_permissions` VALUES (38, 1);
INSERT INTO `fs_role_permissions` VALUES (39, 2);
INSERT INTO `fs_role_permissions` VALUES (40, 3);
INSERT INTO `fs_role_permissions` VALUES (42, 3);
INSERT INTO `fs_role_permissions` VALUES (43, 3);
INSERT INTO `fs_role_permissions` VALUES (44, 3);
INSERT INTO `fs_role_permissions` VALUES (45, 4);
INSERT INTO `fs_role_permissions` VALUES (46, 5);
INSERT INTO `fs_role_permissions` VALUES (47, 5);
INSERT INTO `fs_role_permissions` VALUES (48, 6);
INSERT INTO `fs_role_permissions` VALUES (49, 6);
INSERT INTO `fs_role_permissions` VALUES (50, 6);
INSERT INTO `fs_role_permissions` VALUES (51, 7);
INSERT INTO `fs_role_permissions` VALUES (52, 7);
INSERT INTO `fs_role_permissions` VALUES (53, 8);
INSERT INTO `fs_role_permissions` VALUES (54, 9);
INSERT INTO `fs_role_permissions` VALUES (55, 9);
INSERT INTO `fs_role_permissions` VALUES (56, 9);
INSERT INTO `fs_role_permissions` VALUES (57, 9);
INSERT INTO `fs_role_permissions` VALUES (58, 10);
INSERT INTO `fs_role_permissions` VALUES (59, 10);
INSERT INTO `fs_role_permissions` VALUES (60, 11);
INSERT INTO `fs_role_permissions` VALUES (61, 11);
INSERT INTO `fs_role_permissions` VALUES (62, 11);
INSERT INTO `fs_role_permissions` VALUES (63, 11);
INSERT INTO `fs_role_permissions` VALUES (64, 12);
INSERT INTO `fs_role_permissions` VALUES (65, 12);

-- ----------------------------
-- Table structure for fs_roles
-- ----------------------------
DROP TABLE IF EXISTS `fs_roles`;
CREATE TABLE `fs_roles`  (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `administrator_name` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `count` int NOT NULL,
  `description` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `role_name` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  PRIMARY KEY (`role_id`) USING BTREE,
  UNIQUE INDEX `UKirgox6t9co8f3evs8xksimgyi`(`role_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fs_roles
-- ----------------------------
INSERT INTO `fs_roles` VALUES (1, 'ROLE_USER', 7, '普通用户', '普通用户');
INSERT INTO `fs_roles` VALUES (2, 'ROLE_CATEGORY_MANAGER', 1, '管理分类', '分类管理员');
INSERT INTO `fs_roles` VALUES (3, 'ROLE_TAG_MANAGER', 3, '管理标签', '标签管理员');
INSERT INTO `fs_roles` VALUES (4, 'ROLE_COMMENT_MANAGER', 0, '管理评论', '评论管理员');
INSERT INTO `fs_roles` VALUES (5, 'ROLE_MESSAGE_MANAGER', 1, '管理消息', '消息管理员');
INSERT INTO `fs_roles` VALUES (6, 'ROLE_REPORT_MANAGER', 1, '管理举报', '举报管理员');
INSERT INTO `fs_roles` VALUES (7, 'THE_ROLE_MANAGER', 0, '管理角色', '角色管理员');
INSERT INTO `fs_roles` VALUES (8, 'ROLE_SYSTEM_ADMINISTRATOR', 1, '系统管理员', '系统管理员');

-- ----------------------------
-- Table structure for fs_roles_aud
-- ----------------------------
DROP TABLE IF EXISTS `fs_roles_aud`;
CREATE TABLE `fs_roles_aud`  (
  `role_id` int NOT NULL,
  `rev` int NOT NULL,
  `revtype` tinyint NULL DEFAULT NULL,
  `administrator_name` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `role_name` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `count` int NULL DEFAULT NULL,
  PRIMARY KEY (`rev`, `role_id`) USING BTREE,
  CONSTRAINT `FKji7lpnhvs4qo3b3encby29k0u` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fs_roles_aud
-- ----------------------------
INSERT INTO `fs_roles_aud` VALUES (2, 78, 1, 'ROLE_CATEGORY_MANAGER', '管理分类', '分类管理员', 3);
INSERT INTO `fs_roles_aud` VALUES (1, 79, 1, 'ROLE_USER', '普通用户', '普通用户', 6);
INSERT INTO `fs_roles_aud` VALUES (1, 82, 1, 'ROLE_USER', '普通用户', '普通用户', 7);
INSERT INTO `fs_roles_aud` VALUES (2, 82, 1, 'ROLE_CATEGORY_MANAGER', '管理分类', '分类管理员', 4);
INSERT INTO `fs_roles_aud` VALUES (1, 88, 1, 'ROLE_USER', '普通用户', '普通用户', 8);
INSERT INTO `fs_roles_aud` VALUES (2, 88, 1, 'ROLE_CATEGORY_MANAGER', '管理分类', '分类管理员', 5);
INSERT INTO `fs_roles_aud` VALUES (1, 360, 1, 'ROLE_USER', '普通用户', '普通用户', 9);
INSERT INTO `fs_roles_aud` VALUES (8, 530, 1, 'ROLE_MESSAGE_MANAGER', '管理消息', '消息管理', 1);
INSERT INTO `fs_roles_aud` VALUES (7, 531, 1, 'ROLE_COMMENT_MANAGER', '管理评论', '评论管理', 1);
INSERT INTO `fs_roles_aud` VALUES (3, 537, 1, 'ROLE_TAG_MANAGER', '管理标签', '标签管理', 1);
INSERT INTO `fs_roles_aud` VALUES (6, 538, 1, 'ROLE_REPORT_MANAGER', '管理举报', '举报管理', 1);
INSERT INTO `fs_roles_aud` VALUES (1, 577, 1, 'ROLE_USER', '普通用户', '普通用户', 1);
INSERT INTO `fs_roles_aud` VALUES (1, 578, 1, 'ROLE_USER', '普通用户', '普通用户', 2);
INSERT INTO `fs_roles_aud` VALUES (3, 599, 1, 'ROLE_TAG_MANAGER', '管理标签', '标签管理', 2);
INSERT INTO `fs_roles_aud` VALUES (1, 613, 1, 'ROLE_USER', '普通用户', '普通用户', 3);
INSERT INTO `fs_roles_aud` VALUES (3, 629, 1, 'ROLE_TAG_MANAGER', '管理标签', '标签管理员', 3);

-- ----------------------------
-- Table structure for fs_system_logs
-- ----------------------------
DROP TABLE IF EXISTS `fs_system_logs`;
CREATE TABLE `fs_system_logs`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `level` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `logger_name` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `message` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `stack_trace` varchar(4000) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `thread_name` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fs_system_logs
-- ----------------------------

-- ----------------------------
-- Table structure for fs_system_messages
-- ----------------------------
DROP TABLE IF EXISTS `fs_system_messages`;
CREATE TABLE `fs_system_messages`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `message` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `sender_id` int NOT NULL,
  `status` enum('DELIVERED','READ','SENT','UNREAD') CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `target_role` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `created_at` date NOT NULL,
  `read_user_ids` text CHARACTER SET utf16 COLLATE utf16_general_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fs_system_messages
-- ----------------------------
INSERT INTO `fs_system_messages` VALUES (2, '欢迎大家来到FreeShare', 2, 'UNREAD', 'ROLE_USER', '2025-04-12', '2');

-- ----------------------------
-- Table structure for fs_tags
-- ----------------------------
DROP TABLE IF EXISTS `fs_tags`;
CREATE TABLE `fs_tags`  (
  `tag_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NOT NULL,
  `weight` int NULL DEFAULT 0,
  `admin_weight` int NULL DEFAULT NULL,
  `click_count` int NULL DEFAULT NULL,
  `last_used_at` datetime(6) NULL DEFAULT NULL,
  `use_count` int NULL DEFAULT NULL,
  PRIMARY KEY (`tag_id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of fs_tags
-- ----------------------------
INSERT INTO `fs_tags` VALUES (1, '开发', 35, 5, 0, '2025-04-13 10:25:35.559083', 15);
INSERT INTO `fs_tags` VALUES (2, '维护', 20, 4, 0, '2025-04-13 10:25:35.566084', 1);
INSERT INTO `fs_tags` VALUES (4, '美术', 21, 5, 0, '2025-04-13 10:43:22.300694', 1);
INSERT INTO `fs_tags` VALUES (5, '体育', 24, 8, 0, '2025-04-13 10:43:22.338693', 1);
INSERT INTO `fs_tags` VALUES (6, '学习', 28, 10, 0, '2025-04-25 15:45:22.485089', 3);
INSERT INTO `fs_tags` VALUES (11, '摄影', 26, 10, 0, '2025-04-13 10:43:22.356691', 1);

-- ----------------------------
-- Table structure for fs_user_activities
-- ----------------------------
DROP TABLE IF EXISTS `fs_user_activities`;
CREATE TABLE `fs_user_activities`  (
  `activity_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `activity_type` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `post_id` int NULL DEFAULT NULL,
  `activity_detail` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `comment_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`activity_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `post_id`(`post_id` ASC) USING BTREE,
  CONSTRAINT `FK3rpvobp8tb7rcmeofsnqs857e` FOREIGN KEY (`post_id`) REFERENCES `fs_posts` (`post_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fs_user_activities_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `fs_users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 105 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of fs_user_activities
-- ----------------------------
INSERT INTO `fs_user_activities` VALUES (1, 1, 'favorite', 2, '收藏了你的博客', '2025-03-21 09:33:26', b'0', NULL);
INSERT INTO `fs_user_activities` VALUES (20, 3, 'like', 2, NULL, '2025-04-04 23:06:33', b'1', NULL);
INSERT INTO `fs_user_activities` VALUES (26, 2, 'COMMENT', 16, 'kitty评论了文章《解决content过长带来的问题》', '2025-04-13 20:37:47', b'0', NULL);
INSERT INTO `fs_user_activities` VALUES (39, 1, 'REPLY', 16, 'jonny回复了kitty的评论', '2025-04-13 21:59:45', b'0', 11);
INSERT INTO `fs_user_activities` VALUES (55, 2, 'REPLY', 16, 'kitty回复了kitty的评论', '2025-04-14 15:08:06', b'0', 12);
INSERT INTO `fs_user_activities` VALUES (57, 2, 'REPLY', 16, 'kitty回复了jonny的评论', '2025-04-14 16:06:36', b'0', 15);
INSERT INTO `fs_user_activities` VALUES (61, 2, 'LIKE_COMMENT', 16, 'kitty点赞了jonny在解决content过长带来的问题的评论支持楼上', '2025-04-14 17:12:17', b'0', 12);
INSERT INTO `fs_user_activities` VALUES (64, 2, 'LIKE_COMMENT', 16, 'kitty点赞了kitty在解决content过长带来的问题的评论楼主写的非常棒，解决了我的问题😀，支持楼主继续发博客', '2025-04-14 17:12:24', b'0', 11);
INSERT INTO `fs_user_activities` VALUES (65, 2, 'LIKE_COMMENT', 16, 'kitty点赞了kitty在解决content过长带来的问题的评论棒棒哒', '2025-04-14 17:13:36', b'0', 15);
INSERT INTO `fs_user_activities` VALUES (66, 2, 'COMMENT', 16, 'kitty评论了文章《解决content过长带来的问题》', '2025-04-14 17:25:49', b'0', 16);
INSERT INTO `fs_user_activities` VALUES (68, 2, 'LIKE_COMMENT', 16, 'kitty点赞了kitty在解决content过长带来的问题的评论顶顶', '2025-04-23 21:51:43', b'0', 14);
INSERT INTO `fs_user_activities` VALUES (70, 15, 'LIKE', 3, 'helloKittyCatSweet点赞了文章《SpringBoot报错》', '2025-04-24 21:03:15', b'0', NULL);
INSERT INTO `fs_user_activities` VALUES (72, 15, 'COMMENT', 3, 'helloKittyCatSweet评论了文章《SpringBoot报错》', '2025-04-24 21:07:53', b'0', 17);
INSERT INTO `fs_user_activities` VALUES (73, 15, 'LIKE_COMMENT', 2, 'helloKittyCatSweet点赞了111111在第二篇博客的评论这是对楼上的回复', '2025-04-24 21:08:37', b'0', 6);
INSERT INTO `fs_user_activities` VALUES (74, 15, 'LIKE_COMMENT', 2, 'helloKittyCatSweet点赞了kitty在第二篇博客的评论我是孙子', '2025-04-24 21:08:38', b'0', 9);
INSERT INTO `fs_user_activities` VALUES (75, 15, 'COMMENT', 13, 'helloKittyCatSweet评论了文章《未命名文章》', '2025-04-25 19:22:18', b'0', 18);
INSERT INTO `fs_user_activities` VALUES (77, 2, 'LIKE', 3, 'kitty点赞了文章《SpringBoot报错》', '2025-04-25 23:48:40', b'0', NULL);
INSERT INTO `fs_user_activities` VALUES (78, 2, 'COMMENT', 3, 'kitty评论了文章《SpringBoot报错》', '2025-04-25 23:49:10', b'0', 19);
INSERT INTO `fs_user_activities` VALUES (92, 15, 'COMMENT', 16, 'helloKittyCatSweet评论了文章《解决content过长带来的问题》', '2025-04-26 09:53:08', b'0', 20);
INSERT INTO `fs_user_activities` VALUES (93, 2, 'FAVORITE', 16, 'kitty将文章《解决content过长带来的问题》收藏到\"200\"', '2025-04-27 12:53:38', b'0', NULL);
INSERT INTO `fs_user_activities` VALUES (94, 2, 'LIKE', 16, 'kitty点赞了文章《解决content过长带来的问题》', '2025-04-27 12:53:42', b'0', NULL);
INSERT INTO `fs_user_activities` VALUES (95, 2, 'COMMENT', 3, 'kitty评论了文章《SpringBoot报错》', '2025-04-27 12:54:32', b'0', 21);
INSERT INTO `fs_user_activities` VALUES (96, 2, 'LIKE_COMMENT', 3, 'kitty点赞了kitty在SpringBoot报错的评论hi', '2025-04-27 12:54:35', b'0', 21);
INSERT INTO `fs_user_activities` VALUES (97, 2, 'REPLY', 3, 'kitty回复了kitty的评论', '2025-04-27 12:54:48', b'0', 22);
INSERT INTO `fs_user_activities` VALUES (99, 2, 'LIKE_COMMENT', 2, 'kitty点赞了xdxzwxq在第二篇博客的评论这是另外一篇文章的评论', '2025-04-27 20:09:32', b'0', 7);
INSERT INTO `fs_user_activities` VALUES (100, 2, 'REPLY', 2, 'kitty回复了xdxzwxq的评论', '2025-04-27 20:09:56', b'0', 23);
INSERT INTO `fs_user_activities` VALUES (101, 15, 'LIKE', 23, 'helloKittyCatSweet点赞了文章《学习vue day1》', '2025-04-27 20:39:47', b'0', NULL);
INSERT INTO `fs_user_activities` VALUES (102, 15, 'COMMENT', 23, 'helloKittyCatSweet评论了文章《学习vue day1》', '2025-04-27 20:40:20', b'1', 24);
INSERT INTO `fs_user_activities` VALUES (103, 15, 'LIKE_COMMENT', 23, 'helloKittyCatSweet点赞了helloKittyCatSweet在学习vue day1的评论我认为作者写的很棒，请继续努力', '2025-04-27 20:40:23', b'0', 24);
INSERT INTO `fs_user_activities` VALUES (104, 15, 'REPLY', 23, 'helloKittyCatSweet回复了helloKittyCatSweet的评论', '2025-04-27 20:40:31', b'0', 25);

-- ----------------------------
-- Table structure for fs_user_activities_aud
-- ----------------------------
DROP TABLE IF EXISTS `fs_user_activities_aud`;
CREATE TABLE `fs_user_activities_aud`  (
  `activity_id` int NOT NULL,
  `rev` int NOT NULL,
  `revtype` int NULL DEFAULT NULL,
  `activity_detail` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `activity_type` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `created_at` date NULL DEFAULT NULL,
  `post_id` int NULL DEFAULT NULL,
  `user_id` int NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `comment_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`rev`, `activity_id`) USING BTREE,
  CONSTRAINT `FK5qdux8d73yvjl7896ojkjl0pd` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fs_user_activities_aud
-- ----------------------------
INSERT INTO `fs_user_activities_aud` VALUES (2, 12, 2, '点赞了你的博客', 'like', '2025-04-04', 2, 3, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (16, 29, 2, NULL, NULL, '2025-04-04', NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (17, 30, 2, NULL, NULL, '2025-04-04', NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (19, 34, 2, NULL, 'like', '2025-04-04', 2, 3, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (19, 35, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (20, 37, 1, NULL, 'like', NULL, 2, 3, b'1', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (21, 228, 0, 'kitty点赞了文章《解决content过长带来的问题》', 'LIKE', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (22, 229, 0, 'kitty点赞了文章《解决content过长带来的问题》', 'LIKE', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (21, 239, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (23, 240, 0, 'kitty点赞了文章《解决content过长带来的问题》', 'LIKE', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (24, 243, 0, 'kitty将文章《解决content过长带来的问题》收藏到\"报错指南\"', 'FAVORITE', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (24, 250, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (25, 252, 0, 'kitty将文章《解决content过长带来的问题》收藏到\"报错指南\"', 'FAVORITE', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (26, 253, 0, 'kitty评论了文章《解决content过长带来的问题》', 'COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (27, 255, 0, 'kitty评论了文章《解决content过长带来的问题》', 'COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (28, 258, 0, 'kitty点赞了kitty的评论', 'LIKE', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (29, 259, 0, 'kitty取消点赞了kitty的评论', 'UNLIKE', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (30, 260, 0, 'kitty点赞了kitty的评论', 'LIKE', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (31, 264, 0, 'kitty点赞了kitty的评论', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (32, 265, 0, 'kitty点赞了kitty的评论', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (33, 266, 0, 'kitty点赞了kitty的评论', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (34, 267, 0, 'kitty点赞了kitty的评论', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (35, 269, 0, 'kitty点赞了kitty的评论', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (36, 271, 0, 'kitty点赞了kitty的评论', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (37, 273, 0, 'kitty点赞了kitty的评论', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (37, 274, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (38, 275, 0, 'kitty点赞了kitty的评论', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (39, 277, 0, 'jonny回复了kitty的评论', 'REPLY', NULL, 16, 1, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (38, 279, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (40, 290, 0, 'kitty点赞了jonny在解决content过长带来的问题的评论', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (41, 291, 0, 'kitty点赞了jonny在解决content过长带来的问题的评论', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (42, 292, 0, 'kitty点赞了kitty在解决content过长带来的问题的评论', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (40, 295, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (43, 297, 0, 'kitty点赞了jonny在解决content过长带来的问题的评论支持楼上', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (43, 298, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (44, 299, 0, 'kitty点赞了jonny在解决content过长带来的问题的评论支持楼上', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (44, 300, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (45, 301, 0, 'kitty点赞了jonny在解决content过长带来的问题的评论支持楼上', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (45, 302, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (46, 303, 0, 'kitty点赞了kitty在解决content过长带来的问题的评论楼主写的非常棒，解决了我的问题😀', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (46, 304, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (47, 305, 0, 'kitty点赞了kitty在解决content过长带来的问题的评论楼主写的非常棒，解决了我的问题😀', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (47, 306, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (48, 307, 0, 'kitty点赞了jonny在解决content过长带来的问题的评论支持楼上', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (48, 308, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (49, 309, 0, 'kitty点赞了jonny在解决content过长带来的问题的评论支持楼上', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (49, 310, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (50, 311, 0, 'kitty点赞了jonny在解决content过长带来的问题的评论支持楼上', 'LIKE_COMMENT', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (51, 314, 0, 'kitty点赞了kitty在解决content过长带来的问题的评论楼主写的非常棒，解决了我的问题😀', 'LIKE_COMMENT', NULL, 11, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (52, 315, 0, 'kitty点赞了kitty在解决content过长带来的问题的评论楼主写的非常棒，解决了我的问题😀', 'LIKE_COMMENT', NULL, 11, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (53, 316, 0, 'kitty点赞了kitty在解决content过长带来的问题的评论楼主写的非常棒，解决了我的问题😀', 'LIKE_COMMENT', NULL, 11, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (53, 317, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (54, 318, 0, 'kitty点赞了jonny在解决content过长带来的问题的评论支持楼上', 'LIKE_COMMENT', NULL, 12, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (54, 319, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (55, 321, 0, 'kitty回复了kitty的评论', 'REPLY', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (56, 322, 0, 'kitty回复了kitty的评论', 'REPLY', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (57, 323, 0, 'kitty回复了jonny的评论', 'REPLY', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (58, 325, 0, 'kitty点赞了jonny在解决content过长带来的问题的评论支持楼上', 'LIKE_COMMENT', NULL, 12, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (58, 326, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (59, 327, 0, 'kitty点赞了jonny在解决content过长带来的问题的评论支持楼上', 'LIKE_COMMENT', NULL, 12, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (59, 328, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (60, 330, 0, 'kitty点赞了jonny在解决content过长带来的问题的评论支持楼上', 'LIKE_COMMENT', NULL, 16, 2, b'0', 12);
INSERT INTO `fs_user_activities_aud` VALUES (60, 331, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (61, 332, 0, 'kitty点赞了jonny在解决content过长带来的问题的评论支持楼上', 'LIKE_COMMENT', NULL, 16, 2, b'0', 12);
INSERT INTO `fs_user_activities_aud` VALUES (62, 333, 0, 'kitty点赞了kitty在解决content过长带来的问题的评论顶顶', 'LIKE_COMMENT', NULL, 16, 2, b'0', 14);
INSERT INTO `fs_user_activities_aud` VALUES (62, 334, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (63, 335, 0, 'kitty点赞了kitty在解决content过长带来的问题的评论棒棒哒', 'LIKE_COMMENT', NULL, 16, 2, b'0', 15);
INSERT INTO `fs_user_activities_aud` VALUES (64, 336, 0, 'kitty点赞了kitty在解决content过长带来的问题的评论楼主写的非常棒，解决了我的问题😀，支持楼主继续发博客', 'LIKE_COMMENT', NULL, 16, 2, b'0', 11);
INSERT INTO `fs_user_activities_aud` VALUES (63, 337, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (65, 338, 0, 'kitty点赞了kitty在解决content过长带来的问题的评论棒棒哒', 'LIKE_COMMENT', NULL, 16, 2, b'0', 15);
INSERT INTO `fs_user_activities_aud` VALUES (66, 339, 0, 'kitty评论了文章《解决content过长带来的问题》', 'COMMENT', NULL, 16, 2, b'0', 16);
INSERT INTO `fs_user_activities_aud` VALUES (67, 451, 0, 'kitty将文章《方差公式》收藏到\"默认收藏夹\"', 'FAVORITE', NULL, 11, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (68, 455, 0, 'kitty点赞了kitty在解决content过长带来的问题的评论顶顶', 'LIKE_COMMENT', NULL, 16, 2, b'0', 14);
INSERT INTO `fs_user_activities_aud` VALUES (69, 488, 0, 'helloKittyCatSweet点赞了文章《SpringBoot报错》', 'LIKE', NULL, 3, 15, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (69, 489, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (70, 490, 0, 'helloKittyCatSweet点赞了文章《SpringBoot报错》', 'LIKE', NULL, 3, 15, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (71, 492, 0, 'helloKittyCatSweet将文章《SpringBoot报错》收藏到\"默认收藏夹\"', 'FAVORITE', NULL, 3, 15, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (72, 494, 0, 'helloKittyCatSweet评论了文章《SpringBoot报错》', 'COMMENT', NULL, 3, 15, b'0', 17);
INSERT INTO `fs_user_activities_aud` VALUES (73, 495, 0, 'helloKittyCatSweet点赞了111111在第二篇博客的评论这是对楼上的回复', 'LIKE_COMMENT', NULL, 2, 15, b'0', 6);
INSERT INTO `fs_user_activities_aud` VALUES (74, 496, 0, 'helloKittyCatSweet点赞了kitty在第二篇博客的评论我是孙子', 'LIKE_COMMENT', NULL, 2, 15, b'0', 9);
INSERT INTO `fs_user_activities_aud` VALUES (75, 516, 0, 'helloKittyCatSweet评论了文章《未命名文章》', 'COMMENT', NULL, 13, 15, b'0', 18);
INSERT INTO `fs_user_activities_aud` VALUES (76, 519, 0, 'helloKittyCatSweet将文章《SpringBoot报错》收藏到\"默认收藏夹\"', 'FAVORITE', NULL, 3, 15, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (76, 520, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (77, 540, 0, 'kitty点赞了文章《SpringBoot报错》', 'LIKE', NULL, 3, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (78, 541, 0, 'kitty评论了文章《SpringBoot报错》', 'COMMENT', NULL, 3, 2, b'0', 19);
INSERT INTO `fs_user_activities_aud` VALUES (79, 544, 0, 'helloKittyCatSweet点赞了文章《学习vue day1》', 'LIKE', NULL, 23, 15, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (79, 545, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (80, 546, 0, 'helloKittyCatSweet点赞了文章《学习vue day1》', 'LIKE', NULL, 23, 15, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (81, 547, 0, 'helloKittyCatSweet点赞了文章《学习vue day1》', 'LIKE', NULL, 23, 15, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (81, 548, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (82, 549, 0, 'helloKittyCatSweet点赞了文章《学习vue day1》', 'LIKE', NULL, 23, 15, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (82, 550, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (83, 551, 0, 'helloKittyCatSweet点赞了文章《学习vue day1》', 'LIKE', NULL, 23, 15, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (83, 552, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (84, 553, 0, 'helloKittyCatSweet点赞了文章《学习vue day1》', 'LIKE', NULL, 23, 15, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (84, 554, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (85, 555, 0, 'helloKittyCatSweet点赞了文章《学习vue day1》', 'LIKE', NULL, 23, 15, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (85, 556, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (86, 558, 0, 'helloKittyCatSweet将文章《学习vue day1》收藏到\"默认收藏夹\"', 'FAVORITE', NULL, 23, 15, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (86, 559, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (87, 561, 0, 'helloKittyCatSweet将文章《学习vue day1》收藏到\"默认收藏夹\"', 'FAVORITE', NULL, 23, 15, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (87, 562, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (88, 563, 0, 'helloKittyCatSweet点赞了文章《学习vue day1》', 'LIKE', NULL, 23, 15, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (89, 565, 0, 'helloKittyCatSweet将文章《学习vue day1》收藏到\"默认收藏夹\"', 'FAVORITE', NULL, 23, 15, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (89, 566, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (90, 568, 0, 'helloKittyCatSweet将文章《学习vue day1》收藏到\"默认收藏夹\"', 'FAVORITE', NULL, 23, 15, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (90, 569, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (91, 571, 0, 'helloKittyCatSweet将文章《学习vue day1》收藏到\"默认收藏夹\"', 'FAVORITE', NULL, 23, 15, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (92, 573, 0, 'helloKittyCatSweet评论了文章《解决content过长带来的问题》', 'COMMENT', NULL, 16, 15, b'0', 20);
INSERT INTO `fs_user_activities_aud` VALUES (25, 603, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (93, 605, 0, 'kitty将文章《解决content过长带来的问题》收藏到\"200\"', 'FAVORITE', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (23, 606, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (94, 607, 0, 'kitty点赞了文章《解决content过长带来的问题》', 'LIKE', NULL, 16, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (95, 608, 0, 'kitty评论了文章《SpringBoot报错》', 'COMMENT', NULL, 3, 2, b'0', 21);
INSERT INTO `fs_user_activities_aud` VALUES (96, 609, 0, 'kitty点赞了kitty在SpringBoot报错的评论hi', 'LIKE_COMMENT', NULL, 3, 2, b'0', 21);
INSERT INTO `fs_user_activities_aud` VALUES (97, 610, 0, 'kitty回复了kitty的评论', 'REPLY', NULL, 3, 2, b'0', 22);
INSERT INTO `fs_user_activities_aud` VALUES (67, 612, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (98, 616, 0, 'kitty点赞了文章《第二篇博客》', 'LIKE', NULL, 2, 2, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (98, 617, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (99, 619, 0, 'kitty点赞了xdxzwxq在第二篇博客的评论这是另外一篇文章的评论', 'LIKE_COMMENT', NULL, 2, 2, b'0', 7);
INSERT INTO `fs_user_activities_aud` VALUES (100, 620, 0, 'kitty回复了xdxzwxq的评论', 'REPLY', NULL, 2, 2, b'0', 23);
INSERT INTO `fs_user_activities_aud` VALUES (88, 622, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (101, 623, 0, 'helloKittyCatSweet点赞了文章《学习vue day1》', 'LIKE', NULL, 23, 15, b'0', NULL);
INSERT INTO `fs_user_activities_aud` VALUES (91, 624, 2, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `fs_user_activities_aud` VALUES (102, 625, 0, 'helloKittyCatSweet评论了文章《学习vue day1》', 'COMMENT', NULL, 23, 15, b'0', 24);
INSERT INTO `fs_user_activities_aud` VALUES (103, 626, 0, 'helloKittyCatSweet点赞了helloKittyCatSweet在学习vue day1的评论我认为作者写的很棒，请继续努力', 'LIKE_COMMENT', NULL, 23, 15, b'0', 24);
INSERT INTO `fs_user_activities_aud` VALUES (104, 627, 0, 'helloKittyCatSweet回复了helloKittyCatSweet的评论', 'REPLY', NULL, 23, 15, b'0', 25);
INSERT INTO `fs_user_activities_aud` VALUES (102, 635, 1, 'helloKittyCatSweet评论了文章《学习vue day1》', 'COMMENT', NULL, 23, 15, b'1', 24);

-- ----------------------------
-- Table structure for fs_user_follow
-- ----------------------------
DROP TABLE IF EXISTS `fs_user_follow`;
CREATE TABLE `fs_user_follow`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) NOT NULL,
  `follower_id` int NOT NULL,
  `following_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fs_user_follow
-- ----------------------------
INSERT INTO `fs_user_follow` VALUES (4, '2025-04-16 19:31:51.946050', 15, 2);
INSERT INTO `fs_user_follow` VALUES (5, '2025-04-16 20:20:39.000000', 2, 2);
INSERT INTO `fs_user_follow` VALUES (6, '2025-04-20 15:38:36.322579', 2, 1);
INSERT INTO `fs_user_follow` VALUES (7, '2025-04-25 23:51:40.623046', 2, 15);

-- ----------------------------
-- Table structure for fs_user_roles
-- ----------------------------
DROP TABLE IF EXISTS `fs_user_roles`;
CREATE TABLE `fs_user_roles`  (
  `role_id` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`role_id`, `user_id`) USING BTREE,
  INDEX `FKsqevuaetupdtwy0d2igd6eef0`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FK10ul5003ik32i9ecej9c0ulso` FOREIGN KEY (`role_id`) REFERENCES `fs_roles` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKsqevuaetupdtwy0d2igd6eef0` FOREIGN KEY (`user_id`) REFERENCES `fs_users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fs_user_roles
-- ----------------------------
INSERT INTO `fs_user_roles` VALUES (1, 1);
INSERT INTO `fs_user_roles` VALUES (2, 1);
INSERT INTO `fs_user_roles` VALUES (5, 1);
INSERT INTO `fs_user_roles` VALUES (1, 2);
INSERT INTO `fs_user_roles` VALUES (8, 2);
INSERT INTO `fs_user_roles` VALUES (1, 3);
INSERT INTO `fs_user_roles` VALUES (3, 3);
INSERT INTO `fs_user_roles` VALUES (1, 4);
INSERT INTO `fs_user_roles` VALUES (3, 4);
INSERT INTO `fs_user_roles` VALUES (1, 5);
INSERT INTO `fs_user_roles` VALUES (1, 14);
INSERT INTO `fs_user_roles` VALUES (1, 15);
INSERT INTO `fs_user_roles` VALUES (3, 15);
INSERT INTO `fs_user_roles` VALUES (6, 15);

-- ----------------------------
-- Table structure for fs_user_roles_aud
-- ----------------------------
DROP TABLE IF EXISTS `fs_user_roles_aud`;
CREATE TABLE `fs_user_roles_aud`  (
  `role_id` int NOT NULL,
  `user_id` int NOT NULL,
  `rev` int NOT NULL,
  `revtype` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`rev`, `role_id`, `user_id`) USING BTREE,
  CONSTRAINT `FKelowp5csnesad1ej6y7l87n8m` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fs_user_roles_aud
-- ----------------------------
INSERT INTO `fs_user_roles_aud` VALUES (4, 1, 73, 0);
INSERT INTO `fs_user_roles_aud` VALUES (4, 1, 74, 0);
INSERT INTO `fs_user_roles_aud` VALUES (2, 2, 75, 0);
INSERT INTO `fs_user_roles_aud` VALUES (2, 3, 78, 0);
INSERT INTO `fs_user_roles_aud` VALUES (1, 10, 79, 0);
INSERT INTO `fs_user_roles_aud` VALUES (1, 12, 82, 0);
INSERT INTO `fs_user_roles_aud` VALUES (2, 12, 82, 0);
INSERT INTO `fs_user_roles_aud` VALUES (1, 14, 88, 0);
INSERT INTO `fs_user_roles_aud` VALUES (2, 14, 88, 0);
INSERT INTO `fs_user_roles_aud` VALUES (1, 15, 360, 0);
INSERT INTO `fs_user_roles_aud` VALUES (8, 1, 530, 0);
INSERT INTO `fs_user_roles_aud` VALUES (7, 1, 531, 0);
INSERT INTO `fs_user_roles_aud` VALUES (3, 4, 537, 0);
INSERT INTO `fs_user_roles_aud` VALUES (6, 15, 538, 0);
INSERT INTO `fs_user_roles_aud` VALUES (1, 16, 577, 0);
INSERT INTO `fs_user_roles_aud` VALUES (1, 17, 578, 0);
INSERT INTO `fs_user_roles_aud` VALUES (3, 15, 599, 0);
INSERT INTO `fs_user_roles_aud` VALUES (1, 18, 613, 0);
INSERT INTO `fs_user_roles_aud` VALUES (3, 3, 629, 0);

-- ----------------------------
-- Table structure for fs_user_settings
-- ----------------------------
DROP TABLE IF EXISTS `fs_user_settings`;
CREATE TABLE `fs_user_settings`  (
  `setting_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `theme` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `notifications` tinyint(1) NULL DEFAULT 1,
  `github_account` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `bilibili_account` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `csdn_account` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`setting_id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fs_user_settings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `fs_users` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of fs_user_settings
-- ----------------------------
INSERT INTO `fs_user_settings` VALUES (1, 1, 'pink', 1, '', NULL, NULL);
INSERT INTO `fs_user_settings` VALUES (2, 2, 'green', 1, '', NULL, NULL);
INSERT INTO `fs_user_settings` VALUES (3, 3, NULL, 1, NULL, NULL, NULL);
INSERT INTO `fs_user_settings` VALUES (4, 15, 'light', 1, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for fs_users
-- ----------------------------
DROP TABLE IF EXISTS `fs_users`;
CREATE TABLE `fs_users`  (
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
  `phone` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `gender` int NULL DEFAULT NULL,
  `birthday` date NULL DEFAULT NULL,
  `nickname` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `last_login_time` date NULL DEFAULT NULL,
  `tags` varbinary(255) NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `last_login_ip` varchar(45) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `last_login_location` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `signature` longtext CHARACTER SET utf16 COLLATE utf16_general_ci NULL,
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `token`(`token` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 19 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of fs_users
-- ----------------------------
INSERT INTO `fs_users` VALUES (1, 'jonny', '$2a$10$m/UDxOQ0ZRq0wcVLPtZTNOIGiDaJSrt.clReeHOyPTJs4I91LVXpu', 'jonny.jiang@sap.com', '', 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODViMDU1M2ItNTczZi00YTg3LTk2NzctYmQ4MjlmMzg1M2JmIiwic3ViIjoiam9ubnkiLCJpYXQiOjE3NDQ1NTI3NjEsImV4cCI6MTc0NDU1NjM2MX0.Vp4FsE3Dtu8tAfVynqDBs1IbEZqKIlThiyEJfG6FADA', '2025-03-20 10:35:13', '2025-04-25 21:17:01', 1, NULL, NULL, NULL, 0, NULL, NULL, '2025-04-13', NULL, b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/1/signature.png');
INSERT INTO `fs_users` VALUES (2, 'kitty', '$2a$10$wpkUKC/k8s5RfAdOk.SzGu15h0vBlcpqggL6kyxTV0FzfWQgCrlhO', '2021212077@stu.hit.edu.cn', '', 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMDc2YWRlYmUtNjU2NS00MDMxLTliNTUtZDMyMTc3OGJiNDA1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU4MjY2OTksImV4cCI6MTc0NTgzMDI5OX0.KIgaIJaZeItVcaTHYPfb7G_ugdR1I1bDWBNjlu9UCYs', '2025-03-20 10:56:55', '2025-04-28 15:51:39', 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', NULL, '1cS+NtSZF4lT8kA3TtawcA==', 0, '2025-04-22', '小铃铛', '2025-04-28', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users` VALUES (3, 'admin', '$2a$10$wp3M6pohRS8wSHZXWhWx3.g6tuAqntkqsB2GkCVgRsSQB2renrxBq', 'wzk@sap.com', NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZDBmODEwZTQtMTdkMS00M2NhLWFiMDUtYjVjNmM4Zjk2MDRkIiwic3ViIjoiYWRtaW4iLCJpYXQiOjE3NDQ5NDU4ODEsImV4cCI6MTc0NDk0OTQ4MX0._qjHgwFCjbPrhjBFIfWeh6rkbPgpYb3SYPdAW2TP6vo', '2025-03-21 19:48:11', '2025-04-18 11:11:21', 1, NULL, NULL, NULL, 0, NULL, NULL, '2025-04-18', NULL, b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/3/signature.png');
INSERT INTO `fs_users` VALUES (4, 'xdxzwxq', '$2a$10$3Q83Gv7.8IjPaQzaS0cC9ez4CSnD9123O8oPn.GL3cTjJTeSb.HDK', 'xdxzwxq@126.com', NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMmQwZTFhM2EtZWFhMS00MDM5LWEzYTQtZGMzZTcwMWUwNDRiIiwic3ViIjoieGR4end4cSIsImlhdCI6MTc0NDk1NDgzNSwiZXhwIjoxNzQ0OTU4NDM1fQ.E_XuZAcg9_-sQnOT37zviQbwv7lG38CIeVWLcJ_I_OE', '2025-03-26 21:54:58', '2025-04-18 13:40:35', 1, NULL, NULL, NULL, 1, NULL, NULL, '2025-04-18', NULL, b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/4/signature.png');
INSERT INTO `fs_users` VALUES (5, '111111', '$2a$10$gp.z2FUfCfpMS.1Kjdsu5Oag7XNHfJKu.X48qaCTixSDKzScTHxAK', '1448185924@qq.fake', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/user/2/1743518421174-avatar-5490599774882110159-宠物狗.png', 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzI3NzYzZmMtMTc0NS00NjdlLWFiOTQtMGUxZDViMTBkM2M3Iiwic3ViIjoiMTExMTExIiwiaWF0IjoxNzQzNTE0MzMzLCJleHAiOjE3NDM1MTc5MzN9.-Es_His3AnZbmBhnKHX_6wGhNijcq5fQIwD5oLS3WW0', '2025-04-01 21:31:40', '2025-04-27 20:26:44', 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', NULL, '1cS+NtSZF4lT8kA3TtawcA==', 2, NULL, NULL, '2025-04-01', NULL, b'1', NULL, NULL, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/5/signature.png');
INSERT INTO `fs_users` VALUES (14, 'wyx', '$2a$10$wzj7Zjih//9NCyXnFaxZfuIeUvAJi9Zeegdua9eYvUM9a/1Swprw2', '123456789@qq.com', NULL, NULL, '2025-04-06 20:53:27', '2025-04-06 00:00:00', 0, NULL, NULL, NULL, 2, NULL, '', NULL, NULL, b'0', NULL, NULL, '');
INSERT INTO `fs_users` VALUES (15, 'helloKittyCatSweet', '$2a$10$b4jT1bCGzyTeUQzgM/OMYeMG2lFPWKnEwAh49QAluMLcMnvnuejUS', '1448185924@qq.com', 'https://avatars.githubusercontent.com/u/109123755?v=4', 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNzhmM2QzMTgtYmRiNS00ZmNjLTlhMmEtODY5ZTc5NzRlNDI5Iiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NjM4NDA0LCJleHAiOjE3NDU2NDIwMDR9.U1FMMcIy3BP48Xu46gHaoIzLJGjYCj6rhqENNbMDrRI', '2025-04-15 21:59:12', '2025-04-27 20:52:42', 1, NULL, NULL, NULL, 2, NULL, NULL, '2025-04-26', NULL, b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');

-- ----------------------------
-- Table structure for fs_users_aud
-- ----------------------------
DROP TABLE IF EXISTS `fs_users_aud`;
CREATE TABLE `fs_users_aud`  (
  `user_id` int NOT NULL,
  `rev` int NOT NULL,
  `revtype` tinyint NULL DEFAULT NULL,
  `address` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `birthday` date NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `email` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `gender` int NULL DEFAULT NULL,
  `introduction` text CHARACTER SET utf16 COLLATE utf16_general_ci NULL,
  `is_active` bit(1) NULL DEFAULT NULL,
  `last_login_time` date NULL DEFAULT NULL,
  `nickname` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `tags` varbinary(255) NULL DEFAULT NULL,
  `token` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `username` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `is_deleted` bit(1) NULL DEFAULT NULL,
  `last_login_ip` varchar(45) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `last_login_location` varchar(255) CHARACTER SET utf16 COLLATE utf16_general_ci NULL DEFAULT NULL,
  `signature` longtext CHARACTER SET utf16 COLLATE utf16_general_ci NULL,
  PRIMARY KEY (`rev`, `user_id`) USING BTREE,
  CONSTRAINT `FK7dp7ch2x5d6q1lvmcb94q8fd7` FOREIGN KEY (`rev`) REFERENCES `revinfo` (`rev`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of fs_users_aud
-- ----------------------------
INSERT INTO `fs_users_aud` VALUES (2, 1, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-03 22:15:27', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-03', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiM2UzNTY5NDktMGU4ZC00ODBlLWFjYmMtOThiZGE5OTAwMjU0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM2ODk3MjYsImV4cCI6MTc0MzY5MzMyNn0.evKHS07Rg0GOTqC9NlrQeTd_Yjt9JohioQJXT74gonQ', '2025-04-03 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 2, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-03 22:21:06', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-03', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjhmNjY4MmUtMmQ1Ny00M2NhLThmNjUtMjM4ODkwY2RlNDA0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM2OTAwNjYsImV4cCI6MTc0MzY5MzY2Nn0.6f-1Yg5234O9cUbYyANq9HseskNAW9kzSAEpTYmaVt0', '2025-04-03 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 3, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-04 09:14:33', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-04', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjdmZTgyYTItMjAxOS00YzU0LWEzOWUtNTljYWY5Zjg1ZDU4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM3MjkyNzMsImV4cCI6MTc0MzczMjg3M30.xpl8rmWyYXjdA0osL-WmqvB6IuRSrlUtid85SPHm8DE', '2025-04-03 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 4, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-04 09:42:43', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-04', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWUyMzBhNzEtOGI2MS00YjE5LTlhNDktZDQ0NWJmMTYyODE4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM3MzA5NjMsImV4cCI6MTc0MzczNDU2M30.ikXfp10m4nYdtusqE5rhfCHYZ_RsWVfroJuttwizud0', '2025-04-04 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 5, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-04 10:24:43', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-04', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOWI3MzA0NTgtNGI3MC00OTE5LWE5NTUtMzBhODE5ZTVhYzYxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM3MzM0ODMsImV4cCI6MTc0MzczNzA4M30.RRlscqg2WzHxLPvilay39h5P0LYjoYUOeBdfR-ciuQo', '2025-04-04 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 6, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-04 10:28:08', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-04', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZmI2ZTI4N2ItNTc0Zi00ZTVmLWJkZGUtY2IxYzdhOGEyMTk1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM3MzM2ODgsImV4cCI6MTc0MzczNzI4OH0.nbkSIH-SwMQ3yTPNU1gLmGvhZDDIewvzm8HyXJeyIVo', '2025-04-04 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 7, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-04 10:28:57', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-04', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiN2U0MzJlMmItZGUyYS00YmJlLTkwODktMThmZjYwOWZkZGZiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM3MzM3MzcsImV4cCI6MTc0MzczNzMzN30.MDq795JSMvM6z5NVAnXxvzvtKXSplELCw2ucqok8coI', '2025-04-04 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 9, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-04 11:31:20', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-04', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMDFjOWRhOGUtMmI5Ni00MWM3LTg0NjItMjEyODQ1YzVmNTdmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM3Mzc0ODAsImV4cCI6MTc0Mzc0MTA4MH0.bIl1FEUgWxpSHMd86M6eYYWRjVmqD2EPJXVUMo6_OoU', '2025-04-04 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 10, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-04 15:04:47', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-04', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNTU0NTY1MDQtOWE1NS00YTExLTg5NDUtZDFkOTNhNGZjYjcxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM3NTAyODcsImV4cCI6MTc0Mzc1Mzg4N30.1QLfx2O_jnkfvF7zdsJ1HtX9WedxKy56v3YTB-hqIZQ', '2025-04-04 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 11, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-04 16:06:40', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-04', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZWZhMmU0NmMtZTI5NC00NDU1LWI3MDQtZjVkZmE4MTVjOTdmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM3NTM5OTksImV4cCI6MTc0Mzc1NzU5OX0.OsWwnvZ62oySGObn2gyPTQx_T-K9pwA67Vg6JnIjH-Y', '2025-04-04 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 15, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-04 19:24:17', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-04', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNDMxZWU3ZTctOTk2YS00MWJhLWE4OGYtNDJjMzQ4YzMyY2QzIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM3NjU4NTcsImV4cCI6MTc0Mzc2OTQ1N30.2rh3GkU6ABMgozR4EWK3fDKp5ylzevR9kOtRbuH2aCw', '2025-04-04 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 21, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-04 19:58:25', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-04', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjczODc1MDQtZjNhZi00OTc5LTlhYzEtMTZlYmNkNTQwNDRlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM3Njc5MDUsImV4cCI6MTc0Mzc3MTUwNX0.REeuBBPoNFKAANuOQovannLxTVzi2PRKur435QjFXkQ', '2025-04-04 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 22, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-04 19:59:30', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-04', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYjdiZDIwMDEtMTY0Mi00OTk1LWI3MDItNTViZGZlNTljMzNiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM3Njc5NzAsImV4cCI6MTc0Mzc3MTU3MH0.5J_9hbcbXxddMhdAnGozvijiA0QDuIHmzWkIG3UKAkk', '2025-04-04 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 32, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-04 22:58:05', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-04', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzE3OTAxMGUtZGFlZC00Y2NlLWJjOWMtY2EyMGU1MjE4ZjI3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM3Nzg2ODQsImV4cCI6MTc0Mzc4MjI4NH0.ppR0QyHO7qLQWCRhC1AQrF-kihWG3-ZXEjIZBdSYGls', '2025-04-04 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 33, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-04 23:04:53', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-04', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzgyYzQ1ZjQtYTVlMi00NzQ2LWIzNWUtOGI1MjdmOTRiZDBiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM3NzkwOTMsImV4cCI6MTc0Mzc4MjY5M30.H0hLB8PdaL48pvIkOD_b1lx1VlHiCBGV2cJJWAoVcB8', '2025-04-04 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 36, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-05 09:10:14', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-05', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjFkM2E2ZDAtY2FkNC00NzA3LTk5YzktNjMyNDNkMzY3NWI5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM4MTU0MTQsImV4cCI6MTc0MzgxOTAxNH0.NcIWz-baiNDAl93FUFkT0NBnWH_4iZaV7B9gdtxIC54', '2025-04-04 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 38, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-05 14:58:17', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-05', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMDVhMjgyZWEtY2ZhZC00Y2Q1LWFhNDQtODA1YjIyMmJlM2Q0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM4MzYyOTYsImV4cCI6MTc0MzgzOTg5Nn0.CZg4O-4zlu01Meiyk9TJUA4KbJ8cl6QfEinLlaGcJzg', '2025-04-05 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 39, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-05 14:58:22', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-05', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYzNmZGZjMTMtYThjYy00YTE0LWFmYzItYTdhMTZiMjNjMjYwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM4MzYzMDIsImV4cCI6MTc0MzgzOTkwMn0.JrrhnobjRcLJ6r3Jlo0YzQJ3aumM2STl1XfrFB0q9TM', '2025-04-05 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 40, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-05 15:04:08', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-05', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZTNiNzk0OTMtNGI2YS00NjJhLTkxNWYtZTg1N2U3ZjlkZGU2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM4MzY2NDgsImV4cCI6MTc0Mzg0MDI0OH0.T28bShmLscvKnmzBDGFJiV52SXBKlGWSdm8nT69By1s', '2025-04-05 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 41, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-05 15:35:32', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-05', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiY2Q4YmZjYjctODdiMS00YjI1LTkyNzQtY2FhYTE1ZmQ2MWNmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM4Mzg1MzIsImV4cCI6MTc0Mzg0MjEzMn0.k5PINpJ_jVBzYkvsVtUBm4Zg0761gR1Q6Nb8h7TlJLo', '2025-04-05 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 42, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-05 15:54:05', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-05', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZGNiNDI1YTQtNDA5Yy00ZDJjLWIzNDAtYTAxMzViMjA4YjUxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM4Mzk2NDUsImV4cCI6MTc0Mzg0MzI0NX0.xCzAtMdoYt5R5JhZ31OaC8p05PsEZETggdpWndVKHgM', '2025-04-05 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 43, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-05 16:55:54', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-05', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNmZjMTEwMjMtNzMyZS00MzhhLTk4MzgtNjY4Y2IzMWQzZjFhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM4NDMzNTQsImV4cCI6MTc0Mzg0Njk1NH0.mEq_a0u59pqGNBqPHP5bfGGdh5vjVTj-OQPznuiBnTM', '2025-04-05 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 44, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-05 17:12:46', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-05', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOGFkYTczOTgtMzk3YS00NDU2LTkxYTMtMjRjZDIwZTBkNzAxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM4NDQzNjYsImV4cCI6MTc0Mzg0Nzk2Nn0.r9bjpmFQPD8wbfhStnkCFB8MgLpK2t1yv7yfjrL_gEs', '2025-04-05 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 45, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-05 17:17:05', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-05', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiY2M4YzIyMzctNGNmMC00MzIzLWJiODEtM2UxZDhmMDMxZjZjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM4NDQ2MjUsImV4cCI6MTc0Mzg0ODIyNX0.9fH3MluQ-NAF2vgQnoy8LW_1rRhKigsa1FOsekkLajM', '2025-04-05 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 46, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-05 17:30:05', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-05', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWQ2YjgyMzEtYWQwMS00M2M4LTk2Y2QtODU5ZDY2N2YyYzUxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM4NDU0MDUsImV4cCI6MTc0Mzg0OTAwNX0.R21bHCHhofoCSBLGkDww8idUNeyU05lCy1q_kKxtn8w', '2025-04-05 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 53, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-05 23:03:09', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-05', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYmZlNGJhNDktNjFiNC00YjhkLTgxMjEtMzNhZjgwYTUxNmIzIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM4NjUzODksImV4cCI6MTc0Mzg2ODk4OX0.MvOl9w29-AoK5wTa981FIFKr9VftKs2n72Rdf9DYGds', '2025-04-05 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 54, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 09:13:11', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNGNhYmMwOGQtODY1Yi00MjQ3LTk0NzAtYzBiYmJjZjU1ZWEwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5MDE5OTEsImV4cCI6MTc0MzkwNTU5MX0.Ibx4zje8h8aLATKaXs4Qy26E9T2OqnQrIVGKlJzUxrQ', '2025-04-05 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 55, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 09:25:54', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMDJhZjVhMTMtMWYwOC00NWU4LWIzZjMtZWM0OGRkOTMwYzM4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5MDI3NTQsImV4cCI6MTc0MzkwNjM1NH0.Q8rF9HpZ144QHbyRFa7B_DWTocVV2DtunEYnITylQHk', '2025-04-06 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 56, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 09:27:08', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTdlMzMyMmMtZTc5Mi00YTdiLWExOWYtZGY5YmZjM2MyMmNlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5MDI4MjgsImV4cCI6MTc0MzkwNjQyOH0.bclDFpeG4RcZF0p_8qKcOKmRJC9HsMxn9bgE3Ur7fX8', '2025-04-06 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 57, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 09:29:44', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNmMxNzBjMWQtOWRiNC00YTdlLWE5MmYtYmFiZWRjZmRjYzUzIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5MDI5ODQsImV4cCI6MTc0MzkwNjU4NH0.TELIc28pmGfaLXF3K0JpIfNwAm7NDsxT6qaTknWQbvs', '2025-04-06 00:00:00', 'kitty', NULL, NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 58, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 09:50:56', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNmU1NWRkZjAtZWIyZi00YWMxLTg4YzgtODE5MGRmODBjMzU2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5MDQyNTYsImV4cCI6MTc0MzkwNzg1Nn0.e-lMZa3_Cxfo5lNWNa95b2tChz2kF6CrIFVKyF2LHV8', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (4, 59, 1, NULL, NULL, NULL, '2025-04-06 09:53:43', 'xdxzwxq@126.com', 1, NULL, b'1', '2025-04-04', NULL, NULL, NULL, NULL, '2025-04-06 00:00:00', 'xdxzwxq', b'1', NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (5, 60, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/user/2/1743518421174-avatar-5490599774882110159-宠物狗.png', NULL, '2025-04-06 09:58:26', '1448185924@qq.fake', 2, NULL, b'1', '2025-04-01', NULL, '1cS+NtSZF4lT8kA3TtawcA==', NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzI3NzYzZmMtMTc0NS00NjdlLWFiOTQtMGUxZDViMTBkM2M3Iiwic3ViIjoiMTExMTExIiwiaWF0IjoxNzQzNTE0MzMzLCJleHAiOjE3NDM1MTc5MzN9.-Es_His3AnZbmBhnKHX_6wGhNijcq5fQIwD5oLS3WW0', '2025-04-06 00:00:00', '111111', b'1', NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 61, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 10:54:47', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNTQ2NWRhNmQtOWU2NS00OTMyLTkxZjEtNzBlZmI4M2VlZGM4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5MDgwODcsImV4cCI6MTc0MzkxMTY4N30.r_48Nrs38Iru3GoB56_SshqXpDZw826lkGEOJwHtmEo', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 62, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 11:55:37', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOWRlNDNkM2YtOWE5My00MTgwLThiNzgtYzljZTA4OWU5MWJmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5MTE3MzcsImV4cCI6MTc0MzkxNTMzN30.NDR81bcEhLCLAU9AYDtfYn3foMGi8wuZZEg06CAyJ3U', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 63, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 15:00:49', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYjYwZWU1ZTAtYjc2MS00MzM1LTljMzMtYTI4ZTY4ODkyOGQxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5MjI4NDksImV4cCI6MTc0MzkyNjQ0OX0.uPr9t79UjiR8wXJY4L93_KbucGdhHiqO83XtHrxkKHg', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 64, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 15:35:50', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZDU3MGNmYWMtZTMwYy00ZGVjLTgxOTYtNDE3MTYyNmY1NGVjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5MjQ5NTAsImV4cCI6MTc0MzkyODU1MH0.pW4yqXV_8sTni60EKWktL9FLxq4yhA0CAt-dfNMQt6w', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 65, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 15:37:31', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiM2NmOWJiN2UtMWFiOS00NWNlLWI2MzctNTAwZTA5MjVjNmRhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5MjUwNTEsImV4cCI6MTc0MzkyODY1MX0.FSFa_kIeLWzUrF3ARxfGRWByJX_oGvY1bS-yesAQhrk', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 66, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 15:38:13', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYjA2MjViYjMtYWY2Mi00ODlkLWJiYzQtMTg4ZjMwOWEwOWY4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5MjUwOTMsImV4cCI6MTc0MzkyODY5M30.gzbnk2CmzWAUOfv1esRyDsUtah3Y5gWE_T0WSzY_35E', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 67, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 15:42:43', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYzgyZjI1NzYtMDdlNi00ZWI5LWIwZjUtMzQ2N2I2NTlmYTRkIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5MjUzNjMsImV4cCI6MTc0MzkyODk2M30.1WSx3E30jvKXiWv9IFtnjCTJNnML8_OYdS8zjLEIXqc', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 68, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 15:44:15', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiN2U2MjNhNGYtMzhhNi00YzU5LTk4MzEtYmU3MTllMGQwZDI3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5MjU0NTUsImV4cCI6MTc0MzkyOTA1NX0.9jYBKICZtv72Yct6o0LA4G0zFJvKeC6vYd6PW1N0u5M', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 69, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 15:45:48', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMWJiNmRkMzYtN2NiMC00ZGY2LWE2NzctMmI0ZDQ5OWExOWYwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5MjU1NDgsImV4cCI6MTc0MzkyOTE0OH0.wEItWbcmHNFTAUZ24jMmLbvfZ6mvgKGABthMcAo5lYk', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 70, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 15:50:59', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNGY4NWE0Y2YtYTQ2ZC00ODA1LWJiNWYtYjRkMmU4OGRmNDY2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5MjU4NTksImV4cCI6MTc0MzkyOTQ1OX0.Gj0jId4fB-kELP9J98dLUFdPf4A0AYIiRjt74lFsD5c', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 71, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 15:53:08', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTU4YWMxNDQtMmY4YS00NTFiLTlmYTQtYzE4Zjc0OGZiNTE5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5MjU5ODcsImV4cCI6MTc0MzkyOTU4N30.Vk7f-u0NOZGfiBKEcyx_hCzzSC0zAHok_vpr_LNW76g', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 72, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 15:55:05', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYjYzZjk3NDEtOWI4MS00ZGQ1LWE5MGItMDdjZmNlOTdkNTJjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5MjYxMDUsImV4cCI6MTc0MzkyOTcwNX0.HS_z8Qioa1EpbBfszG0hJAI-oO6eo3JhZEqzx24BmVI', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 76, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 16:56:14', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZGU4NWI2OGYtZTAwNC00NmYwLTliMzctNDk4YjI3OWI4ZWRjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5Mjk3NzQsImV4cCI6MTc0MzkzMzM3NH0.Vie6dFApNwOHmFQIgya5OCo6GxyTMkTcQIVvS6sBav8', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 77, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 19:17:02', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZjEwYWU0MzMtMjdmNy00MTRjLWEzNGMtYmJkZjk5ZmUwODY1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5MzgyMjEsImV4cCI6MTc0Mzk0MTgyMX0.75mAelY6p1sL54swQgrJ3TeTGc6AVc0v0QnQQhrmqZ8', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (6, 79, 0, NULL, NULL, NULL, '2025-04-06 20:02:26', 'jonny', NULL, NULL, b'1', NULL, 'jonny.jiang@sap.com', NULL, NULL, NULL, NULL, '1', b'0', NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (7, 79, 0, NULL, NULL, NULL, '2025-04-06 20:02:26', 'kitty', NULL, NULL, b'1', NULL, '2021212077@stu.hit.edu.cn', NULL, NULL, NULL, NULL, '2', b'0', NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (8, 79, 0, NULL, NULL, NULL, '2025-04-06 20:02:26', 'admin', NULL, NULL, b'1', NULL, 'wzk@sap.com', NULL, NULL, NULL, NULL, '3', b'0', NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (9, 79, 0, NULL, NULL, NULL, '2025-04-06 20:02:26', 'xdxzwxq', NULL, NULL, b'1', NULL, 'xdxzwxq@126.com', NULL, NULL, NULL, NULL, '4', b'0', NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (10, 79, 0, NULL, NULL, NULL, '2025-04-06 20:02:26', '123456', NULL, NULL, b'0', NULL, '123456789@qq.com', NULL, NULL, NULL, NULL, '5', b'0', NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (11, 80, 0, NULL, NULL, NULL, '2025-04-06 20:13:49', '小铃铛', 2, NULL, b'0', NULL, 'kitty', NULL, NULL, NULL, '2025-04-06 00:00:00', '2', b'0', NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 81, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 20:19:13', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZDI2OTQ4NzAtYzE2Ny00MzM4LWE3ZDAtOWYxOGI4NDA3ZWE4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5NDE5NTMsImV4cCI6MTc0Mzk0NTU1M30.TWHQvNn_VwF3l-mgUx_g0R53o1GQ8AIXWKQeUbXZOmY', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (12, 82, 0, NULL, NULL, NULL, '2025-04-06 20:24:01', '123456789@qq.com', 2, NULL, b'0', NULL, '', NULL, NULL, NULL, '2025-04-06 00:00:00', 'wyx', b'0', NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (12, 83, 1, NULL, NULL, NULL, '2025-04-06 20:30:43', '123456789@qq.com', 2, NULL, b'0', NULL, '', NULL, NULL, NULL, '2025-04-06 00:00:00', 'wyx', b'1', NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (12, 84, 1, NULL, NULL, NULL, '2025-04-06 20:40:54', '123456789@qq.com', 2, NULL, b'0', NULL, '', NULL, NULL, NULL, '2025-04-06 00:00:00', '', b'1', NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 85, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 20:46:32', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODFiOWNmZTMtMWQxOS00MTY5LTk4NDctMzFlOGFkM2UzNzQwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5NDM1OTIsImV4cCI6MTc0Mzk0NzE5Mn0.blYaxFzifa12g7oK4EirrUEb2qZbh6UeynX4NupH3b4', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 86, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 20:50:06', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzI0ZjViZTktZjY3ZC00YmMxLTllZTYtNjE2MjUzNzFiYmExIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5NDM4MDYsImV4cCI6MTc0Mzk0NzQwNn0.tZt3zcNfy1KhhYx4z55CrJNGIuDZloWZJazb1T34MIo', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 87, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 20:53:03', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOGVjZjMwMjMtYmZmYy00MTRmLWE0N2QtMTE5MjI5YjQ4Y2UxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5NDM5ODMsImV4cCI6MTc0Mzk0NzU4M30.M-0Bn2GbsjQv-y6OLBgZhZBlY0jPT6F5BCpiTPH7gfg', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (12, 88, 1, NULL, NULL, NULL, '2025-04-06 20:53:27', '', 2, NULL, b'0', NULL, '', NULL, NULL, NULL, '2025-04-06 00:00:00', '', b'1', NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (14, 88, 0, NULL, NULL, NULL, '2025-04-06 20:53:27', '123456789@qq.com', 2, NULL, b'0', NULL, '', NULL, NULL, NULL, '2025-04-06 00:00:00', 'wyx', b'0', NULL, NULL, NULL);
INSERT INTO `fs_users_aud` VALUES (2, 89, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-06 23:13:16', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-06', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjFiODI3YmUtNzQ0OS00ZjE2LWIyMWItNmI5OGMyNmNkZGZmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5NTIzOTYsImV4cCI6MTc0Mzk1NTk5Nn0.5WLv4iAqEDvuvHzsjO8taxWQBFxMIwkLpJBkhGJmpfU', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 90, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 10:01:37', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNWNjNGRhOWMtOGY5NC00YTMzLWFiMGItNjUxMDMxN2IyM2ZkIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5OTEyOTcsImV4cCI6MTc0Mzk5NDg5N30.PGCJN01VzWk1yQmcgF-p8vJJZK-s-yvGMkM0AfH6erk', '2025-04-06 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 91, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 10:05:57', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTdmZmY5NjEtZTdjOS00ZjRkLTg1NjctOTRhZmM1ODIzNmE0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5OTE1NTcsImV4cCI6MTc0Mzk5NTE1N30.-gUSN8PzPSaPsVNbyWQvMo6omJ7RpuQ0NwN02DSCsxM', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 92, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 10:07:06', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMWZiZTk3NmEtZGNhYS00MTJhLWJiNWEtYzdjNTQ4MzA2ODc4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5OTE2MjYsImV4cCI6MTc0Mzk5NTIyNn0.IVIoD0LKU0XtGtgT9RFIuGYxjb1DnlX8-Zr3VFW5PLM', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 93, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 10:50:06', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNGEyNGU4OWItZjIyYS00MmY3LWJhMTUtYzk2YmNlMjQ3M2ZkIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5OTQyMDYsImV4cCI6MTc0Mzk5NzgwNn0.kH1tZ6DyN11zzc48veLD3tpB6T_TAS5ZL37QKuyLBR0', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 94, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 10:50:45', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTAxZGRhNmMtM2RlNC00Yzc3LTgxMjgtZjAyZjk4MjYwNTJiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5OTQyNDUsImV4cCI6MTc0Mzk5Nzg0NX0.8_WB3ZS-j_N5xMIY2eK8odbdhJgGAsTD53txJkJ49yg', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 95, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 10:53:12', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZGUzM2FkNTMtNThiOS00ZjIyLWFmZDItODRhNGMxZTE4ZTAwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5OTQzOTIsImV4cCI6MTc0Mzk5Nzk5Mn0.dsWLiE9nUi0DAYVttXyFEOd30niWlM1TKXepj5gLj4k', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 96, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 10:56:53', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTNmMzc4YTEtNzFjMS00YzZjLWEzNjMtMTIxMGIyOGI3MmUwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5OTQ2MTMsImV4cCI6MTc0Mzk5ODIxM30.McWzX4UkbqQuMOp5iTAm3edi5DlUSNV3WEVMpEwaw2w', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 97, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 11:32:28', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiM2E2ZTY2YTQtYjBkOS00ZjI5LWEzNTgtY2UxYjFjMTU1YjA5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5OTY3NDcsImV4cCI6MTc0NDAwMDM0N30.S08L3yjvQKQ5F4w87a2ZKM87-wG4moldl2mJdxIa4Vg', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 98, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 11:33:22', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMGYwYmQ1NmEtY2QxZS00ODRmLTgxZDAtYTM4MmNiM2ZmZTUxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDM5OTY4MDIsImV4cCI6MTc0NDAwMDQwMn0.w-jHgi7PawrqYQsfIVqxiMADe9NGadJJdcK63R8LKsE', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 99, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 12:34:49', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWZiMGI3YWEtN2NiZC00Y2Y2LTgxMjMtODA4ZDhmYTczNjAyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMDA0ODksImV4cCI6MTc0NDAwNDA4OX0.EN9u-3xk-UTRimW5XQgIAGnAYmALffCJ0vEZ6ilmcHY', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 100, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 12:36:29', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMDRkNTlmNzAtZWQwMS00NmIyLWFmN2EtM2M5YWQxMzM3OGU1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMDA1ODksImV4cCI6MTc0NDAwNDE4OX0.L-cXEcUIpDpEWejDraFPyzhc1s-uso6FhuP2e9pIjvY', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 101, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 15:00:49', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNmQ1YWNhMmUtMGVjMS00ZjNjLWI0MjQtMDRiMmNhMjliZmYxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMDkyNDksImV4cCI6MTc0NDAxMjg0OX0.RfSpsw43NJUwAs6CHgTcRNOL23us3ni_tSF1HhrkdSo', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 102, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 16:04:41', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzQxMTM4OTAtODI5Yi00ODM4LWFhNjItM2MyOTE5ODg3YTRiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMTMwODEsImV4cCI6MTc0NDAxNjY4MX0.jenlajqN1do7waaNZqLq8_JAeQ_-6oXmibU_5eN8P2s', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 103, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 17:34:51', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYmU4YTcyNTItMjUxNy00ZjhkLWI2ZTMtNzdkYmMxNGZmZDg2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMTg0OTEsImV4cCI6MTc0NDAyMjA5MX0.GQTSKaCom0shn-2x9zDIMuZ1xaI_xwj568ntJaQ83LU', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 104, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 19:13:47', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZmY1NWRlYzEtZTI4Ni00ZDNlLTgwYWEtYjhlNzRhMmNlNzU1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMjQ0MjcsImV4cCI6MTc0NDAyODAyN30.lOofvKzlmtBreiIcnd18_YGW0plddnwkFufWNjNwpuU', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 105, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 19:46:18', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZjg0ZmM1ODQtYWVkZi00ZDEyLWE4YTAtZDNmNWYyNjUzNGVkIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMjYzNzgsImV4cCI6MTc0NDAyOTk3OH0.HqK8alPYkWIapJq03eLrYJ1NoJuQFPwf4K66C3sOGM8', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 106, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 19:58:08', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYzBkNjYzYTEtM2E5Yy00YjJlLTk2NDktNjE2ZjI1MzIyOGJiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMjcwODgsImV4cCI6MTc0NDAzMDY4OH0.qK1stYOcrJtNkwm81EJF9k0MoZ2bdEIelAnVn3a5R78', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 107, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 19:59:40', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMmY2MDdjODYtYzk5Zi00YmU0LWI1NzgtYzg4ODgwZTJhMzVlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMjcxODAsImV4cCI6MTc0NDAzMDc4MH0.eRers7kjE4XHYtYbCJ_P7c2dReQxBjxAt3Tn_GWI1Ys', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 108, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 20:14:31', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTc5YjVhYjktNzY4Ni00MDBiLWExMWQtODUxNmRiYzQwNTdmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMjgwNzEsImV4cCI6MTc0NDAzMTY3MX0.iKeTjKfbH63jPyplo6fs2hXH_abGFZsXHj3wT5q51to', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 109, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 20:15:23', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZDk2NmE2Y2UtMjJiOC00OGI3LWIyMjItN2ExNmM5ZWNjYWVjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMjgxMjMsImV4cCI6MTc0NDAzMTcyM30.g9ovth2qMofBe0A7HyTFZLPgWXrUeXw4raM2NFqeOow', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 110, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 20:19:30', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNWFlYTZkYmYtNzY3Zi00MTdhLWI0MjEtZWNiOTdkYTIzOTI1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMjgzNzAsImV4cCI6MTc0NDAzMTk3MH0.Ijcy4zzwQUwIbQ3n3pyORYM1-bVddtd7kugb7sScL0s', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 111, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 20:21:52', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOGFmZGM4YmEtN2ZiOC00NDA2LTkyZDUtNGMzYTU3ZDNmNjdiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMjg1MTIsImV4cCI6MTc0NDAzMjExMn0.CMX7RoIWw6Zqj-A5oCNdnI8F9chQUvWPdsq4LecAMA4', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 112, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 20:28:28', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZmY4ZDJlYzAtMDRmYi00ZmYyLTg0MDYtMDhkZTViYmMwNjZmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMjg5MDgsImV4cCI6MTc0NDAzMjUwOH0.MGI61Upw87jbgZoB9G6KkRQwQDHLo_jL-B-uA5_EwVM', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 113, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 20:52:12', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNDZmYTkyNDYtYjYyMi00NmUxLTkxYzUtYWQ5ZGI4ZDkxZWNmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMzAzMzIsImV4cCI6MTc0NDAzMzkzMn0.j-Es044HGKechkWee0-ht7NL2mgU7Z-XlyhKPGtw2mQ', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 114, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 20:57:15', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYjJiMjgwYzMtMTNmOS00NGU4LTkwNDQtNjg0ZjlkZjIzMGNmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMzA2MzUsImV4cCI6MTc0NDAzNDIzNX0.5NeAMEe0QItGJXEu1Di4ybnfw5zdakg_5jBtarVMzD4', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 115, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 21:00:26', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYjI1YTZjNzItNmM3ZC00ZjFhLTgyNDEtZWI1MjRjYWIwZmNlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMzA4MjYsImV4cCI6MTc0NDAzNDQyNn0.G0-erirAY9SAyowe0m7m8sQjRIs7sQ0T7yzahqY2UCQ', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 116, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 21:24:58', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjM0ZjYzZWQtNGVkZC00NTA3LWFhZDktYzc4NjkzM2UxMjZiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMzIyOTgsImV4cCI6MTc0NDAzNTg5OH0.D2uiWb6SLNCNqOIY-jMvhJvPF60HVy472rpKVcFS_Mg', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 117, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 21:26:40', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNGM5MjZhZGItMzAwOS00MjlhLWIxZjUtY2FiYTRmZmUxNTFhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMzI0MDAsImV4cCI6MTc0NDAzNjAwMH0.uxSb4GoZDdekaLB7upJ68gGDdzI6cNEltwPTJUYDnAg', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 118, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-07 22:32:47', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-07', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjcyZjJhY2ItNjMwMi00ZjA4LTkwMDMtZjFmNzE0YjEzNWUyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwMzYzNjYsImV4cCI6MTc0NDAzOTk2Nn0.ZjXAVWyIGF33DL9zTP5oyKH2UmqpjksEnKrWnREkEcU', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 119, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 09:07:18', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTBhMWFlZGQtZjZlMi00ZGNjLWJjMjAtYTMyYzE2ODM0MzMyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwNzQ0MzgsImV4cCI6MTc0NDA3ODAzOH0.UgdGVQfvj7wKC9xLGnOit22sO4tRRfdGwTjXRHICdwo', '2025-04-07 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 120, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 10:07:47', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYmFiNDgyYWYtZDMyZi00N2VkLWIwMjMtMTEwMWIzMjgzMGFhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwNzgwNjcsImV4cCI6MTc0NDA4MTY2N30.EC110MJw5UD7jAvwwDW10QmEx5eEKTT4yjUsKUdKmQU', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 121, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 11:09:30', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYjMzNTgyNWYtY2Q3Ni00MDA1LWEyMmEtNTMwY2ZhMGRkYjE0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwODE3NzAsImV4cCI6MTc0NDA4NTM3MH0.f3dsausTZ8OGjROAvDer5G8MvssZJDBiGtBLXm3eQ4A', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 122, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 14:41:07', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZGM1N2I1YWEtNTkxOC00MGYxLTlmZGUtYzQ0NDdhZTczNmJkIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwOTQ0NjcsImV4cCI6MTc0NDA5ODA2N30.gUiaVcxv2aPKIVwQ00JdZ9HHOttxTnwbVnce8sKFC_0', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 123, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 15:32:16', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZTYwYTU5MTMtMTE3Mi00YTI1LWI1ZjctMjVmNDIyNWY5OTQxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwOTc1MzYsImV4cCI6MTc0NDEwMTEzNn0.EjSzKgr9ANTmWHcPaOpNxZXAiltmvB4Wpp8kIDFHy1E', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 124, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 15:35:30', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMmIyM2ZmM2QtYThlMC00OTg0LWJjNzQtODQ4NzU0OTU0ZmZhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwOTc3MzAsImV4cCI6MTc0NDEwMTMzMH0.kV_SVZWxN_rkTwPWucpaGss0hgqmZ4ESuqZPgVsVF1I', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 125, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 15:38:45', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMTQzYTQ2YjAtNzMwOC00YmI0LTg4ZWMtNmYzMWNkZDY3MzIzIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwOTc5MjQsImV4cCI6MTc0NDEwMTUyNH0.Gw2yh66AkjjkXTw8jII5yeNRabFa625VjL7rt4eIiRo', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 126, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 15:49:32', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjY1ZmMxYjMtMWM0MS00ZjE1LWI3NzMtMWQ3NzE1NzJhMDdmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwOTg1NzIsImV4cCI6MTc0NDEwMjE3Mn0.FoTU4zq21GT-G3HdUFPGUZ7kV3FJl23M-96S3Sb_HuI', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 127, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 15:58:12', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTg3MmI1OWQtMWFkMS00Y2JhLTgwNmEtOTIzOGNhNmM4MTY2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQwOTkwOTIsImV4cCI6MTc0NDEwMjY5Mn0.NnY7CdbTODUu82Sr8bvO57JzfxxvfxLoyndXcsKEo7Q', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 128, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 16:38:28', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzU3NjRiZDMtNzczMi00ODgwLWJlMzItMDY2YWE0Mzk3YWZiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxMDE1MDgsImV4cCI6MTc0NDEwNTEwOH0.fwWbYNfy70fTwqbhStuuyN2zTPisQoJz1tCSWBv-tqg', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 129, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 18:56:39', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTFlN2ExNjctMTk3Zi00M2ZmLWI0NWMtYTYxNzFkZGEyYjEyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxMDk3OTksImV4cCI6MTc0NDExMzM5OX0.VqoEoMdD4OKQRbKK734Tw1cVg3xN9QZgZzIhWLZxzBE', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 130, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 18:58:08', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNTMwZThiOTItMjMwZS00NTY4LTlmY2QtNGJjYmYwMzhiMTZlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxMDk4ODgsImV4cCI6MTc0NDExMzQ4OH0.2sK3GphDbcq7G02ghlBIQCdg-UyJqyyUZsbMalwiA3w', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 131, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 20:08:53', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzY4MTE1YzItNjJmMy00MWMyLWFlYjMtOWFkMjE0NGY2MDU2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxMTQxMzMsImV4cCI6MTc0NDExNzczM30.84xn0rTPjPfVNjrP3IzTJbKV_WfFCN3lH6t4uTZMmp0', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 132, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 20:31:13', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODk3ZGVlZWYtMmMzNS00ZDdkLTgyZWEtOWJjOTA2YTNkNTkyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxMTU0NzMsImV4cCI6MTc0NDExOTA3M30.y1pnlaPPmazN_N6shNwatU3tmox-KJ0ZuaWSAhpIQ98', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 133, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 21:04:22', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOWE5NDQ2OWItNjg5MS00YWJmLTg1ZGEtZWVmMjFlMTI2YTFjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxMTc0NjIsImV4cCI6MTc0NDEyMTA2Mn0.uXcleEdgWrGCsCcdI0bCRNhVAbZJCNPXc4qUvCNbCiU', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 134, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 22:53:55', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMjAxOTYxY2YtOTMxYy00YmRkLTg3MmMtYjI2YzZjMGM1ZmQ3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxMjQwMzUsImV4cCI6MTc0NDEyNzYzNX0.o8bI8NVtzNadWacbzEYseXrteOY4VuoGhDgEaSgeHbY', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 135, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 23:02:41', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZWM3ZjhmZmUtNzljNi00NDY0LWJkMTQtNjY1MzM0OTQ4YzI1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxMjQ1NjEsImV4cCI6MTc0NDEyODE2MX0.bremqB_Wr760nLeSA-T3Oj3nKwQ98IzEiiJGvcsdml4', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 136, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 23:11:20', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMTMwYzk3ZTYtY2I3ZC00MGU1LWE2YjktMWE5ODUzODU4ZDNlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxMjUwODAsImV4cCI6MTc0NDEyODY4MH0.kHO8HoXll0zgJuYVDNQf_YbDOOJ_0PlteIC-4NbwQgU', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 137, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-08 23:13:51', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-08', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMjJmOGY3ZTQtZjQ4OS00OGQzLTk3ZDktZGU2YmU3ZjBlNWI4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxMjUyMzEsImV4cCI6MTc0NDEyODgzMX0.pHnjxOXD9lrlIBqKgOljuzkyJWpTdZd_tUu5Xp9k5Eg', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 138, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 09:33:49', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNGRiNGRkMjUtZmRmMS00OWZiLWIyODUtMjZjNTg4MTU2YTFmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxNjI0MjksImV4cCI6MTc0NDE2NjAyOX0.TwXhPQVe8b7__v7h4vrP9NpDrtBXphoK61y4PjPFp8g', '2025-04-08 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 139, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 10:37:16', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjI5ZWYzMzQtYzhhMy00NDZmLThkOGMtNWNiN2I0MjU0NjZmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxNjYyMzYsImV4cCI6MTc0NDE2OTgzNn0.WTqcM324o6HBQB2lQEICihqHWkX3TyFQqAh2pynZHCI', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 140, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 10:59:52', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYjJhMGJjZDctNTA3MS00YjUwLTllYmQtZmI4OTQxZWRiNTU1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxNjc1OTIsImV4cCI6MTc0NDE3MTE5Mn0.LluHog6f6ceSlsw20HpQX0NWN1tSnlKfSxa4WoPjFKc', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 141, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 12:38:49', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWI4NjFlYTktNDg1NC00Y2ZjLWFlNjgtNGVlYmRhNGJjMmI4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxNzM1MjksImV4cCI6MTc0NDE3NzEyOX0.53I0AZxiwyW6MuGAkujytv3iPE2Yx0VlRAxPRpR0gmA', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 142, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 14:44:00', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTk1YjI0ZDYtYjQ1YS00ZTc1LWFlNDQtOGExOWY1MzAxYjVlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxODEwNDAsImV4cCI6MTc0NDE4NDY0MH0.e6mRTTwhNAPDpNc6dVtkKtQFnut3tLBqssSppG5xDkY', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 143, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 15:49:54', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTM2YjE2MDktNDgwMi00ZDQyLTkwMmQtZWUxNGFiZmMwZTQzIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxODQ5OTQsImV4cCI6MTc0NDE4ODU5NH0.PqxZwUcCKQTcGmyaZhVIrUTgQkUShaPmandDpfKoFT0', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 144, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 16:51:37', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYzZhMWI2YjMtNjFmOS00MDBiLWI4YzgtOWM3NTY5NWFkMGY0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxODg2OTcsImV4cCI6MTc0NDE5MjI5N30.ZNwNDopNfaFvDFKNNvBq75lG9faaosdOO2F09pWvAzI', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 145, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 17:05:41', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOGY2NzNlOWYtOWJjZi00OTMxLThkZTQtNTZlYzE5NjhiNmU2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxODk1NDEsImV4cCI6MTc0NDE5MzE0MX0.fYvzXpVwC6nQj7trcpJsOEMgMqub1bvUGv_-InJy-54', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 146, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 19:03:06', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiN2NkMzk4MDktZTQ3MS00MzM5LWFhMzctMjRlM2YyYzI3NzJhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQxOTY1ODYsImV4cCI6MTc0NDIwMDE4Nn0.0_v4iZPERS8lS3w_lM984YU5BA-GVXWuOFkq_QTmQZo', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 147, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 20:18:00', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMmJmNjI4ZjUtODczNS00ZjJlLWE2NmYtOGY0MWYyMWJkN2Q1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyMDEwODAsImV4cCI6MTc0NDIwNDY4MH0.mAFTRWe-bP4rMWwJs6zE06AmfBNz0lcIhDO0wkkSIlY', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 148, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 20:19:35', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNGU1OTZhZTgtM2VkOS00NzY0LThjZmItYzk2MjAxN2IxMzI1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyMDExNzUsImV4cCI6MTc0NDIwNDc3NX0.XmtGpyWl4wMPq0xv28gCTH8scLCu7GezL8UqGW2BsSE', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 149, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 20:23:29', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNzRlNjhmMWQtZjA4OC00OTIxLThiM2UtNGVhMDZlNjBmOGZmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyMDE0MDksImV4cCI6MTc0NDIwNTAwOX0.a2ChEGb9-cIkQ24cwFqhcLp6QtSAHlIXze1FutkOR1g', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 150, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 21:38:45', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOWYwOGViZWYtMjhiOS00NWY1LTk5MzEtZjA4MjVkOGNmZmRhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyMDU5MjUsImV4cCI6MTc0NDIwOTUyNX0.njile-JQC5PEUimpOjtFz_jwDYILcNx8I6G0EmfS3fc', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 151, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 22:08:32', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzMwMTQ3OTAtMzc1Zi00NGM5LWIxOTAtNjkxMGMyZTg1MDcyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyMDc3MTIsImV4cCI6MTc0NDIxMTMxMn0.gIg0euh_NnHkb6xvIBr-85GuiKw8poCEclgfQkLzRoM', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 152, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 22:11:10', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMDJmZDI5ODEtMTg4OS00ODQwLThiYmUtNWY4YjBkOGJjZGFjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyMDc4NzAsImV4cCI6MTc0NDIxMTQ3MH0.WF49HWFfzSHKbul5j-jUPMnnJD0uAH1gz-Xw_WjqA-o', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 153, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 22:12:43', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMmI3YWIyZDEtOTQ4Ni00NzhlLTkyYjUtMTQ2YjEzNjYwMmQ5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyMDc5NjMsImV4cCI6MTc0NDIxMTU2M30.-KoEOi80rrYhFW4-kGJUOVfly_QDw3bhLGmc2QG1plA', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 154, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 22:14:59', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZTljMTJkYmEtMjQ1MS00MWZkLWFkMzctOTU0ZTk3NThlOGEyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyMDgwOTksImV4cCI6MTc0NDIxMTY5OX0.br3CCHFa2d8wTQtmho8VHrgeWzI4QKJpPfhNshh5dk4', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 155, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 22:41:46', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYmU2NzIwZjgtMDM1Mi00NmYwLThkZDQtN2E3YWFkZjAzZGJlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyMDk3MDYsImV4cCI6MTc0NDIxMzMwNn0.iQfVi6YRsId-gEEq44gAXT2h4Y2sGnSO5PEwUf-ZISE', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 156, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 22:42:11', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODk1M2NjMjMtZWFhNi00YjY0LTgwYWUtZDBhNDNkMDQxZWE4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyMDk3MzEsImV4cCI6MTc0NDIxMzMzMX0.4gBOHC9j_b8g7--USSNMr0qvtSp-jyY2daLO70Gn5as', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 157, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-09 22:42:56', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-09', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiN2Q4ZTg1MjgtOTk2YS00ZTgxLTg5MTQtZDRmMDc4YmZlMzgyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyMDk3NzYsImV4cCI6MTc0NDIxMzM3Nn0.hbTY-lPuGEWxi9FAKQgx09w-jvprBIpoCBfj2H6VmtM', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 158, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-10 09:34:04', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-10', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNzkyZDAzMTUtZTI0MC00ZmU4LWIxMTMtNjUzMTU0Y2FjOGNlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyNDg4NDQsImV4cCI6MTc0NDI1MjQ0NH0.xiOMoUGoW1uRyZt8TFu-YEKCEuMRcsXKRcMYdWLjfdc', '2025-04-09 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 159, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-10 10:41:24', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-10', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZTExOTNkOWEtYmQ5Mi00NmUzLThmY2UtMDdhOTRkNDY3YWYxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyNTI4ODQsImV4cCI6MTc0NDI1NjQ4NH0.mKk8110FFPUgsHjv5ffa6GXdLW8pes66hGGUwck2-UM', '2025-04-10 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 160, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-10 12:45:31', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-10', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMDU0MThjOTQtYjcxYS00MGVhLTgxNzUtNDFkNzEwNjU3MWMwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyNjAzMzEsImV4cCI6MTc0NDI2MzkzMX0.0GmpaVFjmC1Vx5yt-o9ebgMFZovXkG47ySfe-MQ1jWs', '2025-04-10 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 161, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-10 14:53:03', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-10', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiM2Q4ZGFmNzQtMWVmMC00NDg3LWIwNzgtZWM0ZjU5MWUyNzVmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyNjc5ODMsImV4cCI6MTc0NDI3MTU4M30.So_UfAoWUV4hU4nEjaXdZJke6yZqj5O494lzDtwXAYM', '2025-04-10 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 162, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-10 15:58:49', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-10', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNTljOGQ1NjAtNmFhNi00YjQzLWEzNzYtOTk1NWFlMGE1ZDRlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyNzE5MjksImV4cCI6MTc0NDI3NTUyOX0.MTIPnZt2r126jGaDwbUM4olgLZW_qgnh3G_LyvchKjY', '2025-04-10 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 163, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-10 17:04:45', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-10', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMDZhMDc4MmUtN2YwNi00MGI2LWI1ZmUtYzliZDdhZDYwY2M0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyNzU4ODUsImV4cCI6MTc0NDI3OTQ4NX0.hLSIIAflb6DWIdTz9Ie17_-cZeeaHUVZ-Wu2Mu_y5QA', '2025-04-10 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 164, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-10 19:05:08', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-10', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMDYzNDhiOWMtNDc1My00ODc4LTlkN2UtMzg4NDU2NjU3ZWFiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyODMxMDgsImV4cCI6MTc0NDI4NjcwOH0.Zgiq2sMItvI9u_QM8eXkQriq_DQ2sMIcyHMOAIaO00I', '2025-04-10 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 169, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-10 19:55:55', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-10', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMjY0Y2M2NzUtNWMyZC00NGU3LTlmMzEtN2ZjNmJhZGJjNDg1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyODYxNTUsImV4cCI6MTc0NDI4OTc1NX0.RhQ0qO6sEESbsThypROnEHGwdjsTtXD7l2mkIWaesX4', '2025-04-10 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 170, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-10 21:02:41', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-10', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYjY4YzRhMmUtYmZjZi00MjNlLTkwZmYtNGFkMGQ5YWE2NzE4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyOTAxNjEsImV4cCI6MTc0NDI5Mzc2MX0.vOV6OwVTfmzT1Zu5Bkml5huoahVyjKUoMt8ykv3LR-k', '2025-04-10 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 171, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-10 22:44:30', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-10', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMTc3ZGI5ZTAtZDZkNC00MTU5LThhZDUtNTM4ZWFmYWZhMDJkIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQyOTYyNzAsImV4cCI6MTc0NDI5OTg3MH0.F9N0TcP-UUEqVSXBqVe_-dChIT3-s0-koxpBHp9sCEM', '2025-04-10 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 172, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 09:21:01', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTMxMDg3ZTItYzcxZC00NDY5LTg4MWItN2EwYjI4MTdhOTA3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzMzQ0NjEsImV4cCI6MTc0NDMzODA2MX0.6qgothq1aiUgCKRMy9iz4UP3WlWXK2Ol4To4AJ6qpE0', '2025-04-10 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 173, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 09:43:20', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjdhZGE1NzUtMzhjOC00M2I4LTkyYTAtMzM3ODU1YTRkOWJhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzMzU3OTksImV4cCI6MTc0NDMzOTM5OX0.b157XQ22c9Z5JHgVT_oC8RjbR53SoKe5rqX6TSdo0M4', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 174, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 10:10:39', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZDA4ODUzZjAtZmI4NC00YWRiLTlmMWQtOWM2YTAzMjRhMjUxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzMzc0MzksImV4cCI6MTc0NDM0MTAzOX0.hSQU0jCBHlIo8A7e7C7Pu3uLOu09UwVmRMQly_CizVA', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 175, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 10:13:14', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNDQ2NGM1ZTAtNzE5MC00ZjRjLTgzMGItNDY4OWJkM2FhYmQ3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzMzc1OTQsImV4cCI6MTc0NDM0MTE5NH0.wYBJSCkkbtCPVE-vczf1Up6tqglL8KZ5tmLVdGdxS-U', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 176, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 10:18:03', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZGVjY2I1M2EtNGQ2Yy00ZmNhLWFiMGQtM2YyMDAyNWNhOWY2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzMzc4ODMsImV4cCI6MTc0NDM0MTQ4M30.Dhn7pDe9etqODHnCn3RkjK0-yLEoV4XbJrSSbN_z5mE', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 177, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 10:38:29', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYmU4MDk4OWEtZDYwYy00ODNhLTk1ZTEtNzY0NjQxYjc0NmZiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzMzkxMDksImV4cCI6MTc0NDM0MjcwOX0.oxmROr0t-A8eyttjheVPdk0xtyw38gp2u78cEgrOxL4', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 178, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 11:18:15', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTg3YzczOGItMzU0NC00YWY5LTk1ZDEtZjU1MWM0MzhlMGE2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzNDE0OTUsImV4cCI6MTc0NDM0NTA5NX0.ZKxTMxc8XkyvmVhOLEdhMy0SyHJvZO7K4AFXrBSTG0g', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 179, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 12:33:54', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZDA0N2I4ZGItMzk1ZC00ZjI3LWExZGUtMGQ1M2NlOGI2ZDYxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzNDYwMzQsImV4cCI6MTc0NDM0OTYzNH0.bK_MbggHb7QVPVPojcWq0rugAvbH_wbHe_rGwEiRBFk', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 180, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 15:04:55', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiY2YwNjZhYmUtMTgwNy00ZmE1LWExZGYtZDdlMjhkYzcyY2IxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzNTUwOTUsImV4cCI6MTc0NDM1ODY5NX0.mGN87tu1coDwa0gxxyjdtojdxjteHz7yHCnqTrGF8gk', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 181, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 15:33:37', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTg4ZTMwODUtMTAwMi00Njk2LWE0NDQtODE5YmQ1MGYyMDdmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzNTY4MTYsImV4cCI6MTc0NDM2MDQxNn0.Mlwvzl8GSru44WDkpCR-V55Uh96D9OYnpWdLwJjjaEM', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 182, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 15:39:35', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYjQxMzhmNjctOWIxZS00NjJlLThlNGMtYmE5NjgwMGNmYjQ3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzNTcxNzUsImV4cCI6MTc0NDM2MDc3NX0.fVw7VjHD9naGCnI6Zf2kCu95L8Az5q83vgtJ3SAll80', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 183, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 15:43:08', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZTVmZmVkZDgtOTJhMy00NjUwLWE5YTYtMzMxM2Y2YjY0NTRkIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzNTczODgsImV4cCI6MTc0NDM2MDk4OH0._kUerX4GnEt4j3nY0nSVZDr1fk1lf3HqxHJXv-rqbjA', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 184, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 15:44:20', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTVlNjUwY2EtNTk1ZC00MTg1LWI2YzUtNjZjNThmMThmZjJiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzNTc0NjAsImV4cCI6MTc0NDM2MTA2MH0.fmRBdWIwn16A4WCtHP0VtrPGig7aF4pCsQLOPM3DhrM', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 185, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 15:46:13', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODhiZjNiYWUtOTNlMC00NTk2LTg5MzgtODRiOWNjMmNiYTFkIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzNTc1NzMsImV4cCI6MTc0NDM2MTE3M30.w_hVAxDkia4xUIofX4uFy1cX0_P7Jo1Qu2LU8P-GyXU', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 186, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 16:48:24', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZmFiYzU2OGUtMmI2Yy00NDU2LWI4ZjUtOTA0YjM5ZWRiZDU2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzNjEzMDQsImV4cCI6MTc0NDM2NDkwNH0.fbRreFSq-A_ZdFtkfNFtIP9RS29lDveVUnELWkgGsNQ', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 187, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 18:52:59', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNmU2ZjY4ZTYtYzE5Yy00OTNkLTljNWItZGU4MjJmZDc4YTRlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzNjg3NzksImV4cCI6MTc0NDM3MjM3OX0.oQoSMcVAkINmizQ9MyNihBnM79mSg5NuhPYT-T8bWDQ', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 188, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 19:53:46', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjJhNWMwZmEtMmU3Yi00YzQzLThhYTItZWZlZTEzMDQzNzU4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzNzI0MjYsImV4cCI6MTc0NDM3NjAyNn0.2ItkJw_V2aSgvw9OR4ii7Vs0GQK6NClkNh47G80BwJA', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 189, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 21:03:06', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYzZlODM0MzAtMzc4Mi00Y2MwLTliZWQtNzNmMGM0Nzc2MzJhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzNzY1ODYsImV4cCI6MTc0NDM4MDE4Nn0.r8CvDv6sa-3twxNn94QhfYV5_1a1Vn3FYYs6a14oo2M', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 190, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 22:04:04', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZDEyMzhiNTEtMGI1Yy00NTUyLWFlYzUtODczMzg0NTEyNmI4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzODAyNDQsImV4cCI6MTc0NDM4Mzg0NH0.G816kEyZaNks4Y3iszPRzdHhSXoZeAXNNsUs7bn-Hfg', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 191, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-11 23:09:04', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-11', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTJkNTg3M2YtOTNmYi00MGU4LTk2YjItODRjYjk0ZmI5ZTU5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQzODQxNDQsImV4cCI6MTc0NDM4Nzc0NH0.PUojNR16eUQfrW4pxZKwswoNNhob8VdVQsvz2AuraLE', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 192, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 09:53:33', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMWEzMWRhYmQtMjYxMi00ZmVlLWEyMjctYTJhNTgyNGNlY2NjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0MjI4MTMsImV4cCI6MTc0NDQyNjQxM30.ppPAU_FPGd5PXAOlrM92dz_P50cBFOUvyU3Jv8t_tNE', '2025-04-11 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 193, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 10:54:22', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMDJhZDFkMDQtZWE3MS00MDI1LTg1YzQtNDM1Yzg2NzBhZTFkIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0MjY0NjIsImV4cCI6MTc0NDQzMDA2Mn0.RQJBeIsnCZjBWfBKhA_2ZNfFFrRzfDS1SS9mFlrwFP4', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 194, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 12:27:50', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMjk2Njk0YTUtMmFiNy00ZGViLWE2YTYtM2U5ZTBmZTUwNmVjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0MzIwNzAsImV4cCI6MTc0NDQzNTY3MH0.aTuKqynv5mxqQHlX4l2JpSinps1kBkNRVqgQvogJ_lo', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (1, 195, 1, NULL, '', NULL, '2025-04-12 12:55:44', 'jonny.jiang@sap.com', 0, NULL, b'1', '2025-04-12', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMjM4MTA5OTAtZDU4OC00MjBmLTg3OGItYWJhZjMyZDlhYjM1Iiwic3ViIjoiam9ubnkiLCJpYXQiOjE3NDQ0MzM3NDQsImV4cCI6MTc0NDQzNzM0NH0.uBH5pjMbfhnn9_kzyzSRM-inK9bRGho0NWm3uT9Vvb4', '2025-04-06 00:00:00', 'jonny', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (1, 196, 1, NULL, '', NULL, '2025-04-12 12:56:37', 'jonny.jiang@sap.com', 0, NULL, b'1', '2025-04-12', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZWQwYjE5MjYtNzY3YS00MTMyLTgwMjEtNDM5ZDA4M2FmNDY4Iiwic3ViIjoiam9ubnkiLCJpYXQiOjE3NDQ0MzM3OTcsImV4cCI6MTc0NDQzNzM5N30.-ifG90WMi_KS6O9xsfF-mB6NZIy75owDhvGI67zgpVs', '2025-04-12 00:00:00', 'jonny', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 197, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 12:57:27', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODhjYzQzNTAtZjIxYS00ZmRkLWIxMzItMDM1YjdkZDE1MTA3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0MzM4NDcsImV4cCI6MTc0NDQzNzQ0N30.Y2IKAZRskH2xW-C9ZKi8LEVfhUg13qqxrqxZpgCZbJM', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (1, 198, 1, NULL, '', NULL, '2025-04-12 12:58:30', 'jonny.jiang@sap.com', 0, NULL, b'1', '2025-04-12', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjBjMmVjOTQtYTU4Zi00YjNiLTkxNzktZjc4OWRhMjFjMTJkIiwic3ViIjoiam9ubnkiLCJpYXQiOjE3NDQ0MzM5MTAsImV4cCI6MTc0NDQzNzUxMH0.ALpyvpASjLYVQt85ZKbni0UHh7D88uDoCrJG3H9RjMg', '2025-04-12 00:00:00', 'jonny', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 199, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 14:56:58', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNDU1ZmEwNzAtMTY2OS00YjJlLWE3NmQtMGMwNzlmZjNmMjNlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NDEwMTgsImV4cCI6MTc0NDQ0NDYxOH0.b_OOtADFlhTmH1hlHjvPHlGEfcoDdHGerEBZg65vkk8', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 200, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 15:02:26', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZWExYWU5MDgtYjg5OC00MjY0LWI5OTMtMzU5YmEwYzQzNjNmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NDEzNDYsImV4cCI6MTc0NDQ0NDk0Nn0.7K0Oa5SOEls39yihSMDGof3a8zcq6JmwMsGmTpQmSDI', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 201, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 15:46:18', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZGVjZGEzNTYtZTgwNi00MDY4LTg1ZmMtNjA4ZDRlNjdhZmMxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NDM5NzgsImV4cCI6MTc0NDQ0NzU3OH0.0NzNp4zLrkB-6b3Y0fcA1EqU0cCKH-0WkN6QFE5dalI', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 202, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 15:49:21', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMTBjNTU2NGItZmMzNC00N2Y5LWFjNzItMjZiMWQ2ZDE3MDM5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NDQxNjEsImV4cCI6MTc0NDQ0Nzc2MX0.1J7f5Wl7ZvzjDz603m383DGcKw_nG6bYt72uqYxxEgg', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 203, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 16:33:16', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMjM0ZGU1MzItNWQzNS00NjlkLWJmNTQtMGQxMTg3ZTIyNTBiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NDY3OTYsImV4cCI6MTc0NDQ1MDM5Nn0.FvIuyxmpEu0v98m4_P_w9sGA55SoBxjmWGYEPwbKMUU', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 204, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 17:35:18', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNDJjMWE2ZWEtODZiMi00YWVkLTllNDctNjFjMDA2Mjc4MzdlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NTA1MTgsImV4cCI6MTc0NDQ1NDExOH0.b4Rp0Jx8Xmv3akt8QRCPKBysqLWuuX5DjuJqOUD8_tQ', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 205, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 17:35:49', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZmUwM2I5NjctMjE2Ni00MDhiLThjYzEtNTMzN2EzMDhhZmZjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NTA1NDksImV4cCI6MTc0NDQ1NDE0OX0.SywGn56UiARJz0OuQnMt5oj7QM09e_vRcR08s-lIyqE', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 206, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 19:05:42', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNWNiODk5ZWYtMzNlZC00ZjAyLWEzODctZDdlMzAzMTlhMjc2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NTU5NDIsImV4cCI6MTc0NDQ1OTU0Mn0.W8TikZckfbXwQQqGI17NLWxrsrvfZJB0paS9mxwMR60', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 207, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 20:07:05', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMjI2YjA0YWMtMWZjYS00ZTA1LTkxM2EtN2Q1YWQyZmUzYWM3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NTk2MjUsImV4cCI6MTc0NDQ2MzIyNX0.xOw9v1Vz2kEtlLQKsdsUSe7WB-_QmW_EOoYlYEopwyw', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 208, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 20:21:26', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZTliYTc4NWMtZjI0ZC00MzcyLTg5MTYtMDk2NzM2YjEwMzZmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NjA0ODYsImV4cCI6MTc0NDQ2NDA4Nn0.FGr_FY1-FBznkRL9rtQx1UsrOD3AZmcT4TEI3GmCp98', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 209, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 21:24:15', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNTQ4MDE0MzEtZjUyNC00OGY2LWEyNTUtZWM1MzViNGJiZTIzIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NjQyNTUsImV4cCI6MTc0NDQ2Nzg1NX0.DG0XKEM7R2beG61Ge-pDntN0wgX4S3uTEyenmS375yY', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 210, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 22:42:47', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNTExOTY2MGMtYWViYy00ZTBhLWFiZmYtMGQ0ODE1ZjFkMjU1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0Njg5NjcsImV4cCI6MTc0NDQ3MjU2N30.aBG2K6599lG4FwWu0j3m6wQEhu7APgNq0bF1gOHawCE', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 211, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 22:47:35', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNmRmNTJjMWYtZjg2Yi00ZTBjLWI1ZTEtMjlkMTlhY2U3NTNkIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NjkyNTUsImV4cCI6MTc0NDQ3Mjg1NX0.7GIKegxFRH_-d-2inlG8BVHb5whNyZGupjFrV2gKO0k', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 212, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 22:49:47', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTBiZTljMWEtODdhNS00YmIzLWEwMjYtYjQwM2RlYjk2OTYwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NjkzODcsImV4cCI6MTc0NDQ3Mjk4N30.4_q_7uHZx0isybqmDJafIPXC5KQjNrPR5Lwn4J1bpFA', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 213, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 22:57:50', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiM2Y2MWRkODEtY2U5OS00OGQxLThlZWItZjMzYTNkNGIxNThkIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0Njk4NzAsImV4cCI6MTc0NDQ3MzQ3MH0.AQggZ9mWEumufJ57Pr1wNcFT40NXYPt5lOvmg2PhU-A', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 214, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 23:03:57', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODMwZWQzY2MtOGQ1Mi00NDFiLWJkNzQtZTliMGRhZGUyOTZiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NzAyMzcsImV4cCI6MTc0NDQ3MzgzN30.VIpZNbkP0u7AUJpu_uUgB_WaEywc4Ej9iH12dn59bcw', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 215, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 23:05:56', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTVhODRmYjYtZTE2NC00ZDVmLTg5ZTYtZGRmOTg2N2NjZDRlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NzAzNTYsImV4cCI6MTc0NDQ3Mzk1Nn0.DcFDG7kMqGWu6g1lh4nNDaO01nf8UFxGMTqUQqLmffM', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 216, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 23:09:42', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZGUxYjU5NmEtNmNjMy00ODVmLTk0ZTYtMWZiMWYzOGIyOTBkIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NzA1ODIsImV4cCI6MTc0NDQ3NDE4Mn0.f5YTLhjHQA5s7g1wU0mFj99xrB2WPUUfXgrV4y7uOdg', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 217, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 23:12:23', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTE1NDVmYWYtNDRjMC00NTI0LTliNzYtZDI1NmVmOTJhMTVmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NzA3NDMsImV4cCI6MTc0NDQ3NDM0M30.wuCkPoP7klfRoM1VqlvjwEFz4VB375qLr9vju-q41pg', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 218, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 23:13:37', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTQyNTMxZWYtZWU0MS00MmRjLWI1NjgtZWE1NTVjNjU3NTY1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NzA4MTcsImV4cCI6MTc0NDQ3NDQxN30.JNcgo33k0WtPpaiV0Cc2Odez2GcW14f-dtKYV90HcEg', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 219, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-12 23:15:20', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-12', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWFhYzY0ZDUtZGU0OC00MjNjLTg0YWItYjBkNjVkZjliZTMwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ0NzA5MjAsImV4cCI6MTc0NDQ3NDUyMH0.JbUOiPobkkoplfjXpeBXWvHa4VgqsLPNRXWY8kUOXYE', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 220, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 10:20:06', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMTY5ZDMwNWItMGRkNi00NzUyLWI5ZjEtMjYxZWJlMTkzOGRjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1MTA4MDYsImV4cCI6MTc0NDUxNDQwNn0.kVCMWvI1qrULs0xu2VLqoZUTXT7DnJVgEZRMLcU2lzs', '2025-04-12 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 221, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 10:22:50', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZjU5MzlmMTUtZjI1Yi00MmRjLTkzOTAtNTZjYTA0M2E0YWRjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1MTA5NzAsImV4cCI6MTc0NDUxNDU3MH0.yuXz24XtV_1v057fxXlhcwszlnhseyd0_VHYurytfbE', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 222, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 10:25:22', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiM2MzYWI2OTMtZjgxMC00MGZiLTgxNmQtNjc2YjdlNGMxZTkwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1MTExMjIsImV4cCI6MTc0NDUxNDcyMn0.atGzc-5GXHbtHbOfQyZ7zouRARnzDLU97bExXvUGDeI', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 223, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 11:31:23', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMTZmOGVmMmUtYjRmOC00NTJmLWJiNjUtMmRlODM0OTc2OWMwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1MTUwODMsImV4cCI6MTc0NDUxODY4M30.1YDlf5nESAEodxfrhm3h13nha_wabdBdtAwbd0nTbV8', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 224, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 12:43:13', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiY2U2YjQzZWEtYmQ4My00N2Y5LWIyY2EtNTBlNWRiMmVkNTU2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1MTkzOTMsImV4cCI6MTc0NDUyMjk5M30.T9Gpx7Xr-qdi3byvQGxploFK6-lLWT8mzWKNn-rAHh0', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 225, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 14:42:06', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiM2QxZjI4YjQtMDBhNC00ZWI4LWI3ZjEtZjMwMGMzMDNlYmFhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1MjY1MjYsImV4cCI6MTc0NDUzMDEyNn0._NIlaM7FsA3p7rp9gNOnjJYyBAKA2cGNE5rZetFr3u4', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 226, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 15:43:48', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYjU2ODc0YjUtMjExMy00NzFmLTgwMmMtYTVkNTdiOTVjNTUyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1MzAyMjgsImV4cCI6MTc0NDUzMzgyOH0.UScY69r7GCheJyBQqkxeZQ_OqRwVktHVe09nahrReFI', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 227, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 16:45:21', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZjkwNzdkMTUtN2U4MS00YTE1LWE0YTctODcwZjA4MzllMjAxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1MzM5MjEsImV4cCI6MTc0NDUzNzUyMX0.Pn8YTln64zzqfT3ak4zq6dl1-OSGu-3Rhnu8PzjD-YE', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 230, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 17:17:33', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZDU3ZTE5ZjktMjkyYS00Yjc3LTkyOTEtZmFjZDUzZGZjZDczIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1MzU4NTMsImV4cCI6MTc0NDUzOTQ1M30.zRdDk9KBSWvOXxRqLyM-8DdJyQ_GPkX9aVNu8Fpa3uc', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 231, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 17:18:56', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODcyNmE0MzItZDM4MC00MzFlLWI3OGUtNjBhZDlhODBlYTllIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1MzU5MzYsImV4cCI6MTc0NDUzOTUzNn0.mrejgZyjUBW5tSuvUEA-H1RzG8k8nn_1qULExO6DKW4', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 232, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 17:19:53', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTYzMzk0YWMtZjhlOC00MGFlLWFkMjItYjU3ZjIyOTdmYjQ2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1MzU5OTMsImV4cCI6MTc0NDUzOTU5M30.kudn97o03mPR-r_orKD_kCvJmGr7BRPJxb3IQkXcxQ8', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 233, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 17:21:16', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNDRkMjJjMWEtZDcxNy00ZjNkLWJkYWItZmFlZmI1NTE3MmJlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1MzYwNzYsImV4cCI6MTc0NDUzOTY3Nn0.Iz-TPWSt0aSTTudgY3RVT6YQKH98uOnQ3CPbaCrA72U', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 234, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 17:26:58', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZmIyY2NhYjQtYTA3Ni00ZTc2LWE1YjgtZWRiYTFiZDJjNGIxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1MzY0MTgsImV4cCI6MTc0NDU0MDAxOH0.Bw2VGbUNJAcQ-kPvLEEVBlvQ_nN3Qdce1b4hSL6ZTZc', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 235, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 17:27:41', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzg5MmNlZWItNTE0Zi00OGYwLWE3NWYtZTE3YjEwYzA4YWJhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1MzY0NjEsImV4cCI6MTc0NDU0MDA2MX0.blxTu3TJnsyFOSAeecticbi0uLVBXHas2B5Oya0UkLk', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 236, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 17:28:07', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODA2OTVkODktOGUwYy00ZDczLTk4YTktMjBlMGQ4YTQ5NTE5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1MzY0ODcsImV4cCI6MTc0NDU0MDA4N30.SsjU-rtGVBAWX4CkxNdPJWnZ_pklZQXVyCSJvKO_3Os', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 237, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 17:29:19', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMTAwMmFlNTQtYzA5Ny00MmJlLWE2NWItNGY2OTBmNTk4NGExIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1MzY1NTksImV4cCI6MTc0NDU0MDE1OX0.XOnhP-9DG23W1KhAYps1x4mw6RvhN1DNyYaxwgfBFlw', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 238, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 19:07:54', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZWM1NDdjZDAtODVjMS00MjQyLWJjNGYtOGI0NzAxZDBhYmNjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NDI0NzQsImV4cCI6MTc0NDU0NjA3NH0.JkzVP1EKrDfe_rZQvDbfn8y00gAivs-n5tStTAKyHSU', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 241, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 19:31:14', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNzBkNzZmNDAtNjM4My00YTU1LWFmZGMtMGE2NmJlOTQyMDZmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NDM4NzQsImV4cCI6MTc0NDU0NzQ3NH0.6rtvte2XqAcLrcwxiJaMoNo2kuTdgInCPP0Mpuq90FI', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 244, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 19:54:01', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMGM2YmJhOTctNDI2MC00ZjZmLTgwYTMtM2Y5MTRjNjZmZDgxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NDUyNDEsImV4cCI6MTc0NDU0ODg0MX0.iMM4GKkcAIZoBM5USVxJTGyMRbhAmNU9w_FS3rMzTKU', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 245, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 19:54:51', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNmFkNjZmZTQtZjU1Yy00ODEyLWJkZDQtNWRjOWM2OWQ5NWZhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NDUyOTEsImV4cCI6MTc0NDU0ODg5MX0.J9d1nZjAoGQsfTEwSKqLQpmE6G6kFUmKwnF5zqKGBOw', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 246, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 20:08:59', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNDQ0MTViODktZDY5OS00YTc2LWJmNDktODRmOTkyMTkzODRmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NDYxMzksImV4cCI6MTc0NDU0OTczOX0.WJKZy6Aa_Ct69uaewsSEaT4Em7MoOo24w_4jkAx2YAE', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 247, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 20:10:50', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWE3ZDAwNGMtMjEwZi00YTYzLWI1ZGEtMWM4ZTA1M2EwZTNhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NDYyNTAsImV4cCI6MTc0NDU0OTg1MH0.VdZnP3Us04vacVTFrKph1LF9ynzcDIEgsICqj7QsWWA', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 248, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 20:15:34', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYmE0YzNmNmEtY2ZjYi00ZTFlLThhZjMtN2RjMDFiYTFiNWY5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NDY1MzQsImV4cCI6MTc0NDU1MDEzNH0.lRo8zKcU8XePRN0PyLFFxmNP1317NiNQXGvUDMtaIp0', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 254, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 21:19:36', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiY2ZiYzAzOGQtOGE5MC00ODJiLWE0ZTEtNzczZmJkYzE3OTIxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NTAzNzYsImV4cCI6MTc0NDU1Mzk3Nn0.1Auj_rBXq0PNr_11aYcAWkbOAzsrUdDRkwasdjPUiJk', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 256, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 21:37:41', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMWMxMTY1MGMtMDhjOS00Y2VmLWFiN2UtOGViYjUwZGUyNzA0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NTE0NjEsImV4cCI6MTc0NDU1NTA2MX0.0dJlh1Y58hve1ttw4Z8v5kgtgGvK0ANWcRYy5y6qTys', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 257, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 21:40:12', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWQ3YzM0NDEtMDIxMi00ZDVlLTg4NzctZjQwNDJiM2M2ODlmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NTE2MTIsImV4cCI6MTc0NDU1NTIxMn0.kWAymn56CcZ6F8c08JsfYjUB64WjL0D3EncJzgML69A', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 261, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 21:44:41', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODgyZDQzODQtN2JmMi00YjNlLWE1NjYtNTNjNzM5ZDhkZjczIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NTE4ODEsImV4cCI6MTc0NDU1NTQ4MX0.P8p8Y_yVIaxu7lS-Vs3cZNjd4yH-CoB4RAgKNnyQ5fA', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 262, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 21:45:45', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNDI4YjNkZDItNjlmOC00NjE3LThkZmQtZGE0ZTY2MWUxZGI3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NTE5NDUsImV4cCI6MTc0NDU1NTU0NX0.5BgddSDm5HV0PuxhrAt56_Q7FMp7qvWkDboDbd0lCPs', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 263, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 21:46:36', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWNhZGNmYzctNzczYy00NDNkLTliNjUtNzFiNjIwODVhZTg1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NTE5OTYsImV4cCI6MTc0NDU1NTU5Nn0.H5HWVVnmt2LHRWDnftM6vnSZPPz6qVcG75n6lf4y5WU', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 268, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 21:54:53', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOGQ0YjdmZWEtMDI1My00MzA4LTkyODUtMjI4OGQ0YWNhYjA1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NTI0OTMsImV4cCI6MTc0NDU1NjA5M30.cui3b8kkRMoodtLfgLaNQhRQA5TYa0Ufq55MSU6vwdw', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 270, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 21:55:42', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNDk3ZDk2ODMtNTExZS00MmY4LThiM2QtYzcxZTI2YWQ1NWIxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NTI1NDIsImV4cCI6MTc0NDU1NjE0Mn0.mL28a047MBXbc-PrHuig_LxFUg5lZxNdsuix9pjZURM', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 272, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 21:56:48', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZDBhZWZiMTctYWRjZC00ZTQzLWFkYTYtZDFkZGQ4YWQzMWIzIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NTI2MDgsImV4cCI6MTc0NDU1NjIwOH0.ojZHze3C9uDLgOmR5qQ61Ghs-gKLAGwfTla301bsRgA', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (1, 276, 1, NULL, '', NULL, '2025-04-13 21:59:21', 'jonny.jiang@sap.com', 0, NULL, b'1', '2025-04-13', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODViMDU1M2ItNTczZi00YTg3LTk2NzctYmQ4MjlmMzg1M2JmIiwic3ViIjoiam9ubnkiLCJpYXQiOjE3NDQ1NTI3NjEsImV4cCI6MTc0NDU1NjM2MX0.Vp4FsE3Dtu8tAfVynqDBs1IbEZqKIlThiyEJfG6FADA', '2025-04-12 00:00:00', 'jonny', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 278, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 22:02:51', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMTY3ZTJiNGUtNTk5Yy00M2U1LWIzOTktYzIwOTcwNTcxODhiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NTI5NzEsImV4cCI6MTc0NDU1NjU3MX0.OWvrutrsxdLJlEUncC1LVGfJprwqQGRlI2Iy6D4BJ6g', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 280, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 22:05:45', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWQ4OTU5YTItMGU0MC00M2Q0LWJkMzQtODE2NWE5N2YzNDMyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NTMxNDUsImV4cCI6MTc0NDU1Njc0NX0.iam_3wdMPdv8xTcgai23okkp8pstnGOI2TF-eiusjSo', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 281, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 22:07:16', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODhkZDNiMDQtMjZiOS00MDg4LWI2MTUtNDA3Y2U4YzQ1OGQwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NTMyMzYsImV4cCI6MTc0NDU1NjgzNn0.dv8WcFZyPanawWuBLjGl966x6vWCKHl8Trq7qaqdq-8', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 282, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 22:17:42', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMGJmNGRiNjItYjVkYi00YjViLWIyYjEtNTcwNzI2ODhiYjE5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NTM4NjIsImV4cCI6MTc0NDU1NzQ2Mn0.SJ5IFSaV0IOX0qoP-EjqhAHPaUlnxmtvCbYUJ38rvYo', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 283, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 22:30:17', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNWIwYzAxYjUtZDk3NC00YmNmLTkwMWEtZTBjY2ZjYWQxNmIxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NTQ2MTcsImV4cCI6MTc0NDU1ODIxN30.GhDel9WWUqMG6buMGbZvcm_cwt5AU76Wlm5Z_QUal3I', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 284, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 22:32:33', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMTI3MzBiNDItZmYwOS00NzljLTgzNDctMzg4OGYwNjMyYTRlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NTQ3NTMsImV4cCI6MTc0NDU1ODM1M30.HH5PjfN5Q0z69f6ClcLm3QNqCtpeSNfsnnT3BR6qclI', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 285, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 22:36:54', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiY2E1NjQ3YWQtY2NmNC00YmJiLWIyZjMtMTNiNjk3MWFlMTllIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NTUwMTQsImV4cCI6MTc0NDU1ODYxNH0.7x4vDSYo4YcoM2EnNEDeBzOdGmtznAze-HNU_Tc9dmU', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 286, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-13 23:10:12', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-13', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNzQ5NjIwZjItMjdlMi00ZTYyLWEzNzktM2MwY2UxZDc5ODkzIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1NTcwMTIsImV4cCI6MTc0NDU2MDYxMn0.UQjSpZKkQsC-xEQAzzO3OXvGjy94cjoNSzZIHTdmtBM', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 287, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-14 10:54:14', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-14', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTEyMDZiM2EtNmM4My00MTAzLWI5YTctODQ0YjE3ZDJjNDJlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1OTkyNTQsImV4cCI6MTc0NDYwMjg1NH0.f2DMaUS7_Q16nlQ95T31GcyXmv681Sf9ahbEcgWuRqc', '2025-04-13 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 288, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-14 10:54:21', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-14', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWNhOTgyMDctOGZmOS00MDkxLWFiNTQtYTZiMDQ5YTJjZWMxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1OTkyNjEsImV4cCI6MTc0NDYwMjg2MX0.t6n8PUZFNBdy_V7fgJNLFCTAvpnGIfTJbWZ1Ufctk_8', '2025-04-14 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 289, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-14 10:56:12', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-14', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZDBlOTYwOTEtN2QwZC00YzNiLWJmOGUtOTE3ODc4MDRmYTdjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ1OTkzNzIsImV4cCI6MTc0NDYwMjk3Mn0.g1dzY9pehbJmrgVN13hSbb7Zn2A41xbVz1L7acZiAJ4', '2025-04-14 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 293, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-14 11:12:43', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-14', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTViNWUzNjMtOWM0Ni00ZTFjLTg3MzEtYzI4MjE3ZjQyMjMxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ2MDAzNjMsImV4cCI6MTc0NDYwMzk2M30.RNeKWRhfLOhBKpS0MIHEym5hIPw_G92JWj78or4AE20', '2025-04-14 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 294, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-14 11:14:18', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-14', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzUyYTc4NDMtZWUzZi00YzU0LWE4Y2UtZTlkZjk3MzhkZGE5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ2MDA0NTgsImV4cCI6MTc0NDYwNDA1OH0.rlKCO6Gu373mQbW35BPnDKV2vkBWVrbsaIsAuFgHSK4', '2025-04-14 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 296, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-14 11:16:16', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-14', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMDBiOWQ1N2UtZjJkOS00OTM4LTgzOWYtMzQ1NjBjYTY3OGQwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ2MDA1NzYsImV4cCI6MTc0NDYwNDE3Nn0.y5iK0e_tZmg4L5WWWhOCR-oF_cfxYFrN_Vg0Knery84', '2025-04-14 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 312, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-14 11:31:29', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-14', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYzQ4YzAzOTEtM2MyZi00ZTNlLTg4NjMtZWE0ZDgwYTgwMTEwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ2MDE0ODksImV4cCI6MTc0NDYwNTA4OX0.g0buwO5gWtNtwJF2yDNdvAUhQz6mVIkDIHmIEh4V_Mg', '2025-04-14 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 313, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-14 11:33:17', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-14', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZDFhYWNlOWItMTg1YS00MzY0LTk3NTYtNjk5MTZjNjZhODE5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ2MDE1OTcsImV4cCI6MTc0NDYwNTE5N30.w2RQTVcjCkF2XPcRJM-FOUpIX7L7jlwyZMObT-s7CNU', '2025-04-14 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 320, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-14 15:07:45', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-14', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOWQ5MTRjNTgtNmE2NC00MzgwLTlmNzAtZGNmMjY4OGVjZjQ1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ2MTQ0NjUsImV4cCI6MTc0NDYxODA2NX0.UeE_37DOHJRUivX_otE3ewSOhLb3cosy7OqtNEE4uvs', '2025-04-14 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 324, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-14 16:11:11', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-14', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZjliZWEwOTAtMzc5Yi00MTdjLWFkNTctMDQwYTdhODY5ZjdhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ2MTgyNzEsImV4cCI6MTc0NDYyMTg3MX0.aDckr_2XXGNH2evBNDmW0Jm_HvG4jvfa5lsIN8olGUM', '2025-04-14 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 329, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-14 17:11:58', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-14', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYzllZjc0YWYtYmY0Zi00Njk2LWExNGYtMzEyZTY4ZDI2YWJjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ2MjE5MTcsImV4cCI6MTc0NDYyNTUxN30.ffqFlIESthsAb77FIKRi2hcTqQhRw3cFhutDIz2-Vmc', '2025-04-14 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 340, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-14 18:53:10', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-14', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWI4NTcyMTQtNjUxOC00OTBhLWE5NzgtZjcxZTU2YzY1ODY0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ2Mjc5OTAsImV4cCI6MTc0NDYzMTU5MH0.G8h-fR59Mh7MYVpynYB_SO2vyhNFaOkJ9o1h7tchFEg', '2025-04-14 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', NULL);
INSERT INTO `fs_users_aud` VALUES (2, 341, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-14 20:16:34', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-14', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNGE5OTQxMTYtOTFmYS00NWE5LThjYmMtMGZkZjdhYjQ2OTgyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ2MzI5OTQsImV4cCI6MTc0NDYzNjU5NH0.8sKIo32rgouaaQZbIVfqMGCyzUJl-743ZeR_sk9iH-E', '2025-04-14 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', '');
INSERT INTO `fs_users_aud` VALUES (2, 342, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-14 20:38:35', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-14', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNmYwMjY3N2MtYTIzZi00ZDU5LWI3NTctMzhmZDk1MTRhNGZhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ2MzQzMTUsImV4cCI6MTc0NDYzNzkxNX0.pe6A4bZFrcqJv0ppC-NZq6vAO3NIiyKODvFno8SlKCE', '2025-04-14 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 343, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-14 21:05:21', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-14', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZTkzMmYzODEtOWYxMS00NmM1LThhNWQtNzI0ZDg5ZTZjNzVmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ2MzU5MjEsImV4cCI6MTc0NDYzOTUyMX0.a13Z0HqozNsiAS3wqv1qH1X_BtzPCCn_lOuo6v27wgU', '2025-04-14 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 344, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-14 21:24:55', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-14', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZWM0YTE0NTEtM2JkZS00YjdjLWE3MzUtMTRiMDEyZTc1MDdlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ2MzcwOTUsImV4cCI6MTc0NDY0MDY5NX0.rCw0TPzLMLSEFrUG3N9qNrff2R3x7K7VMBGeR7z5I_E', '2025-04-14 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 345, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-14 21:25:56', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-14', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZGZiYWM0OGUtODQ2Ny00NWJiLTg5ZTEtZWU0ZTRhMGMzMDFmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ2MzcxNTYsImV4cCI6MTc0NDY0MDc1Nn0.PG3zjE_t9FRKVNgoa7SlfyfFzQEUq8tF6x1wNSDy39A', '2025-04-14 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 346, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-15 10:10:41', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-15', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZWZjZWJkZDgtYzFlYS00OGNmLWE0YWUtZjFkYWY1MjAwYjg1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ2ODMwNDEsImV4cCI6MTc0NDY4NjY0MX0.IWaI6m0UWNCuceDsPA_EMQ1G3et3t8OSWa1NsCpdLVo', '2025-04-14 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 347, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-15 10:32:01', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-15', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYjZmN2MxOGMtNjg5Ni00MTUwLTkyYjItNTM3ZGEzZjZjYzg1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ2ODQzMjEsImV4cCI6MTc0NDY4NzkyMX0.kkzUSuqJ9AEcGGH1yhruvvv_K72YVW9E2ZAdDM97uqY', '2025-04-15 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 348, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-15 15:00:58', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-15', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYjU1MmE1YTgtMThjYS00NjFmLWFiZTEtYzMyODFlMWE0ODg1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ3MDA0NTgsImV4cCI6MTc0NDcwNDA1OH0.vNBEbERwu5nJmF8m1Gkofoj8TXrjQS6qQ1g4bBn6Bpk', '2025-04-15 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 353, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-15 16:04:54', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-15', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMjliYTk4YjUtNGM2Ni00MzA2LTgzYTAtNGNjM2NjOTY2NzVjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ3MDQyOTQsImV4cCI6MTc0NDcwNzg5NH0.JhtmvPpvQcfWn6GQq6YUb0CgnSiC9DyaPYxO1oHwLUE', '2025-04-15 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 359, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-15 16:46:26', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-15', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYzI5NjJkMDItZmUwZC00YmY1LTk3MTMtZTk0ZTc3YjllOWRmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ3MDY3ODYsImV4cCI6MTc0NDcxMDM4Nn0.vMjTBEOV9VqpF0QTufqNH-hXp3dJxp6xodG6SwHmVmE', '2025-04-15 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 360, 0, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-15 21:59:16', '1448185924@qq.com', 2, NULL, b'1', NULL, NULL, NULL, NULL, NULL, NULL, 'helloKittyCatSweet', b'0', NULL, NULL, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 361, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-15 23:05:39', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-15', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMmI5ZTBhMzgtYTgxNC00MGEwLWE1MGYtYzY2YjJhYjk1Mzg5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ3Mjk1MzgsImV4cCI6MTc0NDczMzEzOH0.rx7_zrdk0JS9rrJIwY52f8xkNQSkdxDArrmUi-3whdM', '2025-04-15 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 362, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-16 12:45:13', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-16', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNTcyNzAyZTUtNjNiNy00NTlmLTlhZDAtYzY5OGE3NzgwOGJhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ3Nzg3MTMsImV4cCI6MTc0NDc4MjMxM30.vvfY7fUtaa6EEwSC-yu4C0kQoYp6cSwOCoEhHFuvXek', '2025-04-15 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 363, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-16 14:58:57', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-16', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTQ1NzRmZDctZjNjNC00MDc5LWJiMmMtOWMxMjJiY2FmYjIwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ3ODY3MzcsImV4cCI6MTc0NDc5MDMzN30.y24Tx3iULZpSyXuWVeu7Row32Lpt0WrbSGZg7ckuh1M', '2025-04-16 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 364, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-16 15:25:48', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-16', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzFkNmQzYjEtNDczYi00NWMxLTkwNjctZDFlMGYxMjk2YmY5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ3ODgzNDgsImV4cCI6MTc0NDc5MTk0OH0.eRESXH6bE8M2uq4pah-U7aDUooV0n7qcK5JObInmdaM', '2025-04-16 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 365, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-16 15:27:21', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-16', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNDY3YzBjMWQtYWI2Ny00NjRmLWI3YzAtYWUxMWZjZmFlNjhiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ3ODg0NDEsImV4cCI6MTc0NDc5MjA0MX0.bmmLhHHxNT0DyNQSLbpt8c4cnaygBzMo9v7bZJ7bBuY', '2025-04-16 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 366, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-16 19:02:12', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-16', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZmMyOWQ0MTQtNDg0Ni00MjVkLTg5NTEtZTBhMjg4NzhlN2M1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4MDEzMzIsImV4cCI6MTc0NDgwNDkzMn0.1CLXQyImNIurRsQ-5KL7ZnykZWLmcj3ChoAh05FGGmQ', '2025-04-16 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 367, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-16 19:32:10', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-16', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNzc4ODA3MGMtMzMwNC00MjE5LTgwM2UtMThhNzQ2NGI1NDJiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4MDMxMzAsImV4cCI6MTc0NDgwNjczMH0.OPzafAykFt4uQ7MtrtF9ot9Rdv3P7Z-NaEHCg4h23ac', '2025-04-16 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 368, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-16 19:49:38', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-16', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOGM0M2Y2MjItOGY5My00YzZlLWFlYjQtNjU1OWRiMDU2ZTliIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4MDQxNzgsImV4cCI6MTc0NDgwNzc3OH0.mBRIGKxkfrd9oGN5NQnaCmQ-qrlLSolwCtkpvgEcxFs', '2025-04-16 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 369, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-16 20:04:33', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-16', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzc1YmNmYTktOTM1ZC00NTI0LTk5NjAtMTIyZTY2N2FmZmY0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4MDUwNzMsImV4cCI6MTc0NDgwODY3M30.oX-3PwwoCemouj5KkC7DatCTqUSt5Jb-MvOAgBJ5miE', '2025-04-16 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 370, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-16 20:04:54', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-16', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWQ3N2I5NzYtM2ExMC00ODIxLTk4MDUtOWY1YWNjNmIwZGYyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4MDUwOTQsImV4cCI6MTc0NDgwODY5NH0.QWUwRqy1tlNxcaIQPECGXoRFrWKkETmWSHzkPuJD_NQ', '2025-04-16 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 371, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-16 20:06:25', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-16', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzJhZmEwOTgtYThiZS00NjM5LWEzODAtMWZmYzhmZDk3ZmZmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4MDUxODUsImV4cCI6MTc0NDgwODc4NX0.2-VI0VtI3zrlYzKmGuR8mfKQf25t6WuvWUkCIqodahI', '2025-04-16 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 372, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-16 20:17:26', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-16', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMmQ1ZmYyZWYtYzVjMi00ZGIwLWI2ZDctNTkxOTE5MGMwZWY0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4MDU4NDYsImV4cCI6MTc0NDgwOTQ0Nn0.up_fw5FGQu7K259tetFGBzZpUmB9V0yE37I6u9BTuOA', '2025-04-16 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 373, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 12:42:52', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNzY2MjE5OTMtZjczMi00ZWRmLWEyMDUtMDdhNDMyNTNlMjYzIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4NjQ5NzIsImV4cCI6MTc0NDg2ODU3Mn0.EehyJVkmQdJG7aDWnCa1DWLwA_UVXS5bPEOP-sfoGYw', '2025-04-16 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 374, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 14:54:57', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTJhNTdmMGUtZGMxYi00ZDA2LTlmNjAtYmJjMGUwNGZjZTNlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4NzI4OTcsImV4cCI6MTc0NDg3NjQ5N30.UTrvldfbodqvTap_ujuWr5cHjCiBoYerH62s_fazxvo', '2025-04-17 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 375, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 15:07:00', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMGUwNDNlNzItN2M3Yy00ODc4LTkyYjEtZmU1YjExYjhlZmQ5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4NzM2MjAsImV4cCI6MTc0NDg3NzIyMH0._uoD3Hs8KZ1gOqDS5LyNk7Nvnf4DfYp1GdHzrseXpTo', '2025-04-17 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 376, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 15:43:21', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNTczYzI4ZGEtYjUwNi00ZmQ3LWE4Y2EtYjQwZTVlNjdlY2Y5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4NzU4MDEsImV4cCI6MTc0NDg3OTQwMX0.agBMvPFrPY_wXHEecFjiWsDZqDKURqImz0ui9qTkDUs', '2025-04-17 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 377, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 15:50:20', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTZiYmQxNmUtOTBhZS00ZWM3LTg5ZGYtNzNhYjUxMzEwZmU4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4NzYyMjAsImV4cCI6MTc0NDg3OTgyMH0.6uwEY1tGHHEuAaQJY4fKKPGIYA7otyGYaPwMHaKsmUU', '2025-04-17 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 378, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 16:09:28', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNzcxYjE3NjUtZjA2ZS00NjAyLWIyNGItZDgyMGU1ZDE0MzA3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4NzczNjgsImV4cCI6MTc0NDg4MDk2OH0.GCzDGVDzYZwBN2Mrsi6QQOY3XPKEhi2xr_P7JjXlDD8', '2025-04-17 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 379, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 19:32:04', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOWU3ZGM0MWEtZjg1YS00MWY5LWFiN2ItYzZjYzQwM2EyZGJhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4ODk1MjQsImV4cCI6MTc0NDg5MzEyNH0.wVM1W62srIjXCszRd8oalZB4ky1e2Ndca44uamLue1Y', '2025-04-17 00:00:00', 'kitty', b'0', '10.241.189.74', '内网IP 内网IP', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 380, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 19:52:18', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYjg0Yjc1NjItMzViNy00MTdhLWE4MTQtNjVjMzRlNzhhN2ZiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4OTA3MzgsImV4cCI6MTc0NDg5NDMzOH0.yBibGaGkQ7zSz2Z-PMulJAXgNs5zvMZsNSWqYtboUe8', '2025-04-17 00:00:00', 'kitty', b'0', '10.241.189.74', '内网IP 内网IP', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 381, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 20:02:41', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZmI3OWU0OTItYWUxZC00NWM2LThjNjQtNmQ5Y2M1ODBhYjNhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4OTEzNjEsImV4cCI6MTc0NDg5NDk2MX0.NReLs1nIEZ3sWXwPuQoC-A0iA8BtEu1E5ZjvvjbQNFQ', '2025-04-17 00:00:00', 'kitty', b'0', '10.241.189.74', '内网IP 内网IP', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 382, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 20:26:13', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNTcyOGEyZWMtNjczYi00ZTdhLWFjOTItMGQ0MDI0YTRiNTIyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4OTI3NzMsImV4cCI6MTc0NDg5NjM3M30.Pj2wWLQj7yBZCGH5QIDqVZeIJGioiq1w8YZOA8STEbE', '2025-04-17 00:00:00', 'kitty', b'0', '2602:feda:d40:8f5b::1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 383, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 20:40:20', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTczYzM3ODUtOTI1MS00OTMyLTgzYzAtZjliMGFmNDY3M2ExIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4OTM2MjAsImV4cCI6MTc0NDg5NzIyMH0.vk5xsm1lq-U3f4ERjePiJs00d0eRnwl6ROPxppRoDV0', '2025-04-17 00:00:00', 'kitty', b'0', '103.149.145.161', '亚太地区 亚太地区', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 384, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 20:43:25', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTY1MjEzMzUtZGU4ZS00YzU4LTk0N2UtNGVmNmZkZmNkMTYyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4OTM4MDUsImV4cCI6MTc0NDg5NzQwNX0.dhRfYqR5ZXldZ37gN8k9wl7Vlb70F8iy5dj0BlLjaKs', '2025-04-17 00:00:00', 'kitty', b'0', '205.198.69.27', '中国 台湾省 科进', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 385, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 20:44:54', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZWVhYTM5MjAtOWExZS00OTAxLWI5YzktZDc2NmQxOGQyYTNjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4OTM4OTQsImV4cCI6MTc0NDg5NzQ5NH0.ZgevHIpAqb4xBKesPaN5M3oo7lognb_M2SCeeE_8Upg', '2025-04-17 00:00:00', 'kitty', b'0', '205.198.69.27', '中国 台湾省 科进', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 386, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 20:45:38', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNDE1Zjk4ZmEtY2Q2My00NWJiLWJlNjMtNWJkNGU3NjlkNTg0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4OTM5MzgsImV4cCI6MTc0NDg5NzUzOH0.29KvU_ZOt0bfUro9c0qtPygchQkNJ8SYnJkr0hNLROs', '2025-04-17 00:00:00', 'kitty', b'0', '205.198.69.27', '中国 台湾省 科进', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 387, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 20:49:20', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiN2Y0Nzc3YTItMWQwOC00Y2M3LWI0M2QtMTQ5NWZlYTNlYmE5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4OTQxNjAsImV4cCI6MTc0NDg5Nzc2MH0.PyQ8qpNMpw4GCAQguhIEqj_SzfRjF8DAyRoKG_TMJbY', '2025-04-17 00:00:00', 'kitty', b'0', '205.198.69.27', '中国 台湾省 科进', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 388, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 20:52:38', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYmZkZDkzY2QtNWE5ZC00YmM1LTg1ZTYtMjBhMzVmOTAyNWRlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4OTQzNTgsImV4cCI6MTc0NDg5Nzk1OH0.tv3eGbg7WyGFZ-I_s9JjZEzL8qjuMCuHx00QCZ6OmPk', '2025-04-17 00:00:00', 'kitty', b'0', '205.198.69.27', '中国 台湾省 科进', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 389, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 20:56:26', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTA1YTJjMDYtMjM5Ni00ZmNlLWEzZDItMjdhMjcwNjRhZjVmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4OTQ1ODYsImV4cCI6MTc0NDg5ODE4Nn0.7NghlaCbtL9UehH9OSQBPOgaevqzkKzYdWSCNSq3Ojs', '2025-04-17 00:00:00', 'kitty', b'0', '205.198.69.27', '中国 台湾省 科进', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 390, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 21:57:49', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMGVkMzZmYWUtZDQwMy00ZmQyLTgzMmEtNzJjYzJmMzVhMDFhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4OTgyNjksImV4cCI6MTc0NDkwMTg2OX0.QOdgoHthd9Y6Fld6tAyqoCyt_1JDs7fNPKsSBURzO1Y', '2025-04-17 00:00:00', 'kitty', b'0', '205.198.69.27', '中国 台湾省 科进', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 391, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 22:19:03', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiN2EzNGZlZjQtYWVkMi00ZDkwLWI5YTItZWJlYWE4NzFmYTBkIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4OTk1NDMsImV4cCI6MTc0NDkwMzE0M30.2HeXV_6QGFBasTqb8rOwrKai1Rq3giFM3r2OUZAN-co', '2025-04-17 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 392, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 22:24:51', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMjI5MWQ5YTMtMzNlNy00NWJjLTk0NmEtNTczOGZjMmE5MWVlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ4OTk4OTEsImV4cCI6MTc0NDkwMzQ5MX0.QhWdlTGAs2vXIrfIxUcnDRHGC0t5epEo769HAkorbJ8', '2025-04-17 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 393, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 22:27:21', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZmZhOTkxZWMtMDRiYi00MzAyLTliMGUtNmY1Y2E2MmYyZDA2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ5MDAwNDEsImV4cCI6MTc0NDkwMzY0MX0.8l4VJYhqdWgyd25kYqnVFJCbvLd52zVCT5WWuL5mYoY', '2025-04-17 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 394, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 22:36:12', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNDgzNmI0Y2YtY2EyMC00OGZhLTg4OWItNGVkZTJmNGZhYjYzIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ5MDA1NzIsImV4cCI6MTc0NDkwNDE3Mn0.18yvAjK0eYtGVfOTF5HynXDXiwTd8t0gMDMVvOYPhQw', '2025-04-17 00:00:00', 'kitty', b'0', '2602:feda:d40:8f5b::1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 395, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 22:37:33', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjczYmYwZDktODg4MS00MjE5LWE4YWMtYzlmNGJhMzNhN2UzIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ5MDA2NTMsImV4cCI6MTc0NDkwNDI1M30.2SY5QKuU7OfsB7P_axUSKq0H6shsf3e2owRztdGTLS0', '2025-04-17 00:00:00', 'kitty', b'0', '2602:feda:d40:8f5b::1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 396, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-17 22:41:24', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-17', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZjc4ZWY1ZjktZGUwYS00ZmMyLWExYTAtNTY1MDc4MThjMjI2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ5MDA4ODQsImV4cCI6MTc0NDkwNDQ4NH0.SFXDVCd1oiQDbJ27EVKbqT0gR5F09w4jmAfU63G3_bA', '2025-04-17 00:00:00', 'kitty', b'0', '205.198.69.27', '中国 台湾省 科进', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 397, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-18 09:55:02', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-18', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNGE0NWZmOTQtMjgwYS00MzAwLWIxZTQtOTFkOTExMDE5NjU0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ5NDEzMDIsImV4cCI6MTc0NDk0NDkwMn0.GH94_K1xIXcfnSaOVfgfqqXrYwmYNv8OYNCwMvavDWg', '2025-04-17 00:00:00', 'kitty', b'0', '103.1.158.145', '中国 香港', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 398, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-18 10:21:45', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-18', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZWJjMjY2MGEtYjJmYS00Y2FjLWFmYTUtOTkwMWIzMDI2NmJmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ5NDI5MDUsImV4cCI6MTc0NDk0NjUwNX0.E0x0UUCyeq_cWg_D0V9YVKpgHjxSs-4NkokNa1u-8tE', '2025-04-18 00:00:00', 'kitty', b'0', '2403:27c0:c03:2::20', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 399, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-18 10:26:54', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-18', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTA5ZjMzYTctODdkYS00NGFiLWJjYzgtNmY4ODVhOTU2ZDY5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ5NDMyMTMsImV4cCI6MTc0NDk0NjgxM30._kqlXmXDEMs8aOyWhhMOQIYIZTOPz6J1t5Pv3PBDhPo', '2025-04-18 00:00:00', 'kitty', b'0', '103.1.158.145', '中国 香港', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 400, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-18 10:34:38', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-18', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMmFmOTNlMmYtMGJhOC00N2Q3LWI5NTUtZjUzNGU3MjMxMzMwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ5NDM2NzgsImV4cCI6MTc0NDk0NzI3OH0.qtg2E7af4Ag9Pxvo0g-lf8Vu7EKcVxh3IFPO5lTv4GU', '2025-04-18 00:00:00', 'kitty', b'0', '103.1.158.145', '中国 香港', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 401, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-18 10:40:04', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-18', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjVlMjU3MmQtODY2NS00NGNhLWIwZTUtMDNjNzQ4YTQ3M2Q3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ5NDQwMDQsImV4cCI6MTc0NDk0NzYwNH0.gnSvtAI0XzXKQzW4sfRtpgnxK-mt8QrqBpVVUEeAJKI', '2025-04-18 00:00:00', 'kitty', b'0', '103.1.158.145', '中国 香港', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 402, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-18 10:54:26', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-18', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZWRhMmQzN2YtYmYzYi00YmFjLWFkODYtNDQxODc1NTMyNjhiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ5NDQ4NjYsImV4cCI6MTc0NDk0ODQ2Nn0.UBJmtJpREza7KUAAM4CA42acmBbcGyb4741Cc9Wamsk', '2025-04-18 00:00:00', 'kitty', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (3, 403, 1, NULL, NULL, NULL, '2025-04-18 11:11:21', 'wzk@sap.com', 0, NULL, b'1', '2025-04-18', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZDBmODEwZTQtMTdkMS00M2NhLWFiMDUtYjVjNmM4Zjk2MDRkIiwic3ViIjoiYWRtaW4iLCJpYXQiOjE3NDQ5NDU4ODEsImV4cCI6MTc0NDk0OTQ4MX0._qjHgwFCjbPrhjBFIfWeh6rkbPgpYb3SYPdAW2TP6vo', '2025-04-06 00:00:00', 'admin', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/3/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 404, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-18 11:24:31', '1448185924@qq.com', 2, NULL, b'1', '2025-04-18', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTkzMjU5ZjgtZDlmZS00NjFmLTgxZjQtMzE2ZjAwZDdlNTczIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ0OTQ2NjcxLCJleHAiOjE3NDQ5NTAyNzF9.EwMMrk0jwBmJ9r0cpYfETT2xz9Xx9LSN-eFrmgSeCLI', '2025-04-15 00:00:00', 'helloKittyCatSweet', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 405, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-18 11:24:35', '1448185924@qq.com', 2, NULL, b'1', '2025-04-18', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNTE3ZDkwNTMtZDdjMC00M2Y5LTk1OGQtZWE4Nzc5MjM2ZGU3Iiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ0OTQ2Njc1LCJleHAiOjE3NDQ5NTAyNzV9.0ykduSVE2E1hdmJLgjNYrpGAbhqc1sNQ_lpmEwn4AZ8', '2025-04-18 00:00:00', 'helloKittyCatSweet', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (4, 406, 1, NULL, NULL, NULL, '2025-04-18 11:28:21', 'xdxzwxq@126.com', 1, NULL, b'1', '2025-04-18', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWY1ZWU2ZWQtMmI1OS00NTQ1LTg4ZTAtZjQ0NzIwZWQ5MDI1Iiwic3ViIjoieGR4end4cSIsImlhdCI6MTc0NDk0NjkwMSwiZXhwIjoxNzQ0OTUwNTAxfQ.gcEL8j1zsPRSd18Ft7L0rR5TpPnIQoxTfk7zIwMGfVA', '2025-04-06 00:00:00', 'xdxzwxq', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/4/signature.png');
INSERT INTO `fs_users_aud` VALUES (4, 407, 1, NULL, NULL, NULL, '2025-04-18 11:28:36', 'xdxzwxq@126.com', 1, NULL, b'1', '2025-04-18', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiM2ViNTcwYzAtZTNkZC00OTY0LTk3MTItMzhlNDU5Yjc0MThlIiwic3ViIjoieGR4end4cSIsImlhdCI6MTc0NDk0NjkxNiwiZXhwIjoxNzQ0OTUwNTE2fQ.SVQFhdQUa1dtc2eA-KC9-yMRG5E0knF7X5WLb4UqqqQ', '2025-04-18 00:00:00', 'xdxzwxq', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/4/signature.png');
INSERT INTO `fs_users_aud` VALUES (4, 408, 1, NULL, NULL, NULL, '2025-04-18 11:37:44', 'xdxzwxq@126.com', 1, NULL, b'1', '2025-04-18', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMTczNDUwODYtNWI1Yy00MGJlLWJjOWUtMmIxY2M3OWQxODBkIiwic3ViIjoieGR4end4cSIsImlhdCI6MTc0NDk0NzQ2NCwiZXhwIjoxNzQ0OTUxMDY0fQ.IIVaOsqRcmHJXgpRRDQ6kGOBYAbURDrgmpmpl7xfT40', '2025-04-18 00:00:00', 'xdxzwxq', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/4/signature.png');
INSERT INTO `fs_users_aud` VALUES (4, 409, 1, NULL, NULL, NULL, '2025-04-18 11:38:08', 'xdxzwxq@126.com', 1, NULL, b'1', '2025-04-18', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWIyNGI0NmItNjQ5MS00MmY5LTg5NTAtMTM3NDM3ZDg3N2E4Iiwic3ViIjoieGR4end4cSIsImlhdCI6MTc0NDk0NzQ4OCwiZXhwIjoxNzQ0OTUxMDg4fQ.gdyMrek1DCri1XQOh6s9iQqL-Er6Bxg4Ty9-w7aqdXI', '2025-04-18 00:00:00', 'xdxzwxq', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/4/signature.png');
INSERT INTO `fs_users_aud` VALUES (4, 410, 1, NULL, NULL, NULL, '2025-04-18 11:39:25', 'xdxzwxq@126.com', 1, NULL, b'1', '2025-04-18', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNTczOTEyOGItN2FhNy00YTE0LTgwMGYtOGViMjA0NDc5YTA0Iiwic3ViIjoieGR4end4cSIsImlhdCI6MTc0NDk0NzU2NSwiZXhwIjoxNzQ0OTUxMTY1fQ.vcW8AfAYh9cJhewO3dh4Sem7vBHVg5Jo2jQtFRjqUH0', '2025-04-18 00:00:00', 'xdxzwxq', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/4/signature.png');
INSERT INTO `fs_users_aud` VALUES (4, 411, 1, NULL, NULL, NULL, '2025-04-18 11:39:41', 'xdxzwxq@126.com', 1, NULL, b'1', '2025-04-18', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMWE0ZWQ4MzQtOWVlNS00YzFmLWE5YmUtMDEwYzJkNGI5MmIxIiwic3ViIjoieGR4end4cSIsImlhdCI6MTc0NDk0NzU4MSwiZXhwIjoxNzQ0OTUxMTgxfQ.vRoBkQ-wVZU0K2hyoiU34WfzHNS00eMHxz4r0LMBJ_c', '2025-04-18 00:00:00', 'xdxzwxq', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/4/signature.png');
INSERT INTO `fs_users_aud` VALUES (4, 412, 1, NULL, NULL, NULL, '2025-04-18 11:40:30', 'xdxzwxq@126.com', 1, NULL, b'1', '2025-04-18', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzA2MWFiNjAtNjUyNy00MGU4LWIwMzctNThlODk1MjFjYzkxIiwic3ViIjoieGR4end4cSIsImlhdCI6MTc0NDk0NzYzMCwiZXhwIjoxNzQ0OTUxMjMwfQ.bevYiB_ZSKda2Ly_qI2ozY0qan1ZA9C_uJE-ckQcA84', '2025-04-18 00:00:00', 'xdxzwxq', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/4/signature.png');
INSERT INTO `fs_users_aud` VALUES (4, 413, 1, NULL, NULL, NULL, '2025-04-18 11:40:44', 'xdxzwxq@126.com', 1, NULL, b'1', '2025-04-18', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWU1MDA2MDEtODBkZS00NTE5LTg3NmEtMzE0ODM2MjY5MGFlIiwic3ViIjoieGR4end4cSIsImlhdCI6MTc0NDk0NzY0NCwiZXhwIjoxNzQ0OTUxMjQ0fQ.UzaQuxnB1kyYKXQzl-4NXIIovL3-mEGiGtYrpNW-buw', '2025-04-18 00:00:00', 'xdxzwxq', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/4/signature.png');
INSERT INTO `fs_users_aud` VALUES (4, 414, 1, NULL, NULL, NULL, '2025-04-18 11:41:49', 'xdxzwxq@126.com', 1, NULL, b'1', '2025-04-18', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNDYxOThkMGEtYzBiNS00MmQ1LTkzZTMtNDA1YzIxNDI3MmM3Iiwic3ViIjoieGR4end4cSIsImlhdCI6MTc0NDk0NzcwOSwiZXhwIjoxNzQ0OTUxMzA5fQ.xbv_93hi-efxDZzG30J2QB6_7M-tTf1uJd3u-0YS19E', '2025-04-18 00:00:00', 'xdxzwxq', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/4/signature.png');
INSERT INTO `fs_users_aud` VALUES (4, 415, 1, NULL, NULL, NULL, '2025-04-18 11:43:47', 'xdxzwxq@126.com', 1, NULL, b'1', '2025-04-18', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMGEzYWRlZDAtYzAyZi00Y2Q3LWJmZDItZWM4OGY1NzA4YjQ0Iiwic3ViIjoieGR4end4cSIsImlhdCI6MTc0NDk0NzgyNywiZXhwIjoxNzQ0OTUxNDI3fQ.mLoDECwunu1IXwR2aVyNUe9EBlyq26CvnZAKyQUcDXQ', '2025-04-18 00:00:00', 'xdxzwxq', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/4/signature.png');
INSERT INTO `fs_users_aud` VALUES (4, 416, 1, NULL, NULL, NULL, '2025-04-18 11:47:26', 'xdxzwxq@126.com', 1, NULL, b'1', '2025-04-18', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNmIxM2U3NDgtOWE0My00ZGM3LWFkNTUtNWRkNTA3NjQ4OWRjIiwic3ViIjoieGR4end4cSIsImlhdCI6MTc0NDk0ODA0NiwiZXhwIjoxNzQ0OTUxNjQ2fQ.Z9bJOWx8CoJVjOdMYGg-AYFOkzinv43RK6LZq9Rks38', '2025-04-18 00:00:00', 'xdxzwxq', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/4/signature.png');
INSERT INTO `fs_users_aud` VALUES (4, 417, 1, NULL, NULL, NULL, '2025-04-18 11:48:34', 'xdxzwxq@126.com', 1, NULL, b'1', '2025-04-18', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNzZlOGEzZTktMDg4Mi00ZDE1LWI1MGQtOWM0MjUyMmUxZGM0Iiwic3ViIjoieGR4end4cSIsImlhdCI6MTc0NDk0ODExNCwiZXhwIjoxNzQ0OTUxNzE0fQ.0J2i4EBSbGXMFCMCQVQZDNMChxSOJ3_F3_SDM868k0E', '2025-04-18 00:00:00', 'xdxzwxq', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/4/signature.png');
INSERT INTO `fs_users_aud` VALUES (4, 418, 1, NULL, NULL, NULL, '2025-04-18 11:50:07', 'xdxzwxq@126.com', 1, NULL, b'1', '2025-04-18', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZTNkNzI1ZmItMDA1Mi00MzQ2LThkODYtMDkwYzM5ZWUxMzYwIiwic3ViIjoieGR4end4cSIsImlhdCI6MTc0NDk0ODIwNywiZXhwIjoxNzQ0OTUxODA3fQ.bTyO76vA4UxDpvxHpIcimJUW6LsmNpAkL8GRYII0clM', '2025-04-18 00:00:00', 'xdxzwxq', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/4/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 419, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-18 12:54:03', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-18', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiM2E0Mzg1NmYtZDE4YS00YmI2LWEyNDAtYjE1Y2Y3NzBlOTBlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ5NTIwNDMsImV4cCI6MTc0NDk1NTY0M30.k9GGZmMmH2sDhrKlTZcjGW5CZ4DIcchiLOLffZaCtyA', '2025-04-18 00:00:00', 'kitty', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (4, 420, 1, NULL, NULL, NULL, '2025-04-18 13:40:35', 'xdxzwxq@126.com', 1, NULL, b'1', '2025-04-18', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMmQwZTFhM2EtZWFhMS00MDM5LWEzYTQtZGMzZTcwMWUwNDRiIiwic3ViIjoieGR4end4cSIsImlhdCI6MTc0NDk1NDgzNSwiZXhwIjoxNzQ0OTU4NDM1fQ.E_XuZAcg9_-sQnOT37zviQbwv7lG38CIeVWLcJ_I_OE', '2025-04-18 00:00:00', 'xdxzwxq', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/4/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 421, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-18 15:06:39', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-18', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTQwZjE2M2MtODU3Yi00YzRjLTk5OWYtOTVkZDU3NmVkN2Y3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ5NTk5OTksImV4cCI6MTc0NDk2MzU5OX0.lIpaKPc5D0yQ7gR4E9f6fUeqTfvb22EuLoME_imNdg8', '2025-04-18 00:00:00', 'kitty', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 422, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-18 15:06:44', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-18', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiN2JmNjg5MWEtM2IwZC00NjE3LWI2YTUtMmU5YTI3ZWMyNGM0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ5NjAwMDQsImV4cCI6MTc0NDk2MzYwNH0.YsX-zfLksGu-UKJPuKOerLG3ZOt-OIA9oohyH4j4Bvw', '2025-04-18 00:00:00', 'kitty', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 423, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-18 15:09:31', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-18', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzE0NmRlODUtN2Q0ZC00YmFhLWE5NTQtMjQzOTYyYTRkMzNhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ5NjAxNzEsImV4cCI6MTc0NDk2Mzc3MX0.X6ABA_b87mVYUiX-er9QTI6whOkCuDMrt6HjYz9hRK4', '2025-04-18 00:00:00', 'kitty', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 424, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-18 15:28:42', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-18', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZTBlZmYwZGEtZmYyZS00ODMxLWE4NTEtODRhOWFhNDE3Yjc2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ5NjEzMjIsImV4cCI6MTc0NDk2NDkyMn0.6aqwuEr9i5cb3YiryvRWPc3xzYXj8XtCOgp4Rr3yfKE', '2025-04-18 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 425, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-18 15:42:23', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-18', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNTEzMTcyZTktOWRjZC00ZTJhLWFhNzUtYzJkNWQ2N2U3Y2MxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ5NjIxNDMsImV4cCI6MTc0NDk2NTc0M30.UGD1Al2bF0Xt3qVBwersltsTTGTgWOx_S-X2Sx-OtvI', '2025-04-18 00:00:00', 'kitty', b'0', '112.6.224.44', '中国 山东省 威海市 移动', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 426, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-18 17:14:29', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-18', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZTkyZWM1ZGYtYjAzMy00NmI0LWIxN2ItODc2OGMwMDNjNTY1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDQ5Njc2NjksImV4cCI6MTc0NDk3MTI2OX0.OFbwIq6YvzxzkiTjk5CHQEmnrrUwwPMqzl4BgVJ2Wd8', '2025-04-18 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 427, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-19 11:28:46', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-19', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODFmMGNkNTAtYjFjZC00Yzg2LWFlNWEtZWVmZTM3OTdjOTgyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDUwMzMzMjUsImV4cCI6MTc0NTAzNjkyNX0.jglkDqYoZHeSC_DMXR71Mz52PN7Jc-VBm6GNn0OXtvI', '2025-04-18 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 428, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-19 15:18:10', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-19', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNzJkNzYzMjktZjI3Ny00NzhkLWJmZTctMDg0NTBlMGJmZDhhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDUwNDcwOTAsImV4cCI6MTc0NTA1MDY5MH0.Jf5VAJHqu8VAO70SK-CUJrf6L1Eym0mIMsrqI-IiUt8', '2025-04-19 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 429, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-20 10:21:18', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-20', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNmE3Y2RhMTUtZWZiOC00MmFjLThhYzAtN2JkOTJlMjI2YTEyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDUxMTU2NzgsImV4cCI6MTc0NTExOTI3OH0.NW4U8hPgxs1WvHGGOwqzgXiBIugs6HFjSv3au4hPX0Y', '2025-04-19 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 430, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-20 10:24:15', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-20', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZWM2MTZkY2UtMjNhNC00ZTE5LWI0NGQtM2Q2YTE3MGMyYjk4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDUxMTU4NTUsImV4cCI6MTc0NTExOTQ1NX0.lnSzWwkFbjjGZxlc7InyxdSvIqQCDHChW_TOHAaqW8Q', '2025-04-20 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 431, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-20 10:33:56', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-20', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTA3ZWUwYWQtY2Q5NS00YzllLTgyMDUtYzkyMWZlY2M4ZjE0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDUxMTY0MzYsImV4cCI6MTc0NTEyMDAzNn0.a4uq1aZ0r4ZN3x1nwWkXIEQoAFE89IyMaBAkc_rMM54', '2025-04-20 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 432, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-20 15:18:25', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-20', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000002770400000002740009E69785E8A18CE5AEB6740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZjBjM2UwMTQtZjQxZC00ODQzLWJjMDctYThmYWVmMWNjMGE3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDUxMzM1MDUsImV4cCI6MTc0NTEzNzEwNX0.ClwTj4mZuT-eCvPITtVzoeBN84VPUTiXYLCZttTWVls', '2025-04-20 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 433, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-20 15:20:28', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-20', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A6578700000000077040000000078, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZjBjM2UwMTQtZjQxZC00ODQzLWJjMDctYThmYWVmMWNjMGE3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDUxMzM1MDUsImV4cCI6MTc0NTEzNzEwNX0.ClwTj4mZuT-eCvPITtVzoeBN84VPUTiXYLCZttTWVls', '2025-04-20 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 434, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-20 15:20:35', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-20', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZjBjM2UwMTQtZjQxZC00ODQzLWJjMDctYThmYWVmMWNjMGE3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDUxMzM1MDUsImV4cCI6MTc0NTEzNzEwNX0.ClwTj4mZuT-eCvPITtVzoeBN84VPUTiXYLCZttTWVls', '2025-04-20 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 435, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-20 15:58:52', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-20', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNTdiZmQwMDEtMzBjYy00M2ZiLWJjMDctMTZlZWU0YmM0ZDcxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDUxMzU5MzIsImV4cCI6MTc0NTEzOTUzMn0.vHA4ihxe9JPslyuiipvvx-C7E4o-F18OfCca3C8WA7U', '2025-04-20 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 436, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-20 21:18:48', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-20', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNGM1YzAxZDktMzRlYi00YTgxLWE3YTctOGYxMTAwYmYxMWI3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDUxNTUxMjgsImV4cCI6MTc0NTE1ODcyOH0.Gmh6yzO_GD_VWuxBUBaC3LnkDt8TYVCOkrb7j8p5sCo', '2025-04-20 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 437, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-20 21:21:53', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-20', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZjAzNDRhYjgtMDYzYi00NTE3LWJhMTItYmQyZWEzNTdmODdkIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDUxNTUzMTMsImV4cCI6MTc0NTE1ODkxM30.k2ioduaTUxf6Z5MAHueL0mmmx42xG96yNENbIJ55WgU', '2025-04-20 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 438, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-21 09:22:21', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-21', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMDkxMDJiMzgtNjNiOS00ZTYyLThiNzAtMGFiM2ZjM2U1MTU1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDUxOTg1NDEsImV4cCI6MTc0NTIwMjE0MX0.4M08Nx8mPNGrGaL8NNsCOFRBZLMTJjq3yTMJ-DZ3NNo', '2025-04-20 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 439, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-21 10:27:39', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-21', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjYxNTdjY2QtNzA2Ni00MjUzLWExNTAtMzg4MDhjZDkyYmI5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDUyMDI0NTksImV4cCI6MTc0NTIwNjA1OX0.SjilU3CWoDGRGRmE_pZtiUqlJmw-E6nu5pipMZD5NrM', '2025-04-21 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 440, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-21 11:53:18', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-21', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZDk3NDU5YzUtYjQzMi00ZWZkLTg3YTYtNGJiNWJhODExOTI1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDUyMDc1OTgsImV4cCI6MTc0NTIxMTE5OH0.P7K8sRRzEj9gx8bFTMKBOMxuRTdNVCaUE60ECuQDmZ0', '2025-04-21 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 441, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-21 15:33:24', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-21', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTBjYjk0ODEtYzg1OS00NzRiLTliM2ItMjU4NTY5YjY0MGI1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDUyMjA4MDQsImV4cCI6MTc0NTIyNDQwNH0.-kHeofynklELTrUjS1xTIx9-lNICSrnJGfLq5JKVMi8', '2025-04-21 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 442, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-21 15:34:15', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-21', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWQwMDhhNDgtYzZjOC00MzIxLThjZGItNDhiMzBlZmViYjQzIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDUyMjA4NTUsImV4cCI6MTc0NTIyNDQ1NX0.RTk-MCiH_-IMfsPRjaYYzOwjrr4HSSsOKi6IzQNUNP8', '2025-04-21 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 443, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-21 21:01:10', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-21', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTM1MGUzNmItMmQ0My00YjBhLTk2Y2UtY2NhMjU1ZmQ5NjFiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDUyNDA0NzAsImV4cCI6MTc0NTI0NDA3MH0.lkUP8u3Hf-aSGGu3bJmoNMcB1B5EeIiqpO8XcGMYA8U', '2025-04-21 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 444, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-23 10:07:48', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-23', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiM2ZiYjk0ZDQtYWJlOS00ZjM4LWE0NjQtYTVkOWM5ZjhlNWQzIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDUzNzQwNjgsImV4cCI6MTc0NTM3NzY2OH0.7EPzmrbgTP_AbrEQYmGL-xSAg-zGn1fL_3HY8L0twCM', '2025-04-21 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 445, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-23 17:38:06', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-23', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjliMmE2YWEtZTFjNS00MDM2LWFjMmUtNGNjOGZmOWRjMzk0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0MDEwODYsImV4cCI6MTc0NTQwNDY4Nn0.S2B2rdBWavk49V9GODpYmmaYo4K4KGfn94EcU-QcIMc', '2025-04-23 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 446, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-23 19:29:38', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-23', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOGY3ZjlmNDAtODE3NC00OWEzLThkYWYtZjU3NzQzMzExOTU4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0MDc3NzgsImV4cCI6MTc0NTQxMTM3OH0.7V3nv69NpUVctHlwFowooARsfV59sEQ2UPt5zrpGino', '2025-04-23 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 447, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-23 19:40:21', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-23', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNGNkNmQ4YjUtMGVlNy00ZTkxLWE3ODEtYjllOTRkNzM3YTcyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0MDg0MjEsImV4cCI6MTc0NTQxMjAyMX0.gFZ0ZE2hjeu3u3mUotNm4ixtcGQrXKWSUnOVUsTEUyw', '2025-04-23 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 448, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-23 19:55:48', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-23', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzhmZjA3NWItN2Q5MS00NzZjLWE4YjAtM2VhNTJiNTdjYjA4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0MDkzNDgsImV4cCI6MTc0NTQxMjk0OH0.GzW9MeCjrhPCiq2E6cGgT6Ozx9IrAA6KolMA-L0CJW4', '2025-04-23 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 449, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-23 21:12:36', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-23', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTc4YTA0YzgtN2UxOS00NzRlLWFkNGMtYmI0M2Q2ODhkNTRjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0MTM5NTYsImV4cCI6MTc0NTQxNzU1Nn0.Fu3ZMGNLaq4Rf2ChWGNwsxr7cf-97HPdY9if2P4tNPI', '2025-04-23 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 452, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-23 21:22:21', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-23', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMTZjMjNiMjAtNWUyMy00NzllLWJhZjAtZjMyY2IxY2FiMTZiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0MTQ1NDEsImV4cCI6MTc0NTQxODE0MX0.dWeMtkilv_obyX4xk2LFvAqs-pBB1gjRObqCEDgvzmI', '2025-04-23 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 453, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-23 21:33:50', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-23', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZTYxYTk4OWItYjQ3Zi00OTgwLTg4ZGUtNDg0ZWE3MjI2MjQ3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0MTUyMzAsImV4cCI6MTc0NTQxODgzMH0.ziqLTUxIJIFj10irlFd52LA6nmx-8ZvRT7dZYl331SI', '2025-04-23 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 454, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-23 21:51:24', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-23', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZWJlNWRjODQtZTk0Yi00NzcwLTliODktNGY2N2EwOWRhN2NjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0MTYyODQsImV4cCI6MTc0NTQxOTg4NH0.bZWRCrIyXJ0AD_znnaMPUqzBBXcYAWw_cuoVfqiTm_c', '2025-04-23 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 456, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-23 21:57:43', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-23', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZGU1Y2Y2YzAtNDJjMy00YWIzLWIxODktYWZmN2U0YzQyZjAxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0MTY2NjMsImV4cCI6MTc0NTQyMDI2M30.xB0rzcdBtYjsZjaPffwNRo46FOIwIqj7WAQR7QE6DL0', '2025-04-23 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 457, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-23 22:06:02', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-23', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNzJjMTU4OTEtMmE1MS00M2YxLWI4ZjItZGJmZDljMzM3OGNkIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0MTcxNjIsImV4cCI6MTc0NTQyMDc2Mn0.PIw-QPvh5sadir_hI-_jyrGnrvk4MgjhoN7sA7LJ6iw', '2025-04-23 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 458, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 09:50:20', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzZmMTk4ZDMtZWYyZC00MTU2LWFmODQtYTk2MjZjNzQzN2QxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NTk0MTksImV4cCI6MTc0NTQ2MzAxOX0.emo2KfsCAPZ6LpqbRlQp3mw86L35kifOuQ9geZJ-bEU', '2025-04-23 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 459, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 10:00:57', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiM2M1MGY4MzEtZTBkNC00NGNlLTk2N2EtNTZiOTBjMjEzNDRmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NjAwNTcsImV4cCI6MTc0NTQ2MzY1N30.8gaMS4TDODUlgKR652WGZAWmeAGgNGG4euiFnWaN57U', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 460, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 10:02:40', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMmJmYWUxN2ItNmYxNS00ZTc2LWI4MDEtYTA2Y2E0MmUyMGJlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NjAxNjAsImV4cCI6MTc0NTQ2Mzc2MH0.eSQqqzgJzz6v6UJYh3ZYQ2RA2chqPddsAnAAimV7mF0', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 461, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 10:13:13', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzUxYTljMmItNmUwNC00OWRjLWI2N2QtMTg3NTdmZTdmYmRmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NjA3OTMsImV4cCI6MTc0NTQ2NDM5M30.dyO6KSdQ7u3gefiEt8x6PbMhwCH63LYMWQnmbB1t-lo', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 462, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 10:18:50', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMDI0NDFmMjgtNGVmYi00OTc5LWI3YzQtZmIwMTRlZmE0NDliIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NjExMjksImV4cCI6MTc0NTQ2NDcyOX0.dudr_sZ_fFuKmQ9ZnT0Z8mYs5_zwob9gd_2ptIoftLw', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 463, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 10:22:31', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMTUzMDgwYzUtMGFlNy00YzU4LWIwZWEtZDY0OWVkNDBlNWUyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NjEzNTEsImV4cCI6MTc0NTQ2NDk1MX0.H8-6ElsuH59Yoz1utg23pe4WHkJDSO6tlQ0qEiib0Ms', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 464, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 10:27:15', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjc0ZmVmNTItZTRhZi00OTUwLTljMjUtMDdjMWE1MWM0ZjU4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NjE2MzUsImV4cCI6MTc0NTQ2NTIzNX0.iEq7EA6p6z3xDjBDTtEqADK5L2kMI5Zfcf69QIm8Qzk', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 465, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 10:28:30', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTFmODYzNzUtNjE3Ni00N2I4LWFlOWEtYTI1M2ZmZDk5ZGFmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NjE3MTAsImV4cCI6MTc0NTQ2NTMxMH0.8xK2x5i8BjwMdGN58xFdmlj_9Y8yuTnNENLfopH9les', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 466, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 10:32:21', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNzQzNWM2YmMtZjY5ZC00NTgxLWExNTktZGI5NzdiNDc3OWIxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NjE5NDEsImV4cCI6MTc0NTQ2NTU0MX0.J2k3RHXxKsD4Iqq5eqkiEzvpOewdrkSpT0V1EZJTtnY', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 467, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 10:33:28', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTExOGE3ZTQtNjkwZi00ZDQ2LTlkMzMtZTQwMjU0YTVkNWU3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NjIwMDgsImV4cCI6MTc0NTQ2NTYwOH0.axFp92O5xLTmnRj6aRKVUldfRdZK2L314k-e9AOsrDg', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 468, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 10:35:36', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNWZhMDYyZjAtNjMzOC00MjZiLThlMTgtZGY4ZTBiYWY2Njg3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NjIxMzYsImV4cCI6MTc0NTQ2NTczNn0._n5g78GfHNTHAMUHtqJXTzi3WqCCWTaDGdJzumY-2_8', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 469, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 10:46:15', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZWYzYjUwZjUtOGExOC00YjFjLWI0MTYtY2NlZmU0NTU2Y2IwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NjI3NzQsImV4cCI6MTc0NTQ2NjM3NH0.s6gRT8PuKtl9M8bde7vxIBO8S9wsN55VrVjTxgrE7zg', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 470, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 10:46:33', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZTY1ZjEwY2EtNjQyYy00ZDA1LTg5MTYtNDhkZjgwMjQxNWVjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NjI3OTMsImV4cCI6MTc0NTQ2NjM5M30.rdx8NR3kctqZ9KX0e3-9iLMmeDTwYxP6tqkxAh9BwEo', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 471, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 10:48:16', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNzJmYzk5NmYtNjQ1Mi00YTFhLWJkYTQtMjdkZmUwMTk4NGM4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NjI4OTYsImV4cCI6MTc0NTQ2NjQ5Nn0.KQWf-ZvrzMQBnGh7jOD8AOyeDkAPJf3H9ufZDgmswA0', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 472, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 11:03:40', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzNlMzNkZGQtMmQ2Ni00ODA5LWI0M2QtZTNkNTQ1OGIxNTVkIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NjM4MjAsImV4cCI6MTc0NTQ2NzQyMH0.DMANDs4QsSLNt-aG51_ug2cZKSZSiCiBpC711o3v8D0', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 473, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 11:19:19', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZTIwNzVmMWQtOWJmNC00Yzk2LThkOGUtYzdiMDBkZjM5YWU2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NjQ3NTksImV4cCI6MTc0NTQ2ODM1OX0.Cr7XwUmlY97HnfO0FZylvE_b_Mm4Viua3vrxIRguM84', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 474, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 11:25:36', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMDhiZmQ0ZjUtYWI1OC00MWE3LTljY2EtMDA5YTU2ZTE4MTQ3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NjUxMzYsImV4cCI6MTc0NTQ2ODczNn0.vFy5hQtf9O1y7YHhN5MB5KiUbMPIaJy6gzNL5jd14EU', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 475, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 11:30:05', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiM2RhZDFlNWUtMmEzZi00MTQ2LWI5MDktMTEwOGEyNWJkYTRjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NjU0MDUsImV4cCI6MTc0NTQ2OTAwNX0.KGxQszjbrIhONMk3WZiJiuhgGtRKgwQbL9fun85Lphc', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 476, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 12:49:57', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiY2E5NDA0YzAtODEyMi00ZTIxLWI4MDUtYjc1NTgwNTg3MWY3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NzAxOTYsImV4cCI6MTc0NTQ3Mzc5Nn0.KZmdTwTF9svfsm8Xne4H3r2YgMZyVzZYFYmmNEj4_bI', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 477, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 14:53:52', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNDRmOGE3M2MtOWQ1ZC00MTU3LThmNzEtZGFkN2E3OTVlMDU4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0Nzc2MzIsImV4cCI6MTc0NTQ4MTIzMn0.Q7dq3AMaYNMzYYQNFEpTWi6MunPg1yymLwwYfDx8_pE', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 478, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 14:58:13', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODhmOGJhMTQtODg3NC00MTNmLThmMzktMDQxODliNWI5M2MyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0Nzc4OTMsImV4cCI6MTc0NTQ4MTQ5M30.TOd4N9qQbTCRx-WNydB8Vc6nMVD3agpVZyiOoagxEP0', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 479, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 15:04:04', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOGY3N2Y2YTMtMDViZS00MDIwLTk1OGYtNjI3MDEyODQ1YzA5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NzgyNDQsImV4cCI6MTc0NTQ4MTg0NH0.kihzmSY3BGGl4QeOU0jcuAnAnu_6cRTU3mWfjw6K66Q', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 480, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 15:04:29', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOGMyNDYzNzgtNmNjYi00YmVjLWEwMzAtYzE4NzBhNzgzNmI5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NzgyNjksImV4cCI6MTc0NTQ4MTg2OX0.9R75WGxyGzOY45rWRYCBprKRWIL465h_XVCduX42shg', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 481, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 15:06:29', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODgyODQ1ZTUtZTJiYy00NmZmLWE0OGEtYmNjN2NkZWJkMzE1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0NzgzODksImV4cCI6MTc0NTQ4MTk4OX0.SLorFD-2HutmBi8OEZf8dFutZg4AfwNYQIfUJTM0z9w', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 482, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 16:01:57', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTFmOTU4MzUtMjZhYS00OTRjLWFkYzYtM2FiNDdhNmI1ODE5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0ODE3MTcsImV4cCI6MTc0NTQ4NTMxN30.0GehGPPiPqOfzZxmyCoBIXMo81xJ_BEmjBCr8oT6WfE', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 483, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 17:15:03', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzVkOTZhOWYtMzZkMS00MGYyLWFjOTktOGM3NmY3MDExMjJmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0ODYxMDMsImV4cCI6MTc0NTQ4OTcwM30.n-CCn7dZwQPDh_ETmtr6gXhvcBzCC1B2NGznPf36PUk', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 484, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-24 19:05:55', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-24', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZDBkYWE3MzEtNzA2NC00ZGNmLThlMmEtMGMxZmU5ZjcxNDcyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU0OTI3NTUsImV4cCI6MTc0NTQ5NjM1NX0.Cx5HUGmnSDfImM3s6LpuxMQMCLasiJHlUTRiZPFETEo', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 485, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-24 20:53:46', '1448185924@qq.com', 2, NULL, b'1', '2025-04-24', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMmY1M2EzYWMtNTNhNC00NWE2LTgwY2EtM2VkOTMyZTE4ODY0Iiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NDk5MjI2LCJleHAiOjE3NDU1MDI4MjZ9.PVDID1LorbqPyAyXU3kAcL-R8F3B06a6DjF3i38Pju8', '2025-04-18 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (1, 486, 1, NULL, '', NULL, '2025-04-24 20:54:01', 'jonny.jiang@sap.com', 0, NULL, b'1', '2025-04-13', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODViMDU1M2ItNTczZi00YTg3LTk2NzctYmQ4MjlmMzg1M2JmIiwic3ViIjoiam9ubnkiLCJpYXQiOjE3NDQ1NTI3NjEsImV4cCI6MTc0NDU1NjM2MX0.Vp4FsE3Dtu8tAfVynqDBs1IbEZqKIlThiyEJfG6FADA', '2025-04-13 00:00:00', 'jonny', b'0', '0:0:0:0:0:0:0:1', '未知', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/1/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 487, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-24 20:57:11', '1448185924@qq.com', 2, NULL, b'1', '2025-04-24', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiM2UwMWQwZDQtMTEyMC00OTc0LWIxYWItMzI0NzBmY2MwMWYyIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NDk5NDMxLCJleHAiOjE3NDU1MDMwMzF9.40bqhNXmJPZPKmI3ivAY488Dd4pRTv4-lqgM7PYtn0M', '2025-04-24 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 497, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-24 22:57:56', '1448185924@qq.com', 2, NULL, b'1', '2025-04-24', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYjczYzk1ZTEtNDNjOS00MDRiLWIyYjktNDhhMGRmYjY2N2FiIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTA2Njc2LCJleHAiOjE3NDU1MTAyNzZ9.NbL0w_t_6NYawuSuVFY0Bx9rbqlnTAruRKCWa9Oe09U', '2025-04-24 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 498, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-25 09:36:00', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-25', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMDgzYTM5NzMtODU3ZS00ZTQxLWE5YTAtODIxZTFhMTJjYThjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU1NDQ5NjAsImV4cCI6MTc0NTU0ODU2MH0.MHx9B-GsEH5GudGSnHDTD9i-bVrbn0Wfj1K7KsC0vfk', '2025-04-24 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 499, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-25 09:37:18', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-25', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOGU0ZGQ5MDgtMDNkNS00MzkxLWE5M2YtMzg4MzA5MDQwNTg0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU1NDUwMzgsImV4cCI6MTc0NTU0ODYzOH0.1Dh1muAs-FgKMJY2NKrApGNCNgVr6I0YBJpAFzWdEJA', '2025-04-25 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 500, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-25 09:38:58', '1448185924@qq.com', 2, NULL, b'1', '2025-04-25', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiN2RjYjBmNzEtNjEwYi00ZjljLTlmMTItODhmMDdmMDk3NGRkIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTQ1MTM4LCJleHAiOjE3NDU1NDg3Mzh9.d-wKUovOwuo34kMMXETUlARckAGLt2KlPCDGJ9uKAJY', '2025-04-24 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 501, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-25 09:42:28', '1448185924@qq.com', 2, NULL, b'1', '2025-04-25', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiN2I5NzBjZGUtMWM0Ni00N2MwLTg3OTgtZTA3ZjNkMDA2ZmRkIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTQ1MzQ4LCJleHAiOjE3NDU1NDg5NDh9.0mqZYuN6koqljuaryImeVud1cJPi-R_UULr0Ltl0iAY', '2025-04-25 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 502, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-25 10:10:10', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-25', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODc0M2QxZDctMTllYy00OGZjLThkYTctZjRlY2NiMmIzZWQ4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU1NDcwMTAsImV4cCI6MTc0NTU1MDYxMH0.2i8IGcLjFg_hCyM7wBDHdKlFZEYkN9iM-Iaji-gb91E', '2025-04-25 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 503, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-25 11:13:02', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-25', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWRlOTAwYWEtOGQ1NC00NmZmLThjYmItMmJjOWYwMjQxYzFjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU1NTA3ODEsImV4cCI6MTc0NTU1NDM4MX0.oyjv3u2poHF35vXx10x6DzNxJNY1Fp0fIPN-c2YQYN4', '2025-04-25 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 504, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-25 11:23:22', '1448185924@qq.com', 2, NULL, b'1', '2025-04-25', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTU0ZDk1YWUtZGNmNi00MTI1LWEwM2QtNWRjYTI4YTZiZWIwIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTUxNDAyLCJleHAiOjE3NDU1NTUwMDJ9.tKxWBXKZRx8mOgmxGCjOX6p_Efhl8Eczt6J0IgwDqRM', '2025-04-25 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 505, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-25 11:26:56', '1448185924@qq.com', 2, NULL, b'1', '2025-04-25', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYzhhNDMzNmUtM2M4ZC00ZmFkLTg2ZGItMzI0MzhiNzA5Y2I5Iiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTUxNjE2LCJleHAiOjE3NDU1NTUyMTZ9.fnXDquOTKV4_UYDPoxDkz59Z1PFDEF7enVp1UfEj4_w', '2025-04-25 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 506, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-25 14:47:43', '1448185924@qq.com', 2, NULL, b'1', '2025-04-25', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTY2NzEzNDUtODg3ZC00MGNmLTg5NTEtNGUwNThjNTJiM2Y4Iiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTYzNjYzLCJleHAiOjE3NDU1NjcyNjN9.XY44fmQuUs-N5U5Iy0Zc8T-OL0SXan8aiiBI_ybZufg', '2025-04-25 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 507, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-25 14:58:52', '1448185924@qq.com', 2, NULL, b'1', '2025-04-25', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNWZhMDk5MjctODJlNC00YjYwLWExYzYtNmNkNjliZTEwNzkyIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTY0MzMyLCJleHAiOjE3NDU1Njc5MzJ9.M-ZjWXDX4xSyug_PLAjVz52fMJ_aW468gzcXDHSr9dI', '2025-04-25 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 508, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-25 15:55:24', '1448185924@qq.com', 2, NULL, b'1', '2025-04-25', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNDdmODFmNzktY2IyMS00YWNhLTk4MWEtMWU0Yjg1ZDZjOWIwIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTY3NzI0LCJleHAiOjE3NDU1NzEzMjR9.O-Qz6aZAtsRPwcWtTduFrA5gJsZCnLBW0lTQhUXkFp8', '2025-04-25 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 509, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-25 16:00:57', '1448185924@qq.com', 2, NULL, b'1', '2025-04-25', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYmQ3NjNhZDgtNmQyMS00OWU3LWI3ODAtZDdmMDBlNDVmNWJiIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTY4MDU3LCJleHAiOjE3NDU1NzE2NTd9.azhVjQcguW54UWzwfhN-TwX5VGx6qJvLQW0IETOG5Tk', '2025-04-25 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 510, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-25 16:05:57', '1448185924@qq.com', 2, NULL, b'1', '2025-04-25', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTNlZGFlYzAtMTczNC00NjIxLTg4ZjUtNWM5ZjJlYWU1NTEwIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTY4MzU3LCJleHAiOjE3NDU1NzE5NTd9.5yNdDi9pM5ilJruRMGEdeNsaxjvPvk-BTYuX7gmppO4', '2025-04-25 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 511, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-25 16:09:04', '1448185924@qq.com', 2, NULL, b'1', '2025-04-25', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYzY5MmRjZDktZmZkMi00YTdlLTk3ODktNDAyYzliZTM1NTI2Iiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTY4NTQ0LCJleHAiOjE3NDU1NzIxNDR9.6xbT4eIfDXrmpxWOMyfA5U779VlhxL_CSCsukHYp69A', '2025-04-25 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 512, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-25 17:02:54', '1448185924@qq.com', 2, NULL, b'1', '2025-04-25', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNWI1MmEyMjQtNDE2Yi00NjJkLTgxNTktMmYyNWRjZTNjNGFlIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTcxNzc0LCJleHAiOjE3NDU1NzUzNzR9.7jR72FDMmg9c2_QGjyt1i7WalE919JE_Rg5KJQAQLuo', '2025-04-25 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 513, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-25 17:17:41', '1448185924@qq.com', 2, NULL, b'1', '2025-04-25', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjIwMTc5YzUtYzhmYy00ZDEwLWIxOTItYTIxYWQwMDU2NzFlIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTcyNjYwLCJleHAiOjE3NDU1NzYyNjB9.59AWFDF_9jFfS7f-XYAoDuL9Lq-5q2PCBb4olS8E8IM', '2025-04-25 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 514, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-25 19:18:33', '1448185924@qq.com', 2, NULL, b'1', '2025-04-25', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNzdjMmJiOWMtMmE0NS00YjBjLWI0ZTktOTNmZjAzODY0N2VjIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTc5OTEzLCJleHAiOjE3NDU1ODM1MTN9.lPE3MZe9_D4MnvaOZHrdmJFj8AUnjh515ISo-UxaAto', '2025-04-25 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 515, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-25 19:21:28', '1448185924@qq.com', 2, NULL, b'1', '2025-04-25', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYzAwYjdmYzgtNTllMy00MDVmLWJmMDAtYmNiNWJlYTczYjBiIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTgwMDg3LCJleHAiOjE3NDU1ODM2ODd9.1BrTzyXee2iJnX0fakwjYWwMR6nZmtghkXWxt6IBU_8', '2025-04-25 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 521, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-25 20:21:51', '1448185924@qq.com', 2, NULL, b'1', '2025-04-25', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNTMzMTBiOWQtYTYyYi00ZDg5LThkMjMtN2MwNWQ3MTM5ZWM4Iiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTgzNzExLCJleHAiOjE3NDU1ODczMTF9.pELrUg5oPR2z4TOlfu9QqfgYsaoKzaK3q8z1GCjUOMY', '2025-04-25 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 522, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-25 20:38:40', '1448185924@qq.com', 2, NULL, b'1', '2025-04-25', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMmNjYWM0ZDktM2YyZC00YTY4LTg1NjctZDU5ZDRjNWQ1MGJkIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTg0NzIwLCJleHAiOjE3NDU1ODgzMjB9.tHIgwCmf4J6yFpejVxwGRjW9FwbYp6hPcrFQ_gxiMZk', '2025-04-25 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 523, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-25 20:43:07', '1448185924@qq.com', 2, NULL, b'1', '2025-04-25', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYWM3NzhmM2EtZjdiZS00NzAyLWExZTgtYmEwMzQzMzg5MDEwIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NTg0OTg3LCJleHAiOjE3NDU1ODg1ODd9.T38lhnmvgXF8FuKXprvlnUPbhP00ZUtzgAHLI3G_dGU', '2025-04-25 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 526, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-25 21:00:12', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-25', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNzU3NjY1ZmQtMzc4Ny00ZTY2LWE2OGItOTUzODMyNTQ1Y2MyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU1ODYwMTIsImV4cCI6MTc0NTU4OTYxMn0.sKJJu0ZqRZbLtCmy0EXeW8rUavVIBZVvMlwopWNwo54', '2025-04-25 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 527, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-25 21:05:58', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-25', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTU2MGEwNTYtZmNjNy00YjlmLTlhNGMtNzYxYTc4YzAxNGM3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU1ODYzNTcsImV4cCI6MTc0NTU4OTk1N30.OA3pqYOLYYORmYTiJ2zBoIgrP6aezJeew15Eb48Whe0', '2025-04-25 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 528, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-25 21:08:42', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-25', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOWVhODJlMDItZTE1OS00MTk3LThmYTgtYmIxOWRkZDJiNDkxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU1ODY1MjIsImV4cCI6MTc0NTU5MDEyMn0.1t8oQ_n7uNv9Dfz90EuHh2NEq2zPymPvOm5T8_VAxkA', '2025-04-25 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 532, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-25 21:24:57', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-25', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMWJkMTMxYzQtZmU1Ni00NjFlLTliNTQtMjZjMjMxMDQ3Nzg1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU1ODc0OTcsImV4cCI6MTc0NTU5MTA5N30.XR_Jg8CzFaM9eosPXSd4FLZJLwUcHjhQ_EO1uX7rY5w', '2025-04-25 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 533, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-25 21:50:06', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-25', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTEwMDY2ZTctMzBmNS00OTNlLWFlZWQtYjgxNTFlNDdmMzRmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU1ODkwMDYsImV4cCI6MTc0NTU5MjYwNn0.PhLZMof2sNu_SeZnQsgS0CRN6Hb4gKD0JXk6f26pifc', '2025-04-25 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 534, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-25 21:54:59', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-25', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMTk1MDA3ZDQtYTQ4My00ZjQ4LWE3YjAtMGE3ZjNkOTRhMjQ1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU1ODkyOTksImV4cCI6MTc0NTU5Mjg5OX0.ENrws8SpqZCiIfTEzXXxnFXpJUPMGuIZNgHTNJwhPV8', '2025-04-25 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 535, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-25 22:04:13', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-25', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYzIxYzc5NjktYmM4Zi00Y2Y0LWJhNzUtNTkxYTI0YWQwZDAxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU1ODk4NTMsImV4cCI6MTc0NTU5MzQ1M30.gn9ZvOFjRy-TIsj2xXIofSkprtc0gSf7DLwSNO0H2ps', '2025-04-25 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 536, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-25 23:07:58', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-25', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNGE3NWI3YjItMzY0Yy00NzcxLThmM2QtZTQ0YzU3YzQ2NDYwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU1OTM2NzgsImV4cCI6MTc0NTU5NzI3OH0.VWugIG4jctDkKWa35KaCoqix_kbPGRna7rwdCB2Wl0E', '2025-04-25 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 539, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-25 23:48:33', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-25', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZTU1YjU2ZTQtZjc0OS00YTNkLWJmMmItMTI0NzM5NzNkN2Y3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU1OTYxMTMsImV4cCI6MTc0NTU5OTcxM30.vh_IkxtMckvXx1UtrDwRXAlqqaecIp3wTKOlmU2bU-A', '2025-04-25 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 543, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-26 09:15:34', '1448185924@qq.com', 2, NULL, b'1', '2025-04-26', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzIzZDkyYjgtODYyYy00ZDg5LThkMmUtMzhiYjk0NDdlYWQzIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NjMwMTM0LCJleHAiOjE3NDU2MzM3MzR9.Iz5GXS5FiEEsiLQvA7RkRocksY2xs2nwk00fswWVBMM', '2025-04-25 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 572, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-26 09:52:46', '1448185924@qq.com', 2, NULL, b'1', '2025-04-26', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODExZGQ1YzMtZWY3MS00OGY0LWFiNDgtYTI0YjMwZDAwZjM1Iiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NjMyMzY2LCJleHAiOjE3NDU2MzU5NjZ9.NsoBAKicSI9gQjTig3qJGkI7oCCvmqSLoO6pcY4cZgI', '2025-04-26 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 574, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-26 10:59:26', '1448185924@qq.com', 2, NULL, b'1', '2025-04-26', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNTQzYTgxYWItN2ZkNC00ZTQ3LWEyMjktMGE5NmFjNjk0MzM4Iiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NjM2MzY2LCJleHAiOjE3NDU2Mzk5NjZ9.EeX3IX2BwxclgPwcWixdIrj65-ywjC0-aWICtyZjgYw', '2025-04-26 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 575, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-26 11:19:25', '1448185924@qq.com', 2, NULL, b'1', '2025-04-26', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZGNiMTY3YzAtZDAyYi00OTg3LWJjODUtMDgwNDA0YjcyZGYwIiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NjM3NTY1LCJleHAiOjE3NDU2NDExNjV9.45v2rHu4KVpQdcX7wxxi8kOA5hpcvuJGlJGSF2vmpEw', '2025-04-26 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (15, 576, 1, NULL, 'https://avatars.githubusercontent.com/u/109123755?v=4', NULL, '2025-04-26 11:33:24', '1448185924@qq.com', 2, NULL, b'1', '2025-04-26', NULL, NULL, NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNzhmM2QzMTgtYmRiNS00ZmNjLTlhMmEtODY5ZTc5NzRlNDI5Iiwic3ViIjoiaGVsbG9LaXR0eUNhdFN3ZWV0IiwiaWF0IjoxNzQ1NjM4NDA0LCJleHAiOjE3NDU2NDIwMDR9.U1FMMcIy3BP48Xu46gHaoIzLJGjYCj6rhqENNbMDrRI', '2025-04-26 00:00:00', 'helloKittyCatSweet', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/15/signature.png');
INSERT INTO `fs_users_aud` VALUES (16, 577, 0, NULL, NULL, NULL, '2025-04-26 16:16:04', '123456', 2, NULL, b'1', NULL, NULL, NULL, NULL, NULL, NULL, '123456@qq.com', b'0', NULL, NULL, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/16/signature.png');
INSERT INTO `fs_users_aud` VALUES (17, 578, 0, NULL, 'https://avatars.githubusercontent.com/u/89336197?v=4', NULL, '2025-04-26 17:03:32', '123456@qq.com', 2, NULL, b'1', NULL, NULL, NULL, NULL, NULL, NULL, 'Laura-Ting', b'0', NULL, NULL, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/17/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 579, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-26 17:34:04', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-26', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOGNhZDMwNjMtNTBjNS00MTI1LWE3NDMtZDJmYjVhN2QxYTI1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU2NjAwNDMsImV4cCI6MTc0NTY2MzY0M30.-jfmZqHd7HD1UYSVLj5589uV9_lbphhnVZELnm8RYDY', '2025-04-25 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 580, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-26 19:06:22', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-26', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiY2VkZGNkOGMtMjNiOC00ZWIzLTlkMzQtZTIzYWJkZjhhN2MzIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU2NjU1ODIsImV4cCI6MTc0NTY2OTE4Mn0.eOBxj8jVkAJrsBvXt3JbC1n8merSDmPQUZEGEr4Perg', '2025-04-26 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 581, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-26 21:32:46', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-26', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNDIyODA1NjYtZWU1NC00OTU2LTlkNjUtY2FmNjNhYjZlMDU0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU2NzQzNjYsImV4cCI6MTc0NTY3Nzk2Nn0.XRJiM_-21SsHlRzZcE1e6vY0Ww_krlnMkTgkAjwifpA', '2025-04-26 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 582, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-26 23:09:29', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-26', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNzgyZWZkMmUtMWU5NC00ZDMyLThhYzMtYmMyMjRlODE2NmJhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU2ODAxNjksImV4cCI6MTc0NTY4Mzc2OX0.7_E8vmAs-fIXf38vF60GBWMHO0Dg-SayVZxlsi8Fnxw', '2025-04-26 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 583, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 08:48:50', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTgzNzUzYzAtMDc1MS00MGU2LWEyODYtZmFkZmFmMjNhYjRmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3MTQ5MzAsImV4cCI6MTc0NTcxODUzMH0.GuECBlk0DzLmG7NfwUtXIWkcIYyb-FROBxPMywqaSkk', '2025-04-26 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 584, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 09:16:25', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMjI1NTcyN2MtY2YwOS00NDJiLTgwNTItMDgzMjFhNmNmM2JjIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3MTY1ODUsImV4cCI6MTc0NTcyMDE4NX0.BQrP-gTot4mPcFwYflc6o_0NCy5znRNGaSkRKNc5aGw', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 585, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 09:17:22', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMDY3Y2ZkZjctZmQyNy00MDRjLTkyNDktZWQ4OWIyNGJmYjcxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3MTY2NDIsImV4cCI6MTc0NTcyMDI0Mn0.QqJUuzdFzB7gyWps-JF3YqyXYIueYqogUmgUBO4_q0A', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 586, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 09:17:45', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZDRkYjEzZmEtYWYyMC00NGUzLTk5YTUtMzhkOTNiYmJmZGFmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3MTY2NjUsImV4cCI6MTc0NTcyMDI2NX0.O0brYbVpkR1vXyVBYwZUPS5oQfnJTk1iShwmoFpsWCg', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 587, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 09:21:40', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMjY2YmNhM2QtZTQ0ZS00ZmI3LTk1ZmMtOTExNzg4NzU3MjUyIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3MTY5MDAsImV4cCI6MTc0NTcyMDUwMH0.MT0Tq5BBjoco_VJj5VZXhPSmIHl-_l8pi0kTKUhsl8U', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 588, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 09:27:13', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZDEzNzVmNTgtOTBhZi00MWVhLTg0MDctNzBmZmZmNDdhMjVlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3MTcyMzIsImV4cCI6MTc0NTcyMDgzMn0.zmQBVS-sVJOBhT3FUYTxqEqveJXf2RqFsW9xGMFF4Qc', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 589, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 09:28:54', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZTZmYzQ4ZjgtZTYwMC00ZjI4LWE4MzctZGJhN2ViN2MzMjMwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3MTczMzQsImV4cCI6MTc0NTcyMDkzNH0.g69GyZ8R9m83fy22YpMqu3WWbLSgmxjzuXtbEnG9fTY', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 590, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 09:31:02', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYjljYzM3MDMtNmUxOC00NTViLTkzNzgtMDM0Y2RhMTc5Y2Y0Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3MTc0NjIsImV4cCI6MTc0NTcyMTA2Mn0.TekFpnOc4tz5x3pGtHRNGfT1IIubDWljIKKMX_9C-9k', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 591, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 09:45:26', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNTQ1NTYxMjMtNWZmYi00OTRjLTkwNGQtYWNmMzliMzM2NGUxIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3MTgzMjYsImV4cCI6MTc0NTcyMTkyNn0.-vMxTpX62e0Vdr3QJ7KQAzUjKh2DXChQgQ12tPMoTqs', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 592, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 10:45:49', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMTUwNjYwM2YtNDcwNi00ZjhhLThjMzUtZTRkM2QxOWIwOWUzIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3MjE5NDgsImV4cCI6MTc0NTcyNTU0OH0.jUnjift_AXOThv9d7l2NzIV7MqcRS_B9ejhLe0aapI8', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 593, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 11:28:38', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMmI5MDkwMmEtNGQ0YS00YTE0LWE0MGYtZjNkZjJiNTAyYjY3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3MjQ1MTgsImV4cCI6MTc0NTcyODExOH0.Hiv2CKu06BU370qh_Rw0f1zrvmb1SbhRCyt8oGuu4eg', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 594, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 11:31:59', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZDk0NTNjODQtYTc3OS00ZWM0LTlkYzYtOWEzZjBhMjI3NGUwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3MjQ3MTksImV4cCI6MTc0NTcyODMxOX0.qZ6pJQId_u5yrWcybbdHnRtvd6qVO_mlveMPa7wAFLg', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 595, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 11:37:36', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjFlNDNhMjktNjY5MC00MTAyLWFjMjktZjhmMDE4NmJlNTljIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3MjUwNTYsImV4cCI6MTc0NTcyODY1Nn0.sMsllqWMUxzxRX5-3RVTWYKwdbf5qDzoOocZIWFZXm0', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 596, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 11:39:12', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNDMzNDhhYTQtMjEwNS00ZThkLTllNjMtMDMzYzNiNjUzN2NlIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3MjUxNTIsImV4cCI6MTc0NTcyODc1Mn0.CZrJpVHg5oi6i8uCnmk8tdy62yhLVO7SFCGBQQoMhGc', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 597, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 11:39:53', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMDg5MDczZDgtNjJmZC00MDJmLWEyMzUtYmVlMmNhNWZkNzBhIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3MjUxOTMsImV4cCI6MTc0NTcyODc5M30.xse_Ar-yjIOSxad9fqy9svxVbku56RTRp2KMjtBquJ0', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 598, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 11:42:33', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZTY4NzI1OTktNWQ4ZC00MWZkLWIxYTEtZWE5ZDdkYTNhOWU5Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3MjUzNTMsImV4cCI6MTc0NTcyODk1M30.wEZOQS_0GB-qdJ3K5FF6Dy1PdP7j-qBwbChpTZLbcrg', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 600, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 12:43:00', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNTZlYTZkZTEtOGI4Ny00YWZiLWE4ZjktODAzZGI2ZmU5YWJmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3Mjg5ODAsImV4cCI6MTc0NTczMjU4MH0.FX67GXcoFkJsgLxup0EpF7bieRabRsimWGCNwjb4Cyc', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 601, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 12:43:47', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODQ0MzhmMTItZmU1YS00MmY4LWI3N2ItMWFiZDQwMTgyMzBiIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3MjkwMjcsImV4cCI6MTc0NTczMjYyN30.DKdcg89X5FVyArmOWad31wRECoxNcR76Q7bic3yN-uQ', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 602, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 12:51:19', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNmQ0ZGJjZGMtZjJmZS00NDI0LTg4ZmMtMTcyZWUxYTg3MTA2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3Mjk0NzksImV4cCI6MTc0NTczMzA3OX0.6j6FRs0WB2gosCOpwbaSEhykAD_r-n80Pe06VByNRyg', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 611, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 13:59:17', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMmE5N2ZhMGYtOTQ4Ny00YzAwLWIwYjEtMTdlZjg2YTMzNzg4Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3MzM1NTYsImV4cCI6MTc0NTczNzE1Nn0.SPrdTGtJ6QQmqtU5hJGOi-zLz0CKDO7BhG4GA_ZPpM8', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (18, 613, 0, NULL, 'https://avatars.githubusercontent.com/u/89336197?v=4', NULL, '2025-04-27 16:58:44', '123456@qq.com', 2, NULL, b'1', NULL, NULL, NULL, NULL, NULL, NULL, 'Laura-Ting', b'0', NULL, NULL, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/18/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 614, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 19:15:11', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNTVmMGFmOTQtN2ZhMy00OTYzLTk2MzItZDNiZWRiYTM0MDY2Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3NTI1MTAsImV4cCI6MTc0NTc1NjExMH0.IOZFIggNqecJJkd8UlL1nSRcOeN3giTeOzM7D1fz0fA', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 615, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 20:08:13', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYTc0ZjgzYWYtMTcxNi00Nzc2LTkyN2QtZjg5OWEwMTNhOGQzIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3NTU2OTMsImV4cCI6MTc0NTc1OTI5M30.yhUWvZLA1aNPpi6tsU-SXOAUQj0RdQZ3jADI75_TLtc', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (5, 621, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/images/user/2/1743518421174-avatar-5490599774882110159-宠物狗.png', NULL, '2025-04-27 20:26:44', '1448185924@qq.fake', 2, NULL, b'1', '2025-04-01', NULL, '1cS+NtSZF4lT8kA3TtawcA==', NULL, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzI3NzYzZmMtMTc0NS00NjdlLWFiOTQtMGUxZDViMTBkM2M3Iiwic3ViIjoiMTExMTExIiwiaWF0IjoxNzQzNTE0MzMzLCJleHAiOjE3NDM1MTc5MzN9.-Es_His3AnZbmBhnKHX_6wGhNijcq5fQIwD5oLS3WW0', '2025-04-06 00:00:00', '111111', b'1', NULL, NULL, 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/5/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 628, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 20:42:11', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiOTI2YTgwNmUtYjJhNC00OTVhLTk0YWYtOGI2OTY5YzI5ZDUwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3NTc3MzEsImV4cCI6MTc0NTc2MTMzMX0.yTH1yGmDHoqQyq3KiUPwnhfHj_JqjKeOK7MAC9rwfys', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 630, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-27 23:17:17', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-27', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiODk1MGIzODktOGE1Yy00MjZlLTllYjktODhjZDliZjE3ZjczIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU3NjcwMzcsImV4cCI6MTc0NTc3MDYzN30.ep6ls6AyxDYCKKYEHPV3mPwT5-e8HCkC0h86Zz7YBLg', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 631, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-28 09:54:23', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-28', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNDIwMGY3MTUtMjlmMC00OGQxLTg2ZTItZGM0MmM0ZWQwMjIwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU4MDUyNjMsImV4cCI6MTc0NTgwODg2M30.kO1jKelf2V_tyF4XvpkqDqEhNnaQQFbYkMxAgIKTfMc', '2025-04-27 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 632, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-28 10:30:42', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-28', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiYmUyZmNjNzMtZmYwNS00ZWQyLWJkZTMtNjk3MGJjMzVkOWU1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU4MDc0NDIsImV4cCI6MTc0NTgxMTA0Mn0.Nsf0CjVnY8qZ8ltdgQ83QiMJ8VEu9iPhEspThpF9Wms', '2025-04-28 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 633, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-28 10:31:42', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-28', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiNjgzYmU5NzctZjNiNi00NjUyLTk0MmYtNWU0ZTMwZTY0NDRmIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU4MDc1MDIsImV4cCI6MTc0NTgxMTEwMn0.xSiueZoH1nHDuaHsRjabyhicdu14kd3x497TPyFrtW8', '2025-04-28 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 634, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-28 10:42:53', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-28', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMGE1Zjg2NjEtN2NmMy00ZTNlLTgwNDQtZDJiYzZiNTdmMTM3Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU4MDgxNzMsImV4cCI6MTc0NTgxMTc3M30.qHUYQcD_d7xX1E7oSmwjrbQ2clTf2pNivw9rfrZ17f0', '2025-04-28 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 636, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-28 11:44:15', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-28', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiZDI5MWVhYjctMjg2Ny00ZTk1LTg3NWYtMTE1ZDU2OGQ0ZTMwIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU4MTE4NTUsImV4cCI6MTc0NTgxNTQ1NX0.H3Wgx79r6Fyg8Fyj6M9s1KbcO_C0m6FCTvLNJM3lbrY', '2025-04-28 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 637, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-28 14:51:26', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-28', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMzNlYzM4OTItMDVmZC00ZmU2LWI2M2UtMzAxYjYzNjNhYjgzIiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU4MjMwODYsImV4cCI6MTc0NTgyNjY4Nn0.BgBWo6OCNI-ifSuScRydJ45DT0kzaOdb1ByxZWgfg5c', '2025-04-28 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');
INSERT INTO `fs_users_aud` VALUES (2, 638, 1, 'z+ZtiK+yhnIA73pio5s8Ur67XCIOLCq4KfnNnMrMe+5RX3CX8+48h737N0Q95JwunWkSj6mmKd6/eH59jCNdrw==', '', '2025-04-22', '2025-04-28 15:51:39', '2021212077@stu.hit.edu.cn', 0, NULL, b'1', '2025-04-28', '小铃铛', '1cS+NtSZF4lT8kA3TtawcA==', 0xACED0005737200136A6176612E7574696C2E41727261794C6973747881D21D99C7619D03000149000473697A65787000000001770400000001740006E78CABE592AA78, 'eyJhbGciOiJIUzI1NiJ9.eyJ1dWlkIjoiMDc2YWRlYmUtNjU2NS00MDMxLTliNTUtZDMyMTc3OGJiNDA1Iiwic3ViIjoia2l0dHkiLCJpYXQiOjE3NDU4MjY2OTksImV4cCI6MTc0NTgzMDI5OX0.KIgaIJaZeItVcaTHYPfb7G_ugdR1I1bDWBNjlu9UCYs', '2025-04-28 00:00:00', 'kitty', b'0', '0:0:0:0:0:0:0:1', '本地', 'https://hello-kitty-cat-sweet.oss-cn-beijing.aliyuncs.com/signature/2/signature.png');

-- ----------------------------
-- Table structure for revinfo
-- ----------------------------
DROP TABLE IF EXISTS `revinfo`;
CREATE TABLE `revinfo`  (
  `rev` int NOT NULL AUTO_INCREMENT,
  `revtstmp` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`rev`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 639 CHARACTER SET = utf16 COLLATE = utf16_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of revinfo
-- ----------------------------
INSERT INTO `revinfo` VALUES (1, 1743689727000);
INSERT INTO `revinfo` VALUES (2, 1743690066583);
INSERT INTO `revinfo` VALUES (3, 1743729273566);
INSERT INTO `revinfo` VALUES (4, 1743730963906);
INSERT INTO `revinfo` VALUES (5, 1743733483491);
INSERT INTO `revinfo` VALUES (6, 1743733688446);
INSERT INTO `revinfo` VALUES (7, 1743733737909);
INSERT INTO `revinfo` VALUES (8, 1743734412244);
INSERT INTO `revinfo` VALUES (9, 1743737480330);
INSERT INTO `revinfo` VALUES (10, 1743750287294);
INSERT INTO `revinfo` VALUES (11, 1743754000028);
INSERT INTO `revinfo` VALUES (12, 1743754370318);
INSERT INTO `revinfo` VALUES (13, 1743755477682);
INSERT INTO `revinfo` VALUES (14, 1743756079095);
INSERT INTO `revinfo` VALUES (15, 1743765857916);
INSERT INTO `revinfo` VALUES (16, 1743765880604);
INSERT INTO `revinfo` VALUES (17, 1743766310972);
INSERT INTO `revinfo` VALUES (18, 1743766735556);
INSERT INTO `revinfo` VALUES (19, 1743766966629);
INSERT INTO `revinfo` VALUES (20, 1743767250073);
INSERT INTO `revinfo` VALUES (21, 1743767905853);
INSERT INTO `revinfo` VALUES (22, 1743767970626);
INSERT INTO `revinfo` VALUES (23, 1743768125097);
INSERT INTO `revinfo` VALUES (24, 1743768372679);
INSERT INTO `revinfo` VALUES (25, 1743768599950);
INSERT INTO `revinfo` VALUES (26, 1743768886860);
INSERT INTO `revinfo` VALUES (27, 1743769071583);
INSERT INTO `revinfo` VALUES (28, 1743769361118);
INSERT INTO `revinfo` VALUES (29, 1743770143629);
INSERT INTO `revinfo` VALUES (30, 1743770286497);
INSERT INTO `revinfo` VALUES (31, NULL);
INSERT INTO `revinfo` VALUES (32, 1743778684963);
INSERT INTO `revinfo` VALUES (33, 1743779093612);
INSERT INTO `revinfo` VALUES (34, 1743779103849);
INSERT INTO `revinfo` VALUES (35, 1743779103896);
INSERT INTO `revinfo` VALUES (36, 1743815414291);
INSERT INTO `revinfo` VALUES (37, 1743817629969);
INSERT INTO `revinfo` VALUES (38, 1743836296999);
INSERT INTO `revinfo` VALUES (39, 1743836302022);
INSERT INTO `revinfo` VALUES (40, 1743836648843);
INSERT INTO `revinfo` VALUES (41, 1743838532805);
INSERT INTO `revinfo` VALUES (42, 1743839645292);
INSERT INTO `revinfo` VALUES (43, 1743843354817);
INSERT INTO `revinfo` VALUES (44, 1743844366598);
INSERT INTO `revinfo` VALUES (45, 1743844625090);
INSERT INTO `revinfo` VALUES (46, 1743845405779);
INSERT INTO `revinfo` VALUES (47, 1743845434156);
INSERT INTO `revinfo` VALUES (48, 1743845440043);
INSERT INTO `revinfo` VALUES (49, 1743845443778);
INSERT INTO `revinfo` VALUES (50, 1743845531671);
INSERT INTO `revinfo` VALUES (51, 1743846434356);
INSERT INTO `revinfo` VALUES (52, 1743846521257);
INSERT INTO `revinfo` VALUES (53, 1743865389025);
INSERT INTO `revinfo` VALUES (54, 1743901991077);
INSERT INTO `revinfo` VALUES (55, 1743902754244);
INSERT INTO `revinfo` VALUES (56, 1743902828635);
INSERT INTO `revinfo` VALUES (57, 1743902984107);
INSERT INTO `revinfo` VALUES (58, 1743904256369);
INSERT INTO `revinfo` VALUES (59, 1743904422988);
INSERT INTO `revinfo` VALUES (60, 1743904706723);
INSERT INTO `revinfo` VALUES (61, 1743908087822);
INSERT INTO `revinfo` VALUES (62, 1743911737898);
INSERT INTO `revinfo` VALUES (63, 1743922849656);
INSERT INTO `revinfo` VALUES (64, 1743924950256);
INSERT INTO `revinfo` VALUES (65, 1743925051976);
INSERT INTO `revinfo` VALUES (66, 1743925093326);
INSERT INTO `revinfo` VALUES (67, 1743925363786);
INSERT INTO `revinfo` VALUES (68, 1743925455113);
INSERT INTO `revinfo` VALUES (69, 1743925548420);
INSERT INTO `revinfo` VALUES (70, 1743925859878);
INSERT INTO `revinfo` VALUES (71, 1743925988014);
INSERT INTO `revinfo` VALUES (72, 1743926105272);
INSERT INTO `revinfo` VALUES (73, 1743926112651);
INSERT INTO `revinfo` VALUES (74, 1743926138789);
INSERT INTO `revinfo` VALUES (75, 1743929622525);
INSERT INTO `revinfo` VALUES (76, 1743929774447);
INSERT INTO `revinfo` VALUES (77, 1743938221977);
INSERT INTO `revinfo` VALUES (78, 1743938882548);
INSERT INTO `revinfo` VALUES (79, 1743940946701);
INSERT INTO `revinfo` VALUES (80, 1743941629165);
INSERT INTO `revinfo` VALUES (81, 1743941953955);
INSERT INTO `revinfo` VALUES (82, 1743942241478);
INSERT INTO `revinfo` VALUES (83, 1743942643913);
INSERT INTO `revinfo` VALUES (84, 1743943254643);
INSERT INTO `revinfo` VALUES (85, 1743943592728);
INSERT INTO `revinfo` VALUES (86, 1743943806256);
INSERT INTO `revinfo` VALUES (87, 1743943983369);
INSERT INTO `revinfo` VALUES (88, 1743944007918);
INSERT INTO `revinfo` VALUES (89, 1743952396829);
INSERT INTO `revinfo` VALUES (90, 1743991297269);
INSERT INTO `revinfo` VALUES (91, 1743991557394);
INSERT INTO `revinfo` VALUES (92, 1743991626721);
INSERT INTO `revinfo` VALUES (93, 1743994206807);
INSERT INTO `revinfo` VALUES (94, 1743994245194);
INSERT INTO `revinfo` VALUES (95, 1743994392818);
INSERT INTO `revinfo` VALUES (96, 1743994613934);
INSERT INTO `revinfo` VALUES (97, 1743996748080);
INSERT INTO `revinfo` VALUES (98, 1743996802573);
INSERT INTO `revinfo` VALUES (99, 1744000489499);
INSERT INTO `revinfo` VALUES (100, 1744000589216);
INSERT INTO `revinfo` VALUES (101, 1744009249067);
INSERT INTO `revinfo` VALUES (102, 1744013081066);
INSERT INTO `revinfo` VALUES (103, 1744018491454);
INSERT INTO `revinfo` VALUES (104, 1744024427935);
INSERT INTO `revinfo` VALUES (105, 1744026378640);
INSERT INTO `revinfo` VALUES (106, 1744027088070);
INSERT INTO `revinfo` VALUES (107, 1744027180439);
INSERT INTO `revinfo` VALUES (108, 1744028071752);
INSERT INTO `revinfo` VALUES (109, 1744028123205);
INSERT INTO `revinfo` VALUES (110, 1744028370214);
INSERT INTO `revinfo` VALUES (111, 1744028512891);
INSERT INTO `revinfo` VALUES (112, 1744028908441);
INSERT INTO `revinfo` VALUES (113, 1744030332419);
INSERT INTO `revinfo` VALUES (114, 1744030635127);
INSERT INTO `revinfo` VALUES (115, 1744030826720);
INSERT INTO `revinfo` VALUES (116, 1744032298568);
INSERT INTO `revinfo` VALUES (117, 1744032400082);
INSERT INTO `revinfo` VALUES (118, 1744036366949);
INSERT INTO `revinfo` VALUES (119, 1744074438441);
INSERT INTO `revinfo` VALUES (120, 1744078067477);
INSERT INTO `revinfo` VALUES (121, 1744081770255);
INSERT INTO `revinfo` VALUES (122, 1744094467621);
INSERT INTO `revinfo` VALUES (123, 1744097536320);
INSERT INTO `revinfo` VALUES (124, 1744097730328);
INSERT INTO `revinfo` VALUES (125, 1744097924972);
INSERT INTO `revinfo` VALUES (126, 1744098572416);
INSERT INTO `revinfo` VALUES (127, 1744099092228);
INSERT INTO `revinfo` VALUES (128, 1744101508519);
INSERT INTO `revinfo` VALUES (129, 1744109799740);
INSERT INTO `revinfo` VALUES (130, 1744109888880);
INSERT INTO `revinfo` VALUES (131, 1744114133930);
INSERT INTO `revinfo` VALUES (132, 1744115473535);
INSERT INTO `revinfo` VALUES (133, 1744117462437);
INSERT INTO `revinfo` VALUES (134, 1744124035867);
INSERT INTO `revinfo` VALUES (135, 1744124561796);
INSERT INTO `revinfo` VALUES (136, 1744125080220);
INSERT INTO `revinfo` VALUES (137, 1744125231341);
INSERT INTO `revinfo` VALUES (138, 1744162429157);
INSERT INTO `revinfo` VALUES (139, 1744166236739);
INSERT INTO `revinfo` VALUES (140, 1744167592957);
INSERT INTO `revinfo` VALUES (141, 1744173529699);
INSERT INTO `revinfo` VALUES (142, 1744181040612);
INSERT INTO `revinfo` VALUES (143, 1744184994355);
INSERT INTO `revinfo` VALUES (144, 1744188697739);
INSERT INTO `revinfo` VALUES (145, 1744189541990);
INSERT INTO `revinfo` VALUES (146, 1744196586793);
INSERT INTO `revinfo` VALUES (147, 1744201080690);
INSERT INTO `revinfo` VALUES (148, 1744201175097);
INSERT INTO `revinfo` VALUES (149, 1744201409743);
INSERT INTO `revinfo` VALUES (150, 1744205925705);
INSERT INTO `revinfo` VALUES (151, 1744207712785);
INSERT INTO `revinfo` VALUES (152, 1744207870586);
INSERT INTO `revinfo` VALUES (153, 1744207963029);
INSERT INTO `revinfo` VALUES (154, 1744208099171);
INSERT INTO `revinfo` VALUES (155, 1744209706154);
INSERT INTO `revinfo` VALUES (156, 1744209731107);
INSERT INTO `revinfo` VALUES (157, 1744209776815);
INSERT INTO `revinfo` VALUES (158, 1744248844406);
INSERT INTO `revinfo` VALUES (159, 1744252884686);
INSERT INTO `revinfo` VALUES (160, 1744260331333);
INSERT INTO `revinfo` VALUES (161, 1744267983460);
INSERT INTO `revinfo` VALUES (162, 1744271929354);
INSERT INTO `revinfo` VALUES (163, 1744275885330);
INSERT INTO `revinfo` VALUES (164, 1744283108253);
INSERT INTO `revinfo` VALUES (165, 1744284242102);
INSERT INTO `revinfo` VALUES (166, 1744284624280);
INSERT INTO `revinfo` VALUES (167, 1744284789567);
INSERT INTO `revinfo` VALUES (168, 1744285224069);
INSERT INTO `revinfo` VALUES (169, 1744286155532);
INSERT INTO `revinfo` VALUES (170, 1744290161552);
INSERT INTO `revinfo` VALUES (171, 1744296270800);
INSERT INTO `revinfo` VALUES (172, 1744334461478);
INSERT INTO `revinfo` VALUES (173, 1744335799999);
INSERT INTO `revinfo` VALUES (174, 1744337439114);
INSERT INTO `revinfo` VALUES (175, 1744337594334);
INSERT INTO `revinfo` VALUES (176, 1744337883624);
INSERT INTO `revinfo` VALUES (177, 1744339109504);
INSERT INTO `revinfo` VALUES (178, 1744341495760);
INSERT INTO `revinfo` VALUES (179, 1744346034876);
INSERT INTO `revinfo` VALUES (180, 1744355095705);
INSERT INTO `revinfo` VALUES (181, 1744356817080);
INSERT INTO `revinfo` VALUES (182, 1744357175571);
INSERT INTO `revinfo` VALUES (183, 1744357388967);
INSERT INTO `revinfo` VALUES (184, 1744357460226);
INSERT INTO `revinfo` VALUES (185, 1744357573485);
INSERT INTO `revinfo` VALUES (186, 1744361304664);
INSERT INTO `revinfo` VALUES (187, 1744368779665);
INSERT INTO `revinfo` VALUES (188, 1744372426704);
INSERT INTO `revinfo` VALUES (189, 1744376586093);
INSERT INTO `revinfo` VALUES (190, 1744380244856);
INSERT INTO `revinfo` VALUES (191, 1744384144792);
INSERT INTO `revinfo` VALUES (192, 1744422813826);
INSERT INTO `revinfo` VALUES (193, 1744426462428);
INSERT INTO `revinfo` VALUES (194, 1744432070448);
INSERT INTO `revinfo` VALUES (195, 1744433744631);
INSERT INTO `revinfo` VALUES (196, 1744433797646);
INSERT INTO `revinfo` VALUES (197, 1744433847391);
INSERT INTO `revinfo` VALUES (198, 1744433910848);
INSERT INTO `revinfo` VALUES (199, 1744441018949);
INSERT INTO `revinfo` VALUES (200, 1744441346766);
INSERT INTO `revinfo` VALUES (201, 1744443978871);
INSERT INTO `revinfo` VALUES (202, 1744444161388);
INSERT INTO `revinfo` VALUES (203, 1744446796146);
INSERT INTO `revinfo` VALUES (204, 1744450518440);
INSERT INTO `revinfo` VALUES (205, 1744450549171);
INSERT INTO `revinfo` VALUES (206, 1744455942074);
INSERT INTO `revinfo` VALUES (207, 1744459625763);
INSERT INTO `revinfo` VALUES (208, 1744460486865);
INSERT INTO `revinfo` VALUES (209, 1744464255382);
INSERT INTO `revinfo` VALUES (210, 1744468967908);
INSERT INTO `revinfo` VALUES (211, 1744469255618);
INSERT INTO `revinfo` VALUES (212, 1744469387181);
INSERT INTO `revinfo` VALUES (213, 1744469870156);
INSERT INTO `revinfo` VALUES (214, 1744470237719);
INSERT INTO `revinfo` VALUES (215, 1744470356105);
INSERT INTO `revinfo` VALUES (216, 1744470582102);
INSERT INTO `revinfo` VALUES (217, 1744470743456);
INSERT INTO `revinfo` VALUES (218, 1744470817617);
INSERT INTO `revinfo` VALUES (219, 1744470920719);
INSERT INTO `revinfo` VALUES (220, 1744510806727);
INSERT INTO `revinfo` VALUES (221, 1744510970988);
INSERT INTO `revinfo` VALUES (222, 1744511122623);
INSERT INTO `revinfo` VALUES (223, 1744515083665);
INSERT INTO `revinfo` VALUES (224, 1744519393635);
INSERT INTO `revinfo` VALUES (225, 1744526526828);
INSERT INTO `revinfo` VALUES (226, 1744530228845);
INSERT INTO `revinfo` VALUES (227, 1744533921563);
INSERT INTO `revinfo` VALUES (228, 1744535032648);
INSERT INTO `revinfo` VALUES (229, 1744535037197);
INSERT INTO `revinfo` VALUES (230, 1744535853432);
INSERT INTO `revinfo` VALUES (231, 1744535936194);
INSERT INTO `revinfo` VALUES (232, 1744535993384);
INSERT INTO `revinfo` VALUES (233, 1744536076662);
INSERT INTO `revinfo` VALUES (234, 1744536418633);
INSERT INTO `revinfo` VALUES (235, 1744536461555);
INSERT INTO `revinfo` VALUES (236, 1744536487076);
INSERT INTO `revinfo` VALUES (237, 1744536559673);
INSERT INTO `revinfo` VALUES (238, 1744542474908);
INSERT INTO `revinfo` VALUES (239, 1744543189452);
INSERT INTO `revinfo` VALUES (240, 1744543190957);
INSERT INTO `revinfo` VALUES (241, 1744543874196);
INSERT INTO `revinfo` VALUES (242, 1744545211341);
INSERT INTO `revinfo` VALUES (243, 1744545211462);
INSERT INTO `revinfo` VALUES (244, 1744545241911);
INSERT INTO `revinfo` VALUES (245, 1744545291600);
INSERT INTO `revinfo` VALUES (246, 1744546139657);
INSERT INTO `revinfo` VALUES (247, 1744546250685);
INSERT INTO `revinfo` VALUES (248, 1744546534768);
INSERT INTO `revinfo` VALUES (249, 1744546542594);
INSERT INTO `revinfo` VALUES (250, 1744546542641);
INSERT INTO `revinfo` VALUES (251, 1744546550016);
INSERT INTO `revinfo` VALUES (252, 1744546550069);
INSERT INTO `revinfo` VALUES (253, 1744547867820);
INSERT INTO `revinfo` VALUES (254, 1744550376255);
INSERT INTO `revinfo` VALUES (255, 1744550710264);
INSERT INTO `revinfo` VALUES (256, 1744551461366);
INSERT INTO `revinfo` VALUES (257, 1744551612277);
INSERT INTO `revinfo` VALUES (258, 1744551848429);
INSERT INTO `revinfo` VALUES (259, 1744551850261);
INSERT INTO `revinfo` VALUES (260, 1744551851293);
INSERT INTO `revinfo` VALUES (261, 1744551881740);
INSERT INTO `revinfo` VALUES (262, 1744551945317);
INSERT INTO `revinfo` VALUES (263, 1744551996615);
INSERT INTO `revinfo` VALUES (264, 1744552210934);
INSERT INTO `revinfo` VALUES (265, 1744552257565);
INSERT INTO `revinfo` VALUES (266, 1744552445541);
INSERT INTO `revinfo` VALUES (267, 1744552476227);
INSERT INTO `revinfo` VALUES (268, 1744552493965);
INSERT INTO `revinfo` VALUES (269, 1744552505914);
INSERT INTO `revinfo` VALUES (270, 1744552542046);
INSERT INTO `revinfo` VALUES (271, 1744552548733);
INSERT INTO `revinfo` VALUES (272, 1744552608442);
INSERT INTO `revinfo` VALUES (273, 1744552618935);
INSERT INTO `revinfo` VALUES (274, 1744552619596);
INSERT INTO `revinfo` VALUES (275, 1744552620931);
INSERT INTO `revinfo` VALUES (276, 1744552761689);
INSERT INTO `revinfo` VALUES (277, 1744552785476);
INSERT INTO `revinfo` VALUES (278, 1744552971296);
INSERT INTO `revinfo` VALUES (279, 1744552993078);
INSERT INTO `revinfo` VALUES (280, 1744553145648);
INSERT INTO `revinfo` VALUES (281, 1744553236580);
INSERT INTO `revinfo` VALUES (282, 1744553862121);
INSERT INTO `revinfo` VALUES (283, 1744554617688);
INSERT INTO `revinfo` VALUES (284, 1744554753129);
INSERT INTO `revinfo` VALUES (285, 1744555014225);
INSERT INTO `revinfo` VALUES (286, 1744557012541);
INSERT INTO `revinfo` VALUES (287, 1744599254132);
INSERT INTO `revinfo` VALUES (288, 1744599261400);
INSERT INTO `revinfo` VALUES (289, 1744599372839);
INSERT INTO `revinfo` VALUES (290, 1744599434690);
INSERT INTO `revinfo` VALUES (291, 1744599923700);
INSERT INTO `revinfo` VALUES (292, 1744599928928);
INSERT INTO `revinfo` VALUES (293, 1744600363410);
INSERT INTO `revinfo` VALUES (294, 1744600458027);
INSERT INTO `revinfo` VALUES (295, 1744600465472);
INSERT INTO `revinfo` VALUES (296, 1744600576951);
INSERT INTO `revinfo` VALUES (297, 1744600826579);
INSERT INTO `revinfo` VALUES (298, 1744600828614);
INSERT INTO `revinfo` VALUES (299, 1744600830269);
INSERT INTO `revinfo` VALUES (300, 1744600832458);
INSERT INTO `revinfo` VALUES (301, 1744600833547);
INSERT INTO `revinfo` VALUES (302, 1744600834640);
INSERT INTO `revinfo` VALUES (303, 1744600836605);
INSERT INTO `revinfo` VALUES (304, 1744600837628);
INSERT INTO `revinfo` VALUES (305, 1744600838624);
INSERT INTO `revinfo` VALUES (306, 1744600839316);
INSERT INTO `revinfo` VALUES (307, 1744600892503);
INSERT INTO `revinfo` VALUES (308, 1744600893095);
INSERT INTO `revinfo` VALUES (309, 1744600893967);
INSERT INTO `revinfo` VALUES (310, 1744600894956);
INSERT INTO `revinfo` VALUES (311, 1744600895786);
INSERT INTO `revinfo` VALUES (312, 1744601489864);
INSERT INTO `revinfo` VALUES (313, 1744601597078);
INSERT INTO `revinfo` VALUES (314, 1744601605272);
INSERT INTO `revinfo` VALUES (315, 1744601606410);
INSERT INTO `revinfo` VALUES (316, 1744601712750);
INSERT INTO `revinfo` VALUES (317, 1744601713539);
INSERT INTO `revinfo` VALUES (318, 1744601715068);
INSERT INTO `revinfo` VALUES (319, 1744601715851);
INSERT INTO `revinfo` VALUES (320, 1744614465191);
INSERT INTO `revinfo` VALUES (321, 1744614486137);
INSERT INTO `revinfo` VALUES (322, 1744614512951);
INSERT INTO `revinfo` VALUES (323, 1744617996431);
INSERT INTO `revinfo` VALUES (324, 1744618271414);
INSERT INTO `revinfo` VALUES (325, 1744619010651);
INSERT INTO `revinfo` VALUES (326, 1744619011739);
INSERT INTO `revinfo` VALUES (327, 1744620705310);
INSERT INTO `revinfo` VALUES (328, 1744620706195);
INSERT INTO `revinfo` VALUES (329, 1744621918001);
INSERT INTO `revinfo` VALUES (330, 1744621935768);
INSERT INTO `revinfo` VALUES (331, 1744621936902);
INSERT INTO `revinfo` VALUES (332, 1744621937833);
INSERT INTO `revinfo` VALUES (333, 1744621939067);
INSERT INTO `revinfo` VALUES (334, 1744621940180);
INSERT INTO `revinfo` VALUES (335, 1744621941722);
INSERT INTO `revinfo` VALUES (336, 1744621944524);
INSERT INTO `revinfo` VALUES (337, 1744622014058);
INSERT INTO `revinfo` VALUES (338, 1744622016146);
INSERT INTO `revinfo` VALUES (339, 1744622749541);
INSERT INTO `revinfo` VALUES (340, 1744627990092);
INSERT INTO `revinfo` VALUES (341, 1744632994671);
INSERT INTO `revinfo` VALUES (342, 1744634315229);
INSERT INTO `revinfo` VALUES (343, 1744635921526);
INSERT INTO `revinfo` VALUES (344, 1744637095479);
INSERT INTO `revinfo` VALUES (345, 1744637156636);
INSERT INTO `revinfo` VALUES (346, 1744683041298);
INSERT INTO `revinfo` VALUES (347, 1744684321623);
INSERT INTO `revinfo` VALUES (348, 1744700458245);
INSERT INTO `revinfo` VALUES (349, 1744701570271);
INSERT INTO `revinfo` VALUES (350, 1744703635903);
INSERT INTO `revinfo` VALUES (351, 1744703686064);
INSERT INTO `revinfo` VALUES (352, 1744704000642);
INSERT INTO `revinfo` VALUES (353, 1744704294398);
INSERT INTO `revinfo` VALUES (354, 1744704313854);
INSERT INTO `revinfo` VALUES (355, 1744704529444);
INSERT INTO `revinfo` VALUES (356, 1744705136709);
INSERT INTO `revinfo` VALUES (357, 1744705332640);
INSERT INTO `revinfo` VALUES (358, 1744705426485);
INSERT INTO `revinfo` VALUES (359, 1744706786342);
INSERT INTO `revinfo` VALUES (360, 1744725556038);
INSERT INTO `revinfo` VALUES (361, 1744729539029);
INSERT INTO `revinfo` VALUES (362, 1744778713403);
INSERT INTO `revinfo` VALUES (363, 1744786737509);
INSERT INTO `revinfo` VALUES (364, 1744788348211);
INSERT INTO `revinfo` VALUES (365, 1744788441617);
INSERT INTO `revinfo` VALUES (366, 1744801332081);
INSERT INTO `revinfo` VALUES (367, 1744803130777);
INSERT INTO `revinfo` VALUES (368, 1744804178321);
INSERT INTO `revinfo` VALUES (369, 1744805073369);
INSERT INTO `revinfo` VALUES (370, 1744805094835);
INSERT INTO `revinfo` VALUES (371, 1744805185330);
INSERT INTO `revinfo` VALUES (372, 1744805846598);
INSERT INTO `revinfo` VALUES (373, 1744864972342);
INSERT INTO `revinfo` VALUES (374, 1744872897127);
INSERT INTO `revinfo` VALUES (375, 1744873620181);
INSERT INTO `revinfo` VALUES (376, 1744875801636);
INSERT INTO `revinfo` VALUES (377, 1744876220634);
INSERT INTO `revinfo` VALUES (378, 1744877368595);
INSERT INTO `revinfo` VALUES (379, 1744889524832);
INSERT INTO `revinfo` VALUES (380, 1744890738301);
INSERT INTO `revinfo` VALUES (381, 1744891361503);
INSERT INTO `revinfo` VALUES (382, 1744892773295);
INSERT INTO `revinfo` VALUES (383, 1744893620721);
INSERT INTO `revinfo` VALUES (384, 1744893805384);
INSERT INTO `revinfo` VALUES (385, 1744893894186);
INSERT INTO `revinfo` VALUES (386, 1744893938629);
INSERT INTO `revinfo` VALUES (387, 1744894160534);
INSERT INTO `revinfo` VALUES (388, 1744894358527);
INSERT INTO `revinfo` VALUES (389, 1744894586533);
INSERT INTO `revinfo` VALUES (390, 1744898269415);
INSERT INTO `revinfo` VALUES (391, 1744899543512);
INSERT INTO `revinfo` VALUES (392, 1744899891730);
INSERT INTO `revinfo` VALUES (393, 1744900041632);
INSERT INTO `revinfo` VALUES (394, 1744900572456);
INSERT INTO `revinfo` VALUES (395, 1744900653343);
INSERT INTO `revinfo` VALUES (396, 1744900884315);
INSERT INTO `revinfo` VALUES (397, 1744941302446);
INSERT INTO `revinfo` VALUES (398, 1744942905028);
INSERT INTO `revinfo` VALUES (399, 1744943213998);
INSERT INTO `revinfo` VALUES (400, 1744943678253);
INSERT INTO `revinfo` VALUES (401, 1744944004785);
INSERT INTO `revinfo` VALUES (402, 1744944866667);
INSERT INTO `revinfo` VALUES (403, 1744945881846);
INSERT INTO `revinfo` VALUES (404, 1744946671844);
INSERT INTO `revinfo` VALUES (405, 1744946675030);
INSERT INTO `revinfo` VALUES (406, 1744946901555);
INSERT INTO `revinfo` VALUES (407, 1744946916245);
INSERT INTO `revinfo` VALUES (408, 1744947464946);
INSERT INTO `revinfo` VALUES (409, 1744947488617);
INSERT INTO `revinfo` VALUES (410, 1744947565123);
INSERT INTO `revinfo` VALUES (411, 1744947581269);
INSERT INTO `revinfo` VALUES (412, 1744947630322);
INSERT INTO `revinfo` VALUES (413, 1744947644612);
INSERT INTO `revinfo` VALUES (414, 1744947709996);
INSERT INTO `revinfo` VALUES (415, 1744947827839);
INSERT INTO `revinfo` VALUES (416, 1744948046588);
INSERT INTO `revinfo` VALUES (417, 1744948114115);
INSERT INTO `revinfo` VALUES (418, 1744948207979);
INSERT INTO `revinfo` VALUES (419, 1744952043643);
INSERT INTO `revinfo` VALUES (420, 1744954835584);
INSERT INTO `revinfo` VALUES (421, 1744959999570);
INSERT INTO `revinfo` VALUES (422, 1744960004318);
INSERT INTO `revinfo` VALUES (423, 1744960171196);
INSERT INTO `revinfo` VALUES (424, 1744961322486);
INSERT INTO `revinfo` VALUES (425, 1744962143976);
INSERT INTO `revinfo` VALUES (426, 1744967669070);
INSERT INTO `revinfo` VALUES (427, 1745033326033);
INSERT INTO `revinfo` VALUES (428, 1745047090802);
INSERT INTO `revinfo` VALUES (429, 1745115678652);
INSERT INTO `revinfo` VALUES (430, 1745115855258);
INSERT INTO `revinfo` VALUES (431, 1745116436786);
INSERT INTO `revinfo` VALUES (432, 1745133505809);
INSERT INTO `revinfo` VALUES (433, 1745133628342);
INSERT INTO `revinfo` VALUES (434, 1745133635770);
INSERT INTO `revinfo` VALUES (435, 1745135932700);
INSERT INTO `revinfo` VALUES (436, 1745155128504);
INSERT INTO `revinfo` VALUES (437, 1745155313778);
INSERT INTO `revinfo` VALUES (438, 1745198541709);
INSERT INTO `revinfo` VALUES (439, 1745202459561);
INSERT INTO `revinfo` VALUES (440, 1745207598379);
INSERT INTO `revinfo` VALUES (441, 1745220804910);
INSERT INTO `revinfo` VALUES (442, 1745220855194);
INSERT INTO `revinfo` VALUES (443, 1745240470776);
INSERT INTO `revinfo` VALUES (444, 1745374068819);
INSERT INTO `revinfo` VALUES (445, 1745401086360);
INSERT INTO `revinfo` VALUES (446, 1745407778750);
INSERT INTO `revinfo` VALUES (447, 1745408421583);
INSERT INTO `revinfo` VALUES (448, 1745409348382);
INSERT INTO `revinfo` VALUES (449, 1745413956474);
INSERT INTO `revinfo` VALUES (450, 1745414103484);
INSERT INTO `revinfo` VALUES (451, 1745414103646);
INSERT INTO `revinfo` VALUES (452, 1745414541708);
INSERT INTO `revinfo` VALUES (453, 1745415230511);
INSERT INTO `revinfo` VALUES (454, 1745416284701);
INSERT INTO `revinfo` VALUES (455, 1745416303706);
INSERT INTO `revinfo` VALUES (456, 1745416663636);
INSERT INTO `revinfo` VALUES (457, 1745417162428);
INSERT INTO `revinfo` VALUES (458, 1745459419992);
INSERT INTO `revinfo` VALUES (459, 1745460057435);
INSERT INTO `revinfo` VALUES (460, 1745460160901);
INSERT INTO `revinfo` VALUES (461, 1745460793729);
INSERT INTO `revinfo` VALUES (462, 1745461129943);
INSERT INTO `revinfo` VALUES (463, 1745461351518);
INSERT INTO `revinfo` VALUES (464, 1745461635835);
INSERT INTO `revinfo` VALUES (465, 1745461710862);
INSERT INTO `revinfo` VALUES (466, 1745461941488);
INSERT INTO `revinfo` VALUES (467, 1745462008691);
INSERT INTO `revinfo` VALUES (468, 1745462136456);
INSERT INTO `revinfo` VALUES (469, 1745462774810);
INSERT INTO `revinfo` VALUES (470, 1745462793899);
INSERT INTO `revinfo` VALUES (471, 1745462896150);
INSERT INTO `revinfo` VALUES (472, 1745463820380);
INSERT INTO `revinfo` VALUES (473, 1745464759572);
INSERT INTO `revinfo` VALUES (474, 1745465136912);
INSERT INTO `revinfo` VALUES (475, 1745465405815);
INSERT INTO `revinfo` VALUES (476, 1745470197030);
INSERT INTO `revinfo` VALUES (477, 1745477632927);
INSERT INTO `revinfo` VALUES (478, 1745477893612);
INSERT INTO `revinfo` VALUES (479, 1745478244825);
INSERT INTO `revinfo` VALUES (480, 1745478269525);
INSERT INTO `revinfo` VALUES (481, 1745478389450);
INSERT INTO `revinfo` VALUES (482, 1745481717627);
INSERT INTO `revinfo` VALUES (483, 1745486103079);
INSERT INTO `revinfo` VALUES (484, 1745492755562);
INSERT INTO `revinfo` VALUES (485, 1745499226104);
INSERT INTO `revinfo` VALUES (486, 1745499241246);
INSERT INTO `revinfo` VALUES (487, 1745499431601);
INSERT INTO `revinfo` VALUES (488, 1745499690524);
INSERT INTO `revinfo` VALUES (489, 1745499793967);
INSERT INTO `revinfo` VALUES (490, 1745499795670);
INSERT INTO `revinfo` VALUES (491, 1745499800032);
INSERT INTO `revinfo` VALUES (492, 1745499800124);
INSERT INTO `revinfo` VALUES (493, 1745500053651);
INSERT INTO `revinfo` VALUES (494, 1745500073898);
INSERT INTO `revinfo` VALUES (495, 1745500117315);
INSERT INTO `revinfo` VALUES (496, 1745500118372);
INSERT INTO `revinfo` VALUES (497, 1745506676307);
INSERT INTO `revinfo` VALUES (498, 1745544960943);
INSERT INTO `revinfo` VALUES (499, 1745545038822);
INSERT INTO `revinfo` VALUES (500, 1745545138706);
INSERT INTO `revinfo` VALUES (501, 1745545348202);
INSERT INTO `revinfo` VALUES (502, 1745547010657);
INSERT INTO `revinfo` VALUES (503, 1745550782106);
INSERT INTO `revinfo` VALUES (504, 1745551402720);
INSERT INTO `revinfo` VALUES (505, 1745551616822);
INSERT INTO `revinfo` VALUES (506, 1745563663359);
INSERT INTO `revinfo` VALUES (507, 1745564332660);
INSERT INTO `revinfo` VALUES (508, 1745567724148);
INSERT INTO `revinfo` VALUES (509, 1745568057383);
INSERT INTO `revinfo` VALUES (510, 1745568357789);
INSERT INTO `revinfo` VALUES (511, 1745568544617);
INSERT INTO `revinfo` VALUES (512, 1745571774576);
INSERT INTO `revinfo` VALUES (513, 1745572660978);
INSERT INTO `revinfo` VALUES (514, 1745579913167);
INSERT INTO `revinfo` VALUES (515, 1745580087939);
INSERT INTO `revinfo` VALUES (516, 1745580138442);
INSERT INTO `revinfo` VALUES (517, 1745580547274);
INSERT INTO `revinfo` VALUES (518, 1745581160155);
INSERT INTO `revinfo` VALUES (519, 1745581160243);
INSERT INTO `revinfo` VALUES (520, 1745581172965);
INSERT INTO `revinfo` VALUES (521, 1745583711454);
INSERT INTO `revinfo` VALUES (522, 1745584720681);
INSERT INTO `revinfo` VALUES (523, 1745584987639);
INSERT INTO `revinfo` VALUES (524, 1745585216620);
INSERT INTO `revinfo` VALUES (525, 1745585236744);
INSERT INTO `revinfo` VALUES (526, 1745586012819);
INSERT INTO `revinfo` VALUES (527, 1745586358040);
INSERT INTO `revinfo` VALUES (528, 1745586522476);
INSERT INTO `revinfo` VALUES (529, 1745586974210);
INSERT INTO `revinfo` VALUES (530, 1745587028150);
INSERT INTO `revinfo` VALUES (531, 1745587039139);
INSERT INTO `revinfo` VALUES (532, 1745587497760);
INSERT INTO `revinfo` VALUES (533, 1745589006481);
INSERT INTO `revinfo` VALUES (534, 1745589299860);
INSERT INTO `revinfo` VALUES (535, 1745589853428);
INSERT INTO `revinfo` VALUES (536, 1745593678262);
INSERT INTO `revinfo` VALUES (537, 1745595107740);
INSERT INTO `revinfo` VALUES (538, 1745595116605);
INSERT INTO `revinfo` VALUES (539, 1745596113579);
INSERT INTO `revinfo` VALUES (540, 1745596120802);
INSERT INTO `revinfo` VALUES (541, 1745596150594);
INSERT INTO `revinfo` VALUES (542, 1745596187111);
INSERT INTO `revinfo` VALUES (543, 1745630134690);
INSERT INTO `revinfo` VALUES (544, 1745630144407);
INSERT INTO `revinfo` VALUES (545, 1745630574153);
INSERT INTO `revinfo` VALUES (546, 1745630575251);
INSERT INTO `revinfo` VALUES (547, 1745631055307);
INSERT INTO `revinfo` VALUES (548, 1745631104832);
INSERT INTO `revinfo` VALUES (549, 1745631106281);
INSERT INTO `revinfo` VALUES (550, 1745631330754);
INSERT INTO `revinfo` VALUES (551, 1745631331818);
INSERT INTO `revinfo` VALUES (552, 1745631426737);
INSERT INTO `revinfo` VALUES (553, 1745631428714);
INSERT INTO `revinfo` VALUES (554, 1745631667337);
INSERT INTO `revinfo` VALUES (555, 1745631668199);
INSERT INTO `revinfo` VALUES (556, 1745631668934);
INSERT INTO `revinfo` VALUES (557, 1745631674278);
INSERT INTO `revinfo` VALUES (558, 1745631674323);
INSERT INTO `revinfo` VALUES (559, 1745631675434);
INSERT INTO `revinfo` VALUES (560, 1745631808241);
INSERT INTO `revinfo` VALUES (561, 1745631808277);
INSERT INTO `revinfo` VALUES (562, 1745631809543);
INSERT INTO `revinfo` VALUES (563, 1745631945405);
INSERT INTO `revinfo` VALUES (564, 1745631948747);
INSERT INTO `revinfo` VALUES (565, 1745631948780);
INSERT INTO `revinfo` VALUES (566, 1745631966164);
INSERT INTO `revinfo` VALUES (567, 1745632136267);
INSERT INTO `revinfo` VALUES (568, 1745632136333);
INSERT INTO `revinfo` VALUES (569, 1745632137404);
INSERT INTO `revinfo` VALUES (570, 1745632140334);
INSERT INTO `revinfo` VALUES (571, 1745632140376);
INSERT INTO `revinfo` VALUES (572, 1745632366269);
INSERT INTO `revinfo` VALUES (573, 1745632388537);
INSERT INTO `revinfo` VALUES (574, 1745636366745);
INSERT INTO `revinfo` VALUES (575, 1745637565230);
INSERT INTO `revinfo` VALUES (576, 1745638404611);
INSERT INTO `revinfo` VALUES (577, 1745655364162);
INSERT INTO `revinfo` VALUES (578, 1745658212600);
INSERT INTO `revinfo` VALUES (579, 1745660044047);
INSERT INTO `revinfo` VALUES (580, 1745665582213);
INSERT INTO `revinfo` VALUES (581, 1745674366330);
INSERT INTO `revinfo` VALUES (582, 1745680169724);
INSERT INTO `revinfo` VALUES (583, 1745714930618);
INSERT INTO `revinfo` VALUES (584, 1745716585186);
INSERT INTO `revinfo` VALUES (585, 1745716642317);
INSERT INTO `revinfo` VALUES (586, 1745716665391);
INSERT INTO `revinfo` VALUES (587, 1745716900955);
INSERT INTO `revinfo` VALUES (588, 1745717232996);
INSERT INTO `revinfo` VALUES (589, 1745717334406);
INSERT INTO `revinfo` VALUES (590, 1745717462843);
INSERT INTO `revinfo` VALUES (591, 1745718326161);
INSERT INTO `revinfo` VALUES (592, 1745721949099);
INSERT INTO `revinfo` VALUES (593, 1745724518066);
INSERT INTO `revinfo` VALUES (594, 1745724719240);
INSERT INTO `revinfo` VALUES (595, 1745725056729);
INSERT INTO `revinfo` VALUES (596, 1745725152603);
INSERT INTO `revinfo` VALUES (597, 1745725193500);
INSERT INTO `revinfo` VALUES (598, 1745725353264);
INSERT INTO `revinfo` VALUES (599, 1745727954194);
INSERT INTO `revinfo` VALUES (600, 1745728980150);
INSERT INTO `revinfo` VALUES (601, 1745729027824);
INSERT INTO `revinfo` VALUES (602, 1745729479564);
INSERT INTO `revinfo` VALUES (603, 1745729603444);
INSERT INTO `revinfo` VALUES (604, 1745729618337);
INSERT INTO `revinfo` VALUES (605, 1745729618376);
INSERT INTO `revinfo` VALUES (606, 1745729619774);
INSERT INTO `revinfo` VALUES (607, 1745729622153);
INSERT INTO `revinfo` VALUES (608, 1745729672130);
INSERT INTO `revinfo` VALUES (609, 1745729675253);
INSERT INTO `revinfo` VALUES (610, 1745729688363);
INSERT INTO `revinfo` VALUES (611, 1745733557146);
INSERT INTO `revinfo` VALUES (612, 1745733733516);
INSERT INTO `revinfo` VALUES (613, 1745744324524);
INSERT INTO `revinfo` VALUES (614, 1745752511143);
INSERT INTO `revinfo` VALUES (615, 1745755693546);
INSERT INTO `revinfo` VALUES (616, 1745755740412);
INSERT INTO `revinfo` VALUES (617, 1745755741599);
INSERT INTO `revinfo` VALUES (618, 1745755766563);
INSERT INTO `revinfo` VALUES (619, 1745755772544);
INSERT INTO `revinfo` VALUES (620, 1745755796553);
INSERT INTO `revinfo` VALUES (621, 1745756804768);
INSERT INTO `revinfo` VALUES (622, 1745757586443);
INSERT INTO `revinfo` VALUES (623, 1745757587547);
INSERT INTO `revinfo` VALUES (624, 1745757588616);
INSERT INTO `revinfo` VALUES (625, 1745757620107);
INSERT INTO `revinfo` VALUES (626, 1745757623331);
INSERT INTO `revinfo` VALUES (627, 1745757631969);
INSERT INTO `revinfo` VALUES (628, 1745757731477);
INSERT INTO `revinfo` VALUES (629, 1745758433928);
INSERT INTO `revinfo` VALUES (630, 1745767037559);
INSERT INTO `revinfo` VALUES (631, 1745805263223);
INSERT INTO `revinfo` VALUES (632, 1745807442560);
INSERT INTO `revinfo` VALUES (633, 1745807502296);
INSERT INTO `revinfo` VALUES (634, 1745808173562);
INSERT INTO `revinfo` VALUES (635, 1745810711062);
INSERT INTO `revinfo` VALUES (636, 1745811855724);
INSERT INTO `revinfo` VALUES (637, 1745823086857);
INSERT INTO `revinfo` VALUES (638, 1745826699888);

SET FOREIGN_KEY_CHECKS = 1;
