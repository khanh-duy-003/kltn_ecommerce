-- V4: bảng OTP gửi tới số điện thoại (đăng ký và quên mật khẩu theo spec FE /storefront/auth/...).
-- THÊM MỚI, không sửa V1-V3 (rule "không sửa migration đã có"). Dùng SEQUENCE tường minh như
-- document/1. Table.sql (id lấy từ nextval('phone_otps_id_seq'), Mirage cũng lấy id từ sequence này).
-- Mã OTP và registration token chỉ lưu SHA-256 (hex 64 ký tự), không lưu bản rõ.

CREATE SEQUENCE phone_otps_id_seq;

CREATE TABLE phone_otps (
    id                       BIGINT PRIMARY KEY DEFAULT nextval('phone_otps_id_seq'),
    phone                    VARCHAR(20) NOT NULL,
    purpose                  VARCHAR(20) NOT NULL,
    code_hash                VARCHAR(64) NOT NULL,
    expires_at               TIMESTAMPTZ NOT NULL,
    attempts                 INT NOT NULL DEFAULT 0,
    verified_at              TIMESTAMPTZ,
    registration_token_hash  VARCHAR(64),
    registration_expires_at  TIMESTAMPTZ,
    consumed_at              TIMESTAMPTZ,
    created_id               BIGINT,
    created_date             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_id               BIGINT,
    updated_date             TIMESTAMPTZ,
    CONSTRAINT ck_phone_otps_purpose CHECK (purpose IN ('REGISTER', 'RESET_PASSWORD')),
    CONSTRAINT ck_phone_otps_phone_format CHECK (phone ~ '^0[0-9]{9}$')
);
CREATE INDEX ix_phone_otps_phone ON phone_otps (phone, purpose, created_date DESC);
CREATE UNIQUE INDEX uq_phone_otps_reg_token ON phone_otps (registration_token_hash) WHERE registration_token_hash IS NOT NULL;

ALTER SEQUENCE phone_otps_id_seq OWNED BY phone_otps.id;
