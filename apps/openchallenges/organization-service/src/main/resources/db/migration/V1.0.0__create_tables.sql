-- organization definition

CREATE TABLE `organization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `login` varchar(255) NOT NULL UNIQUE,
  `avatar_url` varchar(255) DEFAULT NULL,
  `website_url` varchar(255) DEFAULT NULL,
  `challenge_count` int,
  `description` varchar(280) DEFAULT NULL,
  `challenge_count` int,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

-- contributor_roles definition

CREATE TABLE `challenge_contributor_role`
(
    `id`                    int NOT NULL AUTO_INCREMENT,
    `challenge_id`          bigint(20) NOT NULL,
    `organization_id`       bigint(20) NOT NULL,
    `role`                  ENUM('challenge_organizer', 'data_contributor', 'sponsor'),
    PRIMARY KEY (`id`),
    FOREIGN KEY (`organization_id`) REFERENCES organization(`id`),
    CONSTRAINT unique_item UNIQUE (`id`)
);