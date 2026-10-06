-- V5: giỏ hàng (khách đăng nhập + khách vãng lai) và danh sách yêu thích - spec FE /storefront/cart/cart/...
-- và /storefront/product/customer/wishlist. THÊM MỚI, không sửa V1-V4. Dùng SEQUENCE tường minh như
-- document/1. Table.sql.
--
-- carts: mỗi khách đăng nhập có đúng 1 giỏ (user_id UNIQUE); khách vãng lai có giỏ định danh bằng
-- guest_id (UUID do server cấp, FE gửi lại ở header X-Guest-Cart-Id) - đúng 1 trong 2 cột được điền.
-- cart_items: mỗi SKU tối đa 1 dòng trong 1 giỏ (UNIQUE cart_id + sku_id), quantity >= 1.
-- wishlist_items: mỗi (khách, sản phẩm) tối đa 1 dòng.

CREATE SEQUENCE carts_id_seq;
CREATE SEQUENCE cart_items_id_seq;
CREATE SEQUENCE wishlist_items_id_seq;

CREATE TABLE carts (
    id            BIGINT PRIMARY KEY DEFAULT nextval('carts_id_seq'),
    user_id       BIGINT REFERENCES users (id) ON DELETE CASCADE,
    guest_id      VARCHAR(64),
    created_id    BIGINT,
    created_date  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_id    BIGINT,
    updated_date  TIMESTAMPTZ,
    CONSTRAINT uq_carts_user UNIQUE (user_id),
    CONSTRAINT uq_carts_guest UNIQUE (guest_id),
    CONSTRAINT ck_carts_owner CHECK ((user_id IS NOT NULL AND guest_id IS NULL) OR (user_id IS NULL AND guest_id IS NOT NULL))
);

CREATE TABLE cart_items (
    id            BIGINT PRIMARY KEY DEFAULT nextval('cart_items_id_seq'),
    cart_id       BIGINT NOT NULL REFERENCES carts (id) ON DELETE CASCADE,
    sku_id        BIGINT NOT NULL REFERENCES product_skus (id) ON DELETE CASCADE,
    quantity      INT NOT NULL,
    created_id    BIGINT,
    created_date  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_id    BIGINT,
    updated_date  TIMESTAMPTZ,
    CONSTRAINT uq_cart_items_cart_sku UNIQUE (cart_id, sku_id),
    CONSTRAINT ck_cart_items_quantity CHECK (quantity >= 1)
);

CREATE TABLE wishlist_items (
    id            BIGINT PRIMARY KEY DEFAULT nextval('wishlist_items_id_seq'),
    user_id       BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    product_id    BIGINT NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    created_id    BIGINT,
    created_date  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_wishlist_items_user_product UNIQUE (user_id, product_id)
);
CREATE INDEX ix_wishlist_items_user ON wishlist_items (user_id, created_date DESC);

ALTER SEQUENCE carts_id_seq OWNED BY carts.id;
ALTER SEQUENCE cart_items_id_seq OWNED BY cart_items.id;
ALTER SEQUENCE wishlist_items_id_seq OWNED BY wishlist_items.id;
