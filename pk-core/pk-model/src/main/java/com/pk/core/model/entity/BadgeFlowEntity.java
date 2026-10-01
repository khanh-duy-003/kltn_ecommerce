package com.pk.core.model.entity;

import com.pk.core.model.constant.TableConstant;
import com.pk.core.common.entity.UpdateEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import vn.com.unit.miragesql.miragesql.annotation.Column;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import vn.com.unit.miragesql.miragesql.annotation.Table;

/** 1 luật cấu hình khi nào áp {@link BadgeTemplateEntity} nào (Admin mục P). Bảng `badge_flow`
 * KHÔNG có deleted_id/deleted_date nên extends UpdateEntity (xoá luật CHƯA có endpoint riêng ở spec
 * mục P - chỉ GET/POST/PUT, không có DELETE cho badge-flow, khác badge-templates). Hằng ruleType/
 * channel đặt tiền tố RULE_/CHANNEL_ để tránh trùng tên (cả 2 nhóm đều có giá trị "ALL"). */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.BADGE_FLOW)
public class BadgeFlowEntity extends UpdateEntity {

    public static final String RULE_MANUAL = "MANUAL";
    public static final String RULE_ALL = "ALL";
    public static final String RULE_CATEGORY = "CATEGORY";
    public static final String RULE_COLLECTION = "COLLECTION";
    public static final String RULE_PROMOTION = "PROMOTION";
    public static final String RULE_OUT_OF_STOCK = "OUT_OF_STOCK";

    public static final String CHANNEL_ALL = "ALL";
    public static final String CHANNEL_WEB = "WEB";
    public static final String CHANNEL_MOBILE_WEB = "MOBILE_WEB";
    public static final String CHANNEL_APP = "APP";

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "badge_flow_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "badge_id")
    private Long badgeId;

    @Column(name = "rule_type")
    private String ruleType;

    @Column(name = "rule_ref_id")
    private Long ruleRefId;

    @Column(name = "channel")
    private String channel = CHANNEL_ALL;

    @Column(name = "priority")
    private int priority;

    @Column(name = "active")
    private boolean active = true;

    public BadgeFlowEntity(Long badgeId, String ruleType, Long ruleRefId, String channel, int priority,
                            boolean active) {
        this.badgeId = badgeId;
        this.ruleType = ruleType;
        this.ruleRefId = ruleRefId;
        this.channel = channel != null && !channel.isBlank() ? channel : CHANNEL_ALL;
        this.priority = priority;
        this.active = active;
    }
}
