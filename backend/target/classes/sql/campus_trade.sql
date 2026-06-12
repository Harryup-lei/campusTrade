/*
 Navicat Premium Dump SQL

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 80029 (8.0.29)
 Source Host           : localhost:3306
 Source Schema         : campus_trade

 Target Server Type    : MySQL
 Target Server Version : 80029 (8.0.29)
 File Encoding         : 65001

 Date: 19/12/2025 14:07:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for announcement
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '公告内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'NORMAL' COMMENT '公告类型：HOT-热门，NEW-最新，NORMAL-普通',
  `priority` int NULL DEFAULT 0 COMMENT '优先级，数字越大越靠前',
  `link_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跳转链接',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用，INACTIVE-停用',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_priority`(`priority` ASC) USING BTREE,
  INDEX `idx_time`(`start_time` ASC, `end_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '平台公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcement
-- ----------------------------
INSERT INTO `announcement` VALUES (1, '防诈骗安全提醒须知', '请各位同学注意交易安全，不要轻信线下转账，使用平台担保交易更安全。遇到可疑情况请及时联系管理员。', 'HOT', 100, '', 'ACTIVE', NULL, NULL, 5, '2025-12-04 22:56:39', '2025-12-05 10:31:33');
INSERT INTO `announcement` VALUES (2, '平台交易规则更新公告', '为了更好地保护买卖双方权益，平台交易规则进行了优化更新，请大家认真阅读并遵守。', 'NEW', 90, NULL, 'ACTIVE', NULL, NULL, 1, '2025-12-04 22:56:39', '2025-12-04 22:56:39');
INSERT INTO `announcement` VALUES (3, '\"毕业季\"主题活动开启', '毕业季来临，学长学姐的闲置物品低价出售，欢迎大家选购。活动期间免手续费！', 'NEW', 80, NULL, 'ACTIVE', NULL, NULL, 0, '2025-12-04 22:56:39', '2025-12-04 22:56:39');
INSERT INTO `announcement` VALUES (4, '信用积分规则说明', '平台信用积分体系正式上线，诚信交易可获得积分奖励，违规行为将扣除信用分。', 'NORMAL', 70, NULL, 'ACTIVE', NULL, NULL, 0, '2025-12-04 22:56:39', '2025-12-04 22:56:39');
INSERT INTO `announcement` VALUES (5, '平台升级维护通知', '平台将于本周六凌晨2:00-4:00进行系统升级维护，期间暂停服务，请大家合理安排时间。', 'NORMAL', 60, NULL, 'ACTIVE', NULL, NULL, 0, '2025-12-04 22:56:39', '2025-12-04 22:56:39');

-- ----------------------------
-- Table structure for banner
-- ----------------------------
DROP TABLE IF EXISTS `banner`;
CREATE TABLE `banner`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `subtitle` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '副标题',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '图片URL',
  `link_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跳转链接',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序顺序，数字越小越靠前',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用，INACTIVE-停用',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE,
  INDEX `idx_time`(`start_time` ASC, `end_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '轮播图表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of banner
-- ----------------------------
INSERT INTO `banner` VALUES (1, '校园二手，值得信赖', '安全交易，诚信为本', 'https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=1200', '/products', 1, 'ACTIVE', NULL, NULL, '2025-12-04 22:37:48', '2025-12-04 22:37:48');
INSERT INTO `banner` VALUES (2, '毕业季清仓大促', '全场低价，先到先得', 'https://images.unsplash.com/photo-1556740738-b6a63e27c4df?w=1200', '/products?tag=毕业季', 2, 'ACTIVE', NULL, NULL, '2025-12-04 22:37:48', '2025-12-04 22:37:48');
INSERT INTO `banner` VALUES (3, '手机电脑平板，应有尽有', '品质保证，放心购买', 'https://imgs.699pic.com/images/501/108/486.jpg!list1x.v2', '/products?categoryId=1', 3, 'ACTIVE', NULL, NULL, '2025-12-04 22:37:48', NULL);

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类描述',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类图标',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` int NULL DEFAULT 1 COMMENT '状态：0禁用，1启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '电子产品', '手机、电脑、平板等电子设备', NULL, 1, 1, '2025-12-03 09:41:27', '2025-12-03 09:41:27');
INSERT INTO `category` VALUES (2, '图书教材', '各类图书、教材、教辅资料', NULL, 2, 1, '2025-12-03 09:41:27', '2025-12-03 09:41:27');
INSERT INTO `category` VALUES (3, '生活用品', '日常生活用品、文具等', NULL, 3, 1, '2025-12-03 09:41:27', '2025-12-03 09:41:27');
INSERT INTO `category` VALUES (4, '运动器材', '运动装备、健身器材等', NULL, 4, 1, '2025-12-03 09:41:27', '2025-12-03 09:41:27');
INSERT INTO `category` VALUES (5, '其他', '其他类别商品', NULL, 5, 1, '2025-12-03 09:41:27', '2025-12-03 09:41:27');
INSERT INTO `category` VALUES (6, '测试分类', '测试', NULL, 0, 1, '2025-12-03 18:47:08', '2025-12-03 18:47:08');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父评论ID',
  `status` int NULL DEFAULT 0 COMMENT '状态：0待审核，1已通过，2已拒绝',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '留言评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, 1, 2, '这个商品还在吗？成色怎么样？', NULL, 0, '2025-12-03 20:05:10');
INSERT INTO `comment` VALUES (2, 1, 3, '在的，成色很好，基本没怎么用过', 1, 0, '2025-12-03 20:35:10');
INSERT INTO `comment` VALUES (3, 4, 8, '你这个能便宜吗？\n', NULL, 0, '2025-12-03 21:07:31');
INSERT INTO `comment` VALUES (4, 2, 8, '熊猫也能卖？？？？', NULL, 0, '2025-12-03 21:09:44');
INSERT INTO `comment` VALUES (5, 6, 7, '很好的！！！', NULL, 0, '2025-12-04 20:48:10');

-- ----------------------------
-- Table structure for credit_record
-- ----------------------------
DROP TABLE IF EXISTS `credit_record`;
CREATE TABLE `credit_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `score_change` int NOT NULL COMMENT '分数变化（正数为增加，负数为减少）',
  `reason` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '变更原因',
  `reason_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原因类型：TRADE/REVIEW/BEHAVIOR/VIOLATION/TASK',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联业务ID（如订单ID、商品ID等）',
  `before_score` int NOT NULL COMMENT '变更前分数',
  `after_score` int NOT NULL COMMENT '变更后分数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_reason_type`(`reason_type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 39 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '信用记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of credit_record
-- ----------------------------
INSERT INTO `credit_record` VALUES (1, 1, 10, '完成首次交易', 'TRADE', NULL, 100, 110, '2025-12-04 20:16:37');
INSERT INTO `credit_record` VALUES (2, 1, 5, '发布优质商品', 'BEHAVIOR', NULL, 110, 115, '2025-12-04 20:16:37');
INSERT INTO `credit_record` VALUES (3, 1, 10, '获得好评', 'REVIEW', NULL, 115, 125, '2025-12-04 20:16:37');
INSERT INTO `credit_record` VALUES (4, 1, -5, '取消订单', 'VIOLATION', NULL, 125, 120, '2025-12-04 20:16:37');
INSERT INTO `credit_record` VALUES (11, 8, 2, '每日签到', 'TASK', NULL, 20, 22, '2025-12-04 20:41:01');
INSERT INTO `credit_record` VALUES (12, 8, 2, '每日登录', 'TASK', NULL, 22, 24, '2025-12-04 20:41:21');
INSERT INTO `credit_record` VALUES (13, 8, -5, '取消订单', 'VIOLATION', 8, 24, 19, '2025-12-04 20:42:36');
INSERT INTO `credit_record` VALUES (14, 8, 10, '完成交易', 'TRADE', 9, 19, 29, '2025-12-04 20:44:12');
INSERT INTO `credit_record` VALUES (15, 1, 10, '完成交易', 'TRADE', 9, 127, 137, '2025-12-04 20:44:12');
INSERT INTO `credit_record` VALUES (16, 8, 5, '发布商品', 'BEHAVIOR', 6, 29, 34, '2025-12-04 20:45:17');
INSERT INTO `credit_record` VALUES (17, 7, 2, '每日登录', 'TASK', NULL, 100, 102, '2025-12-04 20:47:57');
INSERT INTO `credit_record` VALUES (18, 8, 10, '获得好评', 'REVIEW', 5, 34, 44, '2025-12-04 20:48:10');
INSERT INTO `credit_record` VALUES (19, 8, 20, '实名认证', 'TASK', NULL, 44, 64, '2025-12-04 20:51:50');
INSERT INTO `credit_record` VALUES (20, 8, 10, '完善资料', 'TASK', NULL, 64, 74, '2025-12-04 20:51:56');
INSERT INTO `credit_record` VALUES (21, 7, 2, '每日签到', 'TASK', NULL, 102, 104, '2025-12-04 20:57:04');
INSERT INTO `credit_record` VALUES (22, 7, 20, '实名认证', 'TASK', NULL, 104, 124, '2025-12-04 21:00:37');
INSERT INTO `credit_record` VALUES (23, 1, 2, '每日登录', 'TASK', NULL, 137, 139, '2025-12-04 21:04:57');
INSERT INTO `credit_record` VALUES (24, 8, 26, '[管理员调整] cs', 'ADMIN', NULL, 74, 100, '2025-12-04 21:07:05');
INSERT INTO `credit_record` VALUES (25, 6, 2, '每日登录', 'TASK', NULL, 100, 102, '2025-12-04 21:08:13');
INSERT INTO `credit_record` VALUES (26, 8, 20, '实名认证', 'TASK', NULL, 100, 120, '2025-12-04 21:17:44');
INSERT INTO `credit_record` VALUES (27, 6, 5, '发布商品', 'BEHAVIOR', 7, 102, 107, '2025-12-04 21:28:58');
INSERT INTO `credit_record` VALUES (28, 1, 2, '每日登录', 'TASK', NULL, 139, 141, '2025-12-05 09:02:10');
INSERT INTO `credit_record` VALUES (29, 8, 2, '每日登录', 'TASK', NULL, 120, 122, '2025-12-05 09:13:08');
INSERT INTO `credit_record` VALUES (30, 8, 2, '每日签到', 'TASK', NULL, 122, 124, '2025-12-05 09:13:27');
INSERT INTO `credit_record` VALUES (31, 3, 5, '完成交易', 'TRADE', 3, 100, 105, '2025-12-05 10:05:00');
INSERT INTO `credit_record` VALUES (32, 1, 10, '完成交易', 'TRADE', 3, 141, 151, '2025-12-05 10:05:00');
INSERT INTO `credit_record` VALUES (33, 9, 2, '每日登录', 'TASK', NULL, 100, 102, '2025-12-05 16:19:11');
INSERT INTO `credit_record` VALUES (34, 9, 2, '每日签到', 'TASK', NULL, 102, 104, '2025-12-05 16:19:25');
INSERT INTO `credit_record` VALUES (35, 9, 20, '实名认证', 'TASK', NULL, 104, 124, '2025-12-05 16:19:38');
INSERT INTO `credit_record` VALUES (36, 9, 5, '发布商品', 'BEHAVIOR', 8, 124, 129, '2025-12-05 16:20:24');
INSERT INTO `credit_record` VALUES (37, 8, 5, '完成交易', 'TRADE', 10, 124, 129, '2025-12-05 16:22:22');
INSERT INTO `credit_record` VALUES (38, 9, 10, '完成交易', 'TRADE', 10, 129, 139, '2025-12-05 16:22:22');

-- ----------------------------
-- Table structure for customer_service
-- ----------------------------
DROP TABLE IF EXISTS `customer_service`;
CREATE TABLE `customer_service`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户名称',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系邮箱',
  `subject` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '咨询主题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '咨询内容',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'GENERAL' COMMENT '咨询类型：GENERAL-一般咨询, COMPLAINT-投诉, SUGGESTION-建议, TECHNICAL-技术问题',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待处理, PROCESSING-处理中, REPLIED-已回复, CLOSED-已关闭',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '回复内容',
  `reply_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `admin_id` bigint NULL DEFAULT NULL COMMENT '处理管理员ID',
  `admin_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理管理员名称',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '客服咨询表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of customer_service
-- ----------------------------
INSERT INTO `customer_service` VALUES (1, 1, '张三', '13800138001', 'zhangsan@example.com', '商品退款问题', '我购买的商品有质量问题，想要退款，请问如何操作？', 'COMPLAINT', 'PENDING', NULL, NULL, NULL, NULL, '2025-12-05 10:24:08', '2025-12-05 10:24:08');
INSERT INTO `customer_service` VALUES (2, 2, '李四', '13800138002', 'lisi@example.com', '如何发布商品', '我是新用户，请问如何在平台上发布二手商品？需要认证吗？', 'GENERAL', 'REPLIED', '您好！发布商品很简单：1. 登录账号 2. 点击\"发布商品\" 3. 填写商品信息 4. 上传图片 5. 提交审核。新用户建议先完善个人资料哦！', '2025-12-05 09:24:08', 1, 'admin', '2025-12-05 10:24:08', '2025-12-05 10:24:08');
INSERT INTO `customer_service` VALUES (3, 3, '王五', '13800138003', 'wangwu@example.com', '账号登录不了', '我的账号突然登录不了，提示密码错误，但我确认密码是对的', 'TECHNICAL', 'PROCESSING', NULL, NULL, NULL, NULL, '2025-12-05 10:24:08', '2025-12-05 10:24:08');
INSERT INTO `customer_service` VALUES (4, NULL, '游客', '13800138004', 'guest@example.com', '平台使用建议', '建议增加商品搜索功能，能按价格区间筛选', 'SUGGESTION', 'PROCESSING', NULL, NULL, NULL, NULL, '2025-12-05 10:24:08', '2025-12-05 10:24:08');
INSERT INTO `customer_service` VALUES (5, NULL, 'wyy', '18976677565', '2252@qq.com', '测试测试', '测试测试测试测试测试测试测试测试', 'GENERAL', 'REPLIED', '什么？', '2025-12-05 10:37:37', 1, 'admin', '2025-12-05 10:37:23', '2025-12-05 10:37:23');
INSERT INTO `customer_service` VALUES (6, 8, '歪歪', '18909988787', '242424@qq.com', '测试测试', '测试测试测试测试测试测试测试测试测试测试', 'GENERAL', 'REPLIED', '什么啊？', '2025-12-05 10:48:12', 1, 'admin', '2025-12-05 10:48:02', '2025-12-05 10:48:02');
INSERT INTO `customer_service` VALUES (7, 8, '歪歪', '18909988787', '242424@qq.com', '[续问] 测试测试', '**原问题：**\n测试测试测试测试测试测试测试测试测试测试\n\n**客服回复：**\n什么啊？\n\n**继续追问：**\n测试啊帆帆帆帆？', 'GENERAL', 'REPLIED', '哈哈哈哈哈哈哈', '2025-12-05 10:53:06', 1, 'admin', '2025-12-05 10:51:42', '2025-12-05 10:51:42');
INSERT INTO `customer_service` VALUES (8, 9, '测试123', '18974455745', '22333@qq.com', '测试资讯？？？？', '啦啦啦啦啦啦啦啦！哈哈哈哈', 'GENERAL', 'REPLIED', '可以的！！！', '2025-12-05 16:24:27', 1, 'admin', '2025-12-05 16:24:16', '2025-12-05 16:24:16');
INSERT INTO `customer_service` VALUES (9, 9, '测试123', '18974455745', '22333@qq.com', '[续问] 测试资讯？？？？', '**原问题：**\n啦啦啦啦啦啦啦啦！哈哈哈哈\n\n**客服回复：**\n可以的！！！\n\n**继续追问：**\n什么意思？？', 'GENERAL', 'REPLIED', '没什么！！！', '2025-12-05 16:24:52', 1, 'admin', '2025-12-05 16:24:40', '2025-12-05 16:24:40');

-- ----------------------------
-- Table structure for free
-- ----------------------------
DROP TABLE IF EXISTS `free`;
CREATE TABLE `free`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '赠送ID',
  `user_id` bigint NOT NULL COMMENT '发布者ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '图片列表（逗号分隔）',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地点',
  `contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系方式',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'AVAILABLE' COMMENT '状态',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '免费赠送表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of free
-- ----------------------------
INSERT INTO `free` VALUES (1, 1, '闲置台灯免费送', '宿舍用的台灯，搬宿舍不需要了，九成新，功能完好', 1, '/image/free/lamp1.jpg', '3号楼宿舍', '微信：lamp123', 'AVAILABLE', 25, '2025-12-05 00:32:13', '2025-12-05 00:32:13');
INSERT INTO `free` VALUES (2, 1, '考研资料免费赠送', '考研英语、数学、政治全套复习资料', 2, '/image/free/book1.jpg', '图书馆门口', 'QQ：123456', 'AVAILABLE', 48, '2025-12-05 00:32:13', '2025-12-05 00:32:13');
INSERT INTO `free` VALUES (3, 2, '多肉植物免费领养', '养了一年的多肉，毕业带不走', 3, '/image/free/plant1.jpg', '1号楼门口', '电话：13800138000', 'AVAILABLE', 15, '2025-12-05 00:32:13', '2025-12-05 00:32:13');
INSERT INTO `free` VALUES (4, 2, '自行车免费送（需自提）', '大学用的自行车，毕业了用不上', 4, '/image/free/bike1.jpg', '学生公寓停车棚', '微信：bike456', 'COMPLETED', 67, '2025-12-05 00:32:13', '2025-12-05 00:32:13');

-- ----------------------------
-- Table structure for news
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资讯标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资讯内容',
  `cover_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片',
  `author_id` bigint NOT NULL COMMENT '作者ID（管理员）',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态：1-已发布，0-草稿',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_author_id`(`author_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '校园资讯表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of news
-- ----------------------------
INSERT INTO `news` VALUES (1, '校园网络升级通知', '为提升校园网络质量，信息中心将于本周末进行网络设备升级维护。届时部分区域网络可能会出现短暂中断，请各位同学提前做好准备。具体时间：周六08:00-12:00，影响范围：教学楼A、B、C区。敬请谅解。', NULL, 1, 156, '校园通知', 1, '2025-12-03 22:49:21', '2025-12-03 22:49:21');
INSERT INTO `news` VALUES (2, '图书馆延长开放时间通知', '为方便同学们学习备考，图书馆决定从本月起延长开放时间。周一至周五开放时间调整为7:00-23:00，周末开放时间为8:00-22:00。期末考试期间将提供24小时自习室，欢迎大家充分利用图书馆资源。', NULL, 1, 244, '校园通知', 1, '2025-12-03 22:49:21', '2025-12-03 22:49:21');
INSERT INTO `news` VALUES (3, '2025年春季运动会报名开始', '一年一度的春季运动会即将开始！本次运动会设有100米、200米、4x100接力、跳远、跳高等多个项目。有兴趣的同学请于本周五前到体育学院办公室报名。让我们一起展现青春活力，挥洒汗水！', NULL, 1, 89, '校园活动', 1, '2025-12-03 22:49:21', '2025-12-03 22:49:21');
INSERT INTO `news` VALUES (4, '食堂新增窗口营业通知', '为满足同学们多样化的饮食需求，第二食堂新增两个特色窗口：川湘风味和清真餐厅。即日起正式营业，营业时间：11:00-13:30，17:00-19:30。欢迎大家前来品尝！', NULL, 1, 127, '生活服务', 1, '2025-12-03 22:49:21', '2025-12-03 22:49:21');
INSERT INTO `news` VALUES (5, '学术讲座：人工智能的未来发展', '计算机学院将于下周三举办学术讲座，主题为\"人工智能的未来发展趋势\"。主讲人为知名AI专家李教授。讲座时间：下周三19:00，地点：学术报告厅。欢迎感兴趣的同学参加，座位有限，先到先得！', '/api/image/common/1d4af1c3-0b50-4a21-b083-ff0511ce70a9.jpeg', 1, 202, '学术交流', 1, '2025-12-03 22:49:21', '2025-12-04 20:01:13');
INSERT INTO `news` VALUES (6, '测试', '测试是测试测试测试从！！！', '/api/image/common/726c3de3-cc6f-49c5-b269-68665ccc27c8.png', 1, 7, '测试分类', 1, '2025-12-03 23:07:22', '2025-12-04 20:10:10');

-- ----------------------------
-- Table structure for news_category
-- ----------------------------
DROP TABLE IF EXISTS `news_category`;
CREATE TABLE `news_category`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类描述',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序序号',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sort_order`(`sort_order` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '资讯分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of news_category
-- ----------------------------
INSERT INTO `news_category` VALUES (1, '校园通知', '学校官方发布的各类通知公告', 1, 1, '2025-12-04 20:05:30', '2025-12-04 20:05:30');
INSERT INTO `news_category` VALUES (2, '校园活动', '校园文化活动、社团活动等', 2, 1, '2025-12-04 20:05:30', '2025-12-04 20:05:30');
INSERT INTO `news_category` VALUES (3, '学术交流', '学术讲座、研讨会等学术活动', 3, 1, '2025-12-04 20:05:30', '2025-12-04 20:05:30');
INSERT INTO `news_category` VALUES (4, '生活服务', '校园生活相关的服务信息', 4, 1, '2025-12-04 20:05:30', '2025-12-04 20:05:30');
INSERT INTO `news_category` VALUES (6, '测试分类', '测试', 0, 1, '2025-12-04 20:10:00', '2025-12-04 20:10:00');

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知内容',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'SYSTEM' COMMENT '通知类型：SYSTEM-系统通知，REPORT-举报通知，ORDER-订单通知',
  `related_id` bigint NULL DEFAULT NULL COMMENT '关联ID（如举报ID、订单ID等）',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_is_read`(`is_read` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES (1, 6, '您的举报已受理', '您对商品《Iphone1966》的举报已被管理员受理。处理说明：测试', 'REPORT', NULL, 0, '2025-12-04 21:50:37');
INSERT INTO `notification` VALUES (2, 8, '您的商品收到举报', '您的商品《Iphone1966》收到举报并已被受理，请注意商品信息的真实性和准确性。', 'REPORT', NULL, 0, '2025-12-04 21:50:37');
INSERT INTO `notification` VALUES (3, 6, '您的举报已处理完成', '您对商品《Iphone1966》的举报已处理完成。处理结果：测试！（商品已下架） 感谢您对平台的监督！', 'REPORT', NULL, 0, '2025-12-04 21:50:54');
INSERT INTO `notification` VALUES (4, 8, '您的商品收到举报', '您的商品《Iphone1966》的举报已处理完成。处理结果：测试！（商品已下架） 请规范发布商品。', 'REPORT', NULL, 1, '2025-12-04 21:50:54');
INSERT INTO `notification` VALUES (5, 8, '您的举报已受理', '您对商品《测试账号123发布》的举报已被管理员受理。处理说明：马上处理！', 'REPORT', NULL, 0, '2025-12-05 16:23:16');
INSERT INTO `notification` VALUES (6, 9, '您的商品收到举报', '您的商品《测试账号123发布》收到举报并已被受理，请注意商品信息的真实性和准确性。', 'REPORT', NULL, 0, '2025-12-05 16:23:16');
INSERT INTO `notification` VALUES (7, 8, '您的举报已处理完成', '您对商品《测试账号123发布》的举报已处理完成。处理结果：下架商品！！！（商品已下架） 感谢您对平台的监督！', 'REPORT', NULL, 0, '2025-12-05 16:23:27');
INSERT INTO `notification` VALUES (8, 9, '您的商品收到举报', '您的商品《测试账号123发布》的举报已处理完成。处理结果：下架商品！！！（商品已下架） 请规范发布商品。', 'REPORT', NULL, 0, '2025-12-05 16:23:27');

-- ----------------------------
-- Table structure for operation_log
-- ----------------------------
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `admin_id` bigint NULL DEFAULT NULL COMMENT '管理员ID',
  `admin_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '管理员名称',
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型：CREATE-新增 UPDATE-修改 DELETE-删除 QUERY-查询 LOGIN-登录',
  `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作模块：USER-用户 PRODUCT-商品 ORDER-订单 等',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作内容描述',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求方法',
  `params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP归属地',
  `browser` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '浏览器',
  `os` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作系统',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0-失败 1-成功',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `execute_time` bigint NULL DEFAULT NULL COMMENT '执行耗时(ms)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_admin_id`(`admin_id` ASC) USING BTREE,
  INDEX `idx_operation_type`(`operation_type` ASC) USING BTREE,
  INDEX `idx_module`(`module` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_log
-- ----------------------------
INSERT INTO `operation_log` VALUES (19, NULL, 'admin', 'DELETE', 'LOG', '批量删除操作日志，数量：1', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 09:46:52');
INSERT INTO `operation_log` VALUES (20, NULL, 'admin', 'UPDATE', 'USER', '禁用用户：seller1', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 09:53:18');
INSERT INTO `operation_log` VALUES (21, NULL, 'admin', 'DELETE', 'WANT', '删除求购：求购羽毛球拍(ID:5)', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 10:04:45');
INSERT INTO `operation_log` VALUES (22, NULL, 'admin', 'UPDATE', 'ORDER', '订单完成：订单#ORD20231201003', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 10:05:00');
INSERT INTO `operation_log` VALUES (23, NULL, 'admin', 'DELETE', 'ORDER', '删除订单：订单#ORD20231201004(ID:4)', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 10:08:02');
INSERT INTO `operation_log` VALUES (24, NULL, 'admin', 'UPDATE', 'CUSTOMER_SERVICE', '更新咨询状态为处理中：平台使用建议', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 10:31:59');
INSERT INTO `operation_log` VALUES (25, NULL, 'admin', 'UPDATE', 'CUSTOMER_SERVICE', '回复客服咨询：测试测试', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 10:37:37');
INSERT INTO `operation_log` VALUES (26, NULL, 'admin', 'UPDATE', 'CUSTOMER_SERVICE', '回复客服咨询：测试测试', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 10:48:12');
INSERT INTO `operation_log` VALUES (27, NULL, 'admin', 'UPDATE', 'CUSTOMER_SERVICE', '回复客服咨询：[续问] 测试测试', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 10:53:06');
INSERT INTO `operation_log` VALUES (28, NULL, 'admin', 'VIEW', 'PRODUCT', '浏览商品：测试！！！', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 16:19:18');
INSERT INTO `operation_log` VALUES (29, NULL, 'admin', 'CREATE', 'PRODUCT', '发布商品：测试账号123发布', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 16:20:25');
INSERT INTO `operation_log` VALUES (30, NULL, 'admin', 'VIEW', 'PRODUCT', '浏览商品：测试账号123发布', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 16:20:31');
INSERT INTO `operation_log` VALUES (31, NULL, 'admin', 'VIEW', 'PRODUCT', '浏览商品：测试账号123发布', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 16:20:55');
INSERT INTO `operation_log` VALUES (32, NULL, 'admin', 'UPDATE', 'ORDER', '订单支付：订单#ORD202512051620570570', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 16:21:19');
INSERT INTO `operation_log` VALUES (33, NULL, 'admin', 'UPDATE', 'ORDER', '订单发货：订单#ORD202512051620570570', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 16:22:20');
INSERT INTO `operation_log` VALUES (34, NULL, 'admin', 'UPDATE', 'ORDER', '订单完成：订单#ORD202512051620570570', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 16:22:23');
INSERT INTO `operation_log` VALUES (35, NULL, 'admin', 'VIEW', 'PRODUCT', '浏览商品：测试账号123发布', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 16:22:42');
INSERT INTO `operation_log` VALUES (36, NULL, 'admin', 'UPDATE', 'REPORT', '受理举报：举报#4', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 16:23:17');
INSERT INTO `operation_log` VALUES (37, NULL, 'admin', 'UPDATE', 'REPORT', '处理举报：举报#4，并下架商品', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 16:23:27');
INSERT INTO `operation_log` VALUES (38, NULL, 'admin', 'UPDATE', 'CUSTOMER_SERVICE', '回复客服咨询：测试资讯？？？？', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 16:24:27');
INSERT INTO `operation_log` VALUES (39, NULL, 'admin', 'UPDATE', 'CUSTOMER_SERVICE', '回复客服咨询：[续问] 测试资讯？？？？', NULL, NULL, '127.0.0.1', NULL, NULL, NULL, 1, NULL, NULL, '2025-12-05 16:24:52');

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `buyer_id` bigint NOT NULL COMMENT '买家ID',
  `seller_id` bigint NOT NULL COMMENT '卖家ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '总金额',
  `quantity` int NULL DEFAULT 1 COMMENT '数量',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '状态：PENDING待支付/PAID已支付/SHIPPED已发货/COMPLETED已完成/CANCELLED已取消',
  `shipping_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货地址',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `ship_time` datetime NULL DEFAULT NULL COMMENT '发货时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '完成时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_buyer_id`(`buyer_id` ASC) USING BTREE,
  INDEX `idx_seller_id`(`seller_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (1, 'ORD20231201001', 3, 2, 1, 299.00, 1, 'PENDING', '图书馆门口', '13800138001', '尽快发货，谢谢', NULL, NULL, NULL, '2025-12-03 20:15:20', '2025-12-03 20:15:20');
INSERT INTO `order` VALUES (2, 'ORD20231201002', 3, 2, 1, 199.00, 1, 'SHIPPED', '三食堂门口', '13800138002', '下午3点取货', '2025-12-02 20:20:20', '2025-12-05 09:53:59', NULL, '2025-12-02 20:15:20', '2025-12-03 20:15:20');
INSERT INTO `order` VALUES (3, 'ORD20231201003', 3, 2, 1, 399.00, 1, 'COMPLETED', '宿舍楼下', '13800138003', NULL, '2025-12-01 20:20:20', '2025-12-01 21:20:20', '2025-12-05 10:05:00', '2025-12-01 20:15:20', '2025-12-03 20:15:20');
INSERT INTO `order` VALUES (9, 'ORD202512042043246206', 8, 1, 1, 20.00, 1, 'COMPLETED', NULL, NULL, NULL, '2025-12-04 20:43:44', '2025-12-04 20:44:05', '2025-12-04 20:44:12', '2025-12-04 20:43:24', '2025-12-04 20:43:24');
INSERT INTO `order` VALUES (10, 'ORD202512051620570570', 8, 9, 8, 20.00, 1, 'COMPLETED', NULL, NULL, NULL, '2025-12-05 16:21:19', '2025-12-05 16:22:20', '2025-12-05 16:22:23', '2025-12-05 16:20:57', '2025-12-05 16:20:57');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品描述',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `original_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '原价',
  `stock` int NULL DEFAULT 1 COMMENT '库存',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品图片，多张用逗号分隔',
  `category_id` bigint NULL DEFAULT NULL COMMENT '分类ID',
  `seller_id` bigint NOT NULL COMMENT '卖家ID',
  `condition_desc` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '成色描述',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所在地',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览量',
  `favorite_count` int NULL DEFAULT 0 COMMENT '收藏量',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ON_SALE' COMMENT '状态：ON_SALE在售/SOLD已售/OFF_SHELF下架',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_category_id`(`category_id` ASC) USING BTREE,
  INDEX `idx_seller_id`(`seller_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, '测试商品！', '测试商品！', 20.00, NULL, 3, '[\"/api/image/common/0ffe5f84-0ae7-4a71-8e0a-9f56fac9213b.jpeg\",\"/api/image/common/32c49d37-0d66-409e-845c-57f576537d50.png\"]', 6, 1, '可接受', '厕所', 3, 0, 'SOLD', '2025-12-03 19:35:55', '2025-12-03 19:35:55');
INSERT INTO `product` VALUES (2, '测试', '撒发生范德萨发士大夫', 20.00, 50.00, 3, '[\"/api/image/common/7cbfd2f7-4892-421d-b96e-b77c67ffa086.png\"]', 6, 1, '几乎全新', '食堂', 6, 0, 'SOLD', '2025-12-03 19:50:41', '2025-12-03 19:50:41');
INSERT INTO `product` VALUES (4, 'Iphone1966', 'Iphone19Iphone19Iphone19Iphone19', 1999.00, 2999.00, 2, '[\"/api/image/product/d38c5139-9e4c-4ee2-baf2-53ef3f48cb24.jpeg\",\"/api/image/product/f581585a-e74d-4bca-8f89-2a076b7d04e4.png\"]', 1, 8, '几乎全新', '城市99', 15, 0, 'OFF_SALE', '2025-12-03 20:52:47', '2025-12-03 20:52:47');
INSERT INTO `product` VALUES (5, '熊猫熊猫熊猫', '熊猫熊猫熊猫熊猫熊猫', 30.00, 90.00, 1, '[\"/api/image/product/ff92b6cb-c92b-4001-9e0d-1b6cf42c68e4.png\"]', 6, 8, '几乎全新', '图书馆', 3, 0, 'ON_SALE', '2025-12-04 20:27:15', '2025-12-04 20:27:15');
INSERT INTO `product` VALUES (6, '熊猫熊猫熊猫熊猫', '熊猫熊猫熊猫熊猫', 30.00, 40.00, 1, '[\"/api/image/product/7e0911cd-abb7-4646-a378-8b2c5c4810f4.jpeg\"]', 6, 8, '全新', '食堂', 16, 0, 'ON_SALE', '2025-12-04 20:45:17', '2025-12-04 20:45:17');
INSERT INTO `product` VALUES (7, '测试！！！', '测试测试测试测试测试测试', 22.00, 56.00, 1, '[\"/api/image/product/fa93f285-4f5a-4ce8-bc3e-899871c157d7.jpeg\"]', 2, 6, '全新', '测试', 7, 0, 'ON_SALE', '2025-12-04 21:28:58', '2025-12-04 21:28:58');
INSERT INTO `product` VALUES (8, '测试账号123发布', '测试账号123发布测试账号123发布', 20.00, 50.00, 1, '[\"/api/image/product/6379cb5b-d59c-4b8c-9b0d-34971c8d5af2.jpeg\",\"/api/image/product/7290c2f5-86b8-4aca-9ee5-6d3cf3925f70.png\"]', 5, 9, '全新', '食堂', 3, 0, 'OFF_SALE', '2025-12-05 16:20:24', '2025-12-05 16:20:24');

-- ----------------------------
-- Table structure for report
-- ----------------------------
DROP TABLE IF EXISTS `report`;
CREATE TABLE `report`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '举报ID',
  `product_id` bigint NOT NULL COMMENT '被举报商品ID',
  `reporter_id` bigint NOT NULL COMMENT '举报人ID',
  `reason` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '举报原因',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详细描述',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '处理状态：PENDING-待处理，ACCEPTED-已受理，REJECTED-已驳回，RESOLVED-已解决',
  `handler_id` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  `handle_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '处理结果',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '举报时间',
  `handle_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE,
  INDEX `idx_reporter_id`(`reporter_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '举报表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of report
-- ----------------------------
INSERT INTO `report` VALUES (1, 6, 8, '虚假描述', '测试！！', 'ACCEPTED', 1, '测试', '2025-12-04 21:38:49', '2025-12-04 21:39:10');
INSERT INTO `report` VALUES (2, 7, 7, '侵权内容', '？？什么', 'PENDING', NULL, NULL, '2025-12-04 21:42:26', NULL);
INSERT INTO `report` VALUES (3, 4, 6, '重复发布', '测试', 'RESOLVED', 1, '测试！（商品已下架）', '2025-12-04 21:50:12', '2025-12-04 21:50:55');
INSERT INTO `report` VALUES (4, 8, 8, '虚假描述', '测试举报！！！', 'RESOLVED', 1, '下架商品！！！（商品已下架）', '2025-12-05 16:22:56', '2025-12-05 16:23:27');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'USER' COMMENT '角色：USER/SELLER/ADMIN',
  `status` int NULL DEFAULT 1 COMMENT '状态：0禁用，1正常',
  `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '学号',
  `is_verified` tinyint(1) NULL DEFAULT 0 COMMENT '是否已实名认证',
  `profile_completed` tinyint(1) NULL DEFAULT 0 COMMENT '资料是否完善',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_email`(`email` ASC) USING BTREE,
  INDEX `idx_student_id`(`student_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '$2a$10$NxcjW/QFsNh452sYWTsck.nBjjFB2WXAaPwGMmHxm30oiPmb/GLu6', '系统管理员', 'admin@campus.com', '18934400737', '/api/image/avatar/e4c3be85-2b14-4820-908c-97424ad7b06e.jpeg', 'ADMIN', 1, NULL, 0, 0, '2025-12-03 09:41:27', '2025-12-03 11:15:22');
INSERT INTO `user` VALUES (2, 'seller1', '$2a$10$NxcjW/QFsNh452sYWTsck.nBjjFB2WXAaPwGMmHxm30oiPmb/GLu6', '卖家一号', 'seller1@campus.com', NULL, NULL, 'SELLER', 0, NULL, 0, 0, '2025-12-03 09:41:27', '2025-12-03 11:15:24');
INSERT INTO `user` VALUES (3, 'user1', '$2a$10$NxcjW/QFsNh452sYWTsck.nBjjFB2WXAaPwGMmHxm30oiPmb/GLu6', '普通用户', 'user1@campus.com', NULL, NULL, 'USER', 1, NULL, 0, 0, '2025-12-03 09:41:27', '2025-12-03 11:15:28');
INSERT INTO `user` VALUES (6, 'wyq', '$2a$10$1yghKso22sjvhKJjssfUku2PnSxybDa.Zbe.wLTpHB9wTI8.x1YKO', '悲伤的人', '333465@qq.com', '1234141234', '/api/image/avatar/552d501d-f111-424f-a058-eb5ee0f41da9.png', 'USER', 1, NULL, 0, 0, '2025-12-03 11:42:13', '2025-12-03 11:42:13');
INSERT INTO `user` VALUES (7, 'wyk', '$2a$10$38QEMyOGQz/84407hqBv1u0fJAsvu1sBHYCsvUEIViOtrwjwuale.', 'kangzai', '2252@qq.com', '18967755675', '/api/image/avatar/2cb8958b-35d0-456b-8d13-400679d37d21.png', 'USER', 1, '2464646465', 1, 0, '2025-12-03 19:29:08', '2025-12-03 19:29:08');
INSERT INTO `user` VALUES (8, 'wyy', '$2a$10$jCB5zfv9aRGGFcc2eVy8feB4fIQNHozFscEaMub4oP8GJ6AgDCwne', '歪歪', '242424@qq.com', '18909988787', '/api/image/avatar/c175fd7c-7237-4037-88a5-f946d5b410e9.jpeg', 'USER', 1, '2345786544', 1, 1, '2025-12-03 19:29:45', '2025-12-03 19:29:45');
INSERT INTO `user` VALUES (9, 'cs123', '$2a$10$sZiH.6KcuAXtJsxbhW08Beo8Ngto27CnhwNEc.kpOXC0uQEGN8Rh.', '测试123', '22333@qq.com', '18974455745', '/api/image/avatar/4602c1b9-e493-47b5-a761-33675b584370.png', 'USER', 1, '2345678976', 1, 0, '2025-12-05 16:19:04', '2025-12-05 16:19:04');

-- ----------------------------
-- Table structure for user_credit
-- ----------------------------
DROP TABLE IF EXISTS `user_credit`;
CREATE TABLE `user_credit`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `credit_score` int NOT NULL DEFAULT 100 COMMENT '信用分（初始100分）',
  `level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'GOOD' COMMENT '信用等级：EXCELLENT/GOOD/NORMAL/BAD',
  `total_earned` int NOT NULL DEFAULT 0 COMMENT '累计获得积分',
  `total_deducted` int NOT NULL DEFAULT 0 COMMENT '累计扣除积分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_credit_score`(`credit_score` ASC) USING BTREE,
  INDEX `idx_level`(`level` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户信用表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_credit
-- ----------------------------
INSERT INTO `user_credit` VALUES (1, 1, 151, 'EXCELLENT', 61, 10, '2025-12-04 20:16:37', '2025-12-04 20:16:37');
INSERT INTO `user_credit` VALUES (2, 8, 129, 'GOOD', 122, 5, '2025-12-04 20:26:33', '2025-12-04 20:40:50');
INSERT INTO `user_credit` VALUES (3, 7, 124, 'GOOD', 24, 0, '2025-12-04 20:47:57', '2025-12-04 20:47:57');
INSERT INTO `user_credit` VALUES (4, 6, 107, 'GOOD', 7, 0, '2025-12-04 21:08:13', '2025-12-04 21:08:13');
INSERT INTO `user_credit` VALUES (5, 3, 105, 'GOOD', 5, 0, '2025-12-05 10:05:00', '2025-12-05 10:05:00');
INSERT INTO `user_credit` VALUES (6, 9, 139, 'GOOD', 39, 0, '2025-12-05 16:19:11', '2025-12-05 16:19:11');

-- ----------------------------
-- Table structure for want
-- ----------------------------
DROP TABLE IF EXISTS `want`;
CREATE TABLE `want`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '求购标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详细描述',
  `min_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '最低预算',
  `max_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '最高预算',
  `user_id` bigint NOT NULL COMMENT '发布人ID',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-进行中，CLOSED-已关闭',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签（逗号分隔）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '求购信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of want
-- ----------------------------
INSERT INTO `want` VALUES (1, '求购二手iPad 2021', '主要用来记笔记和看网课，外观有磕碰没关系，屏幕不能有划痕。预算2000左右，诚心收。', 1800.00, 2200.00, 1, 'ACTIVE', '平板,Apple,数码', '2025-12-03 22:08:59', '2025-12-03 22:08:59');
INSERT INTO `want` VALUES (2, '收考研英语红宝书', '书最好不要有太多笔记，主要想要单词那本。最好是西区或者图书馆面交。', 20.00, 30.00, 2, 'ACTIVE', '图书,考研', '2025-12-03 22:08:59', '2025-12-03 22:08:59');
INSERT INTO `want` VALUES (3, '求购二手自行车', '通勤用，山地车或公路车都可以，质量好就行。预算500元左右。', 400.00, 600.00, 1, 'ACTIVE', '自行车,交通工具', '2025-12-03 22:08:59', '2025-12-03 22:08:59');
INSERT INTO `want` VALUES (4, '收闲置吉他', '入门练习用，民谣吉他即可，成色要求不高。价格300-500元。', 300.00, 500.00, 3, 'ACTIVE', '乐器,吉他', '2025-12-03 22:08:59', '2025-12-03 22:08:59');
INSERT INTO `want` VALUES (6, '高数上册666', '高数上高数上高数上高数上', 20.00, 25.00, 8, 'ACTIVE', '高数,书籍', '2025-12-03 22:19:22', '2025-12-03 22:22:40');

-- ----------------------------
-- Table structure for want_offer
-- ----------------------------
DROP TABLE IF EXISTS `want_offer`;
CREATE TABLE `want_offer`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '出价ID',
  `want_id` bigint NOT NULL COMMENT '求购信息ID',
  `seller_id` bigint NOT NULL COMMENT '出价人ID',
  `price` decimal(10, 2) NOT NULL COMMENT '出价金额',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品描述',
  `contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系方式',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交易地点',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待处理，ACCEPTED-已接受，REJECTED-已拒绝',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_want_id`(`want_id` ASC) USING BTREE,
  INDEX `idx_seller_id`(`seller_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '求购出价表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of want_offer
-- ----------------------------
INSERT INTO `want_offer` VALUES (2, 6, 8, 20.00, 'cscscscscscscs', '18909988787', '校内面交', 'PENDING', '2025-12-05 16:02:33', '2025-12-05 16:02:33');

SET FOREIGN_KEY_CHECKS = 1;
