-- organization definition

CREATE TABLE `organization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `login` varchar(64) NOT NULL UNIQUE,
  `avatar_key` varchar(255) DEFAULT NULL,
  `website_url` varchar(255) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `challenge_count` int,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `acronym` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT login_check CHECK (char_length(login) >= 2)
);

-- organization_category definition

CREATE TABLE `organization_category`
(
    `id`                    int NOT NULL AUTO_INCREMENT,
    `organization_id`       bigint(20) NOT NULL,
    `category`              ENUM('featured'),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`organization_id`) REFERENCES organization(`id`),
    CONSTRAINT unique_item UNIQUE (`id`)
);

-- contributor_roles definition

CREATE TABLE `challenge_contribution`
(
    `id`                    int NOT NULL AUTO_INCREMENT,
    `challenge_id`          bigint(20) NOT NULL,
    `organization_id`       bigint(20) NOT NULL,
    `role`                  ENUM('challenge_organizer', 'data_contributor', 'sponsor'),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`organization_id`) REFERENCES organization(`id`),
    CONSTRAINT unique_item UNIQUE (`id`)
);