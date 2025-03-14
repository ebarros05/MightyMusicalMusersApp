create table artist
(
    id   integer not null
        primary key,
    name varchar not null
);

alter table artist
    owner to p32001_30;

create table users
(
    username           varchar(50) not null
        constraint user_pkey
            primary key,
    password           varchar(50) not null
        constraint passwordcheck
            check ((password)::text ~ '^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^a-zA-Z\d]).{8,}$'::text),
    first              varchar(50) not null,
    last               varchar(50) not null,
    email              varchar(50) not null
        constraint user_email_key
            unique
        constraint emailcheck
            check ((email)::text ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'::text),
    date_of_birth      date        not null,
    last_accessed_date timestamp   not null,
    creation_date      timestamp   not null
);

alter table users
    owner to p32001_30;

create table following_user
(
    username  varchar not null
        constraint username
            references users,
    following varchar not null
        constraint following_user_user_username_fk
            references users,
    primary key (username, following)
);

alter table following_user
    owner to p32001_30;

create table following_artist
(
    username  varchar not null
        constraint following_artist_user_username_fk
            references users,
    following integer not null
        constraint following_artist_artist_id_fk
            references artist,
    primary key (username, following)
);

alter table following_artist
    owner to p32001_30;

create table genre
(
    genre_id   integer      not null
        primary key,
    genre_type varchar(100) not null
);

alter table genre
    owner to p32001_30;

create table song
(
    title        varchar(100) not null,
    song_id      integer      not null
        constraint song_id
            primary key,
    track_number integer      not null,
    song_length  integer      not null,
    genre_id     integer      not null
        constraint genre_id
            references genre
);

alter table song
    owner to p32001_30;

create table rating
(
    stars    integer not null,
    username varchar not null
        constraint rating_user_username_fk
            references users,
    song_id  integer not null
        constraint rating_song_song_id_fk
            references song,
    title    varchar,
    constraint rating_pk
        primary key (song_id, username)
);

alter table rating
    owner to p32001_30;

create table play_history
(
    time     timestamp not null,
    username varchar   not null
        constraint play_history_users_username_fk
            references users,
    song_id  integer   not null
        constraint play_history_song_song_id_fk
            references song,
    primary key (time, username)
);

alter table play_history
    owner to p32001_30;

create table album
(
    album_id     integer      not null
        primary key,
    name         varchar(100) not null,
    release_date date         not null,
    genre_id     integer      not null
        constraint album_genre_id_fk
            references genre
);

alter table album
    owner to p32001_30;

create table songs_on_album
(
    song_id  integer not null
        references song,
    album_id integer not null
        references album,
    primary key (song_id, album_id)
);

alter table songs_on_album
    owner to p32001_30;

create table song_written_by
(
    song_id   integer not null
        references song,
    artist_id integer not null
        references artist,
    primary key (song_id, artist_id)
);

alter table song_written_by
    owner to p32001_30;

create table playlist
(
    playlist_name   varchar(100)                            not null,
    playlist_number integer                                 not null,
    username        varchar default NULL::character varying not null
        constraint playlist_user_username_fk
            references users,
    song_id         integer                                 not null
        constraint playlist_song_song_id_fk
            references song,
    constraint playlist_pkeys
        primary key (playlist_name, username, playlist_number)
);

alter table playlist
    owner to p32001_30;

create table album_written_by
(
    album_id  integer not null
        references album,
    artist_id integer not null
        references artist,
    primary key (album_id, artist_id)
);

alter table album_written_by
    owner to p32001_30;

