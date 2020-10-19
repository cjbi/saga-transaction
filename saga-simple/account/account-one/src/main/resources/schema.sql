DROP TABLE IF EXISTS `t_account_one`;
CREATE TABLE `t_account_one` (
 `id` bigint(20) NOT NULL AUTO_INCREMENT,
 `amount` decimal(19,2) DEFAULT NULL,
 PRIMARY KEY (`id`)
) ENGINE=InnoDB;
