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

/** Khối nội dung trong 1 trang CMS (BANNER/PRODUCT_CAROUSEL/HERO - Admin mục O). Bảng `cms_blocks`
 * KHÔNG có deleted_id/deleted_date nên extends UpdateEntity - xoá block là HARD delete (xem
 * CmsServiceImpl.deleteBlock()), khác CmsPageEntity. `data` lưu CHUỖI thô (JSON do FE tự đóng gói,
 * VARCHAR(4000)) - Java KHÔNG parse/validate cấu trúc bên trong, cùng kiểu "lưu thô, FE tự diễn giải"
 * như ProductAttributeEntity.options. */
@Getter
@Setter
@NoArgsConstructor
@Table(name = TableConstant.CMS_BLOCKS)
public class CmsBlockEntity extends UpdateEntity {

    public static final String BANNER = "BANNER";
    public static final String PRODUCT_CAROUSEL = "PRODUCT_CAROUSEL";
    public static final String HERO = "HERO";

    @Id
    @PrimaryKey(generationType = GenerationType.SEQUENCE, generator = "cms_blocks_id_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "page_id")
    private Long pageId;

    @Column(name = "type")
    private String type;

    @Column(name = "sort_order")
    private int sortOrder;

    @Column(name = "data")
    private String data;

    public CmsBlockEntity(Long pageId, String type, int sortOrder, String data) {
        this.pageId = pageId;
        this.type = type;
        this.sortOrder = sortOrder;
        this.data = data;
    }
}
