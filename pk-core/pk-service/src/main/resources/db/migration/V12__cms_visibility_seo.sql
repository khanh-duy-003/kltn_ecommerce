-- V12: block có cờ hiển thị (is_visible); page lưu locale, cờ kích hoạt (is_active) và SEO (JSON text) theo form admin.
ALTER TABLE cms_blocks ADD COLUMN IF NOT EXISTS is_visible BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE cms_pages ADD COLUMN IF NOT EXISTS locale VARCHAR(10) NOT NULL DEFAULT 'vi';
ALTER TABLE cms_pages ADD COLUMN IF NOT EXISTS is_active BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE cms_pages ADD COLUMN IF NOT EXISTS seo TEXT;
