CREATE TABLE service_instance
(
    id BIGSERIAL PRIMARY KEY,

    service_name VARCHAR(100) NOT NULL,

    version VARCHAR(20) NOT NULL,

    url VARCHAR(255) NOT NULL,

    weight INTEGER NOT NULL,

    active BOOLEAN NOT NULL
);