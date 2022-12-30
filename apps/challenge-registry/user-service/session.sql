-- @block list users

SELECT * FROM challenge_user;

-- @block delete user by id

DELETE FROM challenge_user WHERE id=1;

-- @block delete all users

DELETE FROM challenge_user;

-- @block delete flyway schema history

DELETE FROM flyway_schema_history;