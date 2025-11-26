-- SQL script to create the events table in the PostgreSQL database
-- Run this script in your PostgreSQL database to create the events table

CREATE TABLE IF NOT EXISTS events (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL
);
