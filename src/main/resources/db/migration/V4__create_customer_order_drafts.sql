CREATE TABLE customer_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_number VARCHAR(32) NOT NULL,
    customer_id UUID NOT NULL REFERENCES users (id) ON DELETE RESTRICT,
    service_id UUID NOT NULL REFERENCES catalog_services (id) ON DELETE RESTRICT,
    service_code VARCHAR(80) NOT NULL,
    game_code VARCHAR(40) NOT NULL,
    configuration JSONB NOT NULL DEFAULT '{}'::jsonb,
    status VARCHAR(40) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_customer_orders_order_number UNIQUE (order_number)
);

CREATE TABLE customer_order_addons (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES customer_orders (id) ON DELETE CASCADE,
    addon_code VARCHAR(80) NOT NULL,
    addon_name VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_customer_order_addons_order_code UNIQUE (order_id, addon_code)
);

CREATE TABLE customer_order_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES customer_orders (id) ON DELETE CASCADE,
    actor_user_id UUID REFERENCES users (id) ON DELETE SET NULL,
    event_type VARCHAR(60) NOT NULL,
    previous_status VARCHAR(40),
    new_status VARCHAR(40) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb
);

CREATE INDEX idx_customer_orders_customer_updated ON customer_orders (customer_id, updated_at DESC);
CREATE INDEX idx_customer_orders_status_updated ON customer_orders (status, updated_at DESC);
CREATE INDEX idx_customer_order_events_order_occurred ON customer_order_events (order_id, occurred_at DESC);
