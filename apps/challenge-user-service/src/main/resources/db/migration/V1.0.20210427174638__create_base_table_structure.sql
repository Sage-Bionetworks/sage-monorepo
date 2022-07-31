-- challenge.challenge_user definition

CREATE TABLE `challenge_user`
(
    `id`                    bigint(20) NOT NULL AUTO_INCREMENT,
    `auth_id`               varchar(255) DEFAULT NULL,
    `status`                varchar(255) DEFAULT NULL,
    `username`              varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
);
