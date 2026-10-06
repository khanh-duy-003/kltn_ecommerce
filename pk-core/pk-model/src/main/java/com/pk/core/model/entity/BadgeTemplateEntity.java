package com.pk.core.model.entity;

import com.pk.core.common.entity.BaseEntity;
import com.pk.core.model.constant.TableConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import vn.com.unit.miragesql.miragesql.annotation.Column;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import vn.com.unit.miragesql.miragesql.annotation.Table;

import java.util.Set;

/**
 * Mẫu nhãn dán sản phẩm theo spec FE (nhóm Badge). Xoá là xoá MỀM (extends BaseEntity); lúc xoá `code` được đổi
 * thành `code~id` để giải phóng mã (cột code UNIQUE). `styleConfig`/`assetMeta` là chuỗi JSON.
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.BADGE_TEMPLATES)
public class BadgeTemplateEntity extends BaseEntity {

    public static final String DRAFT = "DRAFT";
    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";
    public static final String ARCHIVED = "ARCHIVED";

    public static final String BADGE_PRE_ORDER = "PRE_ORDER";
    public static final String BADGE_OUT_OF_STOCK = "OUT_OF_STOCK";
    public static final String BADGE_CAMPAIGN = "CAMPAIGN";

    public static final Set<String> TYPES = Set.of("TEXT", "ICON", "IMAGE", "MINI_BANNER");
    public static final Set<String> BADGE_TYPES = Set.of("CAMPAIGN", "BEST_SELLER", "PRICE_DIFF", "OUT_OF_STOCK",
            "NEW_ARRIVAL", "LOW_STOCK", "PRE_ORDER");
    public static final Set<String> STATUSES = Set.of(DRAFT, ACTIVE, INACTIVE, ARCHIVED);
    public static final Set<String> POSITIONS = Set.of("TOP_LEFT", "TOP_CENTER", "TOP_RIGHT", "CENTER_LEFT",
            "CENTER_RIGHT", "BOTTOM_LEFT", "BOTTOM_CENTER", "BOTTOM_RIGHT", "PRICE_LINE");

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "badge_templates_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "type")
    private String type;

    @Column(name = "badge_type")
    private String badgeType;

    @Column(name = "status")
    private String status = DRAFT;

    @Column(name = "display_text")
    private String displayText;

    @Column(name = "default_position")
    private String defaultPosition = "TOP_LEFT";

    @Column(name = "style_config")
    private String styleConfig;

    @Column(name = "icon")
    private String icon;

    @Column(name = "image")
    private String image;

    @Column(name = "icon_mobile")
    private String iconMobile;

    @Column(name = "image_mobile")
    private String imageMobile;

    @Column(name = "asset_meta")
    private String assetMeta;

    @Column(name = "default_priority_weight")
    private int defaultPriorityWeight;
}
