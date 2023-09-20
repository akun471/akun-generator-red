/*
 Navicat Premium Data Transfer

 Source Server         : localhost_5.7
 Source Server Type    : MySQL
 Source Server Version : 50738
 Source Host           : localhost:3306
 Source Schema         : dynamic_data_source

 Target Server Type    : MySQL
 Target Server Version : 50738
 File Encoding         : 65001

 Date: 03/11/2022 14:14:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for data_source_entity
-- ----------------------------
DROP TABLE IF EXISTS `data_source_entity`;
CREATE TABLE `data_source_entity`  (
  `id` int(10) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT,
  `poll_name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `type` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `driver_class_name` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `url` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  `config_id` int(10) NULL DEFAULT NULL,
  `state` tinyint(4) NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  `update_time` datetime(0) NULL DEFAULT NULL,
  `desc` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of data_source_entity
-- ----------------------------
INSERT INTO `data_source_entity` VALUES (0000000001, 'bsnflowdb', NULL, 'com.mysql.jdbc.Driver', 'jdbc:mysql://192.168.1.61:3306/bsnflowdb?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT%2B8&allowMultiQueries=true&useSSL=false', 'root', '123456', NULL, 1, '2022-07-27 07:00:02', NULL, NULL);
INSERT INTO `data_source_entity` VALUES (0000000002, '60bsnflowdb', NULL, 'com.mysql.jdbc.Driver', 'jdbc:mysql://192.168.1.60:3306/bsnflowdb?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT%2B8&allowMultiQueries=true&useSSL=false', 'root', '123456', NULL, 1, '2022-07-27 07:00:02', NULL, NULL);
INSERT INTO `data_source_entity` VALUES (0000000003, '176regulatory', NULL, 'com.mysql.jdbc.Driver', 'jdbc:mysql://192.168.1.176:3306/regulatory?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT%2B8&allowMultiQueries=true&useSSL=false', 'root', '123456', NULL, 1, NULL, NULL, 'regulatory');
INSERT INTO `data_source_entity` VALUES (0000000004, 'localhost', NULL, 'com.mysql.jdbc.Driver', 'jdbc:mysql://127.0.0.1:3306/temp?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT%2B8&allowMultiQueries=true&useSSL=false', 'root', '123456', NULL, 1, NULL, NULL, 'regulatory');
INSERT INTO `data_source_entity` VALUES (0000000005, 'bcmp_test', NULL, 'com.mysql.jdbc.Driver', 'jdbc:mysql://192.168.1.61:3306/bcmp_test?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&serverTimezone=GMT%2B8&allowMultiQueries=true&useSSL=false', 'root', '123456', NULL, 1, NULL, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
