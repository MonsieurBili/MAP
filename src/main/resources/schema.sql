-- Database schema for DuckNetwork application
-- PostgreSQL Database: MAP

-- Users table (base table for all user types)
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_type VARCHAR(50) NOT NULL CHECK (user_type IN ('PERSON', 'DUCK'))
);

-- Persons table (extends users)
CREATE TABLE IF NOT EXISTS persons (
    id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    last_name VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    occupation VARCHAR(100),
    empathy_level INTEGER DEFAULT 0
);

-- Ducks table (extends users)
CREATE TABLE IF NOT EXISTS ducks (
    id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    tip_rata VARCHAR(50) NOT NULL CHECK (tip_rata IN ('FLYING', 'SWIMMING', 'FLYING_AND_SWIMMING')),
    viteza DOUBLE PRECISION NOT NULL,
    rezistenta DOUBLE PRECISION NOT NULL
);

-- Friendships table
CREATE TABLE IF NOT EXISTS friendships (
    id SERIAL PRIMARY KEY,
    user1_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user2_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT friendship_unique UNIQUE (user1_id, user2_id),
    CONSTRAINT no_self_friendship CHECK (user1_id != user2_id)
);

-- Race events table
CREATE TABLE IF NOT EXISTS race_events (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL
);

-- Lanes table (for race events)
CREATE TABLE IF NOT EXISTS lanes (
    id SERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES race_events(id) ON DELETE CASCADE,
    lane_length DOUBLE PRECISION NOT NULL
);

-- Event participants table
CREATE TABLE IF NOT EXISTS event_participants (
    id SERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES race_events(id) ON DELETE CASCADE,
    duck_id BIGINT NOT NULL REFERENCES ducks(id) ON DELETE CASCADE,
    CONSTRAINT participant_unique UNIQUE (event_id, duck_id)
);

-- Event subscribers table
CREATE TABLE IF NOT EXISTS event_subscribers (
    id SERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES race_events(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT subscriber_unique UNIQUE (event_id, user_id)
);

-- Cards (flocks) table
CREATE TABLE IF NOT EXISTS cards (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    tip_rata VARCHAR(50) NOT NULL CHECK (tip_rata IN ('FLYING', 'SWIMMING', 'FLYING_AND_SWIMMING'))
);

-- Card members table
CREATE TABLE IF NOT EXISTS card_members (
    id SERIAL PRIMARY KEY,
    card_id BIGINT NOT NULL REFERENCES cards(id) ON DELETE CASCADE,
    duck_id BIGINT NOT NULL REFERENCES ducks(id) ON DELETE CASCADE,
    CONSTRAINT card_member_unique UNIQUE (card_id, duck_id)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_friendships_user1 ON friendships(user1_id);
CREATE INDEX IF NOT EXISTS idx_friendships_user2 ON friendships(user2_id);
CREATE INDEX IF NOT EXISTS idx_lanes_event ON lanes(event_id);
CREATE INDEX IF NOT EXISTS idx_event_participants_event ON event_participants(event_id);
CREATE INDEX IF NOT EXISTS idx_event_subscribers_event ON event_subscribers(event_id);
CREATE INDEX IF NOT EXISTS idx_card_members_card ON card_members(card_id);
