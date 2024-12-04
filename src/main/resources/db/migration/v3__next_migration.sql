--V3__next_migration.sql
CREATE TABLE auth (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    isactive BOOLEAN DEFAULT FALSE
);