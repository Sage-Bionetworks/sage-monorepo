INSERT INTO challenge_user (id, email, first_name, identification_number, last_name)
VALUES ('1', 'sam@gmail.com', 'Sam', '808829932V', 'Silva');
INSERT INTO challenge.challenge_user (id, email, first_name, identification_number, last_name)
VALUES ('2', 'guru@gmail.com', 'Guru', '901830556V', 'Darmaraj');
INSERT INTO challenge.challenge_user (id, email, first_name, identification_number, last_name)
VALUES ('3', 'ragu@gmail.com', 'Ragu', '348829932V', 'Sivaraj');
INSERT INTO challenge.challenge_user (id, email, first_name, identification_number, last_name)
VALUES ('4', 'randor@gmail.com', 'Randor', '842829932V', 'Manoon');

INSERT INTO challenge_account
    (actual_balance, available_balance, `number`, status, `type`, user_id)
VALUES (100000.00, 100000.00, 100015003000, 'ACTIVE', 'USER_ACCOUNT', '1'),
       (100000.00, 100000.00, 100015003001, 'ACTIVE', 'USER_ACCOUNT', '1'),
       (100000.00, 100000.00, 100015003002, 'ACTIVE', 'USER_ACCOUNT', '2'),
       (12000.00, 12000.00, 100015003003, 'ACTIVE', 'USER_ACCOUNT', '2'),
       (12000.00, 12000.00, 100015003004, 'ACTIVE', 'USER_ACCOUNT', '2'),
       (12000.00, 12000.00, 100015003005, 'ACTIVE', 'USER_ACCOUNT', '3'),
       (290000.00, 290000.00, 100015003006, 'ACTIVE', 'USER_ACCOUNT', '3'),
       (290000.00, 290000.00, 100015003007, 'ACTIVE', 'USER_ACCOUNT', '3'),
       (290000.00, 290000.00, 100015003008, 'ACTIVE', 'USER_ACCOUNT', '3'),
       (365023.00, 365023.00, 100015003009, 'ACTIVE', 'USER_ACCOUNT', '3'),
       (365023.00, 365023.00, 100015003010, 'ACTIVE', 'USER_ACCOUNT', '4'),
       (365023.00, 89456.00, 100015003011, 'ACTIVE', 'USER_ACCOUNT', '4'),
       (89456.00, 89456.00, 100015003012, 'ACTIVE', 'USER_ACCOUNT', '4'),
       (889000.33, 889000.33, 100015003013, 'ACTIVE', 'USER_ACCOUNT', '4');
