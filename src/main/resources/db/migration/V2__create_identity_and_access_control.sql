CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(320) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    phone VARCHAR(30),
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(40) NOT NULL,
    email_verified_at TIMESTAMPTZ,
    phone_verified_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    system_role BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_roles_code UNIQUE (code)
);

CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(100) NOT NULL,
    name VARCHAR(150) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_permissions_code UNIQUE (code)
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles (id) ON DELETE RESTRICT,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE role_permissions (
    role_id UUID NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions (id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE refresh_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    device_name VARCHAR(150),
    ip_hash VARCHAR(128),
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_refresh_sessions_token_hash UNIQUE (token_hash)
);

CREATE TABLE consents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    consent_type VARCHAR(80) NOT NULL,
    policy_version VARCHAR(50) NOT NULL,
    granted BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_consents_user_type_version UNIQUE (user_id, consent_type, policy_version)
);

CREATE INDEX idx_users_status ON users (status);
CREATE INDEX idx_refresh_sessions_user_id ON refresh_sessions (user_id);
CREATE INDEX idx_refresh_sessions_expires_at ON refresh_sessions (expires_at);
CREATE INDEX idx_consents_user_id ON consents (user_id);

INSERT INTO roles (code, name) VALUES
    ('CUSTOMER', 'Müşteri'),
    ('BOOSTER', 'Booster'),
    ('BOOSTER_ADMIN', 'Booster Yöneticisi'),
    ('SUPPORT_AGENT', 'Destek Uzmanı'),
    ('OPERATIONS_MANAGER', 'Operasyon Yöneticisi'),
    ('FINANCE', 'Finans'),
    ('CONTENT_EDITOR', 'İçerik Editörü'),
    ('ADMIN', 'Sistem Yöneticisi')
ON CONFLICT (code) DO NOTHING;
