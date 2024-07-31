CREATE TABLE IF NOT EXISTS users
(
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    user_id            VARCHAR(255) NOT NULL,
    first_name         VARCHAR(50)  NOT NULL,
    last_name          VARCHAR(58)  NOT NULL,
    email              VARCHAR(100) NOT NULL,
    phone              VARCHAR(30)           DEFAULT NULL,
    bio                VARCHAR(255)          DEFAULT NULL,
    reference_id       VARCHAR(255) NOT NULL,
    qr_code_image_uri  TEXT                  DEFAULT NULL,
    image_url          VARCHAR(255)          DEFAULT 'https://cdn-icons-png.flaticon.com/512/149/149071.png',
    last_login         TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    login_attempts     INT                   DEFAULT 0,
    mfa                BOOLEAN      NOT NULL DEFAULT FALSE,
    enabled            BOOLEAN      NOT NULL DEFAULT FALSE,
    non_expired        BOOLEAN      NOT NULL DEFAULT FALSE,
    non_locked         BOOLEAN      NOT NULL DEFAULT FALSE,
    account_created_by BIGINT       NOT NULL,
    account_updated_by BIGINT       NOT NULL,
    created_at         TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_users_email UNIQUE KEY (email),
    CONSTRAINT uq_users_user_id UNIQUE KEY (user_id),
    FOREIGN KEY fk_users_created_by (account_created_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY fk_users_updated_by (account_updated_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS confirmations
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    confirm_key  VARCHAR(255) NOT NULL,
    user_id      BIGINT       NOT NULL,
    reference_id VARCHAR(255) NOT NULL,
    created_by   BIGINT       NOT NULL,
    updated_by   BIGINT       NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_confirmations_confirm_key UNIQUE KEY (confirm_key),
    CONSTRAINT uq_confirmations_user_id UNIQUE KEY (user_id),
    FOREIGN KEY fk_confirmations_created_by (created_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY fk_confirmations_updated_by (updated_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY fk_confirmations_user_id (user_id) REFERENCES users (user_id) ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS credentials
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    reference_id VARCHAR(255) NOT NULL,
    password     VARCHAR(255) NOT NULL,
    created_by   BIGINT       NOT NULL,
    updated_by   BIGINT       NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_credentials_user_id UNIQUE KEY (user_id),
    FOREIGN KEY fk_credentials_user_id (user_id) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY fk_credentials_created_by (created_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY fk_credentials_updated_by (updated_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS ducuments
(
    id             INT AUTO_INCREMENT PRIMARY KEY,
    document_id    VARCHAR(255) NOT NULL,
    reference_id   VARCHAR(255) NOT NULL,
    extension      VARCHAR(10)  NOT NULL,
    formatted_size VARCHAR(20)  NOT NULL,
    icon           VARCHAR(255) NOT NULL,
    name           VARCHAR(255) NOT NULL,
    size           BIGINT       NOT NULL,
    uri            VARCHAR(255) NOT NULL,
    description    VARCHAR(255) DEFAULT NULL,
    created_by     BIGINT       NOT NULL,
    updated_by     BIGINT       NOT NULL,
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_documents_document_id UNIQUE KEY (document_id),
    FOREIGN KEY fk_documents_created_by (created_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY fk_documents_updated_by (updated_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS roles
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    authority    VARCHAR(255) NOT NULL,
    name         VARCHAR(255) NOT NULL,
    reference_id VARCHAR(255) NOT NULL,
    created_by   BIGINT       NOT NULL,
    updated_by   BIGINT       NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY fk_roles_created_by (created_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY fk_roles_updated_by (updated_by) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_roles
(
    id      INT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    FOREIGN KEY fk_user_roles_user_id (user_id) REFERENCES users (id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY fk_user_roles_role_id (role_id) REFERENCES roles (id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX index_users_email ON users (email);
CREATE INDEX index_users_user_id ON users (user_id);
CREATE INDEX index_confirmations_user_id ON confirmations (user_id);
CREATE INDEX index_credentials_user_id ON credentials (user_id);
CREATE INDEX index_user_roles_user_id ON user_roles (user_id);


