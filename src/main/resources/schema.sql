-- DuckNetwork Database Schema
-- PostgreSQL Database Schema for the MAP Project

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS friendships CASCADE;
DROP TABLE IF EXISTS race_event_participants CASCADE;
DROP TABLE IF EXISTS race_event_lanes CASCADE;
DROP TABLE IF EXISTS race_events CASCADE;
DROP TABLE IF EXISTS cards CASCADE;
DROP TABLE IF EXISTS ducks CASCADE;
DROP TABLE IF EXISTS persons CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Users table (base table for all users)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_type VARCHAR(50) NOT NULL, -- 'PERSON' or 'DUCK'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Persons table (extends users)
CREATE TABLE persons (
    id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    last_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    date_of_birth DATE NOT NULL,
    occupation VARCHAR(255),
    empathy_level INTEGER DEFAULT 0
);

-- Ducks table (extends users)
CREATE TABLE ducks (
    id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    duck_type VARCHAR(50) NOT NULL, -- 'FLYING', 'SWIMMING', 'FLYING_AND_SWIMMING'
    speed DOUBLE PRECISION NOT NULL,
    resistance DOUBLE PRECISION NOT NULL
);

-- Cards table
CREATE TABLE cards (
    id BIGSERIAL PRIMARY KEY,
    card_number VARCHAR(255) NOT NULL UNIQUE,
    card_holder VARCHAR(255),
    expiry_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Friendships table
CREATE TABLE friendships (
    id BIGSERIAL PRIMARY KEY,
    user1_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user2_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user1_id, user2_id),
    CHECK (user1_id < user2_id) -- Ensure consistent ordering to prevent duplicates
);

-- Race Events table
CREATE TABLE race_events (
    id BIGSERIAL PRIMARY KEY,
    event_name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Race Event Lanes table
CREATE TABLE race_event_lanes (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES race_events(id) ON DELETE CASCADE,
    lane_number DOUBLE PRECISION NOT NULL
);

-- Race Event Participants table
CREATE TABLE race_event_participants (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES race_events(id) ON DELETE CASCADE,
    duck_id BIGINT NOT NULL REFERENCES ducks(id) ON DELETE CASCADE,
    UNIQUE(event_id, duck_id)
);

-- Create indexes for better query performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_friendships_user1 ON friendships(user1_id);
CREATE INDEX idx_friendships_user2 ON friendships(user2_id);
CREATE INDEX idx_race_event_lanes_event ON race_event_lanes(event_id);
CREATE INDEX idx_race_event_participants_event ON race_event_participants(event_id);
CREATE INDEX idx_race_event_participants_duck ON race_event_participants(duck_id);

-- Insert some sample data (optional, can be removed if not needed)
-- This data matches the format from the text files

COMMENT ON TABLE users IS 'Base table for all users in the system';
COMMENT ON TABLE persons IS 'Person users with personal information';
COMMENT ON TABLE ducks IS 'Duck users for racing';
COMMENT ON TABLE friendships IS 'Relationships between users';
COMMENT ON TABLE race_events IS 'Swimming race events';
COMMENT ON TABLE cards IS 'User cards';
