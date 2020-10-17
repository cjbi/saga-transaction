DROP TABLE IF EXISTS `t_user_wallet`;
CREATE TABLE `t_user_wallet` (
 `id` bigint(20) NOT NULL AUTO_INCREMENT,
 `user_id` varchar(255) DEFAULT NULL,
 `money` decimal(19,2) DEFAULT NULL,
 PRIMARY KEY (`id`)
) ENGINE=InnoDB;
