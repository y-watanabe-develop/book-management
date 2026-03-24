CREATE TABLE authors (
                         id          BIGSERIAL PRIMARY KEY,
                         name        VARCHAR(255) NOT NULL,
                         birth_date  DATE NOT NULL CHECK (birth_date <= CURRENT_DATE)
);

CREATE TABLE books (
                       id           BIGSERIAL PRIMARY KEY,
                       title        VARCHAR(255) NOT NULL,
                       price        INTEGER NOT NULL CHECK (price >= 0),
                       publish_status VARCHAR(20) NOT NULL DEFAULT 'UNPUBLISHED'
                           CHECK (publish_status IN ('UNPUBLISHED', 'PUBLISHED'))
);

CREATE TABLE book_authors (
                              book_id    BIGINT NOT NULL REFERENCES books(id),
                              author_id  BIGINT NOT NULL REFERENCES authors(id),
                              PRIMARY KEY (book_id, author_id)
);