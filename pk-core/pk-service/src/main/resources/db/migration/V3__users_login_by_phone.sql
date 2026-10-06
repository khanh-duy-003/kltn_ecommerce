-- V3: đăng nhập bằng SỐ ĐIỆN THOẠI thay cho email. THÊM MỚI, không sửa V1 (rule "không sửa
-- migration đã có"). Sau V3: users.phone là định danh đăng nhập (NOT NULL, UNIQUE, dạng 0xxxxxxxxx);
-- users.email chỉ còn là thông tin phụ TUỲ CHỌN (nullable, vẫn UNIQUE khi có giá trị - Postgres cho
-- phép nhiều NULL trong cột UNIQUE nên nhiều user không có email vẫn hợp lệ).

-- 1) Chuẩn hoá SĐT cũ về dạng 0xxxxxxxxx (V1 cho nhập "+", khoảng trắng và không bắt buộc).
UPDATE users SET phone = regexp_replace(phone, '[\s.\-()]', '', 'g') WHERE phone IS NOT NULL;
UPDATE users SET phone = '0' || substr(phone, 4) WHERE phone LIKE '+84%';
UPDATE users SET phone = '0' || substr(phone, 3) WHERE phone ~ '^84[0-9]{9}$';

-- 2) User cũ chưa có SĐT: gán số GIẢ duy nhất theo id (0000000001...) để đặt được NOT NULL. Đây chỉ là
--    chỗ giữ chỗ - cần cập nhật số thật. SĐT cũ sai định dạng KHÔNG bị ghi đè: migration sẽ dừng ở
--    CHECK bên dưới để bạn tự sửa dữ liệu, thay vì mất số thật. Trùng SĐT sau chuẩn hoá cũng dừng ở UNIQUE.
UPDATE users SET phone = '0' || lpad(id::text, 9, '0') WHERE phone IS NULL;

ALTER TABLE users ALTER COLUMN phone SET NOT NULL;
ALTER TABLE users ADD CONSTRAINT uq_users_phone UNIQUE (phone);
ALTER TABLE users ADD CONSTRAINT ck_users_phone_format CHECK (phone ~ '^0[0-9]{9}$');

-- 3) Email thành cột phụ: bỏ NOT NULL. uq_users_email và ck_users_email_lowercase giữ nguyên
--    (CHECK với NULL luôn đạt, UNIQUE bỏ qua NULL).
ALTER TABLE users ALTER COLUMN email DROP NOT NULL;
