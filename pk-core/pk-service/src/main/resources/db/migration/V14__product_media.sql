-- V14: thư viện ảnh/video của sản phẩm (sku_id NULL = dùng chung cho cả sản phẩm; có sku_id = riêng SKU).
CREATE SEQUENCE IF NOT EXISTS product_media_id_seq;
CREATE TABLE product_media (
    id            BIGINT PRIMARY KEY DEFAULT nextval('product_media_id_seq'),
    product_id    BIGINT NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    sku_id        BIGINT REFERENCES product_skus (id) ON DELETE CASCADE,
    url           VARCHAR(500) NOT NULL,
    alt           VARCHAR(200),
    media_type    VARCHAR(10) NOT NULL DEFAULT 'IMAGE',
    sort_order    INTEGER NOT NULL DEFAULT 0,
    is_primary    BOOLEAN NOT NULL DEFAULT FALSE,
    created_id    BIGINT,
    created_date  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_id    BIGINT,
    updated_date  TIMESTAMPTZ,
    CONSTRAINT ck_product_media_type CHECK (media_type IN ('IMAGE', 'VIDEO'))
);
CREATE INDEX ix_product_media_product ON product_media (product_id, sort_order);
ALTER SEQUENCE product_media_id_seq OWNED BY product_media.id;
