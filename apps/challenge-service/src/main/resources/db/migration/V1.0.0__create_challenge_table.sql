-- challenge.challenge definition

CREATE TABLE `challenge`
(
    `id`                    bigint(20) NOT NULL AUTO_INCREMENT,
    `name`                  varchar(255) DEFAULT NULL,
    -- `email`                 varchar(255) DEFAULT NULL,
    -- `login`                 varchar(255) UNIQUE,
    -- `avatar_url`            varchar(255) DEFAULT NULL,
    -- `website_url`           varchar(255) DEFAULT NULL,
    -- `description`           varchar(255) DEFAULT NULL,
    `created_at`            DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

-- KEY                `FKk9w2ogq595jbe8r2due7vv3xr` (`account_id`),
-- CONSTRAINT `FKk9w2ogq595jbe8r2due7vv3xr` FOREIGN KEY (`account_id`) REFERENCES `banking_core_account` (`id`)