create table if not exists images
(
    id    serial
        constraint images_pk primary key,
    image bytea not null
);--

create table if not exists users
(
    id                              serial
        constraint users_pk primary key,
    email                           varchar(255) not null,
    username                        varchar(255),
    password                        varchar(255) not null,
    registered                      boolean      not null default true,
    created_at                      timestamp    not null default now(),
    is_provider                     boolean      not null default false,
    verified                        boolean      not null default false,
    verification_token              varchar(1024),
    verification_token_created_at   timestamp             default now(),
    reset_password_token            varchar(1024),
    reset_password_token_created_at timestamp             default now(),
    profile_image_id                integer,
    description                     text,
    language                        varchar(5),
    constraint users_profile_image_id_fk foreign key (profile_image_id) references images (id)
);--

create table if not exists offerings
(
    id          serial
        constraint offerings_pk primary key,
    user_id     integer not null
        constraint offerings_users_id_fk
            references users
            on delete cascade,
    name        text,
    category    text
        constraint category_check check (category = ANY
                                         (ARRAY ['PHOTOGRAPHY'::text, 'VIDEO'::text, 'MUSIC'::text, 'CATERING'::text, 'DECORATION'::text, 'FLOWERS'::text, 'VENUE'::text, 'OTHER'::text])),
    description text,
    min_price   numeric(10, 2),
    max_price   numeric(10, 2),
    price_type  text
        constraint price_type_check check (price_type = ANY
                                           (ARRAY ['PER_HOUR'::text, 'PER_DAY'::text, 'PER_EVENT'::text, 'PER_PERSON'::text, 'PER_ITEM'::text, 'OTHER'::text])),
    max_guests  integer,
    district    VARCHAR(255),
    deleted     boolean not null default false
);--

create table if not exists offering_images
(
    image_id    integer not null
        constraint offerings_images_image_id_fk
            references images
            on delete cascade,
    offering_id integer not null
        constraint offerings_images_offering_id_fk
            references offerings
            on delete cascade,
    constraint offerings_images_pk primary key (image_id, offering_id)
);--

create table if not exists liked_offerings
(
    user_id     integer not null
        constraint liked_offerings_users_id_fk
            references users
            on delete cascade,
    offering_id integer not null
        constraint liked_offerings_offerings_id_fk
            references offerings
            on delete cascade,
    constraint liked_offerings_pk primary key (user_id, offering_id)
);--

create table if not exists events
(
    id               serial
        constraint events_pk primary key,
    user_id          integer not null
        constraint events_users_id_fk
            references users
            on delete cascade,
    name             text,
    description      text,
    date             date,
    number_of_guests integer,
    district         varchar(255)
);--

create table if not exists events_offerings
(
    relation_id serial
        constraint events_offerings_pk primary key,
    event_id    integer not null
        constraint events_offerings_events_id_fk
            references events
            on delete cascade,
    offering_id integer not null
        constraint events_offerings_offerings_id_fk
            references offerings
            on delete cascade,
    status      varchar(16)
        constraint status_check not null check (status = ANY
                                                (ARRAY ['NEW'::varchar, 'PENDING'::varchar, 'ACCEPTED'::varchar, 'REJECTED'::varchar, 'DONE'::varchar]))
        default 'NEW'::varchar,
    constraint events_offerings_pk unique (event_id, offering_id)
);--


create table if not exists messages
(
    id          serial
        constraint messages_pk primary key,
    sender_id   integer   not null
        constraint messages_sender_id_fk references users (id) on delete cascade,
    receiver_id integer   not null
        constraint messages_receiver_id_fk references users (id) on delete cascade,
    relation_id integer
        constraint messages_relation_id_fk references events_offerings (relation_id) on delete cascade,
    content     text,
    created_at  timestamp not null default now(),
    read        boolean   not null default false
);--

create table if not exists reviews
(
    relation_id integer
        constraint reviews_relation_id_fk references events_offerings (relation_id) on delete cascade primary key,
    content     text,
    rating      integer
        constraint rating_check check (rating >= 1 and rating <= 5),
    created_at  timestamp not null default now()
);

create table if not exists event_guests
(
    id            serial
        constraint event_guests_pk primary key,
    event_id      integer
        constraint event_guests_event_id_fk references events (id) on delete cascade,
    guest_mail    varchar(255),
    invite_token  varchar(255),
    invite_status varchar(16)
        constraint invite_status_check check (invite_status = ANY
                                              (ARRAY ['NEW'::varchar,'PENDING'::varchar, 'ACCEPTED'::varchar, 'REJECTED'::varchar])) default 'PENDING'::varchar,
    constraint event_guests_unique unique (event_id, guest_mail)
);



CREATE or replace VIEW conversation AS
SELECT messages.relation_id    AS relation_id,
       (SELECT count(distinct m1.id)
        FROM messages m1
                 JOIN events_offerings eo ON eo.relation_id = m1.relation_id
                 JOIN events e ON e.id = eo.event_id
        WHERE messages.relation_id = m1.relation_id
          AND m1.receiver_id = e.user_id
          AND m1.read = false) AS organizer_unread_messages,
       (SELECT count(distinct m1.id)
        FROM messages m1
                 JOIN events_offerings eo ON eo.relation_id = m1.relation_id
                 JOIN events e ON e.id = eo.event_id
        WHERE messages.relation_id = m1.relation_id
          AND m1.receiver_id != e.user_id
          AND m1.read = false) AS provider_unread_messages,
       (SELECT m1.id
        FROM messages m1
        WHERE m1.relation_id = messages.relation_id
        ORDER BY m1.created_at DESC
        LIMIT 1)               AS last_message
FROM messages
GROUP BY messages.relation_id;


