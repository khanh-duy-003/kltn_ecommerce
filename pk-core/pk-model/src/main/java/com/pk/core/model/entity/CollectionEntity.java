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

/** Bộ sưu tập sản phẩm (nhóm sản phẩm theo chủ đề, hiển thị landing/trang chủ). */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.COLLECTIONS)
public class CollectionEntity extends BaseEntity {

    public static final String DRAFT = "DRAFT";
    public static final String PUBLISHED = "PUBLISHED";

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "collections_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "slug")
    private String slug;

    @Column(name = "description")
    private String description;

    @Column(name = "hero_image_url")
    private String heroImageUrl;

    @Column(name = "status")
    private String status = DRAFT;

    public CollectionEntity(String name, String slug, String description) {
        this.name = name;
        this.slug = slug;
        this.description = description;
    }
}
