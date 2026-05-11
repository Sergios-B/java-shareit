CREATE TABLE IF NOT EXISTS users(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(400) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    owner_id BIGINT NOT NULL,
    request_id BIGINT DEFAULT 0,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT fk_item_to_user FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS booking(
      id BIGINT GENERATED ALWAYS AS IDENTITY,
      start_date TIMESTAMP WITH TIME ZONE NOT NULL,
      end_date TIMESTAMP WITH TIME ZONE NOT NULL,
      item_id BIGINT,
      booker_id BIGINT,
      status VARCHAR(50),
      CONSTRAINT pk_booking PRIMARY KEY (id),
      CONSTRAINT fk_booking_to_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
      CONSTRAINT fk_booking_to_user FOREIGN KEY (booker_id) REFERENCES users(id) ON DELETE CASCADE,
      CONSTRAINT uq_booking_unit UNIQUE (start_date, end_date, item_id)
);

CREATE TABLE IF NOT EXISTS comments(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    text TEXT NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_comment PRIMARY KEY (id),
    CONSTRAINT fk_comment_to_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE,
    CONSTRAINT fr_comment_to_user FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    description TEXT NOT NULL,
    requestor_id BIGINT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id)
)