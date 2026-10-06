-- V9: làm lại module Badge theo spec FE (badge_templates: code/type/badge_type/status/position/style...; badge_flow: name/
-- status/thời gian/rule/channel/templates JSON). Bảng cũ (V2) chỉ chứa dữ liệu mẫu nên DROP và tạo lại. Dùng SEQUENCE
-- tường minh như document/1. Table.sql.

DROP TABLE IF EXISTS badge_flow;
DROP TABLE IF EXISTS badge_templates;

CREATE SEQUENCE IF NOT EXISTS badge_templates_id_seq;
CREATE SEQUENCE IF NOT EXISTS badge_flow_id_seq;

CREATE TABLE badge_templates (
    id                       BIGINT PRIMARY KEY DEFAULT nextval('badge_templates_id_seq'),
    name                     VARCHAR(120) NOT NULL,
    code                     VARCHAR(80) NOT NULL,
    description              VARCHAR(500),
    type                     VARCHAR(20) NOT NULL,
    badge_type               VARCHAR(20) NOT NULL,
    status                   VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    display_text             VARCHAR(60),
    default_position         VARCHAR(20) NOT NULL DEFAULT 'TOP_LEFT',
    style_config             TEXT,
    icon                     VARCHAR(255),
    image                    VARCHAR(500),
    icon_mobile              VARCHAR(255),
    image_mobile             VARCHAR(500),
    asset_meta               TEXT,
    default_priority_weight  INTEGER NOT NULL DEFAULT 0,
    created_id               BIGINT,
    created_date             TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_id               BIGINT,
    updated_date             TIMESTAMPTZ,
    deleted_id               BIGINT,
    deleted_date             TIMESTAMPTZ,
    CONSTRAINT uq_badge_templates_code UNIQUE (code),
    CONSTRAINT ck_badge_templates_type CHECK (type IN ('TEXT', 'ICON', 'IMAGE', 'MINI_BANNER')),
    CONSTRAINT ck_badge_templates_badge_type CHECK (badge_type IN ('CAMPAIGN', 'BEST_SELLER', 'PRICE_DIFF', 'OUT_OF_STOCK', 'NEW_ARRIVAL', 'LOW_STOCK', 'PRE_ORDER')),
    CONSTRAINT ck_badge_templates_status CHECK (status IN ('DRAFT', 'ACTIVE', 'INACTIVE', 'ARCHIVED')),
    CONSTRAINT ck_badge_templates_position CHECK (default_position IN ('TOP_LEFT', 'TOP_CENTER', 'TOP_RIGHT', 'CENTER_LEFT', 'CENTER_RIGHT', 'BOTTOM_LEFT', 'BOTTOM_CENTER', 'BOTTOM_RIGHT', 'PRICE_LINE'))
);

CREATE TABLE badge_flow (
    id            BIGINT PRIMARY KEY DEFAULT nextval('badge_flow_id_seq'),
    name          VARCHAR(150) NOT NULL,
    description   VARCHAR(500),
    status        VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    active_from   TIMESTAMPTZ,
    active_to     TIMESTAMPTZ,
    rule_type     VARCHAR(20) NOT NULL,
    rule_config   TEXT,
    channel       VARCHAR(20) NOT NULL DEFAULT 'ALL',
    templates     TEXT NOT NULL,
    created_id    BIGINT,
    created_date  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_id    BIGINT,
    updated_date  TIMESTAMPTZ,
    CONSTRAINT ck_badge_flow_status CHECK (status IN ('DRAFT', 'ACTIVE', 'INACTIVE')),
    CONSTRAINT ck_badge_flow_rule_type CHECK (rule_type IN ('MANUAL', 'ALL', 'CATEGORY', 'COLLECTION', 'PROMOTION', 'OUT_OF_STOCK')),
    CONSTRAINT ck_badge_flow_channel CHECK (channel IN ('ALL', 'WEB', 'MOBILE_WEB', 'APP'))
);

ALTER SEQUENCE badge_templates_id_seq OWNED BY badge_templates.id;
ALTER SEQUENCE badge_flow_id_seq OWNED BY badge_flow.id;
