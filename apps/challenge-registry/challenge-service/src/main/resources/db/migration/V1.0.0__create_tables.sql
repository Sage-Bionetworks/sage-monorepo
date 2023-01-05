-- challenge.challenge_platform definition

CREATE TABLE `challenge_platform`
(
    `id`                    int NOT NULL AUTO_INCREMENT,
    `name`                  varchar(255) NOT NULL UNIQUE,
    `display_name`          varchar(255) DEFAULT NULL,
    `created_at`            DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

-- challenge.challenge definition

CREATE TABLE `challenge`
(
    `id`                    bigint(20) NOT NULL AUTO_INCREMENT,
    `name`                  varchar(255) DEFAULT NULL,
    `headline`              varchar(80),
    `description`           varchar(280) NOT NULL,
    `status`                ENUM('upcoming', 'active', 'completed'),
    `difficulty`            ENUM('good_for_beginners', 'intermediate', 'advanced'),
    `platform_id`           int,
    -- `email`                 varchar(255) DEFAULT NULL,
    -- `login`                 varchar(255) UNIQUE,
    -- `avatar_url`            varchar(255) DEFAULT NULL,
    -- `website_url`           varchar(255) DEFAULT NULL,
    -- `description`           varchar(255) DEFAULT NULL,
    `created_at`            DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`platform_id`) REFERENCES challenge_platform(`id`)
);

-- KEY                `FKk9w2ogq595jbe8r2due7vv3xr` (`account_id`),
-- CONSTRAINT `FKk9w2ogq595jbe8r2due7vv3xr` FOREIGN KEY (`account_id`) REFERENCES `banking_core_account` (`id`)

-- challenge.challenge_incentive definition

CREATE TABLE `challenge_incentive`
(
    `id`                    int NOT NULL AUTO_INCREMENT,
    `name`                  ENUM('monetary', 'publication', 'speaking_engagement', 'other'),
    `challenge_id`          bigint(20) NOT NULL,
    `created_at`            DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`challenge_id`) REFERENCES challenge(`id`),
    CONSTRAINT unique_item UNIQUE (`name`, `challenge_id`)
);

-- challenge.challenge_submission_type definition

CREATE TABLE `challenge_submission_type`
(
    `id`                    int NOT NULL AUTO_INCREMENT,
    `name`                  ENUM('container_image', 'prediction_file', 'other'),
    `challenge_id`          bigint(20) NOT NULL,
    `created_at`            DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`challenge_id`) REFERENCES challenge(`id`),
    CONSTRAINT unique_item UNIQUE (`name`, `challenge_id`)
);