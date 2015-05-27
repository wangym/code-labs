/* 钓鱼迷 */
CREATE DATABASE diaoyumi;
USE diaoyumi;

/* 用户表 user */
CREATE TABLE IF NOT EXISTS `user` (
	`id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '用户编号,自增主键',
	`email` varchar(100) NOT NULL COMMENT '用户邮箱,唯一,登录用',
	`name` varchar(50) NOT NULL COMMENT '用户昵称,唯一,登录用,不可改',
	`password` char(32) NOT NULL COMMENT '登录密码,MD5后',
	`mobile` varchar(50) DEFAULT NULL COMMENT '手机号码',
	`created` datetime NOT NULL COMMENT '首次创建',
	`modified` timestamp NOT NULL COMMENT '最后变更',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表' AUTO_INCREMENT=1 ;

/* 事件表 event */
CREATE TABLE IF NOT EXISTS `event` (
	`id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '事件编号,自增主键',
	`user_id` int unsigned NOT NULL COMMENT '用户编号',
	`rid` varchar(50) NOT NULL COMMENT '资源编号',
	`type` char(1) NOT NULL COMMENT '事件类型:f我在钓鱼,c我的渔获,m我想钓鱼,b我的渔具',
	`event_time` datetime NOT NULL COMMENT '事件发生时间',
	`lat` decimal(20,17) DEFAULT NULL COMMENT '发生纬度',
	`lng` decimal(20,17) DEFAULT NULL COMMENT '发生经度',
	`place` varchar(100) DEFAULT NULL COMMENT '地点描述',
	`is_new_place` char(1) NOT NULL COMMENT '是否是新发现地点:Y,N',
	`companion` varchar(100) DEFAULT NULL COMMENT '事件同伴',
	`pictrue` varchar(100) DEFAULT NULL COMMENT '事件图片,多个以","相隔',
	`title` varchar(100) DEFAULT NULL COMMENT '简要说明',
	`price` double(8,2) DEFAULT NULL COMMENT '事件费用',
	`intro` text DEFAULT NULL COMMENT '详细描述',
	`properties` text DEFAULT NULL COMMENT '其它属性',
	`status` int(10) unsigned NOT NULL COMMENT '事件状态:0未同步,1已同步',
	`created` datetime NOT NULL COMMENT '首次创建',
	`modified` timestamp NOT NULL COMMENT '最后变更',
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表' AUTO_INCREMENT=1 ;

