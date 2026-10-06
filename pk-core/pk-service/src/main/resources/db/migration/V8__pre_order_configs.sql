-- V8: cấu hình đặt trước (pre-order) theo spec FE (GET/POST /admin/pre-orders, body {enabled, message}). THÊM MỚI,
-- không sửa V1-V7. Dùng SEQUENCE tường minh như document/1. Table.sql.
--
-- Spec chỉ ghi "Danh sách đơn đặt trước" (GET) và "Tạo cấu hình đơn đặt trước" (POST {enabled, message}) nên được hiểu là
-- LỊCH SỬ cấu hình bật/tắt đặt trước: mỗi lần POST thêm 1 dòng, dòng MỚI NHẤT là cấu hình đang hiệu lực. message là thông
-- điệp hiển thị cho khách. CHƯA có luồng đặt hàng pre-order thật (xem document 00-GHI-NHO-DU-AN.md).

CREATE SEQUENCE pre_order_configs_id_seq;

CREATE TABLE pre_order_configs (
    id            BIGINT PRIMARY KEY DEFAULT nextval('pre_order_configs_id_seq'),
    enabled       BOOLEAN NOT NULL,
    message       VARCHAR(500),
    created_id    BIGINT,
    created_date  TIMESTAMPTZ NOT NULL DEFAULT now()
);

ALTER SEQUENCE pre_order_configs_id_seq OWNED BY pre_order_configs.id;
