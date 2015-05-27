-- phpMyAdmin SQL Dump
-- version 3.1.3
-- http://www.phpmyadmin.net
--
-- 主机: localhost
-- 生成日期: 2009 年 04 月 12 日 23:53
-- 服务器版本: 5.1.31
-- PHP 版本: 5.2.8

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";

--
-- 数据库: `wolequ`
--

-- --------------------------------------------------------

--
-- 表的结构 `tbl_article`
--

DROP TABLE IF EXISTS `tbl_article`;
CREATE TABLE IF NOT EXISTS `tbl_article` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `alias` varchar(100) NOT NULL,
  `status` int(11) NOT NULL,
  `title` varchar(200) NOT NULL,
  `title_image` varchar(100) NOT NULL,
  `brief` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `tags` varchar(100) NOT NULL,
  `created` int(11) NOT NULL,
  `modified` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;

--
-- 导出表中的数据 `tbl_article`
--


-- --------------------------------------------------------

--
-- 表的结构 `tbl_tag`
--

DROP TABLE IF EXISTS `tbl_tag`;
CREATE TABLE IF NOT EXISTS `tbl_tag` (
  `articleId` int(11) NOT NULL,
  `tag` char(5) NOT NULL,
  UNIQUE KEY `idx_unique` (`articleId`,`tag`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- 导出表中的数据 `tbl_tag`
--


-- --------------------------------------------------------

--
-- 表的结构 `tbl_base`
--

CREATE TABLE IF NOT EXISTS `tbl_base` (
  `name_space` varchar(10) NOT NULL,
  `article_total` int(11) NOT NULL,
  `article_available` int(11) NOT NULL,
  PRIMARY KEY  (`name_space`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- 导出表中的数据 `tbl_base`
--

INSERT INTO `tbl_base` (`name_space`, `article_total`, `article_available`) VALUES
('wolequ', 0, 0);
