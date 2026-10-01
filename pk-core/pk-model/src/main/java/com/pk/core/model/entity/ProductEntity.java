package com.pk.core.model.entity;

import com.pk.core.model.constant.TableConstant;
import com.pk.core.common.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import vn.com.unit.miragesql.miragesql.annotation.Column;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import vn.com.unit.miragesql.miragesql.annotation.Table;

import java.math.BigDecimal;
import java.util.Date;

/** Sản phẩm. basePrice do service tính lại từ SKU khi đọc (không phụ thuộc cột này còn đúng hay
 * không), cột chỉ để tối ưu sort/lọc giá ở tầng DB. */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.PRODUCTS)
public class ProductEntity extends BaseEntity {

    public static final String DRAFT = "DRAFT";
    public static final String PUBLISHED = "PUBLISHED";
    public static final String ARCHIVED = "ARCHIVED";

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "products_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "slug")
    private String slug;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "description")
    private String description;

    @Column(name = "material")
    private String material;

    @Column(name = "occasion")
    private String occasion;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "base_price")
    private BigDecimal basePrice = BigDecimal.ZERO;

    @Column(name = "status")
    private String status = DRAFT;

    @Column(name = "published_at")
    private Date publishedAt;

    @Column(name = "version")
    private long version;

    public ProductEntity(Long categoryId, String code, String name, String slug) {
        this.categoryId = categoryId;
        this.code = code;
        this.name = name;
        this.slug = slug;
    }

    public void publish() {
        this.status = PUBLISHED;
        this.publishedAt = new Date();
    }

    public boolean isPublished() {
        return PUBLISHED.equals(status);
    }
}
