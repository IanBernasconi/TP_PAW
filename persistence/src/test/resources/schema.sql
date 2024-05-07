-- Create table for images
CREATE TABLE IF NOT EXISTS images (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    image BLOB NOT NULL
    );

-- Create table for users
CREATE TABLE IF NOT EXISTS users (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    username VARCHAR(255),
    phone_number VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    registered BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    country VARCHAR(255) DEFAULT 'UNITED STATES' NOT NULL ,
    is_provider BOOLEAN DEFAULT FALSE NOT NULL,
    verified BOOLEAN DEFAULT FALSE NOT NULL ,
    verification_token VARCHAR(255),
    verification_token_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reset_password_token VARCHAR(255),
    reset_password_token_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    profile_image_id INTEGER,
    description VARCHAR(4096),
    language VARCHAR(5),
    FOREIGN KEY (profile_image_id) REFERENCES images(id)
    );

-- Create table for offerings
CREATE TABLE IF NOT EXISTS offerings (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255),
    category VARCHAR(255) CHECK (category IN ('PHOTOGRAPHY', 'VIDEO', 'MUSIC', 'CATERING', 'DECORATION', 'FLOWERS', 'VENUE', 'OTHER')),
    description VARCHAR(4096),
    min_price NUMERIC(10, 2),
    max_price NUMERIC(10, 2),
    price_type VARCHAR(255) CHECK (price_type IN ('PER_HOUR', 'PER_DAY', 'PER_EVENT', 'PER_PERSON', 'PER_ITEM')),
    max_guests INTEGER,
    country VARCHAR(255),
    district VARCHAR(255)
    );

-- Create table for offering_images
CREATE TABLE IF NOT EXISTS offering_images (
    image_id INTEGER NOT NULL,
    offering_id INTEGER NOT NULL,
    PRIMARY KEY (image_id, offering_id),
    FOREIGN KEY (image_id) REFERENCES images(id) ON DELETE CASCADE,
    FOREIGN KEY (offering_id) REFERENCES offerings(id) ON DELETE CASCADE
    );

-- Create table for liked_offerings
CREATE TABLE IF NOT EXISTS liked_offerings (
    user_id INTEGER NOT NULL,
    offering_id INTEGER NOT NULL,
    PRIMARY KEY (user_id, offering_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (offering_id) REFERENCES offerings(id) ON DELETE CASCADE
    );

-- Create table for events
CREATE TABLE IF NOT EXISTS events (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255),
    description VARCHAR(4096),
    date DATE,
    number_of_guests INTEGER,
    district VARCHAR(255)
    );

-- Create table for events_offerings
CREATE TABLE IF NOT EXISTS events_offerings (
    relation_id INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) primary key,
    event_id INTEGER NOT NULL,
    offering_id INTEGER NOT NULL,
    status VARCHAR(16) DEFAULT 'NEW' NOT NULL,
    UNIQUE (event_id, offering_id),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (offering_id) REFERENCES offerings(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS messages
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    sender_id integer not null constraint messages_sender_id_fk references users(id) on delete cascade,
    receiver_id integer not null constraint messages_receiver_id_fk references users(id) on delete cascade,
    relation_id integer constraint messages_relation_id_fk references events_offerings(relation_id) on delete cascade,
    content VARCHAR(4096),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    read boolean default false not null
);--

create table if not exists reviews (
    relation_id integer constraint reviews_relation_id_fk references events_offerings(relation_id) on delete cascade,
    offering_id integer constraint reviews_offerings_id_fk references offerings(id) on delete cascade,
    content VARCHAR(4096),
    rating integer constraint rating_check check (rating >= 1 and rating <= 5),
    created_at timestamp default CURRENT_TIMESTAMP not null,
    primary key (relation_id)
);

CREATE TABLE IF NOT EXISTS event_offering_categories
(
    event_id    INTEGER not null constraint event_offering_categories_events_id_fk references events  on delete cascade,
    category    VARCHAR(255) CHECK (category IN ('PHOTOGRAPHY', 'VIDEO', 'MUSIC', 'CATERING', 'DECORATION', 'FLOWERS', 'VENUE', 'OTHER')),
    quantity    integer DEFAULT 1 NOT NULL,
    primary key (event_id, category)
);--

/*
CREATE or replace VIEW conversation AS
SELECT messages.relation_id AS relation_id,
       count(CASE WHEN messages.read = true
                      THEN 1
           END) AS unread_messages,
       (SELECT m1.id
        FROM messages m1
        WHERE m1.relation_id = messages.relation_id
        ORDER BY m1.created_at DESC
        LIMIT 1) AS last_message
FROM messages
GROUP BY messages.relation_id;*/
