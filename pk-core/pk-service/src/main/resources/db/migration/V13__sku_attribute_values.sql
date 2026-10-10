-- V13: gán thuộc tính (product_attributes) cho từng SKU: mỗi (sku, attribute) tối đa 1 dòng; MULTISELECT lưu "a||b".
CREATE SEQUENCE IF NOT EXISTS sku_attribute_values_id_seq;
CREATE TABLE sku_attribute_values (
    id            BIGINT PRIMARY KEY DEFAULT nextval('sku_attribute_values_id_seq'),
    sku_id        BIGINT NOT NULL REFERENCES product_skus (id) ON DELETE CASCADE,
    attribute_id  BIGINT NOT NULL REFERENCES product_attributes (id) ON DELETE CASCADE,
    value         VARCHAR(1000) NOT NULL,
    created_id    BIGINT,
    created_date  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_id    BIGINT,
    updated_date  TIMESTAMPTZ,
    CONSTRAINT uq_sku_attribute_values UNIQUE (sku_id, attribute_id)
);
CREATE INDEX ix_sku_attribute_values_sku ON sku_attribute_values (sku_id);
ALTER SEQUENCE sku_attribute_values_id_seq OWNED BY sku_attribute_values.id;
