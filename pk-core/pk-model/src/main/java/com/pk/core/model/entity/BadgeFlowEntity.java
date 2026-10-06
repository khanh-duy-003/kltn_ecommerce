package com.pk.core.model.entity;

import com.pk.core.common.entity.UpdateEntity;
import com.pk.core.model.constant.TableConstant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import vn.com.unit.miragesql.miragesql.annotation.Column;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import vn.com.unit.miragesql.miragesql.annotation.Table;

import java.util.Date;
import java.util.Set;

/**
 * Luồng hiển thị nhãn (spec FE: badge-flow): khi nào (thời gian, kênh, rule) áp những mẫu nhãn nào. Bảng `badge_flow`
 * KHÔNG xoá (spec không có DELETE) nên extends UpdateEntity. `ruleConfig` và `templates` là chuỗi JSON
 * (templates = [{badgeTemplateId, priorityWeight, isPinned}]).
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.BADGE_FLOW)
public class BadgeFlowEntity extends UpdateEntity {

    public static final String DRAFT = "DRAFT";
    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";

    public static final String RULE_MANUAL = "MANUAL";
    public static final String RULE_ALL = "ALL";
    public static final String RULE_CATEGORY = "CATEGORY";
    public static final String RULE_COLLECTION = "COLLECTION";
    public static final String RULE_PROMOTION = "PROMOTION";
    public static final String RULE_OUT_OF_STOCK = "OUT_OF_STOCK";

    public static final String CHANNEL_ALL = "ALL";

    public static final Set<String> STATUSES = Set.of(DRAFT, ACTIVE, INACTIVE);
    public static final Set<String> RULE_TYPES = Set.of(RULE_MANUAL, RULE_ALL, RULE_CATEGORY, RULE_COLLECTION,
            RULE_PROMOTION, RULE_OUT_OF_STOCK);
    public static final Set<String> CHANNELS = Set.of(CHANNEL_ALL, "WEB", "MOBILE_WEB", "APP");

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "badge_flow_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status = DRAFT;

    @Column(name = "active_from")
    private Date activeFrom;

    @Column(name = "active_to")
    private Date activeTo;

    @Column(name = "rule_type")
    private String ruleType;

    @Column(name = "rule_config")
    private String ruleConfig;

    @Column(name = "channel")
    private String channel = CHANNEL_ALL;

    @Column(name = "templates")
    private String templates;

    /** Đang hiệu lực tại `now`: ACTIVE và nằm trong [activeFrom, activeTo] (đầu mút null = không giới hạn). */
    public boolean isActiveAt(Date now) {
        return ACTIVE.equals(status)
                && (activeFrom == null || !now.before(activeFrom))
                && (activeTo == null || !now.after(activeTo));
    }
}
