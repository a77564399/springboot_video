/*
SQLyog Professional v12.09 (64 bit)
MySQL - 5.6.25 : Database - project_bilibili
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`project_bilibili` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `project_bilibili`;

/*Table structure for table `t_auth_element_operation` */

DROP TABLE IF EXISTS `t_auth_element_operation`;

CREATE TABLE `t_auth_element_operation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `elementName` varchar(255) DEFAULT NULL COMMENT '页面元素名称',
  `elementCode` varchar(50) DEFAULT NULL COMMENT '页面元素唯一编码',
  `operationType` varchar(5) DEFAULT NULL COMMENT '操作类型：0可点击  1可见',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='权限控制--页面元素操作表';

/*Data for the table `t_auth_element_operation` */

insert  into `t_auth_element_operation`(`id`,`elementName`,`elementCode`,`operationType`,`createTime`,`updateTime`) values (1,'1','1','1',NULL,NULL);

/*Table structure for table `t_auth_menu` */

DROP TABLE IF EXISTS `t_auth_menu`;

CREATE TABLE `t_auth_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(255) DEFAULT NULL COMMENT '菜单项目名称',
  `code` varchar(50) DEFAULT NULL COMMENT '唯一编码',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='权限控制-页面访问表';

/*Data for the table `t_auth_menu` */

insert  into `t_auth_menu`(`id`,`name`,`code`,`createTime`,`updateTime`) values (4,'1','1',NULL,NULL);

/*Table structure for table `t_auth_role` */

DROP TABLE IF EXISTS `t_auth_role`;

CREATE TABLE `t_auth_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(255) DEFAULT NULL COMMENT '角色名称',
  `code` varchar(50) DEFAULT NULL COMMENT '角色唯一编码',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='权限控制--角色表';

/*Data for the table `t_auth_role` */

insert  into `t_auth_role`(`id`,`name`,`code`,`createTime`,`updateTime`) values (4,'1','1',NULL,NULL);

/*Table structure for table `t_auth_role_element_operation` */

DROP TABLE IF EXISTS `t_auth_role_element_operation`;

CREATE TABLE `t_auth_role_element_operation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `roleId` bigint(20) DEFAULT NULL COMMENT '角色id',
  `elementOperationId` bigint(20) DEFAULT NULL COMMENT '元素操作id',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='权限控制--角色与元素操作关联表';

/*Data for the table `t_auth_role_element_operation` */

insert  into `t_auth_role_element_operation`(`id`,`roleId`,`elementOperationId`,`createTime`) values (1,1,1,NULL);

/*Table structure for table `t_auth_role_menu` */

DROP TABLE IF EXISTS `t_auth_role_menu`;

CREATE TABLE `t_auth_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `roleId` bigint(20) DEFAULT NULL COMMENT '角色id',
  `menuId` bigint(20) DEFAULT NULL COMMENT '页面菜单id',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='权限控制--角色页面菜单关联表';

/*Data for the table `t_auth_role_menu` */

insert  into `t_auth_role_menu`(`id`,`roleId`,`menuId`,`createTime`) values (3,1,1,NULL);

/*Table structure for table `t_collection_group` */

DROP TABLE IF EXISTS `t_collection_group`;

CREATE TABLE `t_collection_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `userId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `name` varchar(50) DEFAULT NULL COMMENT '关注分组名称',
  `type` varchar(5) DEFAULT NULL COMMENT '关注分组类型：0默认分组  1用户自定义分组',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COMMENT='收藏分组表';

/*Data for the table `t_collection_group` */

/*Table structure for table `t_danmu` */

DROP TABLE IF EXISTS `t_danmu`;

CREATE TABLE `t_danmu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `userId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `videoId` bigint(20) DEFAULT NULL COMMENT '视频Id',
  `content` text COMMENT '弹幕内容',
  `danmuTime` varchar(50) DEFAULT NULL COMMENT '弹幕出现时间',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=174 DEFAULT CHARSET=utf8 COMMENT='弹幕记录表';

/*Data for the table `t_danmu` */

/*Table structure for table `t_file` */

DROP TABLE IF EXISTS `t_file`;

CREATE TABLE `t_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `url` varchar(500) DEFAULT NULL COMMENT '文件存储路径',
  `type` varchar(50) DEFAULT NULL COMMENT '文件类型',
  `md5` varchar(500) DEFAULT NULL COMMENT '文件md5唯一标识串',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='文件表';

/*Data for the table `t_file` */

/*Table structure for table `t_following_group` */

DROP TABLE IF EXISTS `t_following_group`;

CREATE TABLE `t_following_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `userId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `name` varchar(50) DEFAULT NULL COMMENT '关注分组名称',
  `type` varchar(5) DEFAULT NULL COMMENT '关注分组类型：0特别关注  1悄悄关注 2默认分组  3用户自定义分组',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COMMENT='用户关注分组表';

/*Data for the table `t_following_group` */

insert  into `t_following_group`(`id`,`userId`,`name`,`type`,`createTime`,`updateTime`) values (1,NULL,'特别关注','0',NULL,NULL),(2,NULL,'悄悄关注','1',NULL,NULL),(3,NULL,'默认分组','2',NULL,NULL),(20,22,'haha','3','2022-09-22 20:45:52',NULL);

/*Table structure for table `t_refresh_token` */

DROP TABLE IF EXISTS `t_refresh_token`;

CREATE TABLE `t_refresh_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `userId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `refreshToken` varchar(500) DEFAULT NULL COMMENT '刷新令牌',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='刷新令牌记录表';

/*Data for the table `t_refresh_token` */

insert  into `t_refresh_token`(`id`,`userId`,`refreshToken`,`createTime`) values (10,22,'eyJraWQiOiIyMiIsInR5cCI6IkpXVCIsImFsZyI6IlJTMjU2In0.eyJpc3MiOiLnrb7lj5HogIUiLCJleHAiOjE2NjY2MTY0ODh9.SIM1yolCqShAy7H1ioE6xazPo90WjnPhCOcoSfoThwmUkej1Qo1WFAkTxuQp0DuvuBAGQkZVd7Yl_sQQMpDAiCMrCHTMPBpvZGaCPxCQkgQRFUbPku30Re9Kq5eByYRkuwEj_oYpBWX3pdhkkTI7vKMmdOTZ6bp6mxnBqjoVPd4','2022-10-17 21:01:29');

/*Table structure for table `t_tag` */

DROP TABLE IF EXISTS `t_tag`;

CREATE TABLE `t_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(255) DEFAULT NULL COMMENT '标签名称',
  `createTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

/*Data for the table `t_tag` */

/*Table structure for table `t_user` */

DROP TABLE IF EXISTS `t_user`;

CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `phone` varchar(100) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `salt` varchar(50) DEFAULT NULL COMMENT '盐值',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

/*Data for the table `t_user` */

insert  into `t_user`(`id`,`phone`,`email`,`password`,`salt`,`createTime`,`updateTime`) values (22,'15324981356',NULL,'c34645bf5d7ceb40a79335b0a7c25664','1663594756480','2022-09-19 21:39:16',NULL),(23,'12345678910',NULL,'1f96679f-4e12-11ed-9084-7c67a242d745','155000',NULL,NULL);

/*Table structure for table `t_user_coin` */

DROP TABLE IF EXISTS `t_user_coin`;

CREATE TABLE `t_user_coin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `userId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `amount` bigint(20) DEFAULT NULL COMMENT '硬币总数',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='用户硬币表';

/*Data for the table `t_user_coin` */

/*Table structure for table `t_user_following` */

DROP TABLE IF EXISTS `t_user_following`;

CREATE TABLE `t_user_following` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `userId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `followingId` int(11) DEFAULT NULL COMMENT '关注用户id',
  `groupId` int(11) DEFAULT NULL COMMENT '关注分组id',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

/*Data for the table `t_user_following` */

/*Table structure for table `t_user_info` */

DROP TABLE IF EXISTS `t_user_info`;

CREATE TABLE `t_user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `nick` varchar(100) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `sign` text COMMENT '签名',
  `gender` varchar(2) DEFAULT NULL COMMENT '性别：0男 1女 2未知',
  `birth` varchar(20) DEFAULT NULL COMMENT '生日',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COMMENT='用户基本信息表';

/*Data for the table `t_user_info` */

insert  into `t_user_info`(`id`,`userId`,`nick`,`avatar`,`sign`,`gender`,`birth`,`createTime`,`updateTime`) values (10,21,'萌新',NULL,NULL,'0','1999-10-01','2022-09-19 21:10:59',NULL),(11,22,'萌新',NULL,NULL,'0','1999-10-01','2022-09-19 21:39:16',NULL);

/*Table structure for table `t_user_moments` */

DROP TABLE IF EXISTS `t_user_moments`;

CREATE TABLE `t_user_moments` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `userId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `type` varchar(5) DEFAULT NULL COMMENT '动态类型：0视频 1直播 2专栏动态',
  `contentId` bigint(20) DEFAULT NULL COMMENT '内容详情id',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='用户动态表';

/*Data for the table `t_user_moments` */

/*Table structure for table `t_user_role` */

DROP TABLE IF EXISTS `t_user_role`;

CREATE TABLE `t_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `roleId` bigint(20) DEFAULT NULL COMMENT '角色id',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户角色关联表';

/*Data for the table `t_user_role` */

insert  into `t_user_role`(`id`,`userId`,`roleId`,`createTime`) values (4,22,1,NULL);

/*Table structure for table `t_video` */

DROP TABLE IF EXISTS `t_video`;

CREATE TABLE `t_video` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `userId` bigint(20) NOT NULL COMMENT '用户id',
  `url` varchar(500) NOT NULL COMMENT '视频链接',
  `thumbnail` varchar(500) NOT NULL COMMENT '封面链接',
  `title` varchar(255) NOT NULL COMMENT '视频标题',
  `type` varchar(5) NOT NULL COMMENT '视频类型：0原创 1转载',
  `duration` varchar(255) NOT NULL COMMENT '视频时长',
  `area` varchar(255) NOT NULL COMMENT '所在分区：0鬼畜 1音乐 2电影',
  `description` text COMMENT '视频简介',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COMMENT='视频投稿记录表';

/*Data for the table `t_video` */

insert  into `t_video`(`id`,`userId`,`url`,`thumbnail`,`title`,`type`,`duration`,`area`,`description`,`createTime`,`updateTime`) values (33,22,'M00/00/00/CoauoGM8OBKEdkFBAAAAAGK-v48527.zip','M00/00/00/CoauoGM8OBKEdkFBAAAAAGK-v48527.zip','萌新13','0','135','0','萌新','2022-10-16 22:13:10',NULL),(34,22,'M00/00/00/CoauoGM8OBKEdkFBAAAAAGK-v48527.zip','M00/00/00/CoauoGM8OBKEdkFBAAAAAGK-v48527.zip','萌新13','0','135','0','萌新','2022-10-16 22:13:12',NULL),(35,22,'M00/00/00/CoauoGM8OBKEdkFBAAAAAGK-v48527.zip','M00/00/00/CoauoGM8OBKEdkFBAAAAAGK-v48527.zip','萌新13','0','135','0','萌新','2022-10-16 22:13:13',NULL),(36,22,'M00/00/00/CoauoGM8OBKEdkFBAAAAAGK-v48527.zip','M00/00/00/CoauoGM8OBKEdkFBAAAAAGK-v48527.zip','萌新13萌新','0','135','0','萌新1333萌新','2022-10-17 15:31:34',NULL);

/*Table structure for table `t_video_binary_picture` */

DROP TABLE IF EXISTS `t_video_binary_picture`;

CREATE TABLE `t_video_binary_picture` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `videoId` bigint(20) DEFAULT NULL COMMENT '视频id',
  `frameNo` int(11) DEFAULT NULL COMMENT '帧数',
  `url` varchar(255) DEFAULT NULL COMMENT '图片链接',
  `videoTimestamp` bigint(20) DEFAULT NULL COMMENT '视频时间戳',
  `createTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8 COMMENT='视频二值图记录表';

/*Data for the table `t_video_binary_picture` */

/*Table structure for table `t_video_coin` */

DROP TABLE IF EXISTS `t_video_coin`;

CREATE TABLE `t_video_coin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '视频投稿id',
  `userId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `videoId` bigint(20) DEFAULT NULL COMMENT '视频投稿id',
  `amount` int(11) DEFAULT NULL COMMENT '投币数',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='视频硬币表';

/*Data for the table `t_video_coin` */

/*Table structure for table `t_video_collection` */

DROP TABLE IF EXISTS `t_video_collection`;

CREATE TABLE `t_video_collection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `videoId` bigint(20) DEFAULT NULL COMMENT '视频投稿id',
  `userId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `groupId` bigint(20) DEFAULT NULL COMMENT '收藏分组id',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='视频收藏记录表';

/*Data for the table `t_video_collection` */

/*Table structure for table `t_video_comment` */

DROP TABLE IF EXISTS `t_video_comment`;

CREATE TABLE `t_video_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `videoId` bigint(20) NOT NULL COMMENT '视频id',
  `userId` bigint(20) NOT NULL COMMENT '用户id',
  `comment` text NOT NULL COMMENT '评论',
  `replyUserId` bigint(20) DEFAULT NULL COMMENT '回复用户id',
  `rootId` bigint(20) DEFAULT NULL COMMENT '根节点评论id',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COMMENT='视频评论表';

/*Data for the table `t_video_comment` */

/*Table structure for table `t_video_like` */

DROP TABLE IF EXISTS `t_video_like`;

CREATE TABLE `t_video_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `userId` bigint(20) NOT NULL COMMENT '用户id',
  `videoId` bigint(20) NOT NULL COMMENT '视频投稿id',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COMMENT='视频点赞记录表';

/*Data for the table `t_video_like` */

/*Table structure for table `t_video_operation` */

DROP TABLE IF EXISTS `t_video_operation`;

CREATE TABLE `t_video_operation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `userId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `videoId` bigint(20) DEFAULT NULL COMMENT '视频id',
  `operationType` varchar(5) DEFAULT NULL COMMENT '操作类型：0点赞、1收藏、2投币',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8 COMMENT='视频操作表';

/*Data for the table `t_video_operation` */

/*Table structure for table `t_video_tag` */

DROP TABLE IF EXISTS `t_video_tag`;

CREATE TABLE `t_video_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `videoId` bigint(20) NOT NULL COMMENT '视频id',
  `tagId` bigint(20) NOT NULL COMMENT '标签id',
  `createTime` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COMMENT='视频标签关联表';

/*Data for the table `t_video_tag` */

/*Table structure for table `t_video_view` */

DROP TABLE IF EXISTS `t_video_view`;

CREATE TABLE `t_video_view` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `videoId` bigint(20) NOT NULL COMMENT '视频id',
  `userId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `clientId` varchar(500) DEFAULT NULL COMMENT '客户端id',
  `ip` varchar(50) DEFAULT NULL COMMENT 'ip',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='视频观看记录表';

/*Data for the table `t_video_view` */

/* Procedure structure for procedure `pre_test1` */

/*!50003 DROP PROCEDURE IF EXISTS  `pre_test1` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`%` PROCEDURE `pre_test1`()
BEGIN
    DECLARE i INT DEFAULT 0;
    SET autocommit = 0;
    WHILE i < 10000 DO
        SET i = i + 1;
        SET @str1 = SUBSTRING(MD5(RAND()),1,20);
        -- 每100条数据str2产生一个null值
        IF i % 100 = 0 THEN
            SET @str2 = NULL;
        ELSE
            SET @str2 = @str1;
        END IF;
        INSERT INTO test1 (`id`, `num1`, `num2`,
        `type1`, `type2`, `str1`, `str2`)
        VALUES (CONCAT('', i), CONCAT('', i),
        CONCAT('', i), i%5, i%5, @str1, @str2);
        -- 事务优化，每一万条数据提交一次事务
        IF i % 10000 = 0 THEN
            COMMIT;
        END IF;
    END WHILE;
END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
