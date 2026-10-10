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

/** Ảnh/video của sản phẩm. skuId null = dùng chung cả sản phẩm; có skuId = chỉ cho SKU đó. */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.PRODUCT_MEDIA)
public class ProductMediaEntity extends UpdateEntity {

    public static final String IMAGE = "IMAGE";
    public static final String VIDEO = "VIDEO";

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "product_media_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "sku_id")
    private Long skuId;

    @Column(name = "url")
    private String url;

    @Column(name = "alt")
    private String alt;

    @Column(name = "media_type")
    private String mediaType = IMAGE;

    @Column(name = "sort_order")
    private int sortOrder;

    /** Ảnh chính của sản phẩm (chỉ xét với media chung, skuId null): đồng bộ sang products.thumbnail_url. */
    @Column(name = "is_primary")
    private boolean primary;
}
