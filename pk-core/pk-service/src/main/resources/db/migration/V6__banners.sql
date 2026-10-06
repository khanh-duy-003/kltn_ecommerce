-- V6: banner trang chủ/danh mục theo spec FE (/storefront/banner/placements/code/{code}/render,
-- /admin/banner/banners...). THÊM MỚI, không sửa V1-V5. Dùng SEQUENCE tường minh như document/1. Table.sql.
--
-- banner_placements: vị trí hiển thị (code duy nhất, VD HOME_HERO) + kiểu hiển thị (display_type). Spec FE
--   KHÔNG có API quản lý vị trí nên seed sẵn vài vị trí (cuối file); thêm vị trí mới = INSERT tay.
-- banners: nội dung banner. actions là JSON (mảng nút bấm) lưu dạng TEXT. placement_code + sort_order là mở rộng
--   ngoài spec (spec không nói banner gắn vào vị trí nào) - mỗi banner thuộc tối đa 1 vị trí. Xoá banner là xoá mềm.

CREATE SEQUENCE banner_placements_id_seq;
CREATE SEQUENCE banners_id_seq;

CREATE TABLE banner_placements (
    id            BIGINT PRIMARY KEY DEFAULT nextval('banner_placements_id_seq'),
    code          VARCHAR(60) NOT NULL,
    name          VARCHAR(150) NOT NULL,
    display_type  VARCHAR(30) NOT NULL DEFAULT 'CAROUSEL',
    created_id    BIGINT,
    created_date  TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_banner_placements_code UNIQUE (code)
);

CREATE TABLE banners (
    id                       BIGINT PRIMARY KEY DEFAULT nextval('banners_id_seq'),
    internal_name            VARCHAR(150) NOT NULL,
    media_url                VARCHAR(500),
    media_mobile_url         VARCHAR(500),
    media_link_url           VARCHAR(500),
    media_poster_url         VARCHAR(500),
    media_mobile_poster_url  VARCHAR(500),
    media_fit                VARCHAR(20) NOT NULL DEFAULT 'cover',
    media_type               VARCHAR(20) NOT NULL DEFAULT 'IMAGE',
    layout                   VARCHAR(20) NOT NULL DEFAULT 'CENTER',
    title                    VARCHAR(200),
    subtitle                 VARCHAR(300),
    title_color              VARCHAR(30),
    actions_layout           VARCHAR(20) NOT NULL DEFAULT 'STACK',
    actions                  TEXT,
    overlay_opacity          NUMERIC(3, 2) NOT NULL DEFAULT 0,
    status                   VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    placement_code           VARCHAR(60),
    sort_order               INTEGER NOT NULL DEFAULT 0,
    created_id               BIGINT,
    created_date             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_id               BIGINT,
    updated_date             TIMESTAMPTZ,
    deleted_id               BIGINT,
    deleted_date             TIMESTAMPTZ,
    CONSTRAINT ck_banners_status CHECK (status IN ('DRAFT', 'ACTIVE', 'INACTIVE', 'ARCHIVED')),
    CONSTRAINT ck_banners_overlay CHECK (overlay_opacity >= 0 AND overlay_opacity <= 1)
);
CREATE INDEX ix_banners_placement ON banners (placement_code, status, sort_order);

ALTER SEQUENCE banner_placements_id_seq OWNED BY banner_placements.id;
ALTER SEQUENCE banners_id_seq OWNED BY banners.id;

INSERT INTO banner_placements (code, name, display_type) VALUES
    ('HOME_HERO',    'Banner chính trang chủ',      'CAROUSEL'),
    ('HOME_PROMO',   'Khuyến mãi trang chủ',        'GRID'),
    ('CATEGORY_TOP', 'Đầu trang danh mục sản phẩm', 'SINGLE')
ON CONFLICT (code) DO NOTHING;
