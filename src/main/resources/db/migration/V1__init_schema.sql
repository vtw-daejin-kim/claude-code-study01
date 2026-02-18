-- ============================================================
-- V1: Initial Schema — E-Commerce MVP
-- ============================================================

-- 1. users
CREATE TABLE users (
    id              BIGSERIAL       PRIMARY KEY,
    login_id        VARCHAR(50)     NOT NULL UNIQUE,
    email           VARCHAR(255)    NOT NULL UNIQUE,
    password_hash   VARCHAR(255)    NOT NULL,
    name            VARCHAR(100)    NOT NULL,
    role            VARCHAR(20)     NOT NULL DEFAULT 'USER',
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now()
);

-- 2. brands
CREATE TABLE brands (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(200)    NOT NULL,
    description     TEXT,
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE INDEX idx_brands_status ON brands (status);

-- 3. products
CREATE TABLE products (
    id              BIGSERIAL       PRIMARY KEY,
    brand_id        BIGINT          NOT NULL REFERENCES brands(id),
    name            VARCHAR(300)    NOT NULL,
    description     TEXT,
    price           DECIMAL(15, 2)  NOT NULL,
    image_url       VARCHAR(1000),
    status          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    deleted_at      TIMESTAMP,
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE INDEX idx_products_brand_id ON products (brand_id);
CREATE INDEX idx_products_status ON products (status);
CREATE INDEX idx_products_created_at ON products (created_at DESC);

-- 4. product_stock (1:1 with products)
CREATE TABLE product_stock (
    product_id      BIGINT          PRIMARY KEY REFERENCES products(id),
    on_hand         INTEGER         NOT NULL DEFAULT 0,
    reserved        INTEGER         NOT NULL DEFAULT 0,
    CONSTRAINT chk_on_hand_non_negative CHECK (on_hand >= 0),
    CONSTRAINT chk_reserved_non_negative CHECK (reserved >= 0)
);

-- 5. product_revisions (audit trail)
CREATE TABLE product_revisions (
    id              BIGSERIAL       PRIMARY KEY,
    product_id      BIGINT          NOT NULL REFERENCES products(id),
    change_type     VARCHAR(20)     NOT NULL,
    before_snapshot JSONB,
    after_snapshot  JSONB           NOT NULL,
    changed_by      VARCHAR(100)    NOT NULL,
    change_reason   VARCHAR(500),
    changed_at      TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE INDEX idx_product_revisions_product_id ON product_revisions (product_id);

-- 6. likes
CREATE TABLE likes (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    product_id      BIGINT          NOT NULL REFERENCES products(id),
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    CONSTRAINT uk_likes_user_product UNIQUE (user_id, product_id)
);

CREATE INDEX idx_likes_product_id ON likes (product_id);

-- 7. cart_items
CREATE TABLE cart_items (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    product_id      BIGINT          NOT NULL REFERENCES products(id),
    quantity        INTEGER         NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now(),
    CONSTRAINT uk_cart_items_user_product UNIQUE (user_id, product_id),
    CONSTRAINT chk_cart_quantity_positive CHECK (quantity > 0)
);

CREATE INDEX idx_cart_items_user_id ON cart_items (user_id);

-- 8. orders
CREATE TABLE orders (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id),
    status          VARCHAR(30)     NOT NULL DEFAULT 'PENDING_PAYMENT',
    order_source    VARCHAR(20)     NOT NULL,
    idempotency_key VARCHAR(100)    UNIQUE,
    total_amount    DECIMAL(15, 2)  NOT NULL,
    expires_at      TIMESTAMP       NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE INDEX idx_orders_user_id ON orders (user_id);
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_expires_at ON orders (expires_at) WHERE status = 'PENDING_PAYMENT';

-- 9. order_items
CREATE TABLE order_items (
    id                      BIGSERIAL       PRIMARY KEY,
    order_id                BIGINT          NOT NULL REFERENCES orders(id),
    product_id              BIGINT          NOT NULL REFERENCES products(id),
    quantity                INTEGER         NOT NULL,
    snapshot_unit_price     DECIMAL(15, 2)  NOT NULL,
    snapshot_product_name   VARCHAR(300)    NOT NULL,
    snapshot_brand_id       BIGINT          NOT NULL,
    snapshot_brand_name     VARCHAR(200)    NOT NULL,
    snapshot_image_url      VARCHAR(1000)
);

CREATE INDEX idx_order_items_order_id ON order_items (order_id);

-- 10. order_cart_restore
CREATE TABLE order_cart_restore (
    id              BIGSERIAL       PRIMARY KEY,
    order_id        BIGINT          NOT NULL UNIQUE REFERENCES orders(id),
    reason          VARCHAR(30)     NOT NULL,
    restored_at     TIMESTAMP       NOT NULL DEFAULT now()
);
