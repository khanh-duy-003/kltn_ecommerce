-- V15: đơn của khách vãng lai (không đăng nhập): user_id cho phép NULL; tra cứu đơn bằng mã đơn + số điện thoại giao hàng.
ALTER TABLE orders ALTER COLUMN user_id DROP NOT NULL;
ALTER TABLE orders ADD COLUMN IF NOT EXISTS guest_email VARCHAR(150);
