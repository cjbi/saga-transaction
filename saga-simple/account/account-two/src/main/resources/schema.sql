DROP TABLE IF EXISTS `t_account_two`;
CREATE TABLE `t_account_two`
(
    `id`     bigint(20) NOT NULL AUTO_INCREMENT,
    `amount` decimal(19, 2) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;
