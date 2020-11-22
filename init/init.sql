SET NAMES utf8;
SET character_set_client = utf8mb4;

CREATE DATABASE IF NOT EXISTS kakao_db;

CREATE TABLE IF NOT EXISTS `throw_money` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `token` char(3) NOT NULL DEFAULT '',
  `roomId` bigint(11) unsigned NOT NULL COMMENT '대화방번호',
  `userId` bigint(11) NOT NULL COMMENT '뿌릴회원번호',
  `memberCnt` int(11) NOT NULL DEFAULT '0' COMMENT '뿌릴대상인원',
  `amount` bigint(11) NOT NULL DEFAULT '0' COMMENT '뿌리기금액',
  `createdAt` datetime NOT NULL,
  `updatedAt` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `throwMoney_roomId_token_idx` (`roomId`,`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `pick_up` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `throwMoneyId` bigint(11) unsigned NOT NULL COMMENT 'throwMoney테이블 FK',
  `userId` bigint(11) unsigned DEFAULT NULL COMMENT '받기한 회원번호',
  `amount` bigint(11) NOT NULL DEFAULT '0' COMMENT '받을 금액',
  `isReceived` char(1) NOT NULL DEFAULT 'N' COMMENT '받음여부(Y:받음, N:받지않음)',
  `createdAt` datetime NOT NULL,
  `updatedAt` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `throwMoneyFK` (`throwMoneyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

GRANT ALL PRIVILEGES ON *.* TO 'root'@'throwmoney_database' IDENTIFIED BY 'password' WITH GRANT OPTION;
SET PASSWORD FOR root@'throwmoney_database' = PASSWORD('root');
FLUSH PRIVILEGES;