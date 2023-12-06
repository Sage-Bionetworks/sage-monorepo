-- challenge_platform definition

CREATE TABLE `challenge_platform`
(
    `id`                    int NOT NULL AUTO_INCREMENT,
    `slug`                  varchar(255) NOT NULL UNIQUE,
    `name`                  varchar(255) NOT NULL UNIQUE,
    `avatar_key`            varchar(255) NOT NULL,
    `website_url`           varchar(255) NOT NULL,
    `created_at`            DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

-- challenge_input_data_type definition

CREATE TABLE `challenge_input_data_type`
(
    `id`                    int NOT NULL AUTO_INCREMENT,
    `slug`                  varchar(255) NOT NULL UNIQUE,
    `name`                  varchar(255) NOT NULL UNIQUE,
    `created_at`            DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

-- challenge definition

CREATE TABLE `challenge`
(
    `id`                    bigint(20) NOT NULL AUTO_INCREMENT,
    `slug`                  varchar(255) NOT NULL,
    `name`                  varchar(255) DEFAULT NULL,
    `headline`              varchar(80),
    `description`           varchar(1000) NOT NULL,
    `avatar_url`            varchar(255),
    `website_url`           varchar(255) NOT NULL,
    `status`                ENUM('upcoming', 'active', 'completed'),
    `difficulty`            ENUM('good_for_beginners', 'intermediate', 'advanced'),
    `platform_id`           int,
    `doi`                   varchar(80),
    `start_date`            DATE,
    `end_date`              DATE,
    -- `email`                 varchar(255) DEFAULT NULL,
    -- `login`                 varchar(255) UNIQUE,
    `created_at`            DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at`            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`platform_id`) REFERENCES challenge_platform(`id`)
);

-- KEY                `FKk9w2ogq595jbe8r2due7vv3xr` (`account_id`),
-- CONSTRAINT `FKk9w2ogq595jbe8r2due7vv3xr` FOREIGN KEY (`account_id`) REFERENCES `banking_core_account` (`id`)

-- challenge_organization_role definition

CREATE TABLE `challenge_contribution`
(
    `id`                    int NOT NULL AUTO_INCREMENT,
    `challenge_id`          bigint(20) NOT NULL,
    `organization_id`       bigint(20) NOT NULL,
    `role`                  ENUM('challenge_organizer', 'data_contributor', 'sponsor'),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`challenge_id`) REFERENCES challenge(`id`),
    CONSTRAINT unique_item UNIQUE (`id`)
);

-- challenge_incentive definition

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

-- challenge_submission_type definition

CREATE TABLE `challenge_submission_type`
(
    `id`                    int NOT NULL AUTO_INCREMENT,
    `name`                  ENUM('container_image', 'prediction_file', 'notebook', 'other'),
    `challenge_id`          bigint(20) NOT NULL,
    `created_at`            DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`challenge_id`) REFERENCES challenge(`id`),
    CONSTRAINT unique_item UNIQUE (`name`, `challenge_id`)
);

-- challenge_x_challenge_input_data_type definition

CREATE TABLE `challenge_x_challenge_input_data_type`
(
    `id`                              int NOT NULL AUTO_INCREMENT,
    `challenge_id`                    bigint(20) NOT NULL,
    `challenge_input_data_type_id`    int NOT NULL,
    `created_at`                      DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`challenge_id`) REFERENCES challenge(`id`),
    FOREIGN KEY (`challenge_input_data_type_id`) REFERENCES challenge_input_data_type(`id`),
    CONSTRAINT unique_item UNIQUE (`challenge_id`, `challenge_input_data_type_id`)
);

-- challenge_star definition

CREATE TABLE `challenge_star`
(
    `id`                    int NOT NULL AUTO_INCREMENT,
    `challenge_id`          bigint(20) NOT NULL,
    `user_id`               bigint(20) NOT NULL,
    `created_at`            DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`challenge_id`) REFERENCES challenge(`id`),
    CONSTRAINT unique_item UNIQUE (`challenge_id`, `user_id`)
);

-- challenge_category definition

CREATE TABLE `challenge_category`
(
    `id`                    int NOT NULL AUTO_INCREMENT,
    `challenge_id`          bigint(20) NOT NULL,
    `category`              ENUM('featured', 'benchmark'),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`challenge_id`) REFERENCES challenge(`id`),
    CONSTRAINT unique_item UNIQUE (`id`)
);
