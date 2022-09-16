-- challenge.challenge_user definition

CREATE TABLE `challenge_user`
(
    `id`                    bigint(20) NOT NULL AUTO_INCREMENT,
    `auth_id`               varchar(255) DEFAULT NULL,
    `status`                varchar(255) DEFAULT NULL,
    `username`              varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
);

-- KEY                `FKk9w2ogq595jbe8r2due7vv3xr` (`account_id`),
-- CONSTRAINT `FKk9w2ogq595jbe8r2due7vv3xr` FOREIGN KEY (`account_id`) REFERENCES `banking_core_account` (`id`)