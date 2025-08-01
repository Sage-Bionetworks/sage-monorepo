CREATE TABLE hello (
    id SERIAL PRIMARY KEY,
    message TEXT
);

INSERT INTO hello (message) VALUES ('Hello World!');