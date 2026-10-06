-- V7: yêu cầu khách hàng theo spec FE (/storefront/customer-request/..., /admin/customer-request/...). THÊM MỚI,
-- không sửa V1-V6. Dùng SEQUENCE tường minh như document/1. Table.sql.
--
-- Một bảng chung cho 3 loại yêu cầu (type): BACK_IN_STOCK (báo khi có hàng lại, theo SĐT + SKU), ORDER_SUPPORT (hỗ
-- trợ đơn hàng, theo SĐT + mã đơn), NEWSLETTER (đăng ký nhận tin, theo email). contact_channel/contact_value là kênh
-- liên hệ. Các cột *_snapshot chụp lại tên/ảnh/biến thể sản phẩm lúc gửi để admin vẫn xem được nếu sản phẩm đổi/xoá.
-- sku_id/product_id/order_id là FK nới lỏng (ON DELETE SET NULL); order_code lưu đúng chuỗi khách nhập (kể cả khi
-- không khớp đơn nào - không để lộ mã đơn có tồn tại hay không).

CREATE SEQUENCE customer_requests_id_seq;

CREATE TABLE customer_requests (
    id                          BIGINT PRIMARY KEY DEFAULT nextval('customer_requests_id_seq'),
    type                        VARCHAR(20) NOT NULL,
    contact_channel             VARCHAR(10) NOT NULL,
    contact_value               VARCHAR(255) NOT NULL,
    status                      VARCHAR(20) NOT NULL DEFAULT 'NEW',
    product_id                  BIGINT REFERENCES products (id) ON DELETE SET NULL,
    sku_id                      BIGINT REFERENCES product_skus (id) ON DELETE SET NULL,
    sku_code                    VARCHAR(64),
    product_name_snapshot       VARCHAR(200),
    product_image_url_snapshot  VARCHAR(500),
    variant_text_snapshot       VARCHAR(300),
    order_id                    BIGINT REFERENCES orders (id) ON DELETE SET NULL,
    order_code                  VARCHAR(60),
    order_type                  VARCHAR(20),
    created_id                  BIGINT,
    created_date                TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_id                  BIGINT,
    updated_date                TIMESTAMPTZ,
    CONSTRAINT ck_customer_requests_type CHECK (type IN ('BACK_IN_STOCK', 'ORDER_SUPPORT', 'NEWSLETTER')),
    CONSTRAINT ck_customer_requests_channel CHECK (contact_channel IN ('PHONE', 'EMAIL')),
    CONSTRAINT ck_customer_requests_status CHECK (status IN ('NEW', 'UNPROCESSED', 'CONTACTED', 'IN_PROGRESS', 'COMPLETED')),
    CONSTRAINT ck_customer_requests_order_type CHECK (order_type IS NULL OR order_type IN ('ORDER', 'PRE_ORDER'))
);
CREATE INDEX ix_customer_requests_type ON customer_requests (type, status, created_date DESC);

ALTER SEQUENCE customer_requests_id_seq OWNED BY customer_requests.id;
