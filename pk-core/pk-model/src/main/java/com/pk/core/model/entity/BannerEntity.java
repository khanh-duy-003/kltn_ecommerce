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

import java.math.BigDecimal;

/**
 * Banner (spec FE nhóm Banner). Xoá là xoá MỀM (extends BaseEntity). {@code actions} là chuỗi JSON của mảng nút bấm
 * (BannerActionResponseDto) - service tự (de)serialize. {@code placementCode}/{@code sortOrder} là mở rộng ngoài spec:
 * banner thuộc tối đa 1 vị trí hiển thị, sắp theo sortOrder.
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.BANNERS)
public class BannerEntity extends BaseEntity {

    public static final String DRAFT = "DRAFT";
    public static final String ACTIVE = "ACTIVE";

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "banners_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "internal_name")
    private String internalName;

    @Column(name = "media_url")
    private String mediaUrl;

    @Column(name = "media_mobile_url")
    private String mediaMobileUrl;

    @Column(name = "media_link_url")
    private String mediaLinkUrl;

    @Column(name = "media_poster_url")
    private String mediaPosterUrl;

    @Column(name = "media_mobile_poster_url")
    private String mediaMobilePosterUrl;

    @Column(name = "media_fit")
    private String mediaFit = "cover";

    @Column(name = "media_type")
    private String mediaType = "IMAGE";

    @Column(name = "layout")
    private String layout = "CENTER";

    @Column(name = "title")
    private String title;

    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "title_color")
    private String titleColor;

    @Column(name = "actions_layout")
    private String actionsLayout = "STACK";

    @Column(name = "actions")
    private String actions;

    @Column(name = "overlay_opacity")
    private BigDecimal overlayOpacity = BigDecimal.ZERO;

    @Column(name = "status")
    private String status = DRAFT;

    @Column(name = "placement_code")
    private String placementCode;

    @Column(name = "sort_order")
    private int sortOrder;
}
