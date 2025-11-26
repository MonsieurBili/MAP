-- SQL script to create the events table in the PostgreSQL database
-- Run this script in your PostgreSQL database to create the events table

CREATE TABLE IF NOT EXISTS events (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL
);

-- Junction table for event participants (SwimmingDucks)
CREATE TABLE IF NOT EXISTS event_participants (
    event_id BIGINT NOT NULL,
    duck_id BIGINT NOT NULL,
    PRIMARY KEY (event_id, duck_id),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (duck_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Junction table for event subscribers (Users)
CREATE TABLE IF NOT EXISTS event_subscribers (
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (event_id, user_id),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
